package com.example.dinner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dinner.entity.AddressBook;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AddressBookService extends IService<AddressBook> {
}
