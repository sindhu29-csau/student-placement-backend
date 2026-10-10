package com.anurag.cs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anurag.cs.entity.Department;
import com.anurag.cs.repository.DepartmentRepository;

@Service
public class DepartmentService {
      @Autowired
      private DepartmentRepository repo;

      public Department createDept(Department dept) {
            return repo.save(dept);
      }

      public List<Department> getAll() {
            return repo.findAll();
      }
}
