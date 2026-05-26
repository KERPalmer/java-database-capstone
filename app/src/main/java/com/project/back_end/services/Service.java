package com.project.back_end.services;
// 1. **@Service Annotation**
// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@org.springframework.stereotype.Service
public class Service {

    // 2. **Constructor Injection for Dependencies**
// The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
// and ensures that all required dependencies are provided at object creation time.
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;
    private final PatientRepository patientRepository;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   DoctorService doctorService,
                   PatientRepository patientRepository,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.doctorService = doctorService;
        this.patientRepository = patientRepository;
        this.patientService = patientService;
    }

    // 3. **validateToken Method**
// This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
// If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
// unauthorized access to protected resources.
    public ResponseEntity<Map<String, String>> validateToken(String token, String user){
        Map<String, String> result = new HashMap<>();
        if (!tokenService.validateToken(token, user)){
            result.put("error", "invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
        result.put("message", "token is valid");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    }
// 4. **validateAdmin Method**
// This method validates the login credentials for an admin user.
// - It first searches the admin repository using the provided username.
// - If an admin is found, it checks if the password matches.
// - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
// - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
// - If no admin is found, it also returns a 401 Unauthorized.
// - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
// This method ensures that only valid admin users can access secured parts of the system.
    public ResponseEntity<Map<String, String>> validateAdmin(Admin recievedAdmin){
        Map<String, String> result = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(recievedAdmin.getUsername());
            if (admin == null) {
                result.put("error", "invalid email ID");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }
            if (!Objects.equals(recievedAdmin.getPassword(), admin.getPassword())) {
                result.put("error", "passwords do not match");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }
            result.put("token", tokenService.generateToken(admin.getUsername()));
            return ResponseEntity.ok(result);
        } catch (Exception e){
            System.out.println("Error: " + e);
            result.put("error", "INTERNAL SERVER ERROR");
            return ResponseEntity.internalServerError().body(result);
        }

}

// 5. **filterDoctor Method**
// This method provides filtering functionality for doctors based on name, specialty, and available time slots.
// - It supports various combinations of the three filters.
// - If none of the filters are provided, it returns all available doctors.
// This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.
    public Map<String, Object> filterDoctor(String name, String specialty, String time){
        boolean hasName = name != null && !name.equalsIgnoreCase("null");
        boolean hasSpecialty = specialty != null && !specialty.equalsIgnoreCase("null");
        boolean hasTime = time != null && !time.equalsIgnoreCase("null");

        if(hasName && hasSpecialty && hasTime) return doctorService.filterDoctorsByNameSpecialtyandTime(name, specialty, time);
        if(hasName && hasSpecialty) return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        if(hasName && hasTime) return doctorService.filterDoctorByNameAndTime(name, time);
        if(hasTime && hasSpecialty) return doctorService.filterDoctorByTimeAndSpecility(time, specialty);
        if(hasName) return doctorService.findDoctorByName(name);
        if(hasSpecialty) return doctorService.filterDoctorBySpecility(specialty);
        if(hasTime) return doctorService.filterDoctorByTime(time);
        return Map.of("doctors", doctorService.getDoctors());
    }

// 6. **validateAppointment Method**
// This method validates if the requested appointment time for a doctor is available.
// - It first checks if the doctor exists in the repository.
// - Then, it retrieves the list of available time slots for the doctor on the specified date.
// - It compares the requested appointment time with the start times of these slots.
// - If a match is found, it returns 1 (valid appointment time).
// - If no matching time slot is found, it returns 0 (invalid).
// - If the doctor doesn’t exist, it returns -1.
// This logic prevents overlapping or invalid appointment bookings.
    public int validateAppointment(Appointment appointment){
        Doctor doctor = appointment.getDoctor();
        Optional<Doctor> result = doctorRepository.findById(doctor.getId());
        if(result.isEmpty()){
            return -1;
        }
        LocalDateTime appointmentDateTime = appointment.getAppointmentTime();
        List<String> availableTimes = doctorService.getDoctorAvailability(
                doctor.getId(), appointmentDateTime.toLocalDate()
        );
        for(String timeSlot : availableTimes){
            LocalTime start = LocalTime.parse(timeSlot.split("-")[0]);
            if(appointmentDateTime.toLocalTime().equals(start)){
                return 1;
            }
        }
        return 0;
    }
// 7. **validatePatient Method**
// This method checks whether a patient with the same email or phone number already exists in the system.
// - If a match is found, it returns false (indicating the patient is not valid for new registration).
// - If no match is found, it returns true.
// This helps enforce uniqueness constraints on patient records and prevent duplicate entries.
    public boolean validatePatient(String email, String phoneNumber){
        return patientRepository.findByEmailOrPhone(email, phoneNumber) == null;
    }
// 8. **validatePatientLogin Method**
// This method handles login validation for patient users.
// - It looks up the patient by email.
// - If found, it checks whether the provided password matches the stored one.
// - On successful validation, it generates a JWT token and returns it with a 200 OK status.
// - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
// - If an exception occurs, it returns a 500 Internal Server Error.
// This method ensures only legitimate patients can log in and access their data securely.
    public  ResponseEntity<Map<String, String>> validatePatientLogin(Login login){
        Map<String, String> result = new HashMap<>();
        try{
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if(patient == null){
                result.put("error", "invalid email");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }
            if(!Objects.equals(patient.getPassword(), login.getPassword())){
                result.put("error", "passwords do not match");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }
            result.put("token", tokenService.generateToken(login.getEmail()));
            return ResponseEntity.ok(result);
        }  catch (Exception e){
            System.out.println("Error: " + e);
            result.put("error", "INTERNAL SERVER ERROR");
            return ResponseEntity.internalServerError().body(result);
        }
    }
// 9. **filterPatient Method**
// This method filters a patient's appointment history based on condition and doctor name.
// - It extracts the email from the JWT token to identify the patient.
// - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
// - If no filters are provided, it retrieves all appointments for the patient.
// This flexible method supports patient-specific querying and enhances user experience on the client side.
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String doctorName, String token){
        Map<String, Object> result = new HashMap<>();
        boolean hasCondition = condition != null && !condition.isBlank();
        boolean hasDoctorName = doctorName!= null && !doctorName.isBlank();

        String extractedEmail = tokenService.extractEmail(token);
        if(extractedEmail == null || extractedEmail.isBlank()){
            result.put("error", "invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
        Patient patient = patientRepository.findByEmail(extractedEmail);

        if(hasCondition && hasDoctorName) return patientService.filterByDoctorAndCondition(doctorName, condition, patient.getId());
        if(hasCondition) return patientService.filterByCondition(condition, patient.getId());
        if(hasDoctorName) return patientService.filterByDoctor(doctorName, patient.getId());
        return patientService.getPatientAppointment(patient.getId(), token);

    }

}
