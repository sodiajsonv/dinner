package com.example.dinner.mappper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dinner.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("select name from category where type=#{type}")
    String[] selectNameByType(int type);
}
