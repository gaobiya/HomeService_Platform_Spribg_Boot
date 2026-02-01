package org.example.homeservice_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.homeservice_platform.common.BusinessException;
import org.example.homeservice_platform.dto.OrderCreateDTO;
import org.example.homeservice_platform.dto.PageResult;
import org.example.homeservice_platform.dto.WorkerWithRatingDTO;
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
    
    @Autowired
    private org.example.homeservice_platform.service.RatingService ratingService;
    
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
        // 从DTO中获取订单金额
        order.setAmount(createDTO.getAmount() != null ? createDTO.getAmount() : BigDecimal.ZERO);
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
        // 派单后为「已派单待接单」，需服务员点击接受后才进入进行中
        order.setStatus("ASSIGNED");
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
        
        if (order.getWorkerId() == null || !order.getWorkerId().equals(workerId)) {
            throw new BusinessException(400, "无权操作此订单");
        }
        
        // 仅当状态为「已派单待接单」时可接受，接受后进入进行中
        if (!"ASSIGNED".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，仅待接单订单可接受");
        }
        
        order.setStatus("IN_PROGRESS");
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
        boolean updated = orderMapper.updateById(order) > 0;
        if (updated && order.getWorkerId() != null) {
            // 客户支付成功，服务员余额增加
            UserInfo worker = userInfoMapper.selectById(order.getWorkerId());
            if (worker != null) {
                java.math.BigDecimal current = worker.getBalance() != null ? worker.getBalance() : java.math.BigDecimal.ZERO;
                worker.setBalance(current.add(amount));
                worker.setUpdatedAt(LocalDateTime.now());
                userInfoMapper.updateById(worker);
            }
        }
        return updated;
    }
    
    /**
     * 自动派单：选择空闲的服务员（优先匹配服务类型，按评分从高到低排序）
     */
    private Long autoAssignWorker(ServiceOrder order) {
        String serviceType = order.getServiceType();
        LocalDateTime serviceTime = order.getServiceTime();
        
        // 用于存储符合条件的服务员及其评分
        java.util.List<java.util.Map<String, Object>> candidateWorkers = new java.util.ArrayList<>();
        
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
                    // 检查是否有空闲时间（包括可服务时间检查）
                    if (!hasTimeConflict(workerId, serviceTime)) {
                        // 获取平均评分
                        Double avgRating = ratingService.getUserAverageRating(workerId);
                        java.util.Map<String, Object> candidate = new java.util.HashMap<>();
                        candidate.put("workerId", workerId);
                        candidate.put("rating", avgRating);
                        candidate.put("priority", 1); // 匹配服务类型，优先级1
                        candidateWorkers.add(candidate);
                    }
                }
            }
        }
        
        // 如果没有找到匹配服务类型的服务员，或者都有时间冲突，则查询所有服务员
        if (candidateWorkers.isEmpty()) {
            LambdaQueryWrapper<UserInfo> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(UserInfo::getRole, "worker");
            List<UserInfo> workers = userInfoMapper.selectList(userWrapper);
            
            // 查找有空闲时间的服务员
            for (UserInfo worker : workers) {
                if (!hasTimeConflict(worker.getId(), serviceTime)) {
                    // 获取平均评分
                    Double avgRating = ratingService.getUserAverageRating(worker.getId());
                    java.util.Map<String, Object> candidate = new java.util.HashMap<>();
                    candidate.put("workerId", worker.getId());
                    candidate.put("rating", avgRating);
                    candidate.put("priority", 2); // 不匹配服务类型，优先级2
                    candidateWorkers.add(candidate);
                }
            }
        }
        
        // 如果没有符合条件的服务员，返回null
        if (candidateWorkers.isEmpty()) {
            return null;
        }
        
        // 按优先级和评分排序：先按优先级（1优先于2），再按评分从高到低
        candidateWorkers.sort((a, b) -> {
            int priorityCompare = ((Integer) a.get("priority")).compareTo((Integer) b.get("priority"));
            if (priorityCompare != 0) {
                return priorityCompare;
            }
            // 优先级相同，按评分从高到低排序
            return ((Double) b.get("rating")).compareTo((Double) a.get("rating"));
        });
        
        // 返回评分最高的服务员ID
        return (Long) candidateWorkers.get(0).get("workerId");
    }
    
    /**
     * 检查时间冲突（包括订单冲突和可服务时间检查）
     */
    private boolean hasTimeConflict(Long workerId, LocalDateTime serviceTime) {
        // 1. 检查是否有订单冲突（已派单待接单、进行中均视为占用该服务员该时段）
        LambdaQueryWrapper<ServiceOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(ServiceOrder::getWorkerId, workerId)
                   .in(ServiceOrder::getStatus, "ASSIGNED", "IN_PROGRESS")
                   .eq(ServiceOrder::getServiceTime, serviceTime);
        long orderCount = orderMapper.selectCount(orderWrapper);
        if (orderCount > 0) {
            return true; // 有订单冲突
        }
        
        // 2. 检查是否在服务员的可服务时间内
        // 先查询该服务员是否有设置可服务时间
        LambdaQueryWrapper<WorkerSchedule> allScheduleWrapper = new LambdaQueryWrapper<>();
        allScheduleWrapper.eq(WorkerSchedule::getWorkerId, workerId);
        long totalScheduleCount = scheduleMapper.selectCount(allScheduleWrapper);
        
        // 如果服务员没有设置任何可服务时间，则允许派单（不检查可服务时间限制）
        if (totalScheduleCount == 0) {
            return false; // 没有设置可服务时间，允许派单，无冲突
        }
        
        // 如果设置了可服务时间，则检查服务时间是否在可服务时间段内
        LambdaQueryWrapper<WorkerSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
        scheduleWrapper.eq(WorkerSchedule::getWorkerId, workerId)
                      .le(WorkerSchedule::getStartTime, serviceTime)
                      .ge(WorkerSchedule::getEndTime, serviceTime);
        long scheduleCount = scheduleMapper.selectCount(scheduleWrapper);
        
        // 如果服务时间不在任何可服务时间段内，则认为有冲突
        return scheduleCount == 0; // 没有匹配的可服务时间记录，认为有冲突
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
        
        // 仅「已派单待接单」时可拒绝；接单后只能完成，不能拒绝
        if (!"ASSIGNED".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，仅待接单时可拒绝");
        }
        
        // 拒绝后：将订单状态改为APPROVED，workerId设为null，等待重新派单
        order.setStatus("APPROVED");
        order.setWorkerId(null);
        order.setAssignedTime(null);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    public java.util.List<WorkerWithRatingDTO> getAvailableWorkersForAssign(Long orderId, boolean sortByRating) {
        // 获取订单信息
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        LocalDateTime serviceTime = order.getServiceTime();
        
        // 查询所有服务员
        LambdaQueryWrapper<UserInfo> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(UserInfo::getRole, "worker");
        List<UserInfo> workers = userInfoMapper.selectList(userWrapper);
        
        // 转换为DTO并过滤符合条件的服务员
        java.util.List<WorkerWithRatingDTO> availableWorkers = new java.util.ArrayList<>();
        for (UserInfo worker : workers) {
            // 检查是否有时间冲突（包括可服务时间检查）
            if (!hasTimeConflict(worker.getId(), serviceTime)) {
                WorkerWithRatingDTO dto = new WorkerWithRatingDTO();
                dto.setId(worker.getId());
                dto.setUsername(worker.getUsername());
                dto.setRole(worker.getRole());
                dto.setPhone(worker.getPhone());
                dto.setAvatarUrl(worker.getAvatarUrl());
                
                // 获取平均评分和评价数量
                List<org.example.homeservice_platform.model.OrderRating> ratings = ratingService.getUserRatings(worker.getId());
                if (ratings != null && !ratings.isEmpty()) {
                    double sum = ratings.stream().mapToInt(org.example.homeservice_platform.model.OrderRating::getRating).sum();
                    dto.setAverageRating(sum / ratings.size());
                    dto.setRatingCount(ratings.size());
                } else {
                    dto.setAverageRating(0.0);
                    dto.setRatingCount(0);
                }
                
                availableWorkers.add(dto);
            }
        }
        
        // 如果要求按评分排序，则从高到低排序
        if (sortByRating) {
            availableWorkers.sort((a, b) -> {
                // 先按评分从高到低排序
                int ratingCompare = b.getAverageRating().compareTo(a.getAverageRating());
                if (ratingCompare != 0) {
                    return ratingCompare;
                }
                // 评分相同，按评价数量从多到少排序
                return b.getRatingCount().compareTo(a.getRatingCount());
            });
        }
        
        return availableWorkers;
    }
}
