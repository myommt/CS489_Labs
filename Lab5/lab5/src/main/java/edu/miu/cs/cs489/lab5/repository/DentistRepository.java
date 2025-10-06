package edu.miu.cs.cs489.lab5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.miu.cs.cs489.lab5.model.Dentist;

public interface DentistRepository extends JpaRepository<Dentist, Long> {
}
