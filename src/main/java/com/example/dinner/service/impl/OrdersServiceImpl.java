package com.example.dinner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dinner.common.CustomException;
import com.example.dinner.entity.*;
import com.example.dinner.mappper.OrdersMapper;
import com.example.dinner.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    public void submit(Orders orders, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("user");

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        //购物车信息
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(lqw);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车没东西");
        }

        //用户信息
        User user = userService.getById(userId);

        //地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("请先完善地址信息");
        }
        //订单表插入数据
        Long orderId = IdWorker.getId();

        //订单明细表赋值
        AtomicInteger amount=new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((res) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(res.getNumber());
            orderDetail.setDishFlavor(res.getDishFlavor());
            orderDetail.setDishId(res.getDishId());
            orderDetail.setSetmealId(res.getSetmealId());
            orderDetail.setName(res.getName());
            orderDetail.setImage(res.getImage());
            orderDetail.setAmount(res.getAmount());
            amount.addAndGet(res.getAmount().multiply(new BigDecimal(res.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        //订单表赋
//        orders.setId(orderId);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setNumber(orderId.toString());
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        ordersService.save(orders);

        orderDetailService.saveBatch(orderDetails);

        shoppingCartService.remove(lqw);

    }
}
