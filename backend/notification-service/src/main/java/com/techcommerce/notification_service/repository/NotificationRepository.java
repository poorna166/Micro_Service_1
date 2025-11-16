package com.techcommerce.notification_service.repository;

import com.techcommerce.notification_service.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a user
     */
    List<Notification> findByUserId(Long userId);
    
    /**
     * Find unread notifications for a user
     */
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    
    /**
     * Find notifications for user with pagination
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * Find notifications by type and user
     */
    List<Notification> findByUserIdAndType(Long userId, String type);
    
    /**
     * Find notifications by order ID
     */
    List<Notification> findByOrderId(Long orderId);
    
    /**
     * Find notifications by payment ID
     */
    List<Notification> findByPaymentId(Long paymentId);
    
    /**
     * Find notifications by status
     */
    @Query("SELECT n FROM Notification n WHERE n.status = :status")
    List<Notification> findByStatus(@Param("status") String status);
}