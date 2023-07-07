package com.example.dinner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dinner.entity.Orders;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Transactional
public interface OrdersService extends IService<Orders> {
    void submit(Orders orders, HttpServletRequest request);
}
