package cs489.dentalsugeryapp.service;

import cs489.dentalsugeryapp.model.Appointment;
import java.util.List;

public interface AppointmentService {
    
    Appointment addNewAppointment(Appointment appointment);
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Integer id);
    Appointment updateAppointment(Appointment appointment);
    void deleteAppointmentById(Integer id);
    
}