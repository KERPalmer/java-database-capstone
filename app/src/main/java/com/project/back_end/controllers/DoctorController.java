package com.project.back_end.controllers;

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.


import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    DoctorService doctorService;
    Service service;

    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.
    @GetMapping("/availability/{user}/{doctorID}/{date}/{toke}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String token
    ){
        Map<String, Object> result = new HashMap<>();

        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, user);
        if(validationResult.getStatusCode().is4xxClientError()){
            result = new HashMap<>(validationResult.getBody());
            return new ResponseEntity<>(result, validationResult.getHeaders(), validationResult.getStatusCode());
        }
        result.put("message", doctorService.getDoctorAvailability(doctorId, date));
        return ResponseEntity.ok(result);
    }

// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor(){
        return ResponseEntity.ok(Map.of("doctors", doctorService.getDoctors()));
    }

// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(@RequestBody @Valid Doctor doctor,
                                                          @PathVariable String token){

        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "admin");
        if(validationResult.getStatusCode().is4xxClientError()){
            return validationResult;
        }
        Map<String, String> result = new HashMap<>();
        return switch(doctorService.saveDoctor(doctor)){
            case 1 -> {
                result.put("message", "Doctor added to db");
                yield ResponseEntity.status(HttpStatus.CREATED).body(result);
            }
            case 0 -> {
                result.put("message", "Doctor already exists");
                yield ResponseEntity.status(HttpStatus.CONFLICT).body(result);
            }
            //also -1
            default -> {
                result.put("message", "Some internal error occurred");
                yield ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        };
    }

// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(
            @RequestBody @Valid Login login
            ){
        return doctorService.validateDoctor(login);
    }


// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody @Valid Doctor doctor,
            @PathVariable String token
    ){
        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "admin");
        if(validationResult.getStatusCode().is4xxClientError()){
            return validationResult;
        }

        Map<String, String> result = new HashMap<>();
        return switch(doctorService.updateDoctor(doctor)) {
            case 1 -> {
                result.put("message", "Doctor updated");
                yield ResponseEntity.ok(result);
            }
            case -1 -> {
                result.put("message", "Doctor not found");
                yield ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            default -> {
                result.put("message", "Some internal error occurred");
                yield ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        };
    }

// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDocotor(
            @PathVariable Long id,
            @PathVariable String token
    ){
        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "admin");
        if(validationResult.getStatusCode().is4xxClientError()){
            return validationResult;
        }

        Map<String, String> result = new HashMap<>();
        return switch(doctorService.deleteDoctor(id)) {
            case 1 -> {
                result.put("message", "Doctor deleted Successfully");
                yield ResponseEntity.ok(result);
            }
            case -1 -> {
                result.put("message", "Doctor not found with id");
                yield ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            default -> {
                result.put("message", "Some internal error occurred");
                yield ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
        };
    }

// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality
    ){
        return ResponseEntity.ok(service.filterDoctor(name, speciality, time));
    }

}
