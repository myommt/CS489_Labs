package edu.miu.cs.cs489.lab5.controller;

import edu.miu.cs.cs489.lab5.model.Appointment;
import edu.miu.cs.cs489.lab5.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final AppointmentService appointmentService;

    public BookingController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<?> book(@RequestParam Long patientId, @RequestParam Long dentistId, @RequestParam(required = false) Long surgeryId, @RequestParam String isoDateTime) {
        try {
            LocalDateTime when = LocalDateTime.parse(isoDateTime);
            Appointment appt = appointmentService.bookAppointment(patientId, dentistId, surgeryId, when);
            return ResponseEntity.ok(appt);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}
