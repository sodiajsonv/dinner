package com.example.dinner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dinner.entity.ShoppingCart;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ShoppingCartService extends IService<ShoppingCart> {
}
