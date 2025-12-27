package com.javasm.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.*;

/**
 * (UserRole)表实体类
 *
 * @author admin
 * @since 2022-07-26 21:14:46
 */
@SuppressWarnings("serial")
@TableName("user_role")
public class UserRole extends Model<UserRole> {
        
    @TableId
    private Integer id;
    
    private Integer userId;
    
    private String roleCode;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
    }
