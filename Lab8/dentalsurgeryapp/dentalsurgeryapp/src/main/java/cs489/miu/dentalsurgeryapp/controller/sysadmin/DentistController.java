package cs489.miu.dentalsurgeryapp.controller.sysadmin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cs489.miu.dentalsurgeryapp.dto.DentistResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.DeleteResponseDTO;
import cs489.miu.dentalsurgeryapp.model.Dentist;
import cs489.miu.dentalsurgeryapp.service.DentistService;

@RestController
@RequestMapping(value = "/dentalsugery/api/dentists")
public class DentistController {

    private final DentistService dentistService;

    public DentistController(DentistService dentistService) {
        this.dentistService = dentistService;
    }

    @GetMapping
    public ResponseEntity<List<DentistResponseDTO>> getAllDentists() {
        List<Dentist> dentists = dentistService.getAllDentists();
        List<DentistResponseDTO> dentistDTOs = dentists.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dentistDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DentistResponseDTO> getDentistById(@PathVariable Integer id) {
        Optional<Dentist> dentist = dentistService.findDentistById(id);
        return dentist.map(d -> ResponseEntity.ok(mapToDTO(d)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<DentistResponseDTO> getDentistByEmail(@PathVariable String email) {
        Optional<Dentist> dentist = dentistService.findDentistByEmail(email);
        return dentist.map(d -> ResponseEntity.ok(mapToDTO(d)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DentistResponseDTO> createDentist(@RequestBody Dentist dentist) {
        Dentist createdDentist = dentistService.saveDentist(dentist);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdDentist));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DentistResponseDTO> updateDentist(@PathVariable Integer id, @RequestBody Dentist dentist) {
        dentist.setDentistId(id);
        Dentist updatedDentist = dentistService.saveDentist(dentist);
        return ResponseEntity.ok(mapToDTO(updatedDentist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponseDTO> deleteDentist(@PathVariable Integer id) {
        boolean deleted = dentistService.deleteDentistById(id);
        if (deleted) {
            DeleteResponseDTO response = new DeleteResponseDTO(true, "Dentist with ID " + id + " has been successfully deleted.");
            return ResponseEntity.ok(response);
        } else {
            DeleteResponseDTO response = new DeleteResponseDTO(false, "Dentist with ID " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    private DentistResponseDTO mapToDTO(Dentist dentist) {
        return new DentistResponseDTO(
                dentist.getDentistId(),
                dentist.getFirstName(),
                dentist.getLastName(),
                dentist.getContactNumber(),
                dentist.getEmail(),
                dentist.getSpecialization()
        );
    }
}
