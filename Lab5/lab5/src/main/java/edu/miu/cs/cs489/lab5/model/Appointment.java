package edu.miu.cs.cs489.lab5.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Dentist dentist;

    @ManyToOne
    private Surgery surgery;

    private LocalDateTime appointmentDateTime;
    private boolean paid = false;

    public Appointment() {}

    public Appointment(Patient patient, Dentist dentist, Surgery surgery, LocalDateTime appointmentDateTime) {
        this.patient = patient;
        this.dentist = dentist;
        this.surgery = surgery;
        this.appointmentDateTime = appointmentDateTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Dentist getDentist() { return dentist; }
    public void setDentist(Dentist dentist) { this.dentist = dentist; }
    public Surgery getSurgery() { return surgery; }
    public void setSurgery(Surgery surgery) { this.surgery = surgery; }
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
