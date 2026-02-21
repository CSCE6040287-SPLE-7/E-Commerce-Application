package com.app.services;

public interface InvoiceService {
    /**
     * Generate an invoice for a given order ID.
     *
     * @param orderId The ID of the order for which to generate the invoice.
     * @return A string representing the generated invoice (e.g., a PDF URL or
     *         invoice details).
     */
    String generateInvoice(Long orderId);

    /**
     * Send an invoice to the user's email for a given order ID (mocked).
     *
     * @param orderId The ID of the order.
     * @return A success message indicating the email was sent.
     */
    String sendInvoiceByEmail(String receiverEmail, Long orderId);
}
