package com.example.parc_sales.observeur;

import com.example.parc_sales.model.Invoice;
import com.example.parc_sales.model.Vehicle;
import com.example.parc_sales.model.Vehicle.Status;
import com.example.parc_sales.repository.VehicleRepository;
import org.springframework.stereotype.Component;

@Component
public class VehicleStatusUpdater implements InvoiceObserver {

    private final VehicleRepository vehicleRepository;

    public VehicleStatusUpdater(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public void onInvoiceChanged(Invoice invoice) {
        Vehicle vehicle = invoice.getVehicle();
        if (vehicle == null) return;

        switch (invoice.getStatus()) {
            case PAID -> vehicle.setStatus(Status.VENDU);
            case WAITING_FOR_PAYMENT -> vehicle.setStatus(Status.RESERVE);
        }

        vehicleRepository.save(vehicle);
    }
}
