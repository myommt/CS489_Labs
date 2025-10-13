package cs489.miu.dentalsurgeryapp.controller.sysadmin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cs489.miu.dentalsurgeryapp.dto.AddressResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.AddressWithPatientsResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.DeleteResponseDTO;
import cs489.miu.dentalsurgeryapp.model.Address;
import cs489.miu.dentalsurgeryapp.service.AddressService;

@RestController
@RequestMapping(value = "/dentalsugery/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAllAddressesSortedByCity() {
        List<AddressResponseDTO> addresses = addressService.getAllAddressesSortedByCity();
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Integer id) {
        Address address = addressService.getAddressById(id);
        if (address != null) {
            return ResponseEntity.ok(mapToDTO(address));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestBody Address address) {
        Address createdAddress = addressService.addNewAddress(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdAddress));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Integer id, @RequestBody Address address) {
        address.setAddressId(id);
        Address updatedAddress = addressService.updateAddress(address);
        if (updatedAddress != null) {
            return ResponseEntity.ok(mapToDTO(updatedAddress));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponseDTO> deleteAddress(@PathVariable Integer id) {
        boolean deleted = addressService.deleteAddressById(id);
        if (deleted) {
            DeleteResponseDTO response = new DeleteResponseDTO(true,
                    "Address with ID " + id + " has been successfully deleted.");
            return ResponseEntity.ok(response);
        } else {
            DeleteResponseDTO response = new DeleteResponseDTO(false, "Address with ID " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/with-patients")
    public ResponseEntity<List<AddressWithPatientsResponseDTO>> getAllAddressesWithPatientsSortedByCity() {
        List<AddressWithPatientsResponseDTO> addresses = addressService.getAllAddressesWithPatientsSortedByCity();
        return ResponseEntity.ok(addresses);
    }

    private AddressResponseDTO mapToDTO(Address address) {
        return new AddressResponseDTO(
                address.getAddressId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipcode());
    }
}
