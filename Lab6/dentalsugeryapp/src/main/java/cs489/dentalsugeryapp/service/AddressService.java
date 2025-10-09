package cs489.dentalsugeryapp.service;

import java.util.List;
import cs489.dentalsugeryapp.model.Address;

public interface AddressService {
    Address addNewAddress(Address address);
    List<Address> getAllAddresses();
    Address getAddressById(Integer id);
    Address updateAddress(Address address);
    void deleteAddressById(Integer id);
}
