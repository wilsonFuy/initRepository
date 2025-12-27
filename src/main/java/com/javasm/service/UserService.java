package com.javasm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.javasm.entity.RoleFunction;
import com.javasm.entity.User;
import com.javasm.entity.UserRole;
import com.javasm.mapper.RoleFunctionDao;
import com.javasm.mapper.UserMapper;
import com.javasm.mapper.UserRoleDao;
import com.javasm.security.LoginUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author: wilson
 * @date: 2022-07-25 10:53
 * @version: 1.0
 */
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleDao userRoleDao;
    @Resource
    private RoleFunctionDao roleFunctionDao;

    public User getByUserName(String username){
        Map<String,Object> map = new HashMap<>();
        map.put("username",username);
        List<User> users = userMapper.selectByMap(map);
        if (CollectionUtils.isEmpty(users)){
            return null;
        }
        return users.get(0);
    }

    @Resource
    private PasswordEncoder passwordEncoder;

    public void updatePassword(String username,String password){
        User user = getByUserName(username);
//        BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
//        user.setPassword("{bcrypt}"+bc.encode(password));
        user.setPassword(passwordEncoder.encode(password));
        userMapper.updateById(user);
    }

    public User getByPhone(String phone) {
        Map<String,Object> map = new HashMap<>();
        map.put("phone",phone);
        List<User> users = userMapper.selectByMap(map);
        if (CollectionUtils.isEmpty(users)){
            return null;
        }
        return users.get(0);
    }

    //更新当前用户信息
    public static void setLoginUser(UserDetails userDetails){
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(),userDetails.getAuthorities())
        );
    }

    /**
     * 自定义权限认证
     * 验证当前登录用户有没有指定功能权限
     * @param functionCode
     * @return
     */
    public boolean check(String functionCode){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final LoginUser currentLogin = (LoginUser) authentication.getPrincipal();
        return currentLogin.getRoles().contains(functionCode);
    }

    /**
     * 根据 userId 获取对应的权限
     * @param userId
     * @return
     */
    public List<String> getFunctions(Integer userId){
        Map<String,Object> map = new HashMap<>();
        map.put("user_id",userId);
        //查询用户有哪些角色
        List<UserRole> userRoles = userRoleDao.selectByMap(map);
        final List<String> collect = userRoles.stream().filter(userRole -> StringUtils.hasText(userRole.getRoleCode()))
                .map(UserRole::getRoleCode).collect(Collectors.toList());
        //根据角色查询对应的功能权限
        final List<RoleFunction> functions = roleFunctionDao.selectList(new QueryWrapper<RoleFunction>().in("role_code", collect));
        //最终返回用户有哪些功能权限
        return functions.stream().filter(roleFunction -> StringUtils.hasText(roleFunction.getRoleCode()))
                .map(RoleFunction::getFunctionCode).collect(Collectors.toList());
    }
}
