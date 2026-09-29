package com.example.employee;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeManager {

    private final List<Employee> employees = new ArrayList<>();

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public List<Employee> getAllEmployees() {
        return employees.stream()
                .sorted((e1, e2) -> Integer.compare(e1.getId(), e2.getId()))
                .collect(Collectors.toList());
    }

    public Employee findEmployeeById(int id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }

        return null;
    }

    public boolean updateEmployee(
            int id,
            String name,
            String email,
            String department,
            double salary) {

        Employee employee = findEmployeeById(id);

        if (employee == null) {
            return false;
        }

        employee.setName(name);
        employee.setEmail(email);
        employee.setDepartment(department);
        employee.setSalary(salary);

        return true;
    }

    public boolean deleteEmployee(int id) {
        return employees.removeIf(employee -> employee.getId() == id);
    }

    public List<Employee> searchEmployees(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllEmployees();
        }

        String searchText = keyword.trim().toLowerCase();

        return employees.stream()
                .filter(employee ->
                        employee.getName().toLowerCase().contains(searchText)
                        || employee.getEmail().toLowerCase().contains(searchText)
                        || employee.getDepartment().toLowerCase().contains(searchText)
                )
                .collect(Collectors.toList());
    }

    public long getEmployeeCount() {
        return employees.size();
    }

    public void displayAllEmployees() {
        for (Employee employee : employees) {
            employee.displayEmployee();
            System.out.println("--------------------");
        }
    }
}