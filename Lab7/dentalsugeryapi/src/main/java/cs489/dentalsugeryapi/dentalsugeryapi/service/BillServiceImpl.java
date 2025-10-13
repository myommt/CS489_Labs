package cs489.dentalsugeryapi.dentalsugeryapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cs489.dentalsugeryapi.dentalsugeryapi.model.Bill;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Patient;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Appointment;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.BillResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.BillResponseDTO.PatientBasicInfoDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.BillResponseDTO.AppointmentBasicInfoDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.repository.BillRepository;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public BillServiceImpl(BillRepository billRepository, 
                          PatientService patientService,
                          AppointmentService appointmentService) {
        this.billRepository = billRepository;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @Override
    public Bill addNewBill(Bill bill) {
        return findOrCreateBill(bill);
    }

    @Override
    public List<BillResponseDTO> getAllBills() {
        return billRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<BillResponseDTO> getAllBillsSortedByTotalCost() {
        return billRepository.findAllByOrderByTotalCostDesc().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public Bill getBillById(Integer id) {
        return billRepository.findById(id).orElse(null);
    }

    @Override
    public Bill updateBill(Bill bill) {
        return billRepository.save(bill);
    }

    @Override
    public boolean deleteBillById(Integer id) {
        if (billRepository.existsById(id)) {
            billRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<BillResponseDTO> getBillsByPatientId(Integer patientId) {
        return billRepository.findByPatientPatientId(patientId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<BillResponseDTO> getBillsByPaymentStatus(String paymentStatus) {
        return billRepository.findByPaymentStatusOrderByTotalCostDesc(paymentStatus).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public Bill findOrCreateBill(Bill bill) {
        // Use findOrCreate for related entities first
        if (bill.getPatient() != null) {
            Patient managedPatient = patientService.findOrCreatePatient(bill.getPatient());
            bill.setPatient(managedPatient);
        }
        
        if (bill.getAppointment() != null) {
            try {
                Appointment managedAppointment = appointmentService.findOrCreateAppointment(bill.getAppointment());
                bill.setAppointment(managedAppointment);
            } catch (Exception e) {
                // Handle appointment creation exception
                throw new RuntimeException("Error creating appointment for bill: " + e.getMessage(), e);
            }
        }
        
        // Check if bill already exists for this appointment (since it's one-to-one)
        if (bill.getAppointment() != null) {
            Bill existingBill = billRepository.findByAppointment(bill.getAppointment());
            if (existingBill != null) {
                return existingBill;
            }
        }
        
        // Bill doesn't exist, create new one
        return billRepository.save(bill);
    }

    private BillResponseDTO mapToDTO(Bill bill) {
        PatientBasicInfoDTO patientDTO = new PatientBasicInfoDTO(
                bill.getPatient().getPatientId(),
                bill.getPatient().getFirstName(),
                bill.getPatient().getLastName(),
                bill.getPatient().getEmail()
        );
        
        AppointmentBasicInfoDTO appointmentDTO = new AppointmentBasicInfoDTO(
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

    @Override
    public boolean hasOutstandingBills(Integer patientId) {
        try {
            Patient patient = patientService.getPatientById(patientId);
            if (patient == null) {
                return false;
            }
            return billRepository.countUnpaidBillsByPatient(patient) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<BillResponseDTO> getOutstandingBillsByPatientId(Integer patientId) {
        try {
            Patient patient = patientService.getPatientById(patientId);
            if (patient == null) {
                return List.of();
            }
            return billRepository.findUnpaidBillsByPatient(patient).stream()
                    .map(this::mapToDTO)
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }
}