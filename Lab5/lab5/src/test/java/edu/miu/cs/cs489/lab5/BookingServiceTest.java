package edu.miu.cs.cs489.lab5;

import edu.miu.cs.cs489.lab5.model.Appointment;
import edu.miu.cs.cs489.lab5.model.Dentist;
import edu.miu.cs.cs489.lab5.model.Patient;
import edu.miu.cs.cs489.lab5.repository.AppointmentRepository;
import edu.miu.cs.cs489.lab5.repository.DentistRepository;
import edu.miu.cs.cs489.lab5.repository.PatientRepository;
import edu.miu.cs.cs489.lab5.service.AppointmentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookingServiceTest {

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DentistRepository dentistRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Test
    public void patientWithOutstandingBillCannotBook() {
    // pass null for Address; test only needs a patient record
    Patient tmp = new Patient("Jane","Doe","555-1111","jane@example.com", null, LocalDateTime.now().toLocalDate());
    tmp.setHasOutstandingBill(true);
    final Patient p = patientRepository.save(tmp);

    final Dentist d = dentistRepository.save(new Dentist("Doc","Who","555-2222","doc@example.com","General"));

    assertThatThrownBy(() -> appointmentService.bookAppointment(p.getId(), d.getId(), null, LocalDateTime.now().plusDays(1)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("outstanding bill");
    }

    @Test
    public void dentistCannotHaveMoreThanFiveAppointmentsPerWeek() {
    final Patient p = patientRepository.save(new Patient("John","Smith","555-3333","john@example.com", null, LocalDateTime.now().toLocalDate()));
    final Dentist d = dentistRepository.save(new Dentist("Doc","Holiday","555-4444","holiday@example.com","General"));

        LocalDateTime monday = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(9).withMinute(0).withSecond(0).withNano(0);

        // create 5 appointments for same week
        for (int i = 0; i < 5; i++) {
            Appointment a = new Appointment(p, d, null, monday.plusDays(i));
            appointmentRepository.save(a);
        }

        // 6th booking should fail
        assertThatThrownBy(() -> appointmentService.bookAppointment(p.getId(), d.getId(), null, monday.plusDays(6)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Dentist already has 5");
    }
}
