package com.example.parc_sales.dto;

import com.example.parc_sales.model.Vehicle.Status;
import java.time.LocalDate;

public class VehicleDto {
    private String vin;
    private Status status;
    private double prixAchat;
    private LocalDate dateAchat;


    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public double getPrixAchat() { return prixAchat; }
    public void setPrixAchat(double prixAchat) { this.prixAchat = prixAchat; }

    public LocalDate getDateAchat() { return dateAchat; }
    public void setDateAchat(LocalDate dateAchat) { this.dateAchat = dateAchat; }
}
