package com.techcommerce.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Send basic email
     */
    public void sendEmail(Long userId, String subject, String body) {
        try {
            log.info("Sending email to user: {}", userId);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(getUserEmail(userId));
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@ecommerce.com");
            
            mailSender.send(message);
            log.info("Email sent successfully to user: {}", userId);
        } catch (Exception e) {
            log.error("Error sending email to user: {}", userId, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send order confirmation email
     */
    public void sendOrderConfirmationEmail(Long userId, String orderId, String orderDetails) {
        String subject = "Order Confirmation - Order #" + orderId;
        String body = buildOrderConfirmationEmail(orderId, orderDetails);
        sendEmail(userId, subject, body);
    }

    /**
     * Send payment receipt email
     */
    public void sendPaymentReceiptEmail(Long userId, String transactionId, String amount) {
        String subject = "Payment Receipt - Transaction #" + transactionId;
        String body = buildPaymentReceiptEmail(transactionId, amount);
        sendEmail(userId, subject, body);
    }

    /**
     * Build order confirmation email body
     */
    private String buildOrderConfirmationEmail(String orderId, String orderDetails) {
        return "Dear Customer,\n\n" +
                "Thank you for your order!\n\n" +
                "Order ID: " + orderId + "\n" +
                "Order Details:\n" + orderDetails + "\n\n" +
                "We will notify you when your order is shipped.\n\n" +
                "Best regards,\n" +
                "E-Commerce Team";
    }

    /**
     * Build payment receipt email body
     */
    private String buildPaymentReceiptEmail(String transactionId, String amount) {
        return "Dear Customer,\n\n" +
                "Thank you for your payment!\n\n" +
                "Transaction ID: " + transactionId + "\n" +
                "Amount: " + amount + "\n\n" +
                "Your payment has been processed successfully.\n\n" +
                "Best regards,\n" +
                "E-Commerce Team";
    }

    /**
     * Get user email - TODO: Call user-service to fetch actual email
     * For now, mock implementation
     */
    private String getUserEmail(Long userId) {
        // TODO: Call UserService via Feign to get user email
        return "user" + userId + "@example.com";
    }
}
