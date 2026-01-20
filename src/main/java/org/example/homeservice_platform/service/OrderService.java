package org.example.homeservice_platform.service;

import org.example.homeservice_platform.dto.OrderCreateDTO;
import org.example.homeservice_platform.dto.PageResult;
import org.example.homeservice_platform.model.ServiceOrder;

import java.util.List;

/**
 * 订单服务接口
 * @author system
 */
public interface OrderService {
    
    /**
     * 创建订单
     * @param customerId 客户ID
     * @param createDTO 订单信息
     * @return 订单ID
     */
    Long createOrder(Long customerId, OrderCreateDTO createDTO);
    
    /**
     * 根据ID获取订单
     * @param orderId 订单ID
     * @return 订单信息
     */
    ServiceOrder getOrderById(Long orderId);
    
    /**
     * 获取客户订单列表
     * @param customerId 客户ID
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    List<ServiceOrder> getCustomerOrders(Long customerId, String status);
    
    /**
     * 获取客户订单列表（分页）
     * @param customerId 客户ID
     * @param status 订单状态（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<ServiceOrder> getCustomerOrdersPage(Long customerId, String status, Long pageNum, Long pageSize);
    
    /**
     * 获取服务员订单列表
     * @param workerId 服务员ID
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    List<ServiceOrder> getWorkerOrders(Long workerId, String status);
    
    /**
     * 获取服务员订单列表（分页）
     * @param workerId 服务员ID
     * @param status 订单状态（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<ServiceOrder> getWorkerOrdersPage(Long workerId, String status, Long pageNum, Long pageSize);
    
    /**
     * 获取所有待审核订单
     * @return 订单列表
     */
    List<ServiceOrder> getPendingOrders();
    
    /**
     * 获取所有待审核订单（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<ServiceOrder> getPendingOrdersPage(Long pageNum, Long pageSize);
    
    /**
     * 审核订单
     * @param orderId 订单ID
     * @param approved 是否通过
     * @return 是否成功
     */
    boolean reviewOrder(Long orderId, boolean approved);
    
    /**
     * 派单
     * @param orderId 订单ID
     * @param workerId 服务员ID（null表示自动派单）
     * @return 是否成功
     */
    boolean assignOrder(Long orderId, Long workerId);
    
    /**
     * 服务员接单
     * @param orderId 订单ID
     * @param workerId 服务员ID
     * @return 是否成功
     */
    boolean acceptOrder(Long orderId, Long workerId);
    
    /**
     * 完成订单
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean completeOrder(Long orderId);
    
    /**
     * 支付订单
     * @param orderId 订单ID
     * @param amount 支付金额
     * @return 是否成功
     */
    boolean payOrder(Long orderId, java.math.BigDecimal amount);
    
    /**
     * 获取所有订单
     * @return 订单列表
     */
    List<ServiceOrder> getAllOrders();
    
    /**
     * 获取所有订单（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<ServiceOrder> getAllOrdersPage(Long pageNum, Long pageSize);
    
    /**
     * 按状态获取订单列表
     * @param status 订单状态
     * @return 订单列表
     */
    List<ServiceOrder> getOrdersByStatus(String status);
    
    /**
     * 按状态获取订单列表（分页）
     * @param status 订单状态
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<ServiceOrder> getOrdersByStatusPage(String status, Long pageNum, Long pageSize);
    
    /**
     * 服务员拒绝订单
     * @param orderId 订单ID
     * @param workerId 服务员ID
     * @return 是否成功
     */
    boolean rejectOrder(Long orderId, Long workerId);
}
