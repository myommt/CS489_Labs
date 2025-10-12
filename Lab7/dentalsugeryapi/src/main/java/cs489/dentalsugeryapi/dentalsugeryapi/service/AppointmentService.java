package cs489.dentalsugeryapi.dentalsugeryapi.service;

import cs489.dentalsugeryapi.dentalsugeryapi.model.Appointment;
import cs489.dentalsugeryapi.dentalsugeryapi.exception.AppointmentLimitExceededException;
import java.util.List;

public interface AppointmentService {
    
    Appointment addNewAppointment(Appointment appointment) throws AppointmentLimitExceededException;
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Integer id);
    Appointment updateAppointment(Appointment appointment) throws AppointmentLimitExceededException;
    boolean deleteAppointmentById(Integer id);
    Appointment findOrCreateAppointment(Appointment appointment) throws AppointmentLimitExceededException;
    
}