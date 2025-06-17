package com.example.parc_sales.controller;

import com.example.parc_sales.dto.VehicleDto;
import com.example.parc_sales.model.Client;
import com.example.parc_sales.model.Vehicle;
import com.example.parc_sales.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister tous les véhicules")
    public List<Vehicle> getAll() {
        return service.getAllVehicles();
    }

    @GetMapping("vin/{vin}")
    @Operation(summary = "Obtenir un véhicule par VIN")
    public Vehicle getByVin(@PathVariable String vin) {
        return service.getVehicleByVin(vin);
    }

    @GetMapping("id/{id}")
    @Operation(summary = "Obtenir un véhicule par ID")
    public Vehicle getById(@PathVariable Long id) {
        return service.getVehicleById(id);
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau véhicule")
    public  ResponseEntity<Vehicle> create(@RequestBody VehicleDto dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(dto.getVin());
        vehicle.setStatus(dto.getStatus());
        vehicle.setPrixAchat(dto.getPrixAchat());
        vehicle.setDateAchat(dto.getDateAchat());

        Vehicle createdVehicle= service.createVehicle(vehicle);

        return ResponseEntity.ok(createdVehicle);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un véhicule existant")
    public Vehicle update(@PathVariable Long id, @RequestBody VehicleDto dto) {
        return service.updateVehicle(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un véhicule par VIN")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
