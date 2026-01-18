package com.ecommerce.sbecom.repository;

import com.ecommerce.sbecom.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    @Query("SELECT ci FROM CartItem ci  WHERE ci.cart.id = ?1 and ci.product.id=?2")
    CartItem findCartItemByProductIdAndCartId(UUID cartId, UUID productId);
}