package cs489.dentalsugeryapi.dentalsugeryapi.service;

 
import java.util.List;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.AddressResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.dto.AddressWithPatientsResponseDTO;
import cs489.dentalsugeryapi.dentalsugeryapi.model.Address;

public interface AddressService {
    Address addNewAddress(Address address);
    List<Address> getAllAddresses();
    Address getAddressById(Integer id);
    Address updateAddress(Address address);
    boolean deleteAddressById(Integer id);
    List<AddressResponseDTO> getAllAddressesSortedByCity();
    Address findOrCreateAddress(Address address);
    List<AddressWithPatientsResponseDTO> getAllAddressesWithPatientsSortedByCity();
}
