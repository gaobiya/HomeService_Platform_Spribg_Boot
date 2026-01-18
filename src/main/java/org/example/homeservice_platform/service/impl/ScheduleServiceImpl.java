package org.example.homeservice_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.homeservice_platform.common.BusinessException;
import org.example.homeservice_platform.mapper.ServiceOrderMapper;
import org.example.homeservice_platform.mapper.UserInfoMapper;
import org.example.homeservice_platform.mapper.WorkerScheduleMapper;
import org.example.homeservice_platform.model.ServiceOrder;
import org.example.homeservice_platform.model.UserInfo;
import org.example.homeservice_platform.model.WorkerSchedule;
import org.example.homeservice_platform.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日程管理服务实现类
 * @author system
 */
@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {
    
    @Autowired
    private WorkerScheduleMapper scheduleMapper;
    
    @Autowired
    private UserInfoMapper userInfoMapper;
    
    @Autowired
    private ServiceOrderMapper orderMapper;
    
    @Override
    @Transactional
    public boolean addSchedule(Long workerId, LocalDateTime startTime, LocalDateTime endTime) {
        // 验证服务员是否存在且角色正确
        UserInfo worker = userInfoMapper.selectById(workerId);
        if (worker == null || !"worker".equals(worker.getRole())) {
            throw new BusinessException(400, "服务员不存在");
        }
        
        // 验证时间段有效性
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new BusinessException(400, "结束时间必须晚于开始时间");
        }
        
        // 检查时间冲突
        if (hasScheduleConflict(workerId, startTime, endTime)) {
            throw new BusinessException(400, "该时间段与已有安排冲突");
        }
        
        // 插入日程
        WorkerSchedule schedule = new WorkerSchedule();
        schedule.setWorkerId(workerId);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setUpdatedAt(LocalDateTime.now());
        
        return scheduleMapper.insert(schedule) > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteSchedule(Long scheduleId, Long workerId) {
        // 验证日程是否存在且属于该服务员
        WorkerSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException(404, "日程不存在");
        }
        
        if (!schedule.getWorkerId().equals(workerId)) {
            throw new BusinessException(403, "无权删除此日程");
        }
        
        return scheduleMapper.deleteById(scheduleId) > 0;
    }
    
    @Override
    public List<WorkerSchedule> getWorkerSchedules(Long workerId) {
        LambdaQueryWrapper<WorkerSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkerSchedule::getWorkerId, workerId)
               .orderByAsc(WorkerSchedule::getStartTime);
        return scheduleMapper.selectList(wrapper);
    }
    
    @Override
    public List<ServiceOrder> getWorkerScheduledOrders(Long workerId) {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getWorkerId, workerId)
               .in(ServiceOrder::getStatus, "IN_PROGRESS", "APPROVED")
               .orderByAsc(ServiceOrder::getServiceTime);
        return orderMapper.selectList(wrapper);
    }
    
    @Override
    public boolean hasScheduleConflict(Long workerId, LocalDateTime startTime, LocalDateTime endTime) {
        // 检查与已有日程的冲突
        LambdaQueryWrapper<WorkerSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
        scheduleWrapper.eq(WorkerSchedule::getWorkerId, workerId)
                      .and(w -> w.and(w1 -> w1.le(WorkerSchedule::getStartTime, startTime)
                                              .ge(WorkerSchedule::getEndTime, startTime))
                                 .or(w2 -> w2.le(WorkerSchedule::getStartTime, endTime)
                                            .ge(WorkerSchedule::getEndTime, endTime))
                                 .or(w3 -> w3.ge(WorkerSchedule::getStartTime, startTime)
                                            .le(WorkerSchedule::getEndTime, endTime)));
        long scheduleCount = scheduleMapper.selectCount(scheduleWrapper);
        
        // 检查与已有订单的冲突
        LambdaQueryWrapper<ServiceOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(ServiceOrder::getWorkerId, workerId)
                   .in(ServiceOrder::getStatus, "IN_PROGRESS", "APPROVED")
                   .and(w -> w.and(w1 -> w1.le(ServiceOrder::getServiceTime, startTime)
                                          .ge(ServiceOrder::getServiceTime, startTime))
                             .or(w2 -> w2.le(ServiceOrder::getServiceTime, endTime)
                                        .ge(ServiceOrder::getServiceTime, endTime))
                             .or(w3 -> w3.ge(ServiceOrder::getServiceTime, startTime)
                                        .le(ServiceOrder::getServiceTime, endTime)));
        long orderCount = orderMapper.selectCount(orderWrapper);
        
        return scheduleCount > 0 || orderCount > 0;
    }
}
