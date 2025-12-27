package com.javasm.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.javasm.security.LoginUser;
import com.javasm.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

/**
 * @author: wilson
 * @date: 2022-07-25 10:31
 * @version: 1.0
 */
@RestController
public class SecController {
    @Resource
    private UserService userService;
    @GetMapping("/sec")
    public String sec(){
        //获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser)authentication.getPrincipal();
        return JSONObject.toJSONString(loginUser);
    }
    @GetMapping("/modify")
    public String modify(@RequestParam String username,@RequestParam String password){
        userService.updatePassword(username,password);
        return "success";
    }
    //用户名密码登录生成验证码
    @GetMapping("/code")
    public void getVerifyCode(HttpServletResponse resp, HttpSession session) throws IOException {
        //验证码配置
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","150");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        Config config = new Config(properties);
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(config);
        //生成验证码
        String text = kaptcha.createText();
        //放到session中
        session.setAttribute("verify_code",text);
        //返回给前端
        resp.setContentType("image/jpeg");
        BufferedImage image = kaptcha.createImage(text);
        try (ServletOutputStream out = resp.getOutputStream()){
            ImageIO.write(image,"jpg",out);
        }
    }
    //手机号登录生成验证码
    @GetMapping("/phone/code")
    public void phoneCode(HttpServletResponse resp, HttpSession session) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "150");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        Config config = new Config(properties);
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(config);
        final String code = kaptcha.createText();
        session.setAttribute("phoneNum",code);
        resp.setContentType("image/jpeg");
        BufferedImage image = kaptcha.createImage(code);
        try (ServletOutputStream out = resp.getOutputStream()){
            ImageIO.write(image,"jpg",out);
        }
    }
}
