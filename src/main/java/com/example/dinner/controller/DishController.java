package com.example.dinner.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dinner.common.R;
import com.example.dinner.dto.DishDto;
import com.example.dinner.entity.Category;
import com.example.dinner.entity.Dish;
import com.example.dinner.entity.DishFlavor;
import com.example.dinner.service.CategoryService;
import com.example.dinner.service.DishFlavorService;
import com.example.dinner.service.DishService;
import com.example.dinner.service.SetMealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        //清除缓存
        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }


    @GetMapping("/page")
    public R<Page<DishDto>> getByPage(int page, int pageSize, String name) {
        Page<DishDto> dishDtoPage = dishService.getPage(page, pageSize, name);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> update(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> updateByIdWithFlavor(@RequestBody DishDto dishDto) {
        dishService.updateByIdWithFlavor(dishDto);
        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);
        return R.success("修改成功");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status, Long[] ids) {
        dishService.updateStatus(status, ids);
        Set keys = redisTemplate.keys("dish*");
        redisTemplate.delete(keys);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        dishService.remove(ids);
        Set keys = redisTemplate.keys("dish*");
        redisTemplate.delete(keys);
        return R.success("删除成功");
    }

    //展示不同分类ID的索引菜品
//    @GetMapping("/list")
//    public R<List<Dish>> listByiD(Dish dish){
//        LambdaQueryWrapper<Dish> lqw=new LambdaQueryWrapper<>();
//        lqw.eq(!StringUtils.isEmpty(dish.getCategoryId()),Dish::getCategoryId, dish.getCategoryId());
//        lqw.eq(Dish::getStatus,1);
//
//        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> dishes = dishService.list(lqw);
//        return R.success(dishes);
//
//    }

    @GetMapping("/list")
    public R<List<DishDto>> listByiD(Dish dish) {
        List<DishDto> dishDto=null;
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();

        ValueOperations valueOperations = redisTemplate.opsForValue();
        dishDto = (List<DishDto>) valueOperations.get(key);

        if (dishDto!=null){
            return R.success(dishDto);
        }


        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(!StringUtils.isEmpty(dish.getCategoryId()), Dish::getCategoryId, dish.getCategoryId());
        lqw.eq(Dish::getStatus, 1);

        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishService.list(lqw);

        dishDto=dishes.stream().map((res)->{
            DishDto dishDto1=new DishDto();

            BeanUtils.copyProperties(res,dishDto1);
            Category category = categoryService.getById(res.getCategoryId());

            if (category!=null){
                String categoryName=category.getName();
                dishDto1.setCategoryName(categoryName);
            }

            Long dishId=res.getId();
            LambdaQueryWrapper<DishFlavor> lqw2=new LambdaQueryWrapper<>();
            lqw2.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> flavors = dishFlavorService.list(lqw2);
            dishDto1.setFlavors(flavors);
            return dishDto1;

        }).collect(Collectors.toList());

        valueOperations.set(key,dishDto,60, TimeUnit.MINUTES);

        return R.success(dishDto);

    }
}
