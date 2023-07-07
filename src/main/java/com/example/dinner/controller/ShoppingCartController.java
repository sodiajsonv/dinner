package com.example.dinner.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dinner.common.R;
import com.example.dinner.entity.ShoppingCart;
import com.example.dinner.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpServletRequest request) {
        log.info(String.valueOf(shoppingCart));
        Long userId = (Long) request.getSession().getAttribute("user");
        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);

        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            lqw.eq(ShoppingCart::getDishId, dishId);
        } else {
            lqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(lqw);
        if (one != null) {
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.updateById(one);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("user");

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        lqw.ne(ShoppingCart::getNumber,0);
        lqw.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> shoppingCarts = shoppingCartService.list(lqw);

        return R.success(shoppingCarts);
    }

    /**
     * 清空购物车
     *
     * @param request 请求
     * @return {@link R}<{@link String}>
     */
    @DeleteMapping("/clean")
    public R<String> clean(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(lqw);
        return R.success("清空成功");
    }

    /**
     * 减少数量
     *
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart, HttpServletRequest request) {
        log.info(String.valueOf(shoppingCart));
        Long dishId = shoppingCart.getDishId();
        Long userId = (Long) request.getSession().getAttribute("user");

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        if (dishId!=null){
            lqw.eq(ShoppingCart::getDishId, dishId);
        }else {
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        lqw.eq(ShoppingCart::getUserId,userId);

        ShoppingCart one = shoppingCartService.getOne(lqw);
        Integer number = one.getNumber();
        number-=1;
        one.setNumber(number);

        shoppingCartService.updateById(one);

        return R.success("修改成功");
    }
}
