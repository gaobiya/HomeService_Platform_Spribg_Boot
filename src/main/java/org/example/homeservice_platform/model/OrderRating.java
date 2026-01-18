package org.example.homeservice_platform.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单评价实体类
 * @author system
 */
@Data
@TableName("order_rating")
public class OrderRating {
    
    /**
     * 评价ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 对应订单ID
     */
    private Long orderId;
    
    /**
     * 评价人ID（客户或服务员）
     */
    private Long raterId;
    
    /**
     * 被评价人ID
     */
    private Long rateeId;
    
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
