package com.anurag.cs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anurag.cs.entity.Department;
import com.anurag.cs.service.DepartmentService;

@RestController
@RequestMapping("/departments") // localhost:8080/department
public class DepartmentController {
      @Autowired
      private DepartmentService service;

      @PostMapping
      public Department createDept(@RequestBody Department dept) {
            return service.createDept(dept);
      }

      @GetMapping
      public List<Department> getAll() {
            return service.getAll();
      }
}
