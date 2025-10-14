package cs489.miu.dentalsurgeryapp.controller.sysadmin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cs489.miu.dentalsurgeryapp.dto.AddressResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.AddressWithPatientsResponseDTO;
import cs489.miu.dentalsurgeryapp.dto.DeleteResponseDTO;
import cs489.miu.dentalsurgeryapp.model.Address;
import cs489.miu.dentalsurgeryapp.service.AddressService;
import jakarta.validation.Valid;

/**
 * Unified Address Controller
 * - MVC pages under /secured/address
 * - REST API under /dentalsugery/api/addresses
 */
@Controller("addressController")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // ===================== MVC (Thymeleaf) endpoints =====================

    @GetMapping({"/secured/address/", "/secured/address/list"})
    public String listAddresses(Model model) {
        List<Address> addresses = addressService.getAllAddresses();
        model.addAttribute("addresses", addresses);
        model.addAttribute("pageTitle", "Address List");
        return "secured/address/list";
    }

    @GetMapping("/secured/address/new")
    public String showNewAddressForm(Model model) {
        model.addAttribute("address", new Address());
        model.addAttribute("pageTitle", "Add New Address");
        return "secured/address/new";
    }

    @PostMapping("/secured/address/new")
    public String createAddressUi(@Valid @ModelAttribute("address") Address address,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Add New Address");
            return "secured/address/new";
        }
    addressService.addNewAddress(address);
        ra.addFlashAttribute("successMessage", "Address has been added.");
        return "redirect:/secured/address/list";
    }

    @GetMapping("/secured/address/edit/{id}")
    public String showEditAddressForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Address address = addressService.getAddressById(id);
        if (address == null) {
            ra.addFlashAttribute("errorMessage", "Address not found with ID: " + id);
            return "redirect:/secured/address/list";
        }
        model.addAttribute("address", address);
        model.addAttribute("pageTitle", "Edit Address");
        return "secured/address/edit";
    }

    @PostMapping("/secured/address/edit/{id}")
    public String updateAddressUi(@PathVariable Integer id,
                                  @Valid @ModelAttribute("address") Address address,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Address");
            return "secured/address/edit";
        }
        address.setAddressId(id);
        Address updated = addressService.updateAddress(address);
        if (updated == null) {
            ra.addFlashAttribute("errorMessage", "Unable to update address ID: " + id);
            return "redirect:/secured/address/list";
        }
        ra.addFlashAttribute("successMessage", "Address has been updated.");
        return "redirect:/secured/address/list";
    }

    @GetMapping("/secured/address/view/{id}")
    public String viewAddress(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Address address = addressService.getAddressById(id);
        if (address == null) {
            ra.addFlashAttribute("errorMessage", "Address not found with ID: " + id);
            return "redirect:/secured/address/list";
        }
        model.addAttribute("address", address);
        model.addAttribute("pageTitle", "Address Details");
        return "secured/address/view";
    }

    // ===================== REST API endpoints =====================

    @ResponseBody
    @GetMapping("/dentalsugery/api/addresses")
    public ResponseEntity<List<AddressResponseDTO>> getAllAddressesSortedByCity() {
        List<AddressResponseDTO> addresses = addressService.getAllAddressesSortedByCity();
        return ResponseEntity.ok(addresses);
    }

    @ResponseBody
    @GetMapping("/dentalsugery/api/addresses/{id}")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Integer id) {
        Address address = addressService.getAddressById(id);
        if (address != null) {
            return ResponseEntity.ok(mapToDTO(address));
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @PostMapping("/dentalsugery/api/addresses")
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestBody Address address) {
        Address createdAddress = addressService.addNewAddress(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdAddress));
    }

    @ResponseBody
    @PutMapping("/dentalsugery/api/addresses/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Integer id, @RequestBody Address address) {
        address.setAddressId(id);
        Address updatedAddress = addressService.updateAddress(address);
        if (updatedAddress != null) {
            return ResponseEntity.ok(mapToDTO(updatedAddress));
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @DeleteMapping("/dentalsugery/api/addresses/{id}")
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

    @ResponseBody
    @GetMapping("/dentalsugery/api/addresses/with-patients")
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
