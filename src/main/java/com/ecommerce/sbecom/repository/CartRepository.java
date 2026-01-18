package com.ecommerce.sbecom.repository;

import com.ecommerce.sbecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
//    @Query("SELECT c FROM Cart c WHERE c.user.email = ?1")
@Query("SELECT c FROM Cart c JOIN FETCH c.user WHERE c.user.email = ?1")
Cart findCartByUserEmail(String email);
//    Cart findCartByUserEmail(String email);


}