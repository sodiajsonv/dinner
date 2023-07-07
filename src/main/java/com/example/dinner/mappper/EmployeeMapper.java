package com.example.dinner.mappper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dinner.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
