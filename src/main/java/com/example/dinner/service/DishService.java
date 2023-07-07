package com.example.dinner.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dinner.dto.DishDto;
import com.example.dinner.entity.Dish;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    Page<DishDto> getPage(int page, int pageSize, String name);

    DishDto getByIdWithFlavor(Long id);

    void updateByIdWithFlavor(DishDto dishDto);

    void updateStatus(int status, Long[] ids);

    /*
     * 删除急批量删除
     * */
    void remove(List<Long> ids);
}
