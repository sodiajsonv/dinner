package com.example.dinner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dinner.entity.Category;

public interface CategoryService extends IService<Category> {

    void remove(Long id);

    String[] getNameByType(int type);
}
