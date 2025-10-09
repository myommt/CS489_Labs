package cs489.dentalsugeryapp.service;

import java.util.List;
import org.springframework.stereotype.Service;
import cs489.dentalsugeryapp.model.Address;
import cs489.dentalsugeryapp.repository.AddressRepository;

@Service
public class AddressServiceImpl implements AddressService {
    
    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public Address addNewAddress(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @Override
    public Address getAddressById(Integer id) {
        return addressRepository.findById(id).orElse(null);
    }

    @Override
    public Address updateAddress(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public void deleteAddressById(Integer id) {
        addressRepository.deleteById(id);
    }
}
