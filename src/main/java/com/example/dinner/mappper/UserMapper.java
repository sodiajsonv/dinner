package com.example.dinner.mappper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dinner.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
