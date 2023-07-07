package com.example.dinner.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dinner.common.R;
import com.example.dinner.entity.User;
import com.example.dinner.service.UserService;
import com.example.dinner.utils.SMSUtils;
import com.example.dinner.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送短信注册
     *
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> getMsg(@RequestBody User user, HttpServletRequest request) {
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode4String(6);
            log.info("手机号" + phone + "的验证码为：" + code);
//            发送短信，暂时没钱，发不起
//            SMSUtils.sendMessage();
//            session中保存验证码
//            request.getSession().setAttribute(phone,code);
            ValueOperations valueOperations = redisTemplate.opsForValue();
            valueOperations.set(phone, code, 5, TimeUnit.MINUTES);
        }
        return R.success("手机验证码发送回成功");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest request) {
        log.info(map.toString());
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
//        session中获取验证码
//        String sessionCode = request.getSession().getAttribute(phone).toString();
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String sessionCode = (String) valueOperations.get(phone);
        if (sessionCode != null && sessionCode.equals(code)) {
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();

            lqw.eq(User::getPhone, phone);
            User user = userService.getOne(lqw);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            request.getSession().setAttribute("user", user.getId());

            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("验证失败");
    }


    /**
     * 退出登录
     *
     * @return {@link R}<{@link String}>
     */
    @PostMapping("loginout")
    public R<String> loginout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("出退成功");
    }
}

