package cs489.dentalsugeryapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cs489.dentalsugeryapp.model.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

}
