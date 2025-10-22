package cs489.dentalsugeryapi.dentalsugeryapi.exception;

public class AppointmentLimitExceededException extends Exception {
    
    public AppointmentLimitExceededException(String message) {
        super(message);
    }
    
    public AppointmentLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}