package org.example.homeservice_platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录DTO
 * @author system
 */
@Data
public class UserLoginDTO {
    
    /**
     * 用户角色：customer-客户, worker-服务员
     */
    @NotBlank(message = "角色不能为空")
    private String role;
    
    /**
     * 用户名或手机号
     */
    @NotBlank(message = "用户名或手机号不能为空")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
