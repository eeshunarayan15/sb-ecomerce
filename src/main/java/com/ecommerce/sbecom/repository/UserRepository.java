package com.ecommerce.sbecom.repository;

import com.ecommerce.sbecom.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // UserRepository.java
    @EntityGraph(attributePaths = {"roles"}) // Sirf roles ko fetch karega
    Optional<User> findByEmail(String email);
//    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByUsernameAndPassword(String username, String password);
    boolean existsByUsernameAndEmail(String username, String email);
    boolean existsByUsernameAndEmailAndPassword(String username, String email, String password);
}