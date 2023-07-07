package com.example.dinner.dto;

import com.example.dinner.entity.SetMeal;
import com.example.dinner.entity.SetMealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetMealDto extends SetMeal {

    private List<SetMealDish> setmealDishes;

    private String categoryName;
}
