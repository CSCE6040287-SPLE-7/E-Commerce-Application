package com.app.services;

public interface InvoiceService {
    /**
     * Generate an invoice for a given order ID.
     *
     * @param orderId The ID of the order for which to generate the invoice.
     * @return A string representing the generated invoice.
     */
    String generateInvoice(Long orderId);

    /**
     * Send an invoice to the user's WhatsApp for a given order ID (mocked).
     *
     * @param mobileNumber The mobile number of the user to send the invoice to.
     * @param orderId The ID of the order.
     * @return A success message indicating the WA message was sent.
     */
    String sendInvoiceByWA(String mobileNumber, Long orderId);
}
