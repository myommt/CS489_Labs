package cs489.dentalsugeryapi.dentalsugeryapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cs489.dentalsugeryapi.dentalsugeryapi.dto.BillRequestDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.BillResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.DeleteResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.OutstandingBillCheckDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Bill;
import cs489.dentalsugeryapi.dentalsugeryapi.service.BillService;
import cs489.dentalsugeryapi.dentalsugeryapi.service.PatientService;
import cs489.dentalsugeryapi.dentalsugeryapi.service.AppointmentService;

@RestController
@RequestMapping(value = "/dentalsugery/api/bills")
public class BillController {

    private final BillService billService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public BillController(BillService billService, 
                         PatientService patientService,
                         AppointmentService appointmentService) {
        this.billService = billService;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<BillResponseDTO>> getAllBills() {
        List<BillResponseDTO> bills = billService.getAllBills();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/sorted-by-cost")
    public ResponseEntity<List<BillResponseDTO>> getAllBillsSortedByTotalCost() {
        List<BillResponseDTO> bills = billService.getAllBillsSortedByTotalCost();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponseDTO> getBillById(@PathVariable Integer id) {
        Bill bill = billService.getBillById(id);
        if (bill != null) {
            return ResponseEntity.ok(mapToDTO(bill));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByPatientId(@PathVariable Integer patientId) {
        List<BillResponseDTO> bills = billService.getBillsByPatientId(patientId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/status/{paymentStatus}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByPaymentStatus(@PathVariable String paymentStatus) {
        List<BillResponseDTO> bills = billService.getBillsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/patient/{patientId}/outstanding")
    public ResponseEntity<List<BillResponseDTO>> getOutstandingBillsByPatientId(@PathVariable Integer patientId) {
        List<BillResponseDTO> outstandingBills = billService.getOutstandingBillsByPatientId(patientId);
        return ResponseEntity.ok(outstandingBills);
    }

    @GetMapping("/patient/{patientId}/has-outstanding")
    public ResponseEntity<OutstandingBillCheckDTO> hasOutstandingBills(@PathVariable Integer patientId) {
        boolean hasOutstanding = billService.hasOutstandingBills(patientId);
        return ResponseEntity.ok(new OutstandingBillCheckDTO(patientId, hasOutstanding));
    }

    @PostMapping
    public ResponseEntity<BillResponseDTO> addNewBill(@RequestBody BillRequestDTO billRequestDTO) {
        try {
            Bill bill = mapToEntity(billRequestDTO);
            Bill createdBill = billService.addNewBill(bill);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdBill));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillResponseDTO> updateBill(@PathVariable Integer id, @RequestBody BillRequestDTO billRequestDTO) {
        try {
            Bill bill = mapToEntity(billRequestDTO);
            bill.setBillId(id);
            Bill updatedBill = billService.updateBill(bill);
            return ResponseEntity.ok(mapToDTO(updatedBill));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponseDTO> deleteBill(@PathVariable Integer id) {
        boolean deleted = billService.deleteBillById(id);
        if (deleted) {
            return ResponseEntity.ok(new DeleteResponseDTO(true, "Bill deleted successfully"));
        } else {
            return ResponseEntity.ok(new DeleteResponseDTO(false, "Bill not found or could not be deleted"));
        }
    }

    private Bill mapToEntity(BillRequestDTO billRequestDTO) {
        Bill bill = new Bill();
        bill.setTotalCost(billRequestDTO.totalCost());
        bill.setPaymentStatus(billRequestDTO.paymentStatus());
        
        // Set patient
        if (billRequestDTO.patientId() != null) {
            try {
                bill.setPatient(patientService.getPatientById(billRequestDTO.patientId()));
            } catch (Exception e) {
                throw new RuntimeException("Patient not found with ID: " + billRequestDTO.patientId());
            }
        }
        
        // Set appointment
        if (billRequestDTO.appointmentId() != null) {
            bill.setAppointment(appointmentService.getAppointmentById(billRequestDTO.appointmentId()));
        }
        
        return bill;
    }

    private BillResponseDTO mapToDTO(Bill bill) {
        BillResponseDTO.PatientBasicInfoDTO patientDTO = new BillResponseDTO.PatientBasicInfoDTO(
                bill.getPatient().getPatientId(),
                bill.getPatient().getFirstName(),
                bill.getPatient().getLastName(),
                bill.getPatient().getEmail()
        );
        
        BillResponseDTO.AppointmentBasicInfoDTO appointmentDTO = new BillResponseDTO.AppointmentBasicInfoDTO(
                bill.getAppointment().getAppointmentId(),
                bill.getAppointment().getAppointmentType(),
                bill.getAppointment().getAppointmentStatus(),
                bill.getAppointment().getAppointmentDateTime()
        );
        
        return new BillResponseDTO(
                bill.getBillId(),
                bill.getTotalCost(),
                bill.getPaymentStatus(),
                patientDTO,
                appointmentDTO
        );
    }
}