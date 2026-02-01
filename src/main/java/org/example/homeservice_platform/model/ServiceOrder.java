package org.example.homeservice_platform.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务订单实体类
 * @author system
 */
@Data
@TableName("service_order")
public class ServiceOrder {
    
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 服务员ID（派单后才有）
     */
    private Long workerId;
    
    /**
     * 服务类型（如：cleaning-保洁, repair-维修等）
     */
    private String serviceType;
    
    /**
     * 服务地址
     */
    private String address;
    
    /**
     * 服务说明
     */
    private String description;
    
    /**
     * 服务预约时间
     */
    private LocalDateTime serviceTime;
    
    /**
     * 订单状态：PENDING-待审核, APPROVED-已审核待派单, ASSIGNED-已派单待接单, IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消, REJECTED-已驳回
     */
    private String status;
    
    /**
     * 派单时间
     */
    private LocalDateTime assignedTime;
    
    /**
     * 订单金额
     */
    private BigDecimal amount;
    
    /**
     * 是否已支付：0-未支付, 1-已支付
     */
    private Integer paid;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
