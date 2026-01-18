package org.example.homeservice_platform.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 服务员日程实体类
 * @author system
 */
@Data
@TableName("worker_schedule")
public class WorkerSchedule {
    
    /**
     * 日程ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 服务员ID
     */
    private Long workerId;
    
    /**
     * 可服务开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 可服务结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
