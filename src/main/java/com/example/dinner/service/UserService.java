package com.example.dinner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dinner.entity.User;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService extends IService<User> {
}
