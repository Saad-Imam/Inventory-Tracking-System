package com.bazaar.inventory_system.repository;

import com.bazaar.inventory_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}