package edu.miu.cs.cs489.lab5.dto;

import java.time.LocalDateTime;

import edu.miu.cs.cs489.lab5.dto.PatientDTO;
import edu.miu.cs.cs489.lab5.dto.DentistDTO;

public class AppointmentDTO {
    private Long id;
    private PatientDTO patient;
    private DentistDTO dentist;
    private SurgeryInfo surgery;
    private LocalDateTime appointmentDateTime;
    private boolean paid;

    public AppointmentDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PatientDTO getPatient() { return patient; }
    public void setPatient(PatientDTO patient) { this.patient = patient; }
    public DentistDTO getDentist() { return dentist; }
    public void setDentist(DentistDTO dentist) { this.dentist = dentist; }
    public SurgeryInfo getSurgery() { return surgery; }
    public void setSurgery(SurgeryInfo surgery) { this.surgery = surgery; }
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public static class SurgeryInfo {
        private Long id;
        private String name;
        public SurgeryInfo() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
