package org.example.homeservice_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.homeservice_platform.common.BusinessException;
import org.example.homeservice_platform.dto.OrderCreateDTO;
import org.example.homeservice_platform.dto.PageResult;
import org.example.homeservice_platform.mapper.ServiceOrderMapper;
import org.example.homeservice_platform.mapper.UserInfoMapper;
import org.example.homeservice_platform.mapper.WorkerScheduleMapper;
import org.example.homeservice_platform.mapper.WorkerServiceTypeMapper;
import org.example.homeservice_platform.model.ServiceOrder;
import org.example.homeservice_platform.model.UserInfo;
import org.example.homeservice_platform.model.WorkerSchedule;
import org.example.homeservice_platform.model.WorkerServiceType;
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
    
    @Autowired
    private WorkerServiceTypeMapper workerServiceTypeMapper;
    
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
    public PageResult<ServiceOrder> getCustomerOrdersPage(Long customerId, String status, Long pageNum, Long pageSize) {
        Page<ServiceOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getCustomerId, customerId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ServiceOrder::getStatus, status);
        }
        wrapper.orderByDesc(ServiceOrder::getCreatedAt);
        Page<ServiceOrder> result = orderMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
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
    public PageResult<ServiceOrder> getWorkerOrdersPage(Long workerId, String status, Long pageNum, Long pageSize) {
        Page<ServiceOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getWorkerId, workerId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ServiceOrder::getStatus, status);
        }
        wrapper.orderByDesc(ServiceOrder::getCreatedAt);
        Page<ServiceOrder> result = orderMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public List<ServiceOrder> getPendingOrders() {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ServiceOrder::getStatus, "PENDING", "APPROVED");
        wrapper.orderByAsc(ServiceOrder::getCreatedAt);
        return orderMapper.selectList(wrapper);
    }
    
    @Override
    public PageResult<ServiceOrder> getPendingOrdersPage(Long pageNum, Long pageSize) {
        Page<ServiceOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ServiceOrder::getStatus, "PENDING", "APPROVED");
        wrapper.orderByAsc(ServiceOrder::getCreatedAt);
        Page<ServiceOrder> result = orderMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
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
            // 审核不通过，驳回订单
            order.setStatus("REJECTED");
        } else {
            // 审核通过，状态改为APPROVED，等待派单
            order.setStatus("APPROVED");
        }
        
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
        
        if (!"APPROVED".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，只有已审核通过的订单才能派单");
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
     * 自动派单：选择空闲的服务员（优先匹配服务类型）
     */
    private Long autoAssignWorker(ServiceOrder order) {
        String serviceType = order.getServiceType();
        
        // 首先查询设置了该服务类型的服务员
        LambdaQueryWrapper<WorkerServiceType> serviceTypeWrapper = new LambdaQueryWrapper<>();
        serviceTypeWrapper.eq(WorkerServiceType::getServiceType, serviceType);
        List<WorkerServiceType> workerServiceTypes = workerServiceTypeMapper.selectList(serviceTypeWrapper);
        
        // 如果找到了设置了该服务类型的服务员，优先从这些服务员中选择
        if (workerServiceTypes != null && !workerServiceTypes.isEmpty()) {
            for (WorkerServiceType wst : workerServiceTypes) {
                Long workerId = wst.getWorkerId();
                // 验证服务员是否存在
                UserInfo worker = userInfoMapper.selectById(workerId);
                if (worker != null && "worker".equals(worker.getRole())) {
                    // 检查是否有空闲时间
                    if (!hasTimeConflict(workerId, order.getServiceTime())) {
                        return workerId;
                    }
                }
            }
        }
        
        // 如果没有找到匹配服务类型的服务员，或者都有时间冲突，则查询所有服务员
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
    
    @Override
    public List<ServiceOrder> getAllOrders() {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ServiceOrder::getCreatedAt);
        return orderMapper.selectList(wrapper);
    }
    
    @Override
    public PageResult<ServiceOrder> getAllOrdersPage(Long pageNum, Long pageSize) {
        Page<ServiceOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ServiceOrder::getCreatedAt);
        Page<ServiceOrder> result = orderMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public List<ServiceOrder> getOrdersByStatus(String status) {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getStatus, status);
        wrapper.orderByDesc(ServiceOrder::getCreatedAt);
        return orderMapper.selectList(wrapper);
    }
    
    @Override
    public PageResult<ServiceOrder> getOrdersByStatusPage(String status, Long pageNum, Long pageSize) {
        Page<ServiceOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getStatus, status);
        wrapper.orderByDesc(ServiceOrder::getCreatedAt);
        Page<ServiceOrder> result = orderMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    @Transactional
    public boolean rejectOrder(Long orderId, Long workerId) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        // 验证订单是否属于该服务员
        if (order.getWorkerId() == null || !order.getWorkerId().equals(workerId)) {
            throw new BusinessException(400, "无权操作此订单");
        }
        
        // 验证订单状态
        if (!"IN_PROGRESS".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，无法拒绝");
        }
        
        // 拒绝后：将订单状态改为APPROVED，workerId设为null，等待重新派单
        order.setStatus("APPROVED");
        order.setWorkerId(null);
        order.setAssignedTime(null);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }
}
