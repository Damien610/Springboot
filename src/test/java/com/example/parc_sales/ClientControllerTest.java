package com.example.parc_sales;

import com.example.parc_sales.model.Client;
import com.example.parc_sales.repository.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Client client;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();

        client = new Client();
        client.setNom("Test");
        client.setPrenom("Client");
        client.setEmail("client@test.com");
        client.setTelephone("0600000000");
        client = clientRepository.save(client);
    }

    @Test
    @Order(1)
    void testGetAllClients() throws Exception {
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom", is("Test")));
    }

    @Test
    @Order(2)
    void testGetClientById() throws Exception {
        mockMvc.perform(get("/api/clients/" + client.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("client@test.com")));
    }

    @Test
    @Order(3)
    void testCreateClient() throws Exception {
        Client newClient = new Client();
        newClient.setNom("John");
        newClient.setPrenom("Doe");
        newClient.setEmail("john.doe@example.com");
        newClient.setTelephone("0612345678");

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("John")))
                .andExpect(jsonPath("$.prenom", is("Doe")));
    }

    @Test
    @Order(4)
    void testUpdateClient() throws Exception {
        client.setNom("Updated");
        client.setPrenom("Name");

        mockMvc.perform(put("/api/clients/" + client.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("Updated")));
    }

    @Test
    @Order(5)
    void testDeleteClient() throws Exception {
        mockMvc.perform(delete("/api/clients/" + client.getId()))
                .andExpect(status().isNoContent());

        Optional<Client> deleted = clientRepository.findById(client.getId());
        Assertions.assertTrue(deleted.isEmpty());
    }
}
