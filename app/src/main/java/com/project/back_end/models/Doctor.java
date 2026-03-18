package com.project.back_end.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Doctor's name cannot be null")
    @Size(min = 3, max = 100, message = "Doctor's name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Doctor's specialty cannot be null")
    @Size(min = 3, max = 50, message = "Doctor's specialty must be between 3 and 50 characters")
    private String specialty;

    @NotNull(message = "Doctor's email cannot be null")
    @jakarta.validation.constraints.Email(message = "Doctor's email must be a valid email address")
    private String email;

    @NotNull(message = "Doctor's password cannot be null")
    @Size(min = 6, message = "Doctor's password must be at least 6 characters long")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "Doctor's phone number cannot be null")
    @Pattern(regexp = "^[0-9]{10}$", message = "Doctor's phone number must be exactly 10 digits")
    private String phone;

// 7. 'availableTimes' field:
//    - Type: private List<String>
//    - Description:
//      - Represents the available times for the doctor in a list of time slots.
//      - Each time slot is represented as a string (e.g., "09:00-10:00", "10:00-11:00").
//      - The @ElementCollection annotation ensures that the list of time slots is stored as a separate collection in the database.

    @ElementCollection
    private List<String> availableTimes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }
}

