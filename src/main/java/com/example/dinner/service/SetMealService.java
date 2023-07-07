package com.example.dinner.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dinner.dto.SetMealDto;
import com.example.dinner.entity.SetMeal;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface SetMealService extends IService<SetMeal> {
    /*
    * 保存套餐和套餐对应的菜品
    * */
    void saveMealWithDish(SetMealDto setMealDto);

    /*
    * 分页查询
    * */
    Page<SetMealDto> getPage(int page, int pageSize, String name);
    /*
    * 删除套餐及关联表数据
    * */
    void remove(List<Long> ids);
}
