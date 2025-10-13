package cs489.dentalsugeryapi.dentalsugeryapi.exception;

public class OutstandingBillException extends Exception {
    
    public OutstandingBillException(String message) {
        super(message);
    }
    
    public OutstandingBillException(String message, Throwable cause) {
        super(message, cause);
    }
}