package com.example.dinner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dinner.entity.SetMealDish;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SetMealDishService extends IService<SetMealDish> {
}
