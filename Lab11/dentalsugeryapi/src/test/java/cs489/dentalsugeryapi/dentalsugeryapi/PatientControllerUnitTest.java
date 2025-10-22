package cs489.dentalsugeryapi.dentalsugeryapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import cs489.dentalsugeryapi.dentalsugeryapi.controller.PatientController;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.AddressResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.PatientResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.service.PatientService;

public class PatientControllerUnitTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllPatients_returnsListOfPatientResponseDTO() {
        // Arrange
        var addr = new AddressResponseDTO(1, "123 Main St", "City", "ST", "12345");
        var dto1 = new PatientResponseDTO(1, "Alice", "Smith", "1112223333", "a@example.com", LocalDate.of(1990,1,1), addr);
        var dto2 = new PatientResponseDTO(2, "Bob", "Jones", "4445556666", "b@example.com", LocalDate.of(1985,5,5), null);
        var list = List.of(dto1, dto2);

        when(patientService.getAllPatients()).thenReturn(list);

        // Act
        ResponseEntity<List<PatientResponseDTO>> response = patientController.getAllPatients();

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2).containsExactlyElementsOf(list);
    }

}
