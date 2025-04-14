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
    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    @GetMapping("/{managerId}")
    public Optional<Manager> getManagerById(@PathVariable Long managerId) {
        return managerRepository.findById(managerId);
    }
    @PostMapping
    public Manager createManager(@RequestBody Manager user) {
        return managerRepository.save(user);
    }

    @DeleteMapping("/{managerId}")
    public void deleteManagerById(@PathVariable Long managerId) {managerRepository.deleteById(managerId);}
}

