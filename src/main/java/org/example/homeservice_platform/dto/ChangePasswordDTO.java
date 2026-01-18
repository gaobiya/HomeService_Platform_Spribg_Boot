package org.example.homeservice_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改密码DTO
 * @author system
 */
@Data
public class ChangePasswordDTO {
    
    @NotBlank(message = "用户ID不能为空")
    private Long userId;
    
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
    private String newPassword;
}
