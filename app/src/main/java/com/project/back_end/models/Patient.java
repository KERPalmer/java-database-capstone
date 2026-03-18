package com.project.back_end.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient's name cannot be null")
    @Size(min = 3, max = 100, message = "Patient's name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Patient's email cannot be null")
    @jakarta.validation.constraints.Email(message = "Patient's email must be a valid email address")
    private String email;

    @NotNull(message = "Patient's password cannot be null")
    @Size(min = 6, message = "Patient's password must be at least 6 characters long")
    private String password;

    @NotNull(message = "Patient's phone number cannot be null")
    @Pattern(regexp = "^[0-9]{10}$", message = "Patient's phone number must be exactly 10 digits")
    private String phone;

    @NotNull(message = "Patient's address cannot be null")
    @Size(max = 255, message = "Patient's address must not exceed 255 characters")
    private String address;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
  

}
