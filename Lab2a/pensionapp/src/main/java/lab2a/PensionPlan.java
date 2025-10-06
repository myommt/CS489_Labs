package lab2a;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PensionPlan {
    private String planReferenceNumber;
    private LocalDate enrollmentDate; // nullable
    private Double monthlyContribution; // nullable

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PensionPlan(String planReferenceNumber, String enrollmentDate, Double monthlyContribution) {
        this.planReferenceNumber = planReferenceNumber;
        this.enrollmentDate = (enrollmentDate == null || enrollmentDate.isEmpty()) ? null : LocalDate.parse(enrollmentDate, F);
        this.monthlyContribution = monthlyContribution;
    }

    public String getPlanReferenceNumber() { return planReferenceNumber; }
    public Optional<LocalDate> getEnrollmentDate() { return Optional.ofNullable(enrollmentDate); }
    public Optional<Double> getMonthlyContribution() { return Optional.ofNullable(monthlyContribution); }

    public boolean hasEnrollmentDate() { return enrollmentDate != null; }

    @Override
    public String toString() {
        return String.format("%s %s %s",
                planReferenceNumber,
                enrollmentDate == null ? "null" : enrollmentDate.format(F),
                monthlyContribution == null ? "null" : String.format("$%.2f", monthlyContribution));
    }
}
