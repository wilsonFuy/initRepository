package com.javasm.security;

import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wilson
 * @date: 2022-07-25 14:32
 * @version: 1.0
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private LoginUserService loginUserService;
    @Resource
    private DataSource dataSource;

    /**
     * remember me 功能是基于token验证的，
     * 这里是通过JdbcTokenRepositoryImpl把token存到persistent_logins表中
     * @return
     */
    @Bean
    public JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl(){
        JdbcTokenRepositoryImpl jt = new JdbcTokenRepositoryImpl();
        jt.setDataSource(dataSource);
        return jt;
    }

    /**
     * 声明一个 PasswordEncoder
     * 在 userservice 中注入使用同时 spring security 自动使用这个解密
     * 这样数据库存储的密码就不需要 "{加密方式}"，这样的前缀
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MyUsernamePasswordAuthenticationFilter myUsernamePasswordAuthenticationFilter() throws Exception {
        MyUsernamePasswordAuthenticationFilter filter = new MyUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSONObject.toJSONString(authentication));
                writer.flush();
                writer.close();
            }
        });
        filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter writer = response.getWriter();
                Map<String,String> map = new HashMap<>();
                map.put("errMsg",exception.getMessage());
                writer.write(JSONObject.toJSONString(map));
                writer.flush();
                writer.close();
            }
        });
        filter.setFilterProcessesUrl("/user/login");
        return filter;
    }

    @Bean
    public PhoneNumAuthenticationFilter phoneNumAuthenticationFilter() throws Exception {
        PhoneNumAuthenticationFilter filter = new PhoneNumAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSONObject.toJSONString(authentication));
                writer.flush();
                writer.close();
            }
        });
        filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter writer = response.getWriter();
                Map<String,String> map = new HashMap<>();
                map.put("errMsg","手机登录失败："+exception.getMessage());
                writer.write(JSONObject.toJSONString(map));
                writer.flush();
                writer.close();
            }
        });
//        filter.setFilterProcessesUrl("/phone/login");
        return filter;
    }

    /**
     * DaoAuthenticationProvider 是默认做账户密码认证的，现在有两种登录方式，手机号和账户密码
     * 如果不在这里声明，账户密码登录不能用
     * @return
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        //对默认的UserDetailsService进行覆盖
        authenticationProvider.setUserDetailsService(loginUserService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Resource
    private PhoneAuthenticationProvider phoneAuthenticationProvider;

    @Bean
    public RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        //让 admin 继承 user 的权限
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return hierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
//                .antMatchers("/admin/**").hasRole("admin")
//                .antMatchers("/user/**").hasRole("user")
//                .antMatchers("/admin/hello").hasAuthority("admin.hello")
                .antMatchers("/code","/phone/code").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();
        //把自定义认证过滤器加到拦截器链中
        http.addFilterAt(myUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(daoAuthenticationProvider());//把账户密码验证加进去
        http.addFilterAfter(phoneNumAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(phoneAuthenticationProvider);//把手机验证码验证逻辑加进去
        //自定义没有权限的返回结果
        http.exceptionHandling().accessDeniedHandler(new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter writer = response.getWriter();
                Map<String,String> map = new HashMap<>();
                map.put("errMsg","没有权限");
                writer.write(JSONObject.toJSONString(map));
                writer.flush();
                writer.close();
            }
        }).authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter writer = response.getWriter();
                Map<String,String> map = new HashMap<>();
                map.put("errMsg","没有登录");
                writer.write(JSONObject.toJSONString(map));
                writer.flush();
                writer.close();
            }
        });
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**","/css/**","/images/**");
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/fail.html").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin().loginPage("/login.html")
//                .loginProcessingUrl("/user/login")
//                .usernameParameter("name")
//                .passwordParameter("pass")
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        response.setContentType("application/json;charset=utf-8");
//                        PrintWriter writer = response.getWriter();
//                        writer.write(JSONObject.toJSONString(authentication));
//                        writer.flush();
//                        writer.close();
//                    }
//                })
//                .failureHandler(new AuthenticationFailureHandler() {
//                    @Override
//                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//                        response.setContentType("application/json;charset=utf-8");
//                        PrintWriter writer = response.getWriter();
//                        Map<String,String> map = new HashMap<>();
//                        map.put("errMsg",exception.getMessage());
//                        writer.write(JSONObject.toJSONString(map));
//                        writer.flush();
//                        writer.close();
//                    }
//                })
////                .defaultSuccessUrl("/index.html",true)
////                .successForwardUrl("/index")
////                .failureUrl("/fail.html")
////                .failureForwardUrl("/fail")
//                .permitAll()
//                .and()
//                .csrf().disable();
//        http.logout().logoutUrl("/user/logout")
////                     .logoutSuccessUrl("/index.html");
//                     .logoutSuccessHandler(new LogoutSuccessHandler() {
//                         @Override
//                         public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                             response.setContentType("application/json;charset=utf-8");
//                             PrintWriter writer = response.getWriter();
//                             writer.write("退出成功");
//                             writer.flush();
//                             writer.close();
//                         }
//                     });
//        http.rememberMe()
//                .tokenRepository(jdbcTokenRepositoryImpl())
//                .userDetailsService(loginUserService);
//        http.exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
//            @Override
//            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//                response.setContentType("application/json;charset=utf-8");
//                PrintWriter writer = response.getWriter();
//                Map<String,String> map = new HashMap<>();
//                map.put("errMsg","尚未登陆，请先登录");
//                writer.write(JSONObject.toJSONString(map));
//                writer.flush();
//                writer.close();
//            }
//        });
//    }
}
