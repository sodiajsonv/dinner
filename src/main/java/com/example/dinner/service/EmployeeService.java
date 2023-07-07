package com.example.dinner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dinner.entity.Employee;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface EmployeeService extends IService<Employee> {
}
