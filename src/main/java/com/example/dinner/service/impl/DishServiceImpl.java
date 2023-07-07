package com.example.dinner.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dinner.common.CustomException;
import com.example.dinner.dto.DishDto;
import com.example.dinner.entity.Category;
import com.example.dinner.entity.Dish;
import com.example.dinner.entity.DishFlavor;
import com.example.dinner.entity.SetMealDish;
import com.example.dinner.mappper.DishMapper;
import com.example.dinner.service.CategoryService;
import com.example.dinner.service.DishFlavorService;
import com.example.dinner.service.DishService;
import com.example.dinner.service.SetMealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetMealDishService setMealDishService;

    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public Page<DishDto> getPage(int page, int pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(!StringUtils.isEmpty(name), Dish::getName, name);
        lqw.orderByAsc(Dish::getPrice);

        dishService.page(dishPage, lqw);

        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        List<Dish> dishList = dishPage.getRecords();

        List<DishDto> dishDtoList = dishList.stream().map((res) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(res, dishDto);
            Long categoryId = res.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category!=null){
                String name1 = category.getName();
                dishDto.setCategoryName(name1);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);
        return dishDtoPage;
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto=new DishDto();
        Dish dish = dishService.getById(id);
        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> lqw=new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavor = dishFlavorService.list(lqw);
        dishDto.setFlavors(flavor);

        return dishDto;
    }

    @Override
    public void updateByIdWithFlavor(DishDto dishDto) {

        dishService.updateById(dishDto);
        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        LambdaQueryWrapper<DishFlavor> lqw=new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lqw);

        flavors=flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public void updateStatus(int status, Long[] ids) {
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.saveOrUpdate(dish);
        }
    }

    /*
    * 删除急批量删除
    * */
    @Override
    public void remove(List<Long> ids) {
        LambdaQueryWrapper<Dish> lqw1=new LambdaQueryWrapper<>();
        lqw1.in(Dish::getId,ids);
        lqw1.eq(Dish::getStatus,1);
        int count1 = dishService.count(lqw1);
        if (count1>0){
            throw new CustomException("请先确定菜品状态为停售，再进行删除");
        }
        LambdaQueryWrapper<SetMealDish> lqw2=new LambdaQueryWrapper<>();
        lqw2.in(SetMealDish::getDishId,ids);
        int count2 = setMealDishService.count(lqw2);
        if (count2>0){
            throw new CustomException("某一套餐包含此菜品，不能删除");
        }
        dishService.removeByIds(ids);
    }
}
