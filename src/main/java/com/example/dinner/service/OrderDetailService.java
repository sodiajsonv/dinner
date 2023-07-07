package com.example.dinner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dinner.entity.OrderDetail;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface OrderDetailService extends IService<OrderDetail> {
}
