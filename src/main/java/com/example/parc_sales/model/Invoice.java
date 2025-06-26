package com.example.parc_sales.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double prix;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference
    private Client client;

    @OneToOne
    @JoinColumn(name = "vehicle_vin")
    private Vehicle vehicle;

    public enum Status {
        WAITING_FOR_PAYMENT,
        PAID;

        public static Status valueOf(Status status) {
            if (status == null) {
                return null;
            }
            for (Status s : Status.values()) {
                if (s.name().equalsIgnoreCase(status.name())) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}

