package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// 1. **Add @Service Annotation**:
//    - This class should be annotated with `@Service` to indicate that it is a service layer class.
//    - The `@Service` annotation marks this class as a Spring-managed bean for business logic.
//    - Instruction: Add `@Service` above the class declaration.

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorID, LocalDate date){
        Optional<Doctor> doctor = doctorRepository.findById(doctorID);
        if(doctor.isEmpty()){
            return List.of("Doctor not found with ID " + doctorID);
        }

        List<String> availableSlots = doctor.get().getAvailableTimes().stream()
                .map(Objects::toString)
                .toList();

        LocalDateTime startOfDate = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorID, startOfDate, endOfDay);

        Set<String> bookedSlots = appointments.stream()
                .map(appt -> {
                    LocalDateTime start = appt.getAppointmentTime();
                    LocalDateTime end = start.plusHours(1);
                    return formatSlot(start, end);
                }).collect(Collectors.toSet());

        return availableSlots.stream()
                .filter(slot->!bookedSlots.contains(slot))
                .toList();
    }

    // 2. **Constructor Injection for Dependencies**:
//    - The `DoctorService` class depends on `DoctorRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies should be injected via the constructor for proper dependency management.
//    - Instruction: Ensure constructor injection is used for injecting dependencies into the service.

// 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
//    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
//    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
//    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.

// 4. **getDoctorAvailability Method**:
//    - Retrieves the available time slots for a specific doctor on a particular date and filters out already booked slots.
//    - The method fetches all appointments for the doctor on the given date and calculates the availability by comparing against booked slots.
//    - Instruction: Ensure that the time slots are properly formatted and the available slots are correctly filtered.

// 5. **saveDoctor Method**:
//    - Used to save a new doctor record in the database after checking if a doctor with the same email already exists.
//    - If a doctor with the same email is found, it returns `-1` to indicate conflict; `1` for success, and `0` for internal errors.
//    - Instruction: Ensure that the method correctly handles conflicts and exceptions when saving a doctor.
// 1 = success 0 = internal error -1 = already exists
    public int saveDoctor(Doctor doctor){
        Doctor result = doctorRepository.findByEmail(doctor.getEmail());
        if(result != null){
            return -1;
        }
        try{
            doctorRepository.save(doctor);
            return 1;
        } catch(Exception e){
            System.out.println("Error saving doctor " + e.getMessage());
            return 0;
        }
    }
// 6. **updateDoctor Method**:
//    - Updates an existing doctor's details in the database. If the doctor doesn't exist, it returns `-1`.
//    - Instruction: Make sure that the doctor exists before attempting to save the updated record and handle any errors properly.
// 1 = success, 0 = internal errors, -1 = doctor not found
    public int updateDoctor(Doctor doctor){
        Doctor currentDoctor = doctorRepository.findByEmail(doctor.getEmail());
        if (currentDoctor == null){
            System.out.println("Error doctor not found");
            return -1;
        }

        try{
            doctorRepository.save(doctor);
            return 1;
        }catch (Exception e){
            System.out.println("Error saving doctor");
            return 0;
        }
    }

// 7. **getDoctors Method**:
//    - Fetches all doctors from the database. It is marked with `@Transactional` to ensure that the collection is properly loaded.
//    - Instruction: Ensure that the collection is eagerly loaded, especially if dealing with lazy-loaded relationships (e.g., available times). 
    @Transactional
    public List<Doctor> getDoctors(){
        return doctorRepository.findAll();
    }

// 8. **deleteDoctor Method**:
//    - Deletes a doctor from the system along with all appointments associated with that doctor.
//    - It first checks if the doctor exists. If not, it returns `-1`; otherwise, it deletes the doctor and their appointments.
//    - Instruction: Ensure the doctor and their appointments are deleted properly, with error handling for internal issues.
//   1 = success, 0 = internal error, -1 = not found
    public int deleteDoctor(long id){
        Optional<Doctor> currentDoctor = doctorRepository.findById(id);
        if (currentDoctor.isEmpty()){
            System.out.println("Error doctor not found");
            return -1;
        }
        try{
            doctorRepository.delete(currentDoctor.get());
            System.out.println("deleted doctor");
            return 1;
        }catch (Exception e){
            System.out.println("Error deleting doctor");
            return 0;
        }
    }
// 9. **validateDoctor Method**:
//    - Validates a doctor's login by checking if the email and password match an existing doctor record.
//    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
//    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.
    public ResponseEntity<Map<String, String>> validateDoctor(Login login){
        Map<String, String> result = new HashMap<>();
        try{
            Doctor doctor = doctorRepository.findByEmail(login.getEmail());
            if(doctor == null ) {
                result.put("error", "cannot find email");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            if(!Objects.equals(doctor.getPassword(), login.getPassword())){
                result.put("error", "passwords don't match");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }
            result.put("token", tokenService.generateToken(doctor.getEmail()));
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }catch (Exception e){
            result.put("error", "Internal Server Error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
// 10. **findDoctorByName Method**:
//    - Finds doctors based on partial name matching and returns the list of doctors with their available times.
//    - This method is annotated with `@Transactional` to ensure that the database query and data retrieval are properly managed within a transaction.
//    - Instruction: Ensure that available times are eagerly loaded for the doctors.
@Transactional
public Map<String, Object> findDoctorByName(String name){
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctorRepository.findByNameLike(name));
        return  result;
}

// 11. **filterDoctorsByNameSpecilityandTime Method**:
//    - Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
//    - The method fetches doctors matching the name and specialty criteria, then filters them based on their availability during the specified time period.
//    - Instruction: Ensure proper filtering based on both the name and specialty as well as the specified time period.

    public Map<String, Object> filterDoctorsByNameSpecialtyandTime(String name, String specialty, String amorPm){
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        result.put("doctors", filterDoctorsByAmorPm(doctors, amorPm));
        return result;

    }


    //NAME CONFUSING SO ITS NOW CALLED FILTER BY AM OR PM
// 12. **filterDoctorByTime Method**:
//    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
//    - This method processes a list of doctors and their available times to return those that fit the time criteria.
//    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.
//    public Map<String, Object> filterDoctorByTime(String amOrPm){
//        Map<String, Object> result = new HashMap<>();
//        List<Doctor> doctors = doctorRepository.findAll();
//        result.put("doctors", filterDoctorsByAmorPm(doctors, amOrPm));
//        return result;
//    }
//

// 13. **filterDoctorByNameAndTime Method**:
//    - Filters doctors based on their name and the specified time period (AM/PM).
//    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
//    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).

    public Map<String, Object> filterDoctorByNameAndTime(
            String name,
            String amOrPm
    ){
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        result.put("doctors", filterDoctorsByAmorPm(doctors, amOrPm));
        return result;
    }
// 14. **filterDoctorByNameAndSpecility Method**:
//    - Filters doctors by name and specialty.
//    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
//    - Instruction: Ensure that both name and specialty are considered when filtering doctors.
    public Map<String, Object> filterDoctorByNameAndSpecility(
            String name,
            String specialty
    ){
        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        result.put("doctors", doctors);
        return result;
    }

// 15. **filterDoctorByTimeAndSpecility Method**:
//    - Filters doctors based on their specialty and availability during a specific time period (AM/PM).
//    - Fetches doctors based on the specified specialty and filters them based on their available time slots for AM/PM.
//    - Instruction: Ensure the time filtering is accurately applied based on the given specialty and time period (AM/PM).
public Map<String, Object> filterDoctorByTimeAndSpecility(
        String specialty,
        String amOrPm
){
    Map<String, Object> result = new HashMap<>();
    List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
    result.put("doctors", filterDoctorsByAmorPm(doctors, amOrPm));
    return result;
}
// 16. **filterDoctorBySpecility Method**:
//    - Filters doctors based on their specialty.
//    - This method fetches all doctors matching the specified specialty and returns them.
//    - Instruction: Make sure the filtering logic works for case-insensitive specialty matching.
public Map<String, Object> filterDoctorBySpecility(
        String specialty
){
    Map<String, Object> result = new HashMap<>();
    result.put("doctors", doctorRepository.findBySpecialtyIgnoreCase(specialty));
    return result;
}
// 17. **filterDoctorsByTime Method**:
//    - Filters all doctors based on their availability during a specific time period (AM/PM).
//    - The method checks all doctors' available times and returns those available during the specified time period.
//    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.
public Map<String, Object> filterDoctorByTime(
        String amOrPm
){
    Map<String, Object> result = new HashMap<>();
    result.put("doctors", filterDoctorsByAmorPm(doctorRepository.findAll(), amOrPm));
    return result;
}



   private String formatSlot(LocalDateTime start, LocalDateTime end){
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
       return start.format(formatter) + "-" + end.format(formatter);
   }

   private List<Doctor>filterDoctorsByAmorPm(List<Doctor> doctors, String amOrPm) {
        return doctors.stream()
                .filter(doctor -> {
                    if (amOrPm == null || amOrPm.isBlank()){
                        return true;
                    }
                    return doctor.getAvailableTimes().stream().anyMatch(slot ->{
                        try{
                            String hour = slot.substring(0,2);
                            int hourInt = Integer.parseInt(hour);
                            return amOrPm.equalsIgnoreCase("am") == (hourInt < 12);
                        }catch (Exception e){
                            return false;
                        }
                    });
                }).toList();
   }
}
