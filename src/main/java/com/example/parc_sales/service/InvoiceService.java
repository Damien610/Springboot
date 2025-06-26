package com.example.parc_sales.service;

import com.example.parc_sales.dto.InvoiceDto;
import com.example.parc_sales.exceptions.ResourceNotFoundException;
import com.example.parc_sales.model.Client;
import com.example.parc_sales.model.Invoice;
import com.example.parc_sales.model.Vehicle;
import com.example.parc_sales.observeur.InvoiceObserver;
import com.example.parc_sales.repository.ClientRepository;
import com.example.parc_sales.repository.InvoiceRepository;
import com.example.parc_sales.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import com.example.parc_sales.model.Invoice.Status;

import java.util.List;


@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final ClientRepository clientRepo;
    private final VehicleRepository vehicleRepo;
    private final List<InvoiceObserver> observers;

    public InvoiceService(InvoiceRepository invoiceRepo, ClientRepository clientRepo, VehicleRepository vehicleRepo,
                          List<InvoiceObserver> observers) {
        this.invoiceRepo = invoiceRepo;
        this.clientRepo = clientRepo;
        this.vehicleRepo = vehicleRepo;
        this.observers = observers;
    }

    public List<Invoice> getAll() {
        return invoiceRepo.findAll();
    }

    public Invoice getById(Long id) {
        return invoiceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));
    }

    public Invoice create(Invoice invoice) {
        Client client = clientRepo.findById(invoice.getClient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        Vehicle vehicle = vehicleRepo.findByVin(invoice.getVehicle().getVin())
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule non trouvé"));

        if (vehicle.getStatus() == Vehicle.Status.RESERVE || vehicle.getStatus() == Vehicle.Status.VENDU) {
            throw new IllegalArgumentException("Le véhicule est déjà réservé ou vendu");
        }

        Invoice saved = invoiceRepo.save(invoice);
        notifyObservers(saved);
        return invoiceRepo.save(saved);
    }

    public Invoice update(Long id, InvoiceDto dto){
        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client avec l'ID " + id + " non trouvé"));
        Vehicle vehicle = vehicleRepo.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule non trouvé"));

        if (vehicle.getStatus() == Vehicle.Status.RESERVE || vehicle.getStatus() == Vehicle.Status.VENDU) {
            throw new IllegalArgumentException("Le véhicule est déjà réservé ou vendu");
        }

        invoice.setPrix(dto.getPrix());
        invoice.setStatus(Status.valueOf(dto.getStatus()));
        invoice.setClient(clientRepo.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé")));
        invoice.setVehicle(vehicleRepo.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule non trouvé")));
        invoice.setPrix(dto.getPrix());
        notifyObservers(invoice);
        return invoiceRepo.save(invoice);
    }

    public void delete(Long id) {
        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée"));
        invoiceRepo.delete(invoice);
    }

    private void notifyObservers(Invoice invoice) {
        for (InvoiceObserver observer : observers) {
            observer.onInvoiceChanged(invoice);
        }
    }

    public List<Invoice> getInvoicesByDescendingPrix() {
        return invoiceRepo.findAllByOrderByPrixDesc();
    }
}
