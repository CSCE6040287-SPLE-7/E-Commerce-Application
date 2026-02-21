package com.app.services;

import com.app.entites.Order;
import com.app.exceptions.ResourceNotFoundException;
import com.app.repositories.OrderRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
