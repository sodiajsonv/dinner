package com.example.dinner.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dinner.entity.Employee;
import com.example.dinner.mappper.EmployeeMapper;
import com.example.dinner.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{
}
