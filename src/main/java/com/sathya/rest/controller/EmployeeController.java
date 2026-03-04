package com.sathya.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.sathya.rest.model.Employee;
import com.sathya.rest.service.EmployeeService;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("api/v1/employee")
public class EmployeeController {

 
@Autowired
EmployeeService employeeService;

@GetMapping("/{id}")
public ResponseEntity<EntityModel<Employee>>getEmployeeById(@PathVariable Long id){
	Employee employeeByID = employeeService.getEmployeeByID(id);
	
	EntityModel<Employee> entityModelEmployee=EntityModel.of(employeeByID);
	
	entityModelEmployee.add(
			linkTo(methodOn(EmployeeController.class)
		            .getEmployeeById(id)).withSelfRel());
	entityModelEmployee.add(
			linkTo(methodOn(EmployeeController.class)
					.saveEmployee(employeeByID)).withRel("post")
			);
	entityModelEmployee.add(
			linkTo(methodOn(EmployeeController.class)
					.deleteEmployeeById(id)).withRel("delete")
			);
	entityModelEmployee.add(linkTo(methodOn(EmployeeController.class)
			.updateEmployeeById(id, employeeByID)).withRel("put")
					);
	entityModelEmployee.add(linkTo(methodOn(EmployeeController.class)
			.patchEmployee(id,new HashMap<>())).withRel("patch"));
	
//	entityModelEmployee.add(linkTo(methodOn(EmployeeController.class)
//			.getEmployees()).withRel("getAll"));
	
	return ResponseEntity.status(HttpStatus.OK)
			.header("Info", "Employee found......!!!! ")
			.body(entityModelEmployee);	
	
}
@GetMapping
public ResponseEntity<Page<Employee>> getEmployees(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction) {
    Page<Employee> employees =
            employeeService.getEmployees(page, size, sortBy, direction);

    return ResponseEntity.status(HttpStatus.CREATED)
			.header("information", "Employee saved.....!!!")
			.body(employees);
}
@PostMapping("/save")
public ResponseEntity<Employee> saveEmployee(@Valid @RequestBody Employee employee) {
     
	Employee savedEmployee= employeeService.saveEmployee(employee);

	return ResponseEntity.status(HttpStatus.CREATED)
							.header("information", "Employee saved.....!!!")
							.body(savedEmployee);
}
@PostMapping("/saveall")
public ResponseEntity<List<Employee>> saveAllEmployee(@RequestBody List<Employee> employees){
	
	List<Employee>savedEmps= employeeService.saveAllEmployee(employees);
	
	return ResponseEntity.status(HttpStatus.CREATED)
			.header("information", "Employeess "+savedEmps.size()+" saved.....!!!")
			.body(savedEmps);
	
}
@DeleteMapping("/{id}")
public ResponseEntity<Void>deleteEmployeeById(@PathVariable Long id){
	employeeService.deleteEmployeeById(id);
	return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
}
@DeleteMapping("/delete-all")
public ResponseEntity<Void>deleteAllEmployees(){
	employeeService.deleteAllEmployees();
return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	
}
@PutMapping("/{id}")
public ResponseEntity<Employee> updateEmployeeById(
        @PathVariable Long id,
        @RequestBody Employee employee){

    Employee updated = employeeService.updateEmployeeById(id, employee);
    return ResponseEntity.ok(updated);
}
//  PUT - Update Multiple Employees
@PutMapping("/put-all")
public ResponseEntity<List<Employee>> updateEmployees(
        @RequestBody List<Employee> employees) {

    List<Employee> updatedList = employeeService.updateEmployees(employees);
    return ResponseEntity.ok(updatedList);
}
@PatchMapping("/{id}")
public ResponseEntity<Employee> patchEmployee(
        @PathVariable Long id,
        @RequestBody Map<String, Object> updates) {

    Employee updated = employeeService.patchEmployee(id, updates);

    return ResponseEntity.ok()
            .header("info", "Employee partially updated")
            .body(updated);
}
@PatchMapping("/patch-all")
public ResponseEntity<List<Employee>> patchEmployees(
        @RequestBody List<Map<String, Object>> patchEmployees) {
     List<Employee> updatedEmployees = employeeService.patchEmployees(patchEmployees);

    return ResponseEntity.status(HttpStatus.OK)
    		.header("info", "employees updated")
    		.body(updatedEmployees);
}
@GetMapping("/weather-report/{pincode}")
public ResponseEntity<String> getWeatherResport(@PathVariable String pincode) {
	
	RestTemplate template=new RestTemplate();
	return template.getForEntity("https://api.postalpincode.in/pincode/"+pincode, String.class);
	
}
@GetMapping("/filterbysalary-min")
public ResponseEntity<List<Employee>> filterEmployeesBySalaryMin(@RequestParam double minSalary){
	List<Employee> filterEmployeesBySalary = employeeService.filterEmployeesBySalaryMin(minSalary);
	return ResponseEntity.status(HttpStatus.OK)
			.header("Info", "Employees found......!!!! ")
			.body(filterEmployeesBySalary);	
}
@GetMapping("/filterbysalary-max/")
public ResponseEntity<List<Employee>> filterEmployeesBySalaryMax(@RequestParam double maxSalary){
	List<Employee> filterEmployeesBySalary = employeeService.filterEmployeesBySalaryMax(maxSalary);
	return ResponseEntity.status(HttpStatus.OK)
			.header("Info", "Employees found......!!!! ")
			.body(filterEmployeesBySalary);	
}
}
