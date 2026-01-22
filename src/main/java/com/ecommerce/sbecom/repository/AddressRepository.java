package com.ecommerce.sbecom.repository;

import com.ecommerce.sbecom.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findAllByUserId(UUID userId);
    @Modifying
    @Transactional
    @Query("DELETE FROM Address a WHERE a.id = :addressId AND a.user.id = :userId")
    int deleteByIdAndUserId(@Param("addressId") UUID addressId,
                            @Param("userId") UUID userId);
}