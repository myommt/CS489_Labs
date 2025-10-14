package cs489.miu.dentalsurgeryapp.dto;

import cs489.miu.dentalsurgeryapp.model.AppointmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {
    
    private Long appointmentId;
    
    @NotBlank(message = "Appointment type is required")
    private String appointmentType;
    
    private AppointmentStatus status;
    
    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;
    
    @NotBlank(message = "Appointment time is required")
    private String appointmentTime;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Dentist ID is required")  
    private Long dentistId;
    
    @NotBlank(message = "Reason for visit is required")
    private String reason;
    
    private String urgency = "MEDIUM";
    
    private String notes;
    
    private LocalDateTime appointmentDateTime;
    
    // For backwards compatibility
    private PatientRequestDTO patientRequestDTO;
    private DentistRequestDTO dentistRequestDTO;
    private SurgeryLocationRequestDTO surgeryLocationRequestDTO;
    
    // Getter for appointment status as string (for compatibility)
    public String getAppointmentStatus() {
        return status != null ? status.name() : null;
    }
    
    // Setter for appointment status from string
    public void setAppointmentStatus(String status) {
        this.status = status != null ? AppointmentStatus.valueOf(status) : null;
    }
}
