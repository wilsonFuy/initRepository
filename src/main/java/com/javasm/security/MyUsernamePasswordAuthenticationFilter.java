package com.javasm.security;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author: wilson
 * @date: 2022-07-26 14:37
 * @version: 1.0
 */
//默认是从 UsernamePasswordAuthenticationFilter 的 attemptAuthentication 方法中获取 表单 中的参数
//新建一个类，继承 UsernamePasswordAuthenticationFilter，复写 attemptAuthentication 方法，从 json 中获取参数
public class MyUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")){
            throw new AuthenticationServiceException("请求方式有误: " + request.getMethod());
        }
        if (!request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)){
            throw new AuthenticationServiceException("参数不是json");
        }
        String username = null;
        String password = null;
//        String code = null;
        try {
            Map<String,String> map = JSONObject.parseObject(request.getInputStream(),Map.class);
            username = map.get("username");
            password = map.get("password");
//            code = map.get("code");
        } catch (IOException e) {
            throw new AuthenticationServiceException("参数不对");
        }
        //校验验证码
//        final String verifyCode = (String)request.getSession().getAttribute("verify_code");
//        if (code == null || !code.equals(verifyCode)){
//            throw new AuthenticationServiceException("验证码错误");
//        }
        username = username != null ? username : "";
        username = username.trim();
        password = password != null ? password : "";
        // 封装用户名、密码，下面的 authenticate 方法会从中拿到 用户名， 调用我们的 LoginUserService 获取用户，然后比较密码
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        //设置ip、sessionId信息
        this.setDetails(request, authRequest);
        // authenticate 方法中封装了具体的密码认证逻辑
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
