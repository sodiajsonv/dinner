package com.example.dinner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dinner.common.CustomException;
import com.example.dinner.dto.SetMealDto;
import com.example.dinner.entity.Category;
import com.example.dinner.entity.SetMeal;
import com.example.dinner.entity.SetMealDish;
import com.example.dinner.mappper.SetMealMapper;
import com.example.dinner.service.CategoryService;
import com.example.dinner.service.SetMealDishService;
import com.example.dinner.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements SetMealService {
    @Autowired
    private SetMealDishService setMealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetMealService setMealService;

    @Override
    public void saveMealWithDish(SetMealDto setMealDto) {
        this.save(setMealDto);

        //在套餐是食品中插入套餐号
        Long id = setMealDto.getId();
        List<SetMealDish> setMealDishes = setMealDto.getSetmealDishes();
        setMealDishes.stream().map((res)->{
            res.setSetmealId(id);
            return res;
        }).collect(Collectors.toList());

        setMealDishService.saveBatch(setMealDishes);
    }

    @Override
    public Page<SetMealDto> getPage(int page, int pageSize, String name) {
        Page<SetMeal> page1=new Page<>(page,pageSize);
        Page<SetMealDto> dtoPage=new Page<>();

        LambdaQueryWrapper<SetMeal> lqw=new LambdaQueryWrapper<>();
        lqw.like(!StringUtils.isEmpty(name),SetMeal::getName,name);
        lqw.orderByAsc(SetMeal::getPrice).orderByDesc(SetMeal::getUpdateTime);
        this.page(page1,lqw);

        BeanUtils.copyProperties(page1,dtoPage,"records");

        List<SetMeal> records = page1.getRecords();
        List<SetMealDto> list=records.stream().map((res)->{
            SetMealDto setMealDto = new SetMealDto();

            BeanUtils.copyProperties(res,setMealDto);

            Long id = res.getCategoryId();
            Category category = categoryService.getById(id);
            if(category!=null){
                String categoryName = category.getName();
                setMealDto.setCategoryName(categoryName);
            }
            return setMealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return dtoPage;
    }

    @Override
    public void remove(List<Long> ids) {
        LambdaQueryWrapper<SetMeal> lqw=new LambdaQueryWrapper<>();
        lqw.in(SetMeal::getId,ids);
        lqw.eq(SetMeal::getStatus,1);
        int count = setMealService.count(lqw);
        if (count>0){
            throw new CustomException("请先确定套餐状态为停售，再进行删除");
        }
        setMealService.removeByIds(ids);
//        删除关系表中数据
        LambdaQueryWrapper<SetMealDish> lqw2=new LambdaQueryWrapper();
        lqw2.in(SetMealDish::getSetmealId,ids);
        setMealDishService.remove(lqw2);
    }
}
