package com.anurag.cs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anurag.cs.entity.Company;
import com.anurag.cs.service.CompanyService;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    @Autowired
    private CompanyService service;

    @PostMapping
    public Company create(@RequestBody Company company) {
        return service.create(company);
    }

    @GetMapping
    public List<Company> getAll() {
        return service.getAll();
    }
}
