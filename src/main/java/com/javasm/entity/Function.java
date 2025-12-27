package com.javasm.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.*;

/**
 * (Function)表实体类
 *
 * @author admin
 * @since 2022-07-26 21:14:46
 */
@SuppressWarnings("serial")
@TableName("function")
public class Function extends Model<Function> {
        
    @TableId
    private Integer id;
    
    private String functionName;
    
    private String functionCode;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
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
