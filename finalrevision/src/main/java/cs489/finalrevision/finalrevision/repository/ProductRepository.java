package cs489.finalrevision.finalrevision.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cs489.finalrevision.finalrevision.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
