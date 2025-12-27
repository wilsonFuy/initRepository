package com.javasm.security;

import com.javasm.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author: wilson
 * @date: 2022-07-25 10:57
 * @version: 1.0
 */
public class LoginUser implements UserDetails {

    private User user;

    private List<String> roles;

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 返回这个用户的权限列表
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(roles)){
            return null;
        }
        List<GrantedAuthority> list = new ArrayList<>(roles.size());
        for (String role : roles) {
//            list.add(new SimpleGrantedAuthority("ROLE_"+role));
            list.add(new SimpleGrantedAuthority(role));
        }
//        if ("user".equals(getUsername())){
//            list.add(new SimpleGrantedAuthority("admin.hello"));
//        }
        return list;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 用户是否过期，可以根据用户的信息判断是否过期
     * @return false 表示用户过期，不可登陆；true 可以登陆
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 用户是否锁定，可以根据用户的信息判断是否锁定
     * @return false 用户锁定，不可登陆；true 没有锁定，可以登陆
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 用户密码是否过期
     * @return false 表示过期，不可登陆；true 没有过期，可以登陆
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 用户是否禁用
     * @return false 用户禁用，不可登陆；true 没有禁用，可以登陆
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
