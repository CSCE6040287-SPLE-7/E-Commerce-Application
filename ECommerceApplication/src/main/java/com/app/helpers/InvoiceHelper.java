package com.app.helpers;

import com.app.entites.Order;
import com.app.entites.OrderItem;

public class InvoiceHelper {
    public static String buildOrderInvoice(Order order) {
        StringBuilder invoice = new StringBuilder();
        addHeadFooterSeparator(invoice);
        invoice.append("               INVOICE                   \n");
        addHeadFooterSeparator(invoice);
        invoice.append(String.format("Order ID    : %d\n", order.getOrderId()));
        invoice.append(String.format("Email       : %s\n", order.getEmail()));
        invoice.append(String.format("Order Date  : %s\n", order.getOrderDate()));
        invoice.append(String.format("Order Status: %s\n", order.getOrderStatus()));
        addLineSeparator(invoice);
        invoice.append("Items:\n");

        for (OrderItem item : order.getOrderItems()) {
            invoice.append(String.format(
                    "- %s x%d @ $%.2f (Discount: $%.2f) -> $%.2f\n",
                    item.getProduct().getProductName(),
                    item.getQuantity(),
                    item.getOrderedProductPrice() + item.getDiscount(),
                    item.getDiscount(),
                    item.getOrderedProductPrice() * item.getQuantity()));
        }

        addLineSeparator(invoice);
        if (order.getPayment() != null) {
            invoice.append(String.format("Payment Method: %s\n", order.getPayment().getPaymentMethod()));
        }
        invoice.append(String.format("Total Amount  : $%.2f\n", order.getTotalAmount()));
        addHeadFooterSeparator(invoice);
        invoice.append("       Thank you for your purchase!      \n");
        addHeadFooterSeparator(invoice);

        return invoice.toString();
    }

    public static void addHeadFooterSeparator(StringBuilder invoice) {
        invoice.append("=========================================\n");
    }

    public static void addLineSeparator(StringBuilder invoice) {
        invoice.append("-----------------------------------------\n");
    }

}
