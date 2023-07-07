package com.example.dinner.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dinner.common.R;
import com.example.dinner.dto.SetMealDto;
import com.example.dinner.entity.Category;
import com.example.dinner.entity.Dish;
import com.example.dinner.entity.SetMeal;
import com.example.dinner.entity.SetMealDish;
import com.example.dinner.service.DishService;
import com.example.dinner.service.SetMealDishService;
import com.example.dinner.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private SetMealDishService setMealDishService;
    @Autowired
    private DishService dishService;

    /*
     * 保存套餐
     * */
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetMealDto setMealDto) {
        setMealService.saveMealWithDish(setMealDto);
        return R.success("套餐保存成功");
    }

    /*
     * 分页查询
     * */
    @GetMapping("/page")
    public R<Page<SetMealDto>> getByPage(int page, int pageSize, String name) {

        Page<SetMealDto> page1 = setMealService.getPage(page, pageSize, name);

        return R.success(page1);
    }

    /*
     * 删除套餐
     * */
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {
        setMealService.remove(ids);
        return R.success("删除成功");
    }


    /**
     * 更新状态
     *
     * @param status 状态
     * @param ids    id
     * @return {@link R}<{@link String}>
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status, @RequestParam List<Long> ids) {
        log.info(String.valueOf(status));
        LambdaQueryWrapper<SetMeal> lqw = new LambdaQueryWrapper<>();
        lqw.in(SetMeal::getId, ids);

        List<SetMeal> setMeals = setMealService.listByIds(ids);
        setMeals.stream().map((res) -> {
            res.setStatus(status);
            return res;
        }).collect(Collectors.toList());

        setMealService.updateBatchById(setMeals);

        return R.success("修改成功");
    }


    /**
     * 更新套餐
     *
     * @param id id
     * @return {@link R}<{@link String}>
     */
    @GetMapping("/{id}")
    public R<SetMealDto> updateMeal(@PathVariable Long id) {
        log.info(String.valueOf(id));
        SetMealDto setMealDto = new SetMealDto();
        SetMeal setMeal = setMealService.getById(id);
        BeanUtils.copyProperties(setMeal, setMealDto);

        LambdaQueryWrapper<SetMealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetMealDish::getSetmealId, id);
        List<SetMealDish> setMealDishList = setMealDishService.list(lqw);
        setMealDto.setSetmealDishes(setMealDishList);

        return R.success(setMealDto);
    }

    /**
     * 保存更新
     *
     * @param setMealDto 套餐dto
     * @return {@link R}<{@link String}>
     */
    @PutMapping
    public R<String> saveUpdate(@RequestBody SetMealDto setMealDto) {
        setMealService.updateById(setMealDto);

        List<SetMealDish> setmealDishes = setMealDto.getSetmealDishes();

        LambdaQueryWrapper<SetMealDish> lqw = new LambdaQueryWrapper<>();
        Long id = setMealDto.getId();
        lqw.eq(SetMealDish::getSetmealId, id);
        setMealDishService.remove(lqw);

        setmealDishes.stream().map((res) -> {
            res.setSetmealId(id);
            return res;
        }).collect(Collectors.toList());

        setMealDishService.saveBatch(setmealDishes);

        return R.success("更新成功");
    }

    /**
     * 根据套餐展示套餐菜品
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setMeal.categoryId+'_'+#setMeal.status")
    public R<List<SetMeal>> listDish(SetMeal setMeal) {
        LambdaQueryWrapper<SetMeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setMeal.getCategoryId() != null, SetMeal::getCategoryId, setMeal.getCategoryId());
        lqw.eq(setMeal.getStatus() != null, SetMeal::getStatus, setMeal.getStatus());


        List<SetMeal> setMeals = setMealService.list(lqw);
        return R.success(setMeals);

    }

}
