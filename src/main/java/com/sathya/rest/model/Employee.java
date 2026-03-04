package com.sathya.rest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;

@NotBlank(message = "Name cannot be empty")
private String name;

@NotBlank(message = "Gender is required")
private String gender;

@Min(value = 0, message = "Salary must be greater than or equal to 0")
private double salary;

@Email(message = "Invalid email format")
@NotBlank(message = "Email is required")
private String email;

@NotBlank(message = "Address cannot be empty")
private String address;
}
