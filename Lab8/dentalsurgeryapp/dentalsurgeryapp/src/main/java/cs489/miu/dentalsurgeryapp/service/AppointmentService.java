package cs489.miu.dentalsurgeryapp.service;

import cs489.miu.dentalsurgeryapp.model.Appointment;
import cs489.miu.dentalsurgeryapp.exception.AppointmentLimitExceededException;
import cs489.miu.dentalsurgeryapp.exception.OutstandingBillException;
import java.util.List;

public interface AppointmentService {
    
    Appointment addNewAppointment(Appointment appointment) throws AppointmentLimitExceededException, OutstandingBillException;
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Integer id);
    Appointment updateAppointment(Appointment appointment) throws AppointmentLimitExceededException, OutstandingBillException;
    boolean deleteAppointmentById(Integer id);
    Appointment findOrCreateAppointment(Appointment appointment) throws AppointmentLimitExceededException, OutstandingBillException;
    
}
