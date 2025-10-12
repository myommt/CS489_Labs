package cs489.dentalsugeryapi.dentalsugeryapi.service;

import cs489.dentalsugeryapi.dentalsugeryapi.model.Appointment;
import java.util.List;

public interface AppointmentService {
    
    Appointment addNewAppointment(Appointment appointment);
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Integer id);
    Appointment updateAppointment(Appointment appointment);
    boolean deleteAppointmentById(Integer id);
    
}