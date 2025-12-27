package com.javasm.controller;

import com.javasm.entity.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wilson
 * @date: 2022-07-26 19:34
 * @version: 1.0
 */
@RestController
public class Auth2Controller {
    @GetMapping("/hello")
    // 具有 admin 角色的人才能访问的接口
    @Secured({"ROLE_admin"})
    public String hello() {
        return "hello";
    }

    @GetMapping("/admin/hello")
    // 具有 admin.hello admin.a 权限的人才能访问的接口
    @PreAuthorize("hasAnyAuthority('admin.hello','admin.a')")
    public String admin() {
        return "admin";
    }

    @GetMapping("/user/hello")
    //对集合类型的参数进行过滤 只有username是user的数据才可进入方法
    @PreFilter("filterObject.username == 'user'")
    //对集合类型的返回值进行过滤 只有username是user的数据才可返回
    @PostFilter("filterObject.username == 'user'")
    public List<User> user(@RequestBody List<User> list) {
        for (User user : list) {
            System.out.println(user.getUsername());
        }
        return list;
    }
}
