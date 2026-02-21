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
     * Generate an invoice as a PDF file byte array.
     *
     * @param orderId The ID of the order for which to generate the PDF invoice.
     * @return A byte array representing the PDF file.
     */
    byte[] generatePdfInvoice(Long orderId);
}
