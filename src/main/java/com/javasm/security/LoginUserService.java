package com.javasm.security;

import com.javasm.entity.Function;
import com.javasm.entity.User;
import com.javasm.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wilson
 * @date: 2022-07-25 10:55
 * @version: 1.0
 */
@Component
public class LoginUserService implements UserDetailsService {
    @Resource
    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUserName(username);
        if (user==null)
            throw new UsernameNotFoundException("没有此用户");
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
//        setRoles(loginUser);
        loginUser.setRoles(userService.getFunctions(user.getId()));
        return loginUser;
    }

//    private void setRoles(LoginUser loginUser) {
//        List<String> roles = new ArrayList<>();
//        if ("admin".equals(loginUser.getUsername())){
//            roles.add("admin");
//        }
//        if ("user".equals(loginUser.getUsername())){
//            roles.add("user");
//        }
//        loginUser.setRoles(roles);
//    }

}
