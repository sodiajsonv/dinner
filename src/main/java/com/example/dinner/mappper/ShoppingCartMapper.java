package com.example.dinner.mappper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dinner.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
