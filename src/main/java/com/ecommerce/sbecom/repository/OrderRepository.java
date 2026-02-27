package com.ecommerce.sbecom.repository;

import com.ecommerce.sbecom.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> getOrderByUserId(UUID userid);
@Query("SELECT COALESCE( SUM(o.totalAmount),0) FROM Order o")
    Double getTotalRevenue();
}