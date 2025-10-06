package edu.miu.cs.cs489.lab5.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalCost;
    private String paymentStatus; // e.g., PAID, UNPAID

    @ManyToOne
    private Patient patient;

    @OneToOne
    private Appointment appointment;

    public Bill() {}

    public Bill(BigDecimal totalCost, String paymentStatus, Patient patient, Appointment appointment) {
        this.totalCost = totalCost;
        this.paymentStatus = paymentStatus;
        this.patient = patient;
        this.appointment = appointment;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return Objects.equals(id, bill.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
