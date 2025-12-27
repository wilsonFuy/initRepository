package com.javasm.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.*;

/**
 * (RoleFunction)表实体类
 *
 * @author admin
 * @since 2022-07-26 21:14:46
 */
@SuppressWarnings("serial")
@TableName("role_function")
public class RoleFunction extends Model<RoleFunction> {
        
    @TableId
    private Integer id;
    
    private String roleCode;
    
    private String functionCode;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
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
