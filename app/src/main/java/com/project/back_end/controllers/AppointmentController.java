package com.project.back_end.controllers;

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("/appointments")` to set a base path for all appointment-related endpoints.
//    - This centralizes all routes that deal with booking, updating, retrieving, and canceling appointments.


import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// 2. Autowire Dependencies:
//    - Inject `AppointmentService` for handling the business logic specific to appointments.
//    - Inject the general `Service` class, which provides shared functionality like token validation and appointment checks.
@RestController
@RequestMapping("appointments")
public class AppointmentController {

    AppointmentService appointmentService;
    Service service;

    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // 3. Define the `getAppointments` Method:
//    - Handles HTTP GET requests to fetch appointments based on date and patient name.
//    - Takes the appointment date, patient name, and token as path variables.
//    - First validates the token for role `"doctor"` using the `Service`.
//    - If the token is valid, returns appointments for the given patient on the specified date.
//    - If the token is invalid or expired, responds with the appropriate message and status code.
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date,
            @PathVariable String patientName,
            @PathVariable String token
            ){
        Map<String, Object> result;

        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "doctor");
        if(validationResult.getStatusCode().is4xxClientError()){
            result = new HashMap<>(validationResult.getBody());
            return new ResponseEntity<>(result, validationResult.getHeaders(), validationResult.getStatusCode());
        }
        result = appointmentService.getAppointment(patientName, date, token);
        return ResponseEntity.ok(result);
    }

// 4. Define the `bookAppointment` Method:
//    - Handles HTTP POST requests to create a new appointment.
//    - Accepts a validated `Appointment` object in the request body and a token as a path variable.
//    - Validates the token for the `"patient"` role.
//    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
//    - Returns success if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @RequestBody @Valid Appointment appointment,
            @PathVariable String token
    ){
        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "Patient");
        if(validationResult.getStatusCode().is4xxClientError()) return validationResult;

        Map<String, String> result = new HashMap<>();
        return switch (service.validateAppointment(appointment)) {
            case 1 -> {
                if(appointmentService.bookAppointment(appointment) == 1) {
                    result.put("message", "Appointment booked successfully");
                    yield ResponseEntity.status(HttpStatus.CREATED).body(result);
                }
                result.put("error", "INTERNAL SERVER ERROR");
                yield ResponseEntity.internalServerError().body(result);
            }
            case 0 -> {
                result.put("error", "no valid time available");
                yield ResponseEntity.status(HttpStatus.CONFLICT).body(result);
            }
            case -1 -> {
                result.put("error", "no doctor found");
                yield ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            default -> {
                result.put("error", "INTERNAL SERVER ERROR");
                yield ResponseEntity.internalServerError().body(result);
            }
        };
    }

// 5. Define the `updateAppointment` Method:
//    - Handles HTTP PUT requests to modify an existing appointment.
//    - Accepts a validated `Appointment` object and a token as input.
//    - Validates the token for `"patient"` role.
//    - Delegates the update logic to the `AppointmentService`.
//    - Returns an appropriate success or failure response based on the update result.
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @PathVariable String token,
            @RequestBody @Valid Appointment appointment
    ){
        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "Patient");
        if(validationResult.getStatusCode().is4xxClientError()){
            return validationResult;
        }
        return appointmentService.updateAppointment(appointment);
    }

// 6. Define the `cancelAppointment` Method:
//    - Handles HTTP DELETE requests to cancel a specific appointment.
//    - Accepts the appointment ID and a token as path variables.
//    - Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
//    - Calls `AppointmentService` to handle the cancellation process and returns the result.
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteAppointment(
            @PathVariable Long id,
            @PathVariable String token){
        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "Patient");
        if(validationResult.getStatusCode().is4xxClientError()){
            return validationResult;
        }
        return appointmentService.cancelAppointment(id, token);
    }
}
