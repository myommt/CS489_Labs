package lab2a;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Employee> employees = loadData();

        // Determine next quarter relative to today
        LocalDate today = LocalDate.now();
        Quarter next = Quarter.nextQuarter(today);

        System.out.println("Quarterly Upcoming Enrollees report for next quarter: " + next);
        System.out.println("Quarter start: " + next.getStart() + " end: " + next.getEnd());

        List<Employee> upcoming = new ArrayList<>();
        for (Employee e : employees) {
            // eligible date is employmentDate + 3 years
            LocalDate elig = e.getEligibilityDate();
            boolean inNextQuarter = ( !elig.isBefore(next.getStart()) ) && ( !elig.isAfter(next.getEnd()) );
            if (inNextQuarter && !e.isEnrolled()) {
                upcoming.add(e);
            }
        }

        if (upcoming.isEmpty()) {
            System.out.println("No upcoming enrollees found for the next quarter.");
        } else {
            System.out.println("Employees who will reach 3 years in the next quarter and are NOT enrolled:");
            for (Employee e : upcoming) {
                System.out.printf("%s %s (employeeId=%d) employmentDate=%s eligibilityDate=%s yearlySalary=%.2f\n",
                        e.getFirstName(), e.getLastName(), e.getEmployeeId(), e.getEmploymentDate(), e.getEligibilityDate(), e.getYearlySalary());
            }
        }
    }

    // Loads the in-memory dataset provided in the exercise
    private static List<Employee> loadData() {
        List<Employee> list = new ArrayList<>();

        // Data rows based on problem statement. Note: some plan reference numbers are missing (null) per table layout.
        // 1 EX1089 Daniel Agar 105,945.50 2023-01-17 null $100.00
        Employee e1 = new Employee(1, "Daniel", "Agar", "2023-01-17", 105945.50);
        PensionPlan p1 = new PensionPlan("EX1089", null, 100.00);
        // Enrollment date null -> not enrolled
        e1.setPensionPlan(null);
        list.add(e1);

        // 2 Benard Shaw 197,750.00 2022-09-03 2025-09-03 null
        Employee e2 = new Employee(2, "Benard", "Shaw", "2022-09-03", 197750.00);
        PensionPlan p2 = new PensionPlan(null, "2025-09-03", null);
        // has enrollment date -> enrolled
        e2.setPensionPlan(p2);
        list.add(e2);

        // 3 SM2307 Carly Agar 842,000.75 2014-05-16 2017-05-17 $1,555.50
        Employee e3 = new Employee(3, "Carly", "Agar", "2014-05-16", 842000.75);
        PensionPlan p3 = new PensionPlan("SM2307", "2017-05-17", 1555.50);
        e3.setPensionPlan(p3);
        list.add(e3);

        // 4 Wesley Schneider 74,500.00 2023-07-21 (no plan info)
        Employee e4 = new Employee(4, "Wesley", "Schneider", "2023-07-21", 74500.00);
        e4.setPensionPlan(null);
        list.add(e4);

        // 5 Anna Wiltord 85,750.00 2023-03-15
        Employee e5 = new Employee(5, "Anna", "Wiltord", "2023-03-15", 85750.00);
        e5.setPensionPlan(null);
        list.add(e5);

        // 6 Yosef Tesfalem 100,000.00 2024-10-31
        Employee e6 = new Employee(6, "Yosef", "Tesfalem", "2024-10-31", 100000.00);
        e6.setPensionPlan(null);
        list.add(e6);

        return list;
    }

    // Simple helper class to represent a quarter
    private static class Quarter {
        private final LocalDate start;
        private final LocalDate end;

        Quarter(LocalDate start, LocalDate end) { this.start = start; this.end = end; }
        LocalDate getStart() { return start; }
        LocalDate getEnd() { return end; }

        static Quarter nextQuarter(LocalDate from) {
            int month = from.getMonthValue();
            int year = from.getYear();
            int currentQuarter = (month - 1) / 3 + 1;
            int nextQuarter = currentQuarter == 4 ? 1 : currentQuarter + 1;
            int nextYear = currentQuarter == 4 ? year + 1 : year;

            int startMonth = (nextQuarter - 1) * 3 + 1;
            LocalDate start = LocalDate.of(nextYear, startMonth, 1);
            YearMonth ym = YearMonth.of(nextYear, startMonth + 2);
            LocalDate end = LocalDate.of(nextYear, startMonth + 2, ym.lengthOfMonth());
            return new Quarter(start, end);
        }

        @Override
        public String toString() {
            return String.format("%s to %s", start, end);
        }
    }
}