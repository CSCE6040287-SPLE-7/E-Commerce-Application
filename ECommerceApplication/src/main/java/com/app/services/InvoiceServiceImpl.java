package com.app.services;

import com.app.entites.Order;
import com.app.entites.User;
import com.app.exceptions.ResourceNotFoundException;
import com.app.repositories.OrderRepo;
import com.app.repositories.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.app.helpers.InvoiceHelper.buildOrderInvoice;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public String generateInvoice(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));

        return buildOrderInvoice(order);
    }

    @Transactional
    @Override
    public String sendInvoiceByWA(String mobileNumber, Long orderId) {
        User user = userRepo.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User", "phoneNumber", mobileNumber));

        // Generate the invoice string
        String invoiceContent = generateInvoice(orderId);

        // Mocking the WA send process
        System.out.println("To: " + user.getMobileNumber());
        System.out.println("Message:\nHi " + user.getFirstName() + ", here is your invoice:\n" + invoiceContent);

        return "Success: Invoice for order " + orderId + " has been sent to WhatsApp number " + user.getMobileNumber();
    }
}
