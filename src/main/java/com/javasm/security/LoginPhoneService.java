package com.javasm.security;

import com.javasm.entity.User;
import com.javasm.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: wilson
 * @date: 2022-07-26 16:11
 * @version: 1.0
 */
@Component
public class LoginPhoneService implements UserDetailsService {

    @Resource
    private UserService userService;

    /**
     * 根据手机号查询用户对象
     * @param phone 前端传的手机号
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        // 从数据库查询用户
        User user = userService.getByPhone(phone);
        if (user == null){
            return null;
        }
        // 把用户信息封装到一个 userdetails 对象中，UserDetails是一个接口，LoginUser实现了这个接口
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        return loginUser;
    }
}
