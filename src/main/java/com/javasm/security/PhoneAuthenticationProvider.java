package com.javasm.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: wilson
 * @date: 2022-07-26 16:40
 * @version: 1.0
 */
//用户登录的方式有很多种，每一种都有特定的 Provider 负责处理，DaoAuthenticationProvider 就是负责验证用户名、密码这种方式的登录
//我们的手机号、验证码登录，需要自己创建一个 Provider
//新建 PhoneAuthenticationProvider 实现 AuthenticationProvider 接口，主要实现 authenticate 方法，写我们自己的认证逻辑
@Component
public class PhoneAuthenticationProvider implements AuthenticationProvider {

    @Resource
    private LoginPhoneService loginPhoneService;

    /**
     * 手机号、验证码的认证逻辑
     * @param authentication 其实就是我们封装的 PhoneNumAuthenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        PhoneNumAuthenticationToken token = (PhoneNumAuthenticationToken)authentication;
        final String phone = (String)token.getPrincipal();// 获取手机号
        final String code = (String)token.getCredentials(); // 获取输入的验证码
        // 1. 从 session 中获取验证码
        final HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        final String phoneNum = (String)req.getSession().getAttribute("phoneNum");
        if (!StringUtils.hasText(phoneNum)){
            throw new BadCredentialsException("验证码已过期，请重新发送验证码");
        }
        if (!phoneNum.equals(code)){
            throw new BadCredentialsException("验证码不正确");
        }
        // 2. 根据手机号查询用户信息
        final LoginUser loginUser = (LoginUser) loginPhoneService.loadUserByUsername(phone);
        if (loginUser == null){
            throw new BadCredentialsException("用户不存在，请注册");
        }
        // 3. 把用户封装到 PhoneNumAuthenticationToken 中，
        // 后面就可以使用 SecurityContextHolder.getContext().getAuthentication(); 获取当前登陆用户信息
        PhoneNumAuthenticationToken authenticationToken = new PhoneNumAuthenticationToken(loginUser,code,loginUser.getAuthorities());
        authenticationToken.setDetails(token.getDetails());
        return authenticationToken;
    }

    /**
     * 判断是上面 authenticate 方法的 authentication 参数，是哪种类型
     * Authentication 是个接口，实现类有很多，目前我们最熟悉的就是 PhoneNumAuthenticationToken、UsernamePasswordAuthenticationToken
     * 很明显，我们只支持 PhoneNumAuthenticationToken，因为它封装的是手机号、验证码
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        // 如果参数是 PhoneNumAuthenticationToken 类型，返回true
        return PhoneNumAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
