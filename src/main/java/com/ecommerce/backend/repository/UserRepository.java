package com.ecommerce.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecommerce.backend.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByEmail(String email);
   // Optional<User> findByEmail(String email);

   
}
