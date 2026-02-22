package com.app.controllers;

import com.app.services.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
@Tag(name = "Invoice Management", description = "APIs for managing invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/admin/orders/{orderId}/invoice/send-wa/{mobileNumber}")
    @Operation(summary = "Send Invoice by WhatsApp", description = "Mocks sending an invoice to the user's WhatsApp number")
    public ResponseEntity<String> sendInvoiceByWA(@PathVariable Long orderId, @PathVariable String mobileNumber) {
        String responseMessage = invoiceService.sendInvoiceByWA(mobileNumber, orderId);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
