package com.example.dinner.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import com.example.dinner.common.BaseContext;
import com.example.dinner.common.R;
import com.example.dinner.entity.AddressBook;
import com.example.dinner.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("user");
        addressBook.setUserId(userId);
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook,HttpServletRequest request) {
        log.info("addressBook:{}", addressBook);
        Long userId = (Long) request.getSession().getAttribute("user");
        LambdaUpdateWrapper<AddressBook> lqw=new LambdaUpdateWrapper<>();
        lqw.eq(AddressBook::getUserId,userId);
        lqw.set(AddressBook::getIsDefault,0);
        addressBookService.update(lqw);

        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<AddressBook> getDefault(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("user");
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId);
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook,HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("user");
        addressBook.setUserId(userId);
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }

    /**
     * 修改之后保存地址
     */
    @PutMapping
    public R<String> saveAfterUpdate(@RequestBody AddressBook addressBook){
        log.info(String.valueOf(addressBook));
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }
    /**
     * 删除地址
     */
    @DeleteMapping
    public R<String> dalete(@RequestParam("ids") Long id){
        addressBookService.removeById(id);
        return R.success("删除成功");
    }
}
