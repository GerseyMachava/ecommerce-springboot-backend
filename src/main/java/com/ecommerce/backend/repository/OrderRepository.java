package com.ecommerce.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.User;

public interface OrderRepository extends JpaRepository<Order, Long>{

   List<Order> findAllByUser(User user);
}
