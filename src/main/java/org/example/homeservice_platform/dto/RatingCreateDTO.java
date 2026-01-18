package org.example.homeservice_platform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建评价DTO
 * @author system
 */
@Data
public class RatingCreateDTO {
    
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    @NotNull(message = "评价人ID不能为空")
    private Long raterId;
    
    @NotNull(message = "被评价人ID不能为空")
    private Long rateeId;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    private Integer rating;
    
    private String comment;
}
