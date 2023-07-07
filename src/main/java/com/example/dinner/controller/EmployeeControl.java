package com.example.dinner.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dinner.common.R;
import com.example.dinner.entity.Employee;
import com.example.dinner.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeControl {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.eq(Employee::getUsername, employee.getUsername());

        Employee one = employeeService.getOne(qw);
        if (one == null) {
            return R.error("登录失败");
        }
        if (!one.getPassword().equals(password)) {
            return R.error("登录失败");

        }
        if (one.getStatus() == 0) {
            return R.error("封号了");
        }
        request.getSession().setAttribute("employee", one.getId());
        return R.success(one);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> addEmployee(HttpServletRequest request, @RequestBody Employee employee) {

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        Long id = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);

        employeeService.save(employee);
        return R.success("新增员工成功");

    }

    @GetMapping("/page")
    public R<Page> getByPage(int page, int pageSize, String name) {
        Page<Employee> employeePage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> lwq = new LambdaQueryWrapper<>();
        //name模糊匹配
        lwq.like(!StringUtils.isEmpty(name), Employee::getName, name);
        lwq.orderByDesc(Employee::getUpdateTime);
        employeeService.page(employeePage, lwq);
        return R.success(employeePage);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {

        Long empId = (Long) request.getSession().getAttribute("employee");


//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){

        log.info("6666666"+id);
        Employee employee = employeeService.getById(id);

        return R.success(employee);

    }


}
