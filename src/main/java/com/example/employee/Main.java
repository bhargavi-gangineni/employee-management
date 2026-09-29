package com.example.employee;

public class Main {

    public static void main(String[] args) {

        EmployeeManager manager = new EmployeeManager();

        Employee employee1 = new Employee(
                101,
                "Raviteja",
                "raviteja@example.com",
                "IT",
                50000
        );

        Employee employee2 = new Employee(
                102,
                "Rahul",
                "rahul@example.com",
                "HR",
                45000
        );

        Employee employee3 = new Employee(
                103,
                "Priya",
                "priya@example.com",
                "Finance",
                55000
        );

        manager.addEmployee(employee1);
        manager.addEmployee(employee2);
        manager.addEmployee(employee3);

        System.out.println("===== ALL EMPLOYEES =====");
        manager.displayAllEmployees();

        System.out.println("===== SEARCH EMPLOYEE =====");

        Employee foundEmployee = manager.findEmployeeById(102);

        if (foundEmployee != null) {
            foundEmployee.displayEmployee();
        } else {
            System.out.println("Employee not found.");
        }

        System.out.println("===== DELETE EMPLOYEE =====");

        manager.deleteEmployee(102);

        System.out.println("Employees after deletion:");
        manager.displayAllEmployees();
    }
}