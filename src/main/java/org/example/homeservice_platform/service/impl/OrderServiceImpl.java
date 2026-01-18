package org.example.homeservice_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.homeservice_platform.common.BusinessException;
import org.example.homeservice_platform.dto.OrderCreateDTO;
import org.example.homeservice_platform.mapper.ServiceOrderMapper;
import org.example.homeservice_platform.mapper.UserInfoMapper;
import org.example.homeservice_platform.mapper.WorkerScheduleMapper;
import org.example.homeservice_platform.model.ServiceOrder;
import org.example.homeservice_platform.model.UserInfo;
import org.example.homeservice_platform.model.WorkerSchedule;
import org.example.homeservice_platform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务实现类
 * @author system
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private ServiceOrderMapper orderMapper;
    
    @Autowired
    private UserInfoMapper userInfoMapper;
    
    @Autowired
    private WorkerScheduleMapper scheduleMapper;
    
    @Override
    @Transactional
    public Long createOrder(Long customerId, OrderCreateDTO createDTO) {
        // 验证客户是否存在
        UserInfo customer = userInfoMapper.selectById(customerId);
        if (customer == null || !"customer".equals(customer.getRole())) {
            throw new BusinessException(400, "客户不存在");
        }
        
        ServiceOrder order = new ServiceOrder();
        order.setCustomerId(customerId);
        order.setServiceType(createDTO.getServiceType());
        order.setAddress(createDTO.getAddress());
        order.setDescription(createDTO.getDescription());
        order.setServiceTime(createDTO.getServiceTime());
        order.setStatus("PENDING");
        order.setAmount(BigDecimal.ZERO);
        order.setPaid(0);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        orderMapper.insert(order);
        return order.getId();
    }
    
    @Override
    public ServiceOrder getOrderById(Long orderId) {
        return orderMapper.selectById(orderId);
    }
    
    @Override
    public List<ServiceOrder> getCustomerOrders(Long customerId, String status) {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getCustomerId, customerId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ServiceOrder::getStatus, status);
        }
        wrapper.orderByDesc(ServiceOrder::getCreatedAt);
        return orderMapper.selectList(wrapper);
    }
    
    @Override
    public List<ServiceOrder> getWorkerOrders(Long workerId, String status) {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getWorkerId, workerId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ServiceOrder::getStatus, status);
        }
        wrapper.orderByDesc(ServiceOrder::getCreatedAt);
        return orderMapper.selectList(wrapper);
    }
    
    @Override
    public List<ServiceOrder> getPendingOrders() {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getStatus, "PENDING");
        wrapper.orderByAsc(ServiceOrder::getCreatedAt);
        return orderMapper.selectList(wrapper);
    }
    
    @Override
    @Transactional
    public boolean reviewOrder(Long orderId, boolean approved) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，无法审核");
        }
        
        if (!approved) {
            // 审核不通过，取消订单
            order.setStatus("CANCELLED");
        }
        // 审核通过，状态保持PENDING，等待派单
        
        order.setUpdatedAt(LocalDateTime.now());
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    @Transactional
    public boolean assignOrder(Long orderId, Long workerId) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，无法派单");
        }
        
        // 如果workerId为null，执行自动派单
        if (workerId == null) {
            workerId = autoAssignWorker(order);
            if (workerId == null) {
                throw new BusinessException(400, "暂无可用服务员");
            }
        } else {
            // 验证服务员是否存在且角色正确
            UserInfo worker = userInfoMapper.selectById(workerId);
            if (worker == null || !"worker".equals(worker.getRole())) {
                throw new BusinessException(400, "服务员不存在");
            }
            
            // 检查服务员是否有时间冲突
            if (hasTimeConflict(workerId, order.getServiceTime())) {
                throw new BusinessException(400, "服务员在该时间段已有安排");
            }
        }
        
        order.setWorkerId(workerId);
        order.setStatus("IN_PROGRESS");
        order.setAssignedTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    @Transactional
    public boolean acceptOrder(Long orderId, Long workerId) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        if (!order.getWorkerId().equals(workerId)) {
            throw new BusinessException(400, "无权操作此订单");
        }
        
        if (!"IN_PROGRESS".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确");
        }
        
        // 接单后状态不变，仍为IN_PROGRESS
        order.setUpdatedAt(LocalDateTime.now());
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    @Transactional
    public boolean completeOrder(Long orderId) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        if (!"IN_PROGRESS".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确");
        }
        
        order.setStatus("COMPLETED");
        order.setUpdatedAt(LocalDateTime.now());
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    @Transactional
    public boolean payOrder(Long orderId, BigDecimal amount) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        if (order.getPaid() == 1) {
            throw new BusinessException(400, "订单已支付");
        }
        
        order.setAmount(amount);
        order.setPaid(1);
        order.setUpdatedAt(LocalDateTime.now());
        return orderMapper.updateById(order) > 0;
    }
    
    /**
     * 自动派单：选择空闲的服务员
     */
    private Long autoAssignWorker(ServiceOrder order) {
        // 查询所有服务员
        LambdaQueryWrapper<UserInfo> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(UserInfo::getRole, "worker");
        List<UserInfo> workers = userInfoMapper.selectList(userWrapper);
        
        // 查找有空闲时间的服务员
        for (UserInfo worker : workers) {
            if (!hasTimeConflict(worker.getId(), order.getServiceTime())) {
                return worker.getId();
            }
        }
        
        return null;
    }
    
    /**
     * 检查时间冲突
     */
    private boolean hasTimeConflict(Long workerId, LocalDateTime serviceTime) {
        // 查询服务员在该时间段是否有其他订单
        LambdaQueryWrapper<ServiceOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(ServiceOrder::getWorkerId, workerId)
                   .in(ServiceOrder::getStatus, "PENDING", "IN_PROGRESS")
                   .eq(ServiceOrder::getServiceTime, serviceTime);
        long count = orderMapper.selectCount(orderWrapper);
        
        return count > 0;
    }
}
