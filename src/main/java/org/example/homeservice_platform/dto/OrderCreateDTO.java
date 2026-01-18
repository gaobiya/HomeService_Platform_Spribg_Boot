package org.example.homeservice_platform.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 创建订单DTO
 * @author system
 */
@Data
public class OrderCreateDTO {
    
    @NotBlank(message = "服务类型不能为空")
    private String serviceType;
    
    @NotBlank(message = "服务地址不能为空")
    private String address;
    
    private String description;
    
    @NotNull(message = "服务时间不能为空")
    private LocalDateTime serviceTime;
}
