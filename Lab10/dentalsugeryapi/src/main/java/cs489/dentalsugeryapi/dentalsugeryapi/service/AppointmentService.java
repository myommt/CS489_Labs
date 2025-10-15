package cs489.dentalsugeryapi.dentalsugeryapi.service;

import cs489.dentalsugeryapi.dentalsugeryapi.model.Appointment;
import cs489.dentalsugeryapi.dentalsugeryapi.exception.AppointmentLimitExceededException;
import cs489.dentalsugeryapi.dentalsugeryapi.exception.OutstandingBillException;
import java.util.List;

public interface AppointmentService {
    
    Appointment addNewAppointment(Appointment appointment) throws AppointmentLimitExceededException, OutstandingBillException;
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Integer id);
    Appointment updateAppointment(Appointment appointment) throws AppointmentLimitExceededException, OutstandingBillException;
    boolean deleteAppointmentById(Integer id);
    Appointment findOrCreateAppointment(Appointment appointment) throws AppointmentLimitExceededException, OutstandingBillException;
    
}