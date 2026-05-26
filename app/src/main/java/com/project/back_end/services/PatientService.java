package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

// 1. **Add @Service Annotation**:
//    - The `@Service` annotation is used to mark this class as a Spring service component.
//    - It will be managed by Spring's container and used for business logic related to patients and appointments.
//    - Instruction: Ensure that the `@Service` annotation is applied above the class declaration.
// 2. **Constructor Injection for Dependencies**:
//    - The `PatientService` class has dependencies on `PatientRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies are injected via the constructor to maintain good practices of dependency injection and testing.
//    - Instruction: Ensure constructor injection is used for all the required dependencies.

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 3. **createPatient Method**:
//    - Creates a new patient in the database. It saves the patient object using the `PatientRepository`.
//    - If the patient is successfully saved, the method returns `1`; otherwise, it logs the error and returns `0`.
//    - Instruction: Ensure that error handling is done properly and exceptions are caught and logged appropriately.
// 1 = success, 0 = fail,
    public int createPatient(Patient patient){
        try{
            patientRepository.save(patient);
            System.out.println("Patient Saved");
            return 1;
        } catch (Exception e){
            System.out.println("Error: " + e);
            return 0;
        }
    }
// 4. **getPatientAppointment Method**:
//    - Retrieves a list of appointments for a specific patient, based on their ID.
//    - The appointments are then converted into `AppointmentDTO` objects for easier consumption by the API client.
//    - This method is marked as `@Transactional` to ensure database consistency during the transaction.
//    - Instruction: Ensure that appointment data is properly converted into DTOs and the method handles errors gracefully.
   @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token){
        Map<String, Object> result = new HashMap<>();
        Optional<Patient> patient = patientRepository.findById(id);
        if(patient.isEmpty()){
            result.put("error", "patient not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        if(!Objects.equals(patient.get().getEmail(), tokenService.extractEmail(token))){
            result.put("error", "patient id doesn't match tokens");
            return ResponseEntity.badRequest().body(result);
        }

        try{
            List<AppointmentDTO> appointmentDTOList = appointmentRepository.findByPatientId(id).stream()
                    .map(app -> new AppointmentDTO(
                            app.getId(),
                            app.getDoctor().getId(),
                            app.getDoctor().getName(),
                            app.getPatient().getId(),
                            app.getPatient().getName(),
                            app.getPatient().getEmail(),
                            app.getPatient().getPhone(),
                            app.getPatient().getAddress(),
                            app.getAppointmentTime(),
                            app.getStatus()))
                    .toList();
            result.put("appointment", appointmentDTOList);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (Exception e){
            System.out.println("Error: " + e);
            result.put("error", "INTERNAL SERVER ERROR");
            return ResponseEntity.internalServerError().body(result);
        }
    }
// 5. **filterByCondition Method**:
//    - Filters appointments for a patient based on the condition (e.g., "past" or "future").
//    - Retrieves appointments with a specific status (0 for future, 1 for past) for the patient.
//    - Converts the appointments into `AppointmentDTO` and returns them in the response.
//    - Instruction: Ensure the method correctly handles "past" and "future" conditions, and that invalid conditions are caught and returned as errors.
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id){
        Map<String, Object> result = new HashMap<>();
        if(condition.isBlank() ||
                (!condition.equalsIgnoreCase("past") &&
                (!condition.equalsIgnoreCase("future")))){
            result.put("error", "invalid filter");
            return ResponseEntity.badRequest().body(result);
        }
        int statusInt = condition.equalsIgnoreCase("past") ? 1 : 0;


        Optional<Patient> patient = patientRepository.findById(id);
        if(patient.isEmpty()){
            result.put("error", "patient not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        try{
            List<AppointmentDTO> appointmentDTOList =
                    appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, statusInt).stream()
                    .map(app -> new AppointmentDTO(
                            app.getId(),
                            app.getDoctor().getId(),
                            app.getDoctor().getName(),
                            app.getPatient().getId(),
                            app.getPatient().getName(),
                            app.getPatient().getEmail(),
                            app.getPatient().getPhone(),
                            app.getPatient().getAddress(),
                            app.getAppointmentTime(),
                            app.getStatus()))
                    .toList();
            result.put("appointment", appointmentDTOList);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (Exception e){
            System.out.println("Error: " + e);
            result.put("error", "INTERNAL SERVER ERROR");
            return ResponseEntity.internalServerError().body(result);
        }
    }
// 6. **filterByDoctor Method**:
//    - Filters appointments for a patient based on the doctor's name.
//    - It retrieves appointments where the doctor’s name matches the given value, and the patient ID matches the provided ID.
//    - Instruction: Ensure that the method correctly filters by doctor's name and patient ID and handles any errors or invalid cases.
    public ResponseEntity<Map<String, Object>> filterByDoctor(String doctorName, Long id){
        Map<String, Object> result = new HashMap<>();

        Optional<Patient> patient = patientRepository.findById(id);
        if(patient.isEmpty()){
            result.put("error", "patient not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        result.put("appointments",
                appointmentRepository.filterByDoctorNameAndPatientId(doctorName, id).stream()
                        .map(app -> new AppointmentDTO(
                                app.getId(),
                                app.getDoctor().getId(),
                                app.getDoctor().getName(),
                                app.getPatient().getId(),
                                app.getPatient().getName(),
                                app.getPatient().getEmail(),
                                app.getPatient().getPhone(),
                                app.getPatient().getAddress(),
                                app.getAppointmentTime(),
                                app.getStatus()))
                        .toList());
        return ResponseEntity.ok().body(result);
    }
// 7. **filterByDoctorAndCondition Method**:
//    - Filters appointments based on both the doctor's name and the condition (past or future) for a specific patient.
//    - This method combines filtering by doctor name and appointment status (past or future).
//    - Converts the appointments into `AppointmentDTO` objects and returns them in the response.
//    - Instruction: Ensure that the filter handles both doctor name and condition properly, and catches errors for invalid input.
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition,String doctorName, Long id){
        Map<String, Object> result = new HashMap<>();
        if(condition.isBlank() ||
                (!condition.equalsIgnoreCase("past") &&
                (!condition.equalsIgnoreCase("future"))))
        {
            result.put("error", "invalid filter");
            return ResponseEntity.badRequest().body(result);
        }
        int statusInt = condition.equalsIgnoreCase("past") ? 1 : 0;

        Optional<Patient> patient = patientRepository.findById(id);
        if(patient.isEmpty()){
            result.put("error", "patient not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        try{
            result.put("appointments",
                    appointmentRepository.filterByDoctorNameAndPatientId(doctorName, id).stream()
                            .map(app -> new AppointmentDTO(
                                    app.getId(),
                                    app.getDoctor().getId(),
                                    app.getDoctor().getName(),
                                    app.getPatient().getId(),
                                    app.getPatient().getName(),
                                    app.getPatient().getEmail(),
                                    app.getPatient().getPhone(),
                                    app.getPatient().getAddress(),
                                    app.getAppointmentTime(),
                                    app.getStatus()))
                            .toList());
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (Exception e){
            System.out.println("Error: " + e);
            result.put("error", "INTERNAL SERVER ERROR");
            return ResponseEntity.internalServerError().body(result);
        }
    }

// 8. **getPatientDetails Method**:
//    - Retrieves patient details using the `tokenService` to extract the patient's email from the provided token.
//    - Once the email is extracted, it fetches the corresponding patient from the `patientRepository`.
//    - It returns the patient's information in the response body.
//    - Instruction: Make sure that the token extraction process works correctly and patient details are fetched properly based on the extracted email.
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token){
        Map<String, Object> result = new HashMap<>();
        String email = tokenService.extractEmail(token);
        if(email == null){
            result.put("error", "invalid token");
            return ResponseEntity.badRequest().body(result);
        }

        Patient patient = patientRepository.findByEmail(email);
        if(patient == null){
            result.put("error", "patient in token not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        result.put("patient", patient);
        return ResponseEntity.ok().body(result);
    }
// 9. **Handling Exceptions and Errors**:
//    - The service methods handle exceptions using try-catch blocks and log any issues that occur. If an error occurs during database operations, the service responds with appropriate HTTP status codes (e.g., `500 Internal Server Error`).
//    - Instruction: Ensure that error handling is consistent across the service, with proper logging and meaningful error messages returned to the client.

// 10. **Use of DTOs (Data Transfer Objects)**:
//    - The service uses `AppointmentDTO` to transfer appointment-related data between layers. This ensures that sensitive or unnecessary data (e.g., password or private patient information) is not exposed in the response.
//    - Instruction: Ensure that DTOs are used appropriately to limit the exposure of internal data and only send the relevant fields to the client.
}
