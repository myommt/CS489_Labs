package com.example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Employee and Department Management Console Application
 * Demonstrates many-to-one relationship between Employee and Department entities
 */
public class App {
    
    private static DepartmentDAO departmentDAO = new DepartmentDAO();
    private static EmployeeDAO employeeDAO = new EmployeeDAO();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=== Employee and Department Management System ===");
        System.out.println("Demonstrates JPA/Hibernate with Many-to-One relationship");
        System.out.println();
        
        try {
            // Initialize with some sample data
            initializeSampleData();
            
            // Main menu loop
            boolean running = true;
            while (running) {
                displayMenu();
                int choice = getChoice();
                
                switch (choice) {
                    case 1:
                        addDepartment();
                        break;
                    case 2:
                        addEmployee();
                        break;
                    case 3:
                        viewAllDepartments();
                        break;
                    case 4:
                        viewAllEmployees();
                        break;
                    case 5:
                        viewEmployeesByDepartment();
                        break;
                    case 6:
                        searchEmployeeByEmail();
                        break;
                    case 0:
                        running = false;
                        System.out.println("Thank you for using the Employee Management System!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
                
                if (running) {
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                }
            }
        } finally {
            // Clean up resources
            departmentDAO.close();
            employeeDAO.close();
            scanner.close();
        }
    }
    
    private static void initializeSampleData() {
        System.out.println("Checking for existing data...");
        
        // Check if data already exists
        List<Department> existingDepartments = departmentDAO.findAll();
        List<Employee> existingEmployees = employeeDAO.findAll();
        
        if (!existingDepartments.isEmpty() || !existingEmployees.isEmpty()) {
            System.out.println("Sample data already exists. Skipping initialization.");
            System.out.println("Found " + existingDepartments.size() + " departments and " + existingEmployees.size() + " employees.");
            System.out.println();
            return;
        }
        
        System.out.println("No existing data found. Creating sample data...");
        
        // Create departments
        Department itDept = new Department("Information Technology", "Building A");
        Department hrDept = new Department("Human Resources", "Building B");
        Department financeDept = new Department("Finance", "Building C");
        
        departmentDAO.save(itDept);
        departmentDAO.save(hrDept);
        departmentDAO.save(financeDept);
        
        // Create employees
        Employee emp1 = new Employee("John", "Doe", "john.doe@company.com", 
                                   new BigDecimal("75000"), itDept);
        Employee emp2 = new Employee("Jane", "Smith", "jane.smith@company.com", 
                                   new BigDecimal("82000"), itDept);
        Employee emp3 = new Employee("Mike", "Johnson", "mike.johnson@company.com", 
                                   new BigDecimal("65000"), hrDept);
        Employee emp4 = new Employee("Sarah", "Wilson", "sarah.wilson@company.com", 
                                   new BigDecimal("78000"), financeDept);
        
        employeeDAO.save(emp1);
        employeeDAO.save(emp2);
        employeeDAO.save(emp3);
        employeeDAO.save(emp4);
        
        System.out.println("Sample data initialized successfully!");
        System.out.println();
    }
    
    private static void displayMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Add Department");
        System.out.println("2. Add Employee");
        System.out.println("3. View All Departments");
        System.out.println("4. View All Employees");
        System.out.println("5. View Employees by Department");
        System.out.println("6. Search Employee by Email");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private static int getChoice() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void addDepartment() {
        System.out.println("\n=== Add New Department ===");
        System.out.print("Department Name: ");
        String name = scanner.nextLine().trim();
        
        if (name.isEmpty()) {
            System.out.println("Department name cannot be empty!");
            return;
        }
        
        System.out.print("Location: ");
        String location = scanner.nextLine().trim();
        
        try {
            Department department = new Department(name, location);
            departmentDAO.save(department);
            System.out.println("Department added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding department: " + e.getMessage());
        }
    }
    
    private static void addEmployee() {
        System.out.println("\n=== Add New Employee ===");
        
        // First, show available departments
        List<Department> departments = departmentDAO.findAll();
        if (departments.isEmpty()) {
            System.out.println("No departments available. Please add a department first.");
            return;
        }
        
        System.out.println("Available Departments:");
        for (int i = 0; i < departments.size(); i++) {
            System.out.println((i + 1) + ". " + departments.get(i).getName() + 
                             " (" + departments.get(i).getLocation() + ")");
        }
        
        System.out.print("Select department (1-" + departments.size() + "): ");
        int deptChoice;
        try {
            deptChoice = Integer.parseInt(scanner.nextLine().trim());
            if (deptChoice < 1 || deptChoice > departments.size()) {
                System.out.println("Invalid department selection!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
            return;
        }
        
        Department selectedDepartment = departments.get(deptChoice - 1);
        
        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();
        
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();
        
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("Salary: ");
        String salaryStr = scanner.nextLine().trim();
        
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            System.out.println("Name and email are required!");
            return;
        }
        
        try {
            BigDecimal salary = new BigDecimal(salaryStr);
            Employee employee = new Employee(firstName, lastName, email, salary, selectedDepartment);
            employeeDAO.save(employee);
            System.out.println("Employee added successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid salary format!");
        } catch (Exception e) {
            System.out.println("Error adding employee: " + e.getMessage());
        }
    }
    
    private static void viewAllDepartments() {
        System.out.println("\n=== All Departments ===");
        List<Department> departments = departmentDAO.findAll();
        
        if (departments.isEmpty()) {
            System.out.println("No departments found.");
            return;
        }
        
        for (Department dept : departments) {
            System.out.println(dept);
            // Show employee count for each department
            List<Employee> employees = employeeDAO.findByDepartment(dept.getId());
            System.out.println("  -> Employees: " + employees.size());
        }
    }
    
    private static void viewAllEmployees() {
        System.out.println("\n=== All Employees ===");
        List<Employee> employees = employeeDAO.findAll();
        
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        
        for (Employee emp : employees) {
            System.out.println(emp);
        }
    }
    
    private static void viewEmployeesByDepartment() {
        System.out.println("\n=== Employees by Department ===");
        
        List<Department> departments = departmentDAO.findAll();
        if (departments.isEmpty()) {
            System.out.println("No departments available.");
            return;
        }
        
        System.out.println("Available Departments:");
        for (int i = 0; i < departments.size(); i++) {
            System.out.println((i + 1) + ". " + departments.get(i).getName());
        }
        
        System.out.print("Select department (1-" + departments.size() + "): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > departments.size()) {
                System.out.println("Invalid department selection!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!");
            return;
        }
        
        Department selectedDept = departments.get(choice - 1);
        List<Employee> employees = employeeDAO.findByDepartment(selectedDept.getId());
        
        System.out.println("\nEmployees in " + selectedDept.getName() + ":");
        if (employees.isEmpty()) {
            System.out.println("No employees found in this department.");
        } else {
            for (Employee emp : employees) {
                System.out.println("  " + emp);
            }
        }
    }
    
    private static void searchEmployeeByEmail() {
        System.out.println("\n=== Search Employee by Email ===");
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        
        if (email.isEmpty()) {
            System.out.println("Email cannot be empty!");
            return;
        }
        
        Employee employee = employeeDAO.findByEmail(email);
        if (employee != null) {
            System.out.println("Employee found:");
            System.out.println(employee);
        } else {
            System.out.println("No employee found with email: " + email);
        }
    }
}
