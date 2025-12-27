package com.javasm.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: wilson
 * @date: 2022-07-26 20:38
 * @version: 1.0
 */
@RestController
public class Auth3Controller {
    @GetMapping("/add")
    //默认权限认证
//    @PreAuthorize("hasAuthority('add')")
    //自定义权限认证
    @PreAuthorize("@userService.check('add')")
    public String add(){
        return  "add 方法";
    }

    @GetMapping("/query")
//    @PreAuthorize("hasAuthority('query')")
    @PreAuthorize("@userService.check('query')")
    public String query(){
        return  "query 方法";
    }

    @GetMapping("/update")
//    @PreAuthorize("hasAuthority('update')")
    @PreAuthorize("@userService.check('update')")
    public String update(){
        return  "update 方法";
    }

    @GetMapping("/del")
//    @PreAuthorize("hasAuthority('del')")
    @PreAuthorize("@userService.check('del')")
    public String del(){
        return  "del 方法";
    }
}
