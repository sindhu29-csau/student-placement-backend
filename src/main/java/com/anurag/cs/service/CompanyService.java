package com.anurag.cs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anurag.cs.entity.Company;
import com.anurag.cs.repository.CompanyRepository;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository repo;

    public Company create(Company company) {
        return repo.save(company);
    }

    public List<Company> getAll() {
        return repo.findAll();
    }
}
