package com.example.parc_sales.observeur;

import com.example.parc_sales.model.Invoice;

public interface InvoiceObserver {
    void onInvoiceChanged(Invoice invoice);
}
