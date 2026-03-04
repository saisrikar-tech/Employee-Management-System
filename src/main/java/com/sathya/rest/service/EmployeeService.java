package com.sathya.rest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sathya.rest.exception.EmployeeNotFoundException;
import com.sathya.rest.model.Employee;
import com.sathya.rest.repository.EmployeeRepository;

@Service
public class EmployeeService {
	@Autowired
	EmployeeRepository employeeRepository;

	public Employee saveEmployee(Employee employee) {

	    // 1️ Validation
	    if (employee.getSalary() < 0) {
	        throw new RuntimeException("Salary cannot be negative");
	    }

	    // 2️ Get original salary
	    double salary = employee.getSalary();

	    // 3️ Calculate components (temporary variables only)
	    double hra = salary * 0.30;
	    double da  = salary * 0.05;
	    double pf  = salary * 0.40;

	    // 4️ Update salary
	    double updatedSalary = salary + hra + da - pf;

	    employee.setSalary(updatedSalary);

	    // 5️ Save to DB
	    return employeeRepository.save(employee);
	}

	public List<Employee> saveAllEmployee(List<Employee> employees) {

	    for (Employee employee : employees) {

	        // 1️  Validation
	        if (employee.getSalary() < 0) {
	            throw new RuntimeException("Salary cannot be negative for employee: " 
	                                        + employee.getName());
	        }

	        // 2️ Get salary
	        double salary = employee.getSalary();

	        // 3️ Calculate values (temporary)
	        double hra = salary * 0.30;
	        double da  = salary * 0.05;
	        double pf  = salary * 0.40;

	        // 4️ Update salary
	        double updatedSalary = salary + hra + da - pf;
	        employee.setSalary(updatedSalary);
	    }

	    // 5️ Save all employees
	    return employeeRepository.saveAll(employees);
	}

	public Page<Employee> getEmployees(int page, int size, String sortBy, String direction) {

	    Sort sort = direction.equalsIgnoreCase("asc") ?
	            Sort.by(sortBy).ascending() :
	            Sort.by(sortBy).descending();

	    Pageable pageable = PageRequest.of(page, size, sort);

	    return employeeRepository.findAll(pageable);
	}

	public Employee getEmployeeByID(Long id) {
		Optional<Employee> optionalEmployee = employeeRepository.findById(id);
		if(optionalEmployee.isPresent()) {
			Employee employee = optionalEmployee.get();
			return employee;
		}
		else {
			throw new EmployeeNotFoundException("Employee Not Found by the Id: "+id); 
		}
		
	}

	public void deleteAllEmployees() {
		
		employeeRepository.deleteAll();
	}

	public void deleteEmployeeById(Long id) {
		if(employeeRepository.existsById(id)) {
			employeeRepository.deleteById(id);
		}
		else {
			throw new EmployeeNotFoundException("Employee not found with Id:"+id);
		}
		
	}

	  // ✅ UPDATE (PUT - Full Update)
    public Employee updateEmployeeById(Long id, Employee employee) {
    	if(employeeRepository.existsById(id)) {
    		
    	}
        Employee existing = employeeRepository.findById(id)
        			.orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with id : " + id));

        existing.setName(employee.getName());
        existing.setGender(employee.getGender());
        existing.setSalary(employee.getSalary());
        existing.setEmail(employee.getEmail());
        existing.setAddress(employee.getAddress());

        return employeeRepository.save(existing);
    }

    // PUT - Update Multiple Employees
    public List<Employee> updateEmployees(List<Employee> employees) {

        List<Employee> updatedList = new ArrayList<>();

        for (Employee employee : employees) {

            Employee existingEmployee = employeeRepository.findById(employee.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Employee not found with id " + employee.getId()));

            existingEmployee.setName(employee.getName());
            existingEmployee.setGender(employee.getGender());
            existingEmployee.setSalary(employee.getSalary());
            existingEmployee.setEmail(employee.getEmail());
            existingEmployee.setAddress(employee.getAddress());

            updatedList.add(employeeRepository.save(existingEmployee));
        }

        return updatedList;
    }

    public Employee patchEmployee(Long id, Map<String, Object> updates) {

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        // Loop through fields sent in request body
        updates.forEach((key, value) -> {

            switch (key) {
                case "name":
                    existing.setName((String) value);
                    break;

                case "gender":
                    existing.setGender((String) value);
                    break;

                case "salary":
                    existing.setSalary((Double)value);
                    break;

                case "email":
                    existing.setEmail((String) value);
                    break;

                case "address":
                    existing.setAddress((String) value);
                    break;

                default:
                    throw new RuntimeException("Invalid field: " + key);
            }
        });

        return employeeRepository.save(existing);
    }

    public List<Employee> patchEmployees(List<Map<String, Object>> updatesList) {

        List<Employee> updatedEmployees = new ArrayList<>();

        for (Map<String, Object> updates : updatesList) {

            // 1️ Get ID from body
            Long id = Long.parseLong(updates.get("id").toString());

            Employee existing = employeeRepository.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found : " + id));

            // 2️ Apply updates using switch case
            for (Map.Entry<String, Object> entry : updates.entrySet()) {

                String key = entry.getKey();
                Object value = entry.getValue();

                if ("id".equalsIgnoreCase(key)) continue;

                switch (key) {

                    case "name":
                        existing.setName((String) value);
                        break;

                    case "gender":
                        existing.setGender((String) value);
                        break;

                    case "salary":
                        existing.setSalary(Double.parseDouble(value.toString()));
                        break;

                    case "email":
                        existing.setEmail((String) value);
                        break;

                    case "address":
                        existing.setAddress((String) value);
                        break;

                    default:
                        throw new RuntimeException("Invalid field : " + key);
                }
            }

            updatedEmployees.add(existing);
        }

        // 3 Save all in DB
        return employeeRepository.saveAll(updatedEmployees);
    }

	public List<Employee> filterEmployeesBySalaryMin(double minSalary) {
		return employeeRepository.findBySalaryLessThan(minSalary);
	}
	public List<Employee> filterEmployeesBySalaryMax(double maxSalary) {
		return employeeRepository.findBySalaryGreaterThan(maxSalary);
	}

	
}
