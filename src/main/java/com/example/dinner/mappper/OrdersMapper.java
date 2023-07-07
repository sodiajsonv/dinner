package com.example.dinner.mappper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dinner.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
