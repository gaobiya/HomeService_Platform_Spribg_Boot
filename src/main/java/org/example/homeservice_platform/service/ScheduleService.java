package org.example.homeservice_platform.service;

import org.example.homeservice_platform.model.ServiceOrder;
import org.example.homeservice_platform.model.WorkerSchedule;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日程管理服务接口
 * @author system
 */
public interface ScheduleService {
    
    /**
     * 添加可服务时间段
     * @param workerId 服务员ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否成功
     */
    boolean addSchedule(Long workerId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 删除日程
     * @param scheduleId 日程ID
     * @param workerId 服务员ID（用于权限验证）
     * @return 是否成功
     */
    boolean deleteSchedule(Long scheduleId, Long workerId);
    
    /**
     * 获取服务员的所有日程
     * @param workerId 服务员ID
     * @return 日程列表
     */
    List<WorkerSchedule> getWorkerSchedules(Long workerId);
    
    /**
     * 获取服务员已安排的订单（用于查看时间冲突）
     * @param workerId 服务员ID
     * @return 订单列表
     */
    List<ServiceOrder> getWorkerScheduledOrders(Long workerId);
    
    /**
     * 检查时间段是否冲突
     * @param workerId 服务员ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否冲突
     */
    boolean hasScheduleConflict(Long workerId, LocalDateTime startTime, LocalDateTime endTime);
}
