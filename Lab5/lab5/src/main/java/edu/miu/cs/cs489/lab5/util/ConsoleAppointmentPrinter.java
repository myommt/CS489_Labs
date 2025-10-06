package edu.miu.cs.cs489.lab5.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.miu.cs.cs489.lab5.dto.AppointmentDTO;
import edu.miu.cs.cs489.lab5.dto.DentistDTO;
import edu.miu.cs.cs489.lab5.dto.PatientDTO;
import edu.miu.cs.cs489.lab5.model.Appointment;
import edu.miu.cs.cs489.lab5.repository.AppointmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConsoleAppointmentPrinter implements CommandLineRunner {

    private final AppointmentRepository appointmentRepository;
    private final Environment env;

    public ConsoleAppointmentPrinter(AppointmentRepository appointmentRepository, Environment env) {
        this.appointmentRepository = appointmentRepository;
        this.env = env;
    }

    @Override
    public void run(String... args) throws Exception {
        String val = env.getProperty("app.printAppointments");
        if (val == null || !Boolean.parseBoolean(val)) return;

        List<Appointment> list = appointmentRepository.findAll();
        List<AppointmentDTO> dtos = list.stream().map(a -> {
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
        }).collect(Collectors.toList());

        ObjectMapper om = new ObjectMapper();
        String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(dtos);
        System.out.println(json);

        // exit after printing to console (helps when run as a one-off)
        System.exit(0);
    }
}
