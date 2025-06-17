package com.example.parc_sales.repository;

import com.example.parc_sales.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVin(String vin);
    boolean existsByVin(String vin);
}
