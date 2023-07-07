package com.example.dinner.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dinner.entity.SetMealDish;
import com.example.dinner.mappper.SetMealDishMapper;
import com.example.dinner.service.SetMealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper, SetMealDish> implements SetMealDishService {
}
