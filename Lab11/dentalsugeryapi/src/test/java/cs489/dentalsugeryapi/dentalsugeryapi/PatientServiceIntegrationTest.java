package cs489.dentalsugeryapi.dentalsugeryapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import cs489.dentalsugeryapi.dentalsugeryapi.model.Patient;
import cs489.dentalsugeryapi.dentalsugeryapi.service.PatientService;
import cs489.dentalsugeryapi.dentalsugeryapi.exception.PatientNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
public class PatientServiceIntegrationTest {

    @Autowired
    private PatientService patientService;

    @Test
    void whenPatientExists_getPatientById_returnsPatient() {
        // Arrange - create a patient via service
        Patient p = new Patient();
        p.setFirstName("Test");
        p.setLastName("Patient");
        p.setEmail("test.patient@example.com");
        p.setContactNumber("1234567890");
        p.setDob(LocalDate.of(1990, 1, 1));

        Patient saved = patientService.addNewPatient(p);

        // Act
        Patient fetched = null;
        try {
            fetched = patientService.getPatientById(saved.getPatientId());
        } catch (PatientNotFoundException e) {
            // fail test if exception thrown
            throw new AssertionError("Patient should have been found but was not", e);
        }

        // Assert
        assertThat(fetched).isNotNull();
        assertThat(fetched.getPatientId()).isEqualTo(saved.getPatientId());
        assertThat(fetched.getEmail()).isEqualTo(saved.getEmail());
    }

    @Test
    void whenPatientIdInvalid_getPatientById_throwsPatientNotFoundException() {
        int invalidId = 99999;
        assertThatThrownBy(() -> patientService.getPatientById(invalidId))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining("Patient with ID");
    }

}
