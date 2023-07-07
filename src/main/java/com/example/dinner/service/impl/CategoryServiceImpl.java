package com.example.dinner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dinner.common.CustomException;
import com.example.dinner.entity.Category;
import com.example.dinner.entity.Dish;
import com.example.dinner.entity.SetMeal;
import com.example.dinner.mappper.CategoryMapper;
import com.example.dinner.service.CategoryService;
import com.example.dinner.service.DishService;
import com.example.dinner.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setMealService;

    @Autowired
    private CategoryMapper categoryMapper;

    /*
    根据ID删除
    * */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(lqw1);
        if (count1 > 0) {
            throw new CustomException("当前分类关联了菜品，不能删除");
        }

        LambdaQueryWrapper<SetMeal> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(SetMeal::getCategoryId, id);
        int count2 = setMealService.count(lqw2);
        if (count2 > 0) {
            throw new CustomException("当前分类关联套餐，不能删除");
        }
        super.removeById(id);
    }

    @Override
    public String[] getNameByType(int type) {
        String[] names= categoryMapper.selectNameByType(type);
        return names;
    }
}
