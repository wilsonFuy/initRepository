package com.javasm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author: wilson
 * @date: 2022-07-26 11:05
 * @version: 1.0
 */
@Controller
public class IndexController {
    @PostMapping("/index")
    public String index(){
        System.out.println("index");
        return "redirect:/index.html";
    }
    @PostMapping("/fail")
    public String fail(){
        System.out.println("fail");
        return "redirect:/fail.html";
    }
}
