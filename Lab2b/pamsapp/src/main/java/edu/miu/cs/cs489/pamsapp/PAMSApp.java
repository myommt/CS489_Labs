package edu.miu.cs.cs489.pamsapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import edu.miu.cs.cs489.pamsapp.model.Patient;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;

public class PAMSApp {
    public static void main(String[] args) throws Exception {
        Patient[] patients = new Patient[5];

        patients[0] = new Patient(1, "Daniel", "Agar", "(641) 123-0009", "dagar@m.as", "1 N Street", LocalDate.of(1987,1,19));
        patients[1] = new Patient(2, "Ana", "Smith", null, "amsith@te.edu", "", LocalDate.of(1948,12,5));
        patients[2] = new Patient(3, "Marcus", "Garvey", "(123) 292-0018", null, "4 East Ave", LocalDate.of(2001,9,18));
        patients[3] = new Patient(4, "Jeff", "Goldbloom", "(999) 165-1192", "jgold@es.co.za", "", LocalDate.of(1995,2,28));
        patients[4] = new Patient(5, "Mary", "Washington", null, null, "30 W Burlington", LocalDate.of(1932,5,31));

        // sort by age descending (oldest first)
        Arrays.sort(patients, Comparator.comparingInt(Patient::getAge).reversed());

        // configure Jackson
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // To include age in output, create a small wrapper object or write a custom view.
        // Simplest: write an array of objects that include patient's fields plus age.

        // Build output array of DTOs that include age
        PatientOutput[] out = Arrays.stream(patients).map(p ->
                new PatientOutput(p.getId(), p.getFirstName(), p.getLastName(), p.getPhone(), p.getEmail(), p.getAddress(), p.getDateOfBirth(), p.getAge())
        ).toArray(PatientOutput[]::new);

        File outFile = new File("patients.json");
        mapper.writeValue(outFile, out);

        System.out.println("Wrote " + outFile.getAbsolutePath());
    }

    // private DTO for JSON output
    private static class PatientOutput {
        private int id;
        private String firstName;
        private String lastName;
        private String phone;
        private String email;
        private String address;
        private LocalDate dateOfBirth;
        private int age;

        public PatientOutput(int id, String firstName, String lastName, String phone, String email, String address, LocalDate dateOfBirth, int age) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.email = email;
            this.address = address;
            this.dateOfBirth = dateOfBirth;
            this.age = age;
        }

        public int getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getAddress() { return address; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public int getAge() { return age; }
    }
}
