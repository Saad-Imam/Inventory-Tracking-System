package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.model.Manager;
import com.bazaar.inventory_system.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/managers")
public class ManagerController {

    @Autowired
    private ManagerRepository managerRepository;

    @GetMapping
    public List<Manager> getAllUsers() {
        return managerRepository.findAll();
    }

    @GetMapping("/{managerId}")
    public Optional<Manager> getUserById(@PathVariable Long userId) {
        return managerRepository.findById(userId);
    }

    @PostMapping
    public Manager createUser(@RequestBody Manager user) {
        return managerRepository.save(user);
    }

}

