package org.example.homeservice_platform.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价DTO（包含用户名）
 * @author system
 */
@Data
public class RatingDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 评价ID
     */
    private Long id;
    
    /**
     * 对应订单ID
     */
    private Long orderId;
    
    /**
     * 评价人ID
     */
    private Long raterId;
    
    /**
     * 评价人用户名
     */
    private String raterUsername;
    
    /**
     * 被评价人ID
     */
    private Long rateeId;
    
    /**
     * 被评价人用户名
     */
    private String rateeUsername;
    
    /**
     * 评分 1~5
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String comment;
    
    /**
     * 评价时间
     */
    private LocalDateTime createdAt;
}
