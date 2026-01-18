package org.example.homeservice_platform.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 服务员服务类型实体类
 * @author system
 */
@Data
@TableName("worker_service_type")
public class WorkerServiceType {
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 服务员ID
     */
    private Long workerId;
    
    /**
     * 服务类型（cleaning-保洁, repair-维修, cooking-做饭, babysitting-育儿）
     */
    private String serviceType;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
