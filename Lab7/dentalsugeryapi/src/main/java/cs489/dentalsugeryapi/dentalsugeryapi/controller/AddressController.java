package cs489.dentalsugeryapi.dentalsugeryapi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cs489.dentalsugeryapi.dentalsugeryapi.dto.AddressResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Address;
import cs489.dentalsugeryapi.dentalsugeryapi.service.AddressService;

@RestController
@RequestMapping(value = "/dentalsugery/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses() {
        List<Address> addresses = addressService.getAllAddresses();
        List<AddressResponseDTO> addressDTOs = addresses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(addressDTOs);
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
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer id) {
        addressService.deleteAddressById(id);
        return ResponseEntity.noContent().build();
    }

    private AddressResponseDTO mapToDTO(Address address) {
        return new AddressResponseDTO(
                address.getAddressId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipcode()
        );
    }
}