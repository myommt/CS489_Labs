package cs489.dentalsugeryapp.service;

import cs489.dentalsugeryapp.model.SurgeryLocation;
import cs489.dentalsugeryapp.repository.SurgeryLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SurgeryLocationServiceImpl implements SurgeryLocationService {

    private final SurgeryLocationRepository surgeryLocationRepository;

    @Autowired
    public SurgeryLocationServiceImpl(SurgeryLocationRepository surgeryLocationRepository) {
        this.surgeryLocationRepository = surgeryLocationRepository;
    }

    @Override
    public SurgeryLocation saveSurgeryLocation(SurgeryLocation surgeryLocation) {
        if (surgeryLocation == null) {
            throw new IllegalArgumentException("Surgery location cannot be null");
        }
        return surgeryLocationRepository.save(surgeryLocation);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SurgeryLocation> findSurgeryLocationById(Integer surgeryLocationId) {
        if (surgeryLocationId == null) {
            return Optional.empty();
        }
        return surgeryLocationRepository.findById(surgeryLocationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SurgeryLocation> findSurgeryLocationByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return surgeryLocationRepository.findByName(name.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryLocation> findSurgeryLocationsByContactNumber(String contactNumber) {
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            return List.of();
        }
        return surgeryLocationRepository.findByContactNumber(contactNumber.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryLocation> findSurgeryLocationsByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return List.of();
        }
        return surgeryLocationRepository.findByCity(city.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryLocation> findSurgeryLocationsByState(String state) {
        if (state == null || state.trim().isEmpty()) {
            return List.of();
        }
        return surgeryLocationRepository.findByState(state.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryLocation> findSurgeryLocationsByZipcode(String zipcode) {
        if (zipcode == null || zipcode.trim().isEmpty()) {
            return List.of();
        }
        return surgeryLocationRepository.findByZipcode(zipcode.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryLocation> getAllSurgeryLocations() {
        return surgeryLocationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryLocation> getAllSurgeryLocationsOrderedByName() {
        return surgeryLocationRepository.findAllOrderedByName();
    }

    @Override
    public Optional<SurgeryLocation> updateSurgeryLocation(Integer surgeryLocationId, SurgeryLocation surgeryLocation) {
        if (surgeryLocationId == null || surgeryLocation == null) {
            return Optional.empty();
        }
        
        return surgeryLocationRepository.findById(surgeryLocationId)
                .map(existingSurgeryLocation -> {
                    existingSurgeryLocation.setName(surgeryLocation.getName());
                    existingSurgeryLocation.setContactNumber(surgeryLocation.getContactNumber());
                    existingSurgeryLocation.setLocation(surgeryLocation.getLocation());
                    return surgeryLocationRepository.save(existingSurgeryLocation);
                });
    }

    @Override
    public void deleteSurgeryLocationById(Integer surgeryLocationId) {
        if (surgeryLocationId != null && surgeryLocationRepository.existsById(surgeryLocationId)) {
            surgeryLocationRepository.deleteById(surgeryLocationId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer surgeryLocationId) {
        if (surgeryLocationId == null) {
            return false;
        }
        return surgeryLocationRepository.existsById(surgeryLocationId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return surgeryLocationRepository.existsByName(name.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalSurgeryLocationCount() {
        return surgeryLocationRepository.count();
    }
}