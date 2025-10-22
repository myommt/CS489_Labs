package cs489.dentalsugeryapi.dentalsugeryapi.dto;

public record OutstandingBillCheckDTO(
    Integer patientId,
    boolean hasOutstandingBills
) {
}