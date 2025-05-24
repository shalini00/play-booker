package com.app.playbooker.integrations.payment;

import com.app.playbooker.dto.PaymentResponseDTO;
import com.app.playbooker.entity.Booking;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RazorpayPaymentIntegration {

    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currency";
    public static final String INR = "INR";
    public static final String STATUS = "status";
    public static final String ID = "id";
    public static final String RECEIPT = "receipt";

    @Autowired
    private RazorpayClient razorpayClient;

    public PaymentResponseDTO createPayment(Booking booking, String email) {
        JSONObject request = new JSONObject();
        request.put(AMOUNT, booking.getTotalPrice() * 100);
        request.put(CURRENCY, INR);
        request.put(RECEIPT, email.concat("_").concat(String.valueOf(System.currentTimeMillis())));

        try {
            Order order = razorpayClient.orders.create(request);
            return PaymentResponseDTO.builder()
                    .id(order.get(ID))
                    .status(order.get(STATUS))
                    .receipt(order.get(RECEIPT))
                    .build();
        } catch (RazorpayException e) {
            throw new RuntimeException("Payment failed due to - " + e.getMessage());
        }
    }
}
