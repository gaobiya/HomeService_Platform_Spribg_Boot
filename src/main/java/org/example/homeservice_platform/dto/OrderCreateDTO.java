package org.example.homeservice_platform.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serviceTime;
    
    /**
     * 订单金额
     */
    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于0")
    private BigDecimal amount;
}
