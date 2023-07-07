package com.example.dinner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dinner.entity.DishFlavor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface DishFlavorService extends IService<DishFlavor> {
}
