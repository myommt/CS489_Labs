package lab2a;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Employee {
    private long employeeId;
    private String firstName;
    private String lastName;
    private LocalDate employmentDate;
    private double yearlySalary;
    private PensionPlan pensionPlan; // nullable

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Employee(long employeeId, String firstName, String lastName, String employmentDate, double yearlySalary) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employmentDate = LocalDate.parse(employmentDate, F);
        this.yearlySalary = yearlySalary;
    }

    public long getEmployeeId() { return employeeId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getEmploymentDate() { return employmentDate; }
    public double getYearlySalary() { return yearlySalary; }

    public void setPensionPlan(PensionPlan plan) { this.pensionPlan = plan; }
    public Optional<PensionPlan> getPensionPlan() { return Optional.ofNullable(pensionPlan); }

    // Has been enrolled to a pension plan
    public boolean isEnrolled() { return pensionPlan != null; }

    // Eligible date is employmentDate plus 3 years
    public LocalDate getEligibilityDate() {
        return employmentDate.plusYears(3);
    }

    @Override
    public String toString() {
        return String.format("%d %s %s %.2f %s",
                employeeId, firstName, lastName, yearlySalary, employmentDate.format(F));
    }
}
