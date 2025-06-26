package com.example.parc_sales;

import com.example.parc_sales.controller.VehicleController;
import com.example.parc_sales.dto.VehicleDto;
import com.example.parc_sales.model.Vehicle;
import com.example.parc_sales.model.Vehicle.Status;
import com.example.parc_sales.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(VehicleController.class)
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Vehicle vehicle;

    @BeforeEach
    void setup() {
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setVin("ABC123");
        vehicle.setStatus(Status.DISPONIBLE);
        vehicle.setPrixAchat(12000.0);
        vehicle.setDateAchat(LocalDate.of(2024, 5, 15));
    }

    @Test
    void testGetAllVehicles() throws Exception {
        when(vehicleService.getAllVehicles()).thenReturn(Collections.singletonList(vehicle));

        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].vin", is("ABC123")));
    }

    @Test
    void testGetByVin() throws Exception {
        when(vehicleService.getVehicleByVin("ABC123")).thenReturn(vehicle);

        mockMvc.perform(get("/vehicles/vin/ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vin", is("ABC123")));
    }

    @Test
    void testGetById() throws Exception {
        when(vehicleService.getVehicleById(1L)).thenReturn(vehicle);

        mockMvc.perform(get("/vehicles/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void testCreateVehicle() throws Exception {
        VehicleDto dto = new VehicleDto();
        dto.setVin("ABC123");
        dto.setStatus(Status.DISPONIBLE);
        dto.setPrixAchat(12000.0);
        dto.setDateAchat(LocalDate.of(2024, 5, 15));

        when(vehicleService.createVehicle(any(Vehicle.class))).thenReturn(vehicle);

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vin", is("ABC123")));
    }

    @Test
    void testUpdateVehicle() throws Exception {
        VehicleDto dto = new VehicleDto();
        dto.setVin("ABC123");
        dto.setStatus(Status.VENDU);
        dto.setPrixAchat(12000.0);
        dto.setDateAchat(LocalDate.of(2024, 5, 15));

        vehicle.setStatus(Status.VENDU);

        when(vehicleService.updateVehicle(Mockito.eq(1L), any(VehicleDto.class))).thenReturn(vehicle);

        mockMvc.perform(put("/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("VENDU")));
    }

    @Test
    void testDeleteVehicle() throws Exception {
        mockMvc.perform(delete("/vehicles/1"))
                .andExpect(status().isNoContent());
    }
}