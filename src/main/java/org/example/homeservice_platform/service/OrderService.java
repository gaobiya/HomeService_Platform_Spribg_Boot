package org.example.homeservice_platform.service;

import org.example.homeservice_platform.dto.OrderCreateDTO;
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
     * 获取服务员订单列表
     * @param workerId 服务员ID
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    List<ServiceOrder> getWorkerOrders(Long workerId, String status);
    
    /**
     * 获取所有待审核订单
     * @return 订单列表
     */
    List<ServiceOrder> getPendingOrders();
    
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
}
