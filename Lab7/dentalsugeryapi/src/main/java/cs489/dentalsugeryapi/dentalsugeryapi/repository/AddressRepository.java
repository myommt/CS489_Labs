package cs489.dentalsugeryapi.dentalsugeryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cs489.dentalsugeryapi.dentalsugeryapi.model.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

}
