package com.example.dinner.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dinner.common.R;
import com.example.dinner.entity.Orders;
import com.example.dinner.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 下单
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders, HttpServletRequest request){
        ordersService.submit(orders,request);
        return R.success("下单成功");
    }

    /**
     * 分页查找
     */
    @GetMapping("/page")
    public R<Page> getByPage(int page,int pageSize,Long number){
        Page<Orders> ordersPage=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> lqw=new LambdaQueryWrapper<>();
        lqw.like(!StringUtils.isEmpty(number),Orders::getNumber,number);

        ordersService.page(ordersPage,lqw);

        return R.success(ordersPage);
    }

    @GetMapping("/userPage")
    public R<Page> getByPage(int page,int pageSize,HttpServletRequest request){
        Page<Orders> ordersPage=new Page<>(page,pageSize);
        Long userId = (Long) request.getSession().getAttribute("user");
        LambdaQueryWrapper<Orders> lqw=new LambdaQueryWrapper<>();
        lqw.eq(Orders::getUserId,userId);
        ordersService.page(ordersPage,lqw);

        return R.success(ordersPage);
    }
}
