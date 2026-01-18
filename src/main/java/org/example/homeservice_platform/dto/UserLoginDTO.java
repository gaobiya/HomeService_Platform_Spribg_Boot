package org.example.homeservice_platform.dto;

import lombok.Data;

/**
 * 用户登录DTO
 * @author system
 */
@Data
public class UserLoginDTO {
    
    /**
     * 微信登录临时code（小程序使用）
     */
    private String code;
    
    /**
     * 用户角色：customer-客户, worker-服务员
     */
    private String role;
    
    /**
     * 用户名（派单员登录使用）
     */
    private String username;
    
    /**
     * 密码（派单员登录使用）
     */
    private String password;
}
