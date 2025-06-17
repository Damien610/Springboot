package com.example.parc_sales.service;

import com.example.parc_sales.dto.VehicleDto;
import com.example.parc_sales.exceptions.ResourceNotFoundException;
import com.example.parc_sales.model.Vehicle;
import com.example.parc_sales.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    public List<Vehicle> getAllVehicles() {
        return repository.findAll();
    }

    public Vehicle getVehicleByVin(String vin) {
        return repository.findByVin(vin)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule avec le VIN " + vin + " non trouvé"));
    }

    public Vehicle getVehicleById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule avec l'ID " + id + " non trouvé"));
    }

    public Vehicle createVehicle(Vehicle dto) {
        if (repository.existsByVin(dto.getVin())) {
            throw new ResourceNotFoundException("Véhicule avec le VIN " + dto.getVin() + " déjà existant");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVin(dto.getVin());
        vehicle.setStatus(dto.getStatus());
        vehicle.setPrixAchat(dto.getPrixAchat());
        vehicle.setDateAchat(dto.getDateAchat());

        return repository.save(vehicle);
    }

    public Vehicle updateVehicle(Long id, VehicleDto dto) {
        Vehicle vehicle = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule avec le VIN " + id + " non trouvé"));

        vehicle.setStatus(dto.getStatus());
        vehicle.setPrixAchat(dto.getPrixAchat());
        vehicle.setDateAchat(dto.getDateAchat());

        return repository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        Vehicle vehicle = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule avec le VIN " + id + " non trouvé"));
        repository.delete(vehicle);
    }
}
