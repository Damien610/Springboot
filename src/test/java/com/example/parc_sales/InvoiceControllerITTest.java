package com.example.parc_sales;

import com.example.parc_sales.model.Client;
import com.example.parc_sales.model.Vehicle;
import com.example.parc_sales.repository.ClientRepository;
import com.example.parc_sales.repository.InvoiceRepository;
import com.example.parc_sales.repository.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InvoiceControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Client client;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        invoiceRepository.deleteAll();
        vehicleRepository.deleteAll();
        clientRepository.deleteAll();

        client = new Client();
        client.setNom("Test");
        client.setPrenom("Client");
        client.setEmail("client@test.com");
        client.setTelephone("0102030405");
        client = clientRepository.save(client);

        vehicle = new Vehicle();
        vehicle.setVin("VIN-TEST-123");
        vehicle.setPrixAchat(5000);
        vehicle.setDateAchat(LocalDate.now());
        vehicle.setStatus(Vehicle.Status.DISPONIBLE);
        vehicle = vehicleRepository.save(vehicle);
    }

    @Test
    @Order(1)
    void testCreateInvoice_shouldUpdateVehicleStatusToRESERVE() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("prix", 9000);
        payload.put("status", "WAITING_FOR_PAYMENT");
        payload.put("clientId", client.getId());
        payload.put("vehicleId", vehicle.getId());

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("WAITING_FOR_PAYMENT")))
                .andExpect(jsonPath("$.prix", is(9000.0)));

        Vehicle updated = vehicleRepository.findById(vehicle.getId()).orElseThrow();
        Assertions.assertEquals(Vehicle.Status.RESERVE, updated.getStatus());
    }

    @Test
    @Order(2)
    void testCreateInvoice_shouldUpdateVehicleStatusToVENDU() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("prix", 9000);
        payload.put("status", "PAID");
        payload.put("clientId", client.getId());
        payload.put("vehicleId", vehicle.getId());

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("PAID")));

        Vehicle updated = vehicleRepository.findById(vehicle.getId()).orElseThrow();
        Assertions.assertEquals(Vehicle.Status.VENDU, updated.getStatus());
    }

    @Test
    @Order(3)
    void testCreateInvoice_shouldFailIfVehicleAlreadySold() throws Exception {
        vehicle.setStatus(Vehicle.Status.VENDU);
        vehicleRepository.save(vehicle);

        Map<String, Object> payload = new HashMap<>();
        payload.put("prix", 9000);
        payload.put("status", "PAID");
        payload.put("clientId", client.getId());
        payload.put("vehicleId", vehicle.getId());

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("déjà réservé ou vendu")));
    }
}


