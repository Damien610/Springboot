package com.example.parc_sales;

import com.example.parc_sales.dto.VehicleDto;
import com.example.parc_sales.model.Vehicle;
import com.example.parc_sales.model.Vehicle.Status;
import com.example.parc_sales.repository.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Vehicle vehicle;

    @BeforeEach
    void setup() {
        vehicleRepository.deleteAll();

        vehicle = new Vehicle();
        vehicle.setVin("ABC123");
        vehicle.setStatus(Status.DISPONIBLE);
        vehicle.setPrixAchat(12000.0);
        vehicle.setDateAchat(LocalDate.of(2024, 5, 15));

        vehicle = vehicleRepository.save(vehicle);
    }

    @Test
    void testGetAllVehicles() throws Exception {
        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].vin", is("ABC123")));
    }

    @Test
    void testGetByVin() throws Exception {
        mockMvc.perform(get("/vehicles/vin/ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vin", is("ABC123")));
    }

    @Test
    void testGetById() throws Exception {
        mockMvc.perform(get("/vehicles/id/" + vehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(vehicle.getId().intValue())));
    }

    @Test
    void testCreateVehicle() throws Exception {
        VehicleDto dto = new VehicleDto();
        dto.setVin("DEF456");
        dto.setStatus(Status.DISPONIBLE);
        dto.setPrixAchat(15000.0);
        dto.setDateAchat(LocalDate.of(2025, 1, 1));

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vin", is("DEF456")));
    }

    @Test
    void testUpdateVehicle() throws Exception {
        VehicleDto dto = new VehicleDto();
        dto.setVin("ABC123");
        dto.setStatus(Status.VENDU);
        dto.setPrixAchat(12000.0);
        dto.setDateAchat(LocalDate.of(2024, 5, 15));

        mockMvc.perform(put("/vehicles/" + vehicle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("VENDU")));
    }

    @Test
    void testDeleteVehicle() throws Exception {
        mockMvc.perform(delete("/vehicles/" + vehicle.getId()))
                .andExpect(status().isNoContent());

        Optional<Vehicle> deleted = vehicleRepository.findById(vehicle.getId());
        assert (deleted.isEmpty());
    }
}
