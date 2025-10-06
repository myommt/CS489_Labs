package edu.miu.cs.cs489.lab5.controller;

import edu.miu.cs.cs489.lab5.model.Appointment;
import edu.miu.cs.cs489.lab5.repository.AppointmentRepository;
import edu.miu.cs.cs489.lab5.dto.AppointmentDTO;
import edu.miu.cs.cs489.lab5.dto.PatientDTO;
import edu.miu.cs.cs489.lab5.dto.DentistDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;

    public AppointmentController(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        List<Appointment> list = appointmentRepository.findAll();
        return list.stream().map(a -> {
            AppointmentDTO dto = new AppointmentDTO();
            dto.setId(a.getId());

            if (a.getPatient() != null) {
                PatientDTO p = new PatientDTO();
                p.setId(a.getPatient().getId());
                p.setFirstName(a.getPatient().getFirstName());
                p.setLastName(a.getPatient().getLastName());
                p.setEmail(a.getPatient().getEmail());
                dto.setPatient(p);
            }

            if (a.getDentist() != null) {
                DentistDTO d = new DentistDTO();
                d.setId(a.getDentist().getId());
                d.setFirstName(a.getDentist().getFirstName());
                d.setLastName(a.getDentist().getLastName());
                d.setSpecialization(a.getDentist().getSpecialization());
                dto.setDentist(d);
            }

            if (a.getSurgery() != null) {
                AppointmentDTO.SurgeryInfo s = new AppointmentDTO.SurgeryInfo();
                s.setId(a.getSurgery().getId());
                s.setName(a.getSurgery().getName());
                dto.setSurgery(s);
            }

            dto.setAppointmentDateTime(a.getAppointmentDateTime());
            dto.setPaid(a.isPaid());
            return dto;
        }).toList();
    }
}
