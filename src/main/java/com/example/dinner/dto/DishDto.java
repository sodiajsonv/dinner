package com.example.dinner.dto;


import com.example.dinner.entity.Dish;
import com.example.dinner.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;


@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
