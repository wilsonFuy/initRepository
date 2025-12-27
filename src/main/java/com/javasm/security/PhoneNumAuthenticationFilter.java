package com.javasm.security;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author: wilson
 * @date: 2022-07-26 16:00
 * @version: 1.0
 */
public class PhoneNumAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/phone/login", "POST");
    private String phoneParameter = "phone";
    private String codeParameter = "code";

    public PhoneNumAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }
    public PhoneNumAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("请求方式有误: " + request.getMethod());
        }
        if (!request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)){
            throw new AuthenticationServiceException("参数不是json");
        }
        String phone = null;
        String code = null;
        try {
            Map<String,String> map = JSONObject.parseObject(request.getInputStream(), Map.class);
            phone = map.get("phone");
            code = map.get("code");
        } catch (IOException e) {
            throw new AuthenticationServiceException("参数不对");
        }
//        final String phoneNum = (String)request.getSession().getAttribute("phoneNum");
//        if (code == null || !code.equals(phoneNum)){
//            throw new AuthenticationServiceException("验证码错误");
//        }
        phone = phone != null ? phone : "";
        phone = phone.trim();
        code = code != null ? code : "";
        PhoneNumAuthenticationToken authRequest = new PhoneNumAuthenticationToken(phone, code);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, PhoneNumAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}
