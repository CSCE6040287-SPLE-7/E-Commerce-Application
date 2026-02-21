package com.app.services;

import com.app.entites.Order;
import com.app.exceptions.ResourceNotFoundException;
import com.app.repositories.OrderRepo;
import jakarta.transaction.Transactional;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

import static com.app.helpers.InvoiceHelper.buildOrderInvoice;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private OrderRepo orderRepo;

    @Transactional
    @Override
    public String generateInvoice(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));

        return buildOrderInvoice(order);
    }

    @Transactional
    @Override
    public byte[] generatePdfInvoice(Long orderId) {
        String invoiceText = generateInvoice(orderId);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.COURIER, 12);
            Paragraph paragraph = new Paragraph(invoiceText, font);
            document.add(paragraph);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF invoice for order: " + orderId, e);
        }

        return out.toByteArray();
    }
}
