package cs489.dentalsugeryapi.dentalsugeryapi.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cs489.dentalsugeryapi.dentalsugeryapi.dto.SurgeryLocationResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.AddressResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.DeleteResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.model.SurgeryLocation;
import cs489.dentalsugeryapi.dentalsugeryapi.service.SurgeryLocationService;

@RestController
@RequestMapping(value = "/dentalsugery/api/surgerylocations")
public class SurgeryLocationController {

    private final SurgeryLocationService surgeryLocationService;

    public SurgeryLocationController(SurgeryLocationService surgeryLocationService) {
        this.surgeryLocationService = surgeryLocationService;
    }

    @GetMapping
    public ResponseEntity<List<SurgeryLocationResponseDTO>> getAllSurgeryLocations() {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocations();
        List<SurgeryLocationResponseDTO> surgeryLocationDTOs = surgeryLocations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surgeryLocationDTOs);
    }

    @GetMapping("/ordered")
    public ResponseEntity<List<SurgeryLocationResponseDTO>> getAllSurgeryLocationsOrderedByName() {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.getAllSurgeryLocationsOrderedByName();
        List<SurgeryLocationResponseDTO> surgeryLocationDTOs = surgeryLocations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surgeryLocationDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurgeryLocationResponseDTO> getSurgeryLocationById(@PathVariable Integer id) {
        Optional<SurgeryLocation> surgeryLocation = surgeryLocationService.findSurgeryLocationById(id);
        return surgeryLocation.map(location -> ResponseEntity.ok(mapToDTO(location)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SurgeryLocationResponseDTO> getSurgeryLocationByName(@PathVariable String name) {
        Optional<SurgeryLocation> surgeryLocation = surgeryLocationService.findSurgeryLocationByName(name);
        return surgeryLocation.map(location -> ResponseEntity.ok(mapToDTO(location)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/contact/{contactNumber}")
    public ResponseEntity<List<SurgeryLocationResponseDTO>> getSurgeryLocationsByContactNumber(@PathVariable String contactNumber) {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.findSurgeryLocationsByContactNumber(contactNumber);
        List<SurgeryLocationResponseDTO> surgeryLocationDTOs = surgeryLocations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surgeryLocationDTOs);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<SurgeryLocationResponseDTO>> getSurgeryLocationsByCity(@PathVariable String city) {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.findSurgeryLocationsByCity(city);
        List<SurgeryLocationResponseDTO> surgeryLocationDTOs = surgeryLocations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surgeryLocationDTOs);
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<SurgeryLocationResponseDTO>> getSurgeryLocationsByState(@PathVariable String state) {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.findSurgeryLocationsByState(state);
        List<SurgeryLocationResponseDTO> surgeryLocationDTOs = surgeryLocations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surgeryLocationDTOs);
    }

    @GetMapping("/zipcode/{zipcode}")
    public ResponseEntity<List<SurgeryLocationResponseDTO>> getSurgeryLocationsByZipcode(@PathVariable String zipcode) {
        List<SurgeryLocation> surgeryLocations = surgeryLocationService.findSurgeryLocationsByZipcode(zipcode);
        List<SurgeryLocationResponseDTO> surgeryLocationDTOs = surgeryLocations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surgeryLocationDTOs);
    }

    @PostMapping
    public ResponseEntity<SurgeryLocationResponseDTO> createSurgeryLocation(@RequestBody SurgeryLocation surgeryLocation) {
        SurgeryLocation createdSurgeryLocation = surgeryLocationService.saveSurgeryLocation(surgeryLocation);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdSurgeryLocation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SurgeryLocationResponseDTO> updateSurgeryLocation(@PathVariable Integer id, @RequestBody SurgeryLocation surgeryLocation) {
        Optional<SurgeryLocation> updatedSurgeryLocation = surgeryLocationService.updateSurgeryLocation(id, surgeryLocation);
        return updatedSurgeryLocation.map(location -> ResponseEntity.ok(mapToDTO(location)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponseDTO> deleteSurgeryLocation(@PathVariable Integer id) {
        boolean deleted = surgeryLocationService.deleteSurgeryLocationById(id);
        if (deleted) {
            DeleteResponseDTO response = new DeleteResponseDTO(true, "Surgery Location with ID " + id + " has been successfully deleted.");
            return ResponseEntity.ok(response);
        } else {
            DeleteResponseDTO response = new DeleteResponseDTO(false, "Surgery Location with ID " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Integer id) {
        boolean exists = surgeryLocationService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        boolean exists = surgeryLocationService.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalSurgeryLocationCount() {
        long count = surgeryLocationService.getTotalSurgeryLocationCount();
        return ResponseEntity.ok(count);
    }

    private SurgeryLocationResponseDTO mapToDTO(SurgeryLocation surgeryLocation) {
        AddressResponseDTO addressDTO = null;
        if (surgeryLocation.getLocation() != null) {
            addressDTO = new AddressResponseDTO(
                    surgeryLocation.getLocation().getAddressId(),
                    surgeryLocation.getLocation().getStreet(),
                    surgeryLocation.getLocation().getCity(),
                    surgeryLocation.getLocation().getState(),
                    surgeryLocation.getLocation().getZipcode()
            );
        }

        return new SurgeryLocationResponseDTO(
                surgeryLocation.getSurgeryLocationId(),
                surgeryLocation.getName(),
                surgeryLocation.getContactNumber(),
                addressDTO
        );
    }
}