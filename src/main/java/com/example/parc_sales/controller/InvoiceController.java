package com.example.parc_sales.controller;

import com.example.parc_sales.dto.ClientDto;
import com.example.parc_sales.dto.InvoiceDto;
import com.example.parc_sales.model.Client;
import com.example.parc_sales.model.Invoice;
import com.example.parc_sales.model.Vehicle;
import com.example.parc_sales.service.ClientService;
import com.example.parc_sales.service.InvoiceService;
import com.example.parc_sales.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService service;
    private final ClientService clientService;
    private final VehicleService vehicleService;

    public InvoiceController(InvoiceService service, ClientService clientService, VehicleService vehicleService) {
        this.service = service;
        this.clientService = clientService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les factures")
    public List<Invoice> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une facture par ID")
    public Invoice getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle facture")
    public ResponseEntity<Invoice> create(@RequestBody InvoiceDto dto) {
        Invoice invoice = new Invoice();
        invoice.setPrix(dto.getPrix());
        invoice.setStatus(Invoice.Status.valueOf(dto.getStatus()));
        Client client = clientService.getClientById(dto.getClientId());
        invoice.setClient(client);
        Vehicle vehicle = vehicleService.getVehicleById(dto.getVehicleId());
        invoice.setVehicle(vehicle);
        Invoice createdInvoice = service.create(invoice);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdInvoice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id, @RequestBody InvoiceDto dto) {
        Invoice updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une facture")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-price-desc")
    @Operation(summary = "Lister les factures par prix décroissant")
    public ResponseEntity<List<Invoice>> getInvoicesByPrixDesc() {
        List<Invoice> invoices = service.getInvoicesByDescendingPrix();
        return ResponseEntity.ok(invoices);
    }
}
