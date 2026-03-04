package com.sathya.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sathya.rest.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	public List<Employee> findBySalaryGreaterThan(double minSalary);
	public List<Employee> findBySalaryLessThan(double maxSalary);
}
