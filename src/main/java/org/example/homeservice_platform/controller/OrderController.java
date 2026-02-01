package org.example.homeservice_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.homeservice_platform.common.Result;
import org.example.homeservice_platform.dto.OrderCreateDTO;
import org.example.homeservice_platform.dto.PageResult;
import org.example.homeservice_platform.model.ServiceOrder;
import org.example.homeservice_platform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单控制器
 * @author system
 */
@Tag(name = "订单模块", description = "订单创建、查询、派单、支付等功能")
@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建订单
     */
    @Operation(summary = "创建订单", description = "客户发布服务需求")
    @PostMapping("/create")
    public Result<?> createOrder(@RequestParam Long customerId, 
                                 @Valid @RequestBody OrderCreateDTO createDTO) {
        Long orderId = orderService.createOrder(customerId, createDTO);
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        return Result.success("订单创建成功", result);
    }
    
    /**
     * 获取订单详情
     */
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    @GetMapping("/{orderId}")
    public Result<ServiceOrder> getOrder(@PathVariable Long orderId) {
        ServiceOrder order = orderService.getOrderById(orderId);
        if (order == null) {
            return Result.notFound("订单不存在");
        }
        return Result.success(order);
    }
    
    /**
     * 获取客户订单列表
     */
    @Operation(summary = "获取客户订单列表", description = "查询客户的订单列表")
    @GetMapping("/list/customer")
    public Result<List<ServiceOrder>> getCustomerOrders(@RequestParam Long customerId,
                                                         @RequestParam(required = false) String status) {
        List<ServiceOrder> orders = orderService.getCustomerOrders(customerId, status);
        return Result.success(orders);
    }
    
    /**
     * 获取客户订单列表（分页）
     */
    @Operation(summary = "获取客户订单列表（分页）", description = "查询客户的订单列表，支持分页")
    @GetMapping("/list/customer/page")
    public Result<PageResult<ServiceOrder>> getCustomerOrdersPage(@RequestParam Long customerId,
                                                                  @RequestParam(required = false) String status,
                                                                  @RequestParam(defaultValue = "1") Long pageNum,
                                                                  @RequestParam(defaultValue = "10") Long pageSize) {
        PageResult<ServiceOrder> result = orderService.getCustomerOrdersPage(customerId, status, pageNum, pageSize);
        return Result.success(result);
    }
    
    /**
     * 获取服务员订单列表
     */
    @Operation(summary = "获取服务员订单列表", description = "查询服务员的订单列表")
    @GetMapping("/list/worker")
    public Result<List<ServiceOrder>> getWorkerOrders(@RequestParam Long workerId,
                                                      @RequestParam(required = false) String status) {
        List<ServiceOrder> orders = orderService.getWorkerOrders(workerId, status);
        return Result.success(orders);
    }
    
    /**
     * 获取服务员订单列表（分页）
     */
    @Operation(summary = "获取服务员订单列表（分页）", description = "查询服务员的订单列表，支持分页")
    @GetMapping("/list/worker/page")
    public Result<PageResult<ServiceOrder>> getWorkerOrdersPage(@RequestParam Long workerId,
                                                                 @RequestParam(required = false) String status,
                                                                 @RequestParam(defaultValue = "1") Long pageNum,
                                                                 @RequestParam(defaultValue = "10") Long pageSize) {
        PageResult<ServiceOrder> result = orderService.getWorkerOrdersPage(workerId, status, pageNum, pageSize);
        return Result.success(result);
    }
    
    /**
     * 获取待审核订单列表（派单员）
     */
    @Operation(summary = "获取待审核订单列表", description = "派单员查看待审核的订单")
    @GetMapping("/list/pending")
    public Result<List<ServiceOrder>> getPendingOrders() {
        List<ServiceOrder> orders = orderService.getPendingOrders();
        return Result.success(orders);
    }
    
    /**
     * 获取待审核订单列表（分页）
     */
    @Operation(summary = "获取待审核订单列表（分页）", description = "派单员查看待审核的订单，支持分页")
    @GetMapping("/list/pending/page")
    public Result<PageResult<ServiceOrder>> getPendingOrdersPage(@RequestParam(defaultValue = "1") Long pageNum,
                                                                 @RequestParam(defaultValue = "10") Long pageSize) {
        PageResult<ServiceOrder> result = orderService.getPendingOrdersPage(pageNum, pageSize);
        return Result.success(result);
    }
    
    /**
     * 获取所有订单列表（按状态筛选）
     */
    @Operation(summary = "获取所有订单列表", description = "派单员查看所有订单，支持按状态筛选")
    @GetMapping("/list/all")
    public Result<List<ServiceOrder>> getAllOrders(@RequestParam(required = false) String status) {
        List<ServiceOrder> orders;
        if (status != null && !status.isEmpty()) {
            // 按状态查询
            if ("PENDING".equals(status) || "APPROVED".equals(status)) {
                // 待审核和已审核待派单都从pending接口获取
                orders = orderService.getPendingOrders();
                if ("PENDING".equals(status)) {
                    orders = orders.stream()
                        .filter(order -> "PENDING".equals(order.getStatus()))
                        .collect(java.util.stream.Collectors.toList());
                } else if ("APPROVED".equals(status)) {
                    orders = orders.stream()
                        .filter(order -> "APPROVED".equals(order.getStatus()))
                        .collect(java.util.stream.Collectors.toList());
                }
            } else {
                // 其他状态需要从数据库查询
                orders = orderService.getOrdersByStatus(status);
            }
        } else {
            // 查询所有订单
            orders = orderService.getAllOrders();
        }
        return Result.success(orders);
    }
    
    /**
     * 获取所有订单列表（分页，按状态筛选）
     */
    @Operation(summary = "获取所有订单列表（分页）", description = "派单员查看所有订单，支持按状态筛选和分页")
    @GetMapping("/list/all/page")
    public Result<PageResult<ServiceOrder>> getAllOrdersPage(@RequestParam(required = false) String status,
                                                              @RequestParam(defaultValue = "1") Long pageNum,
                                                              @RequestParam(defaultValue = "10") Long pageSize) {
        PageResult<ServiceOrder> result;
        if (status != null && !status.isEmpty()) {
            // 按状态查询
            if ("PENDING".equals(status) || "APPROVED".equals(status)) {
                // 待审核和已审核待派单都从pending接口获取
                PageResult<ServiceOrder> pendingResult = orderService.getPendingOrdersPage(pageNum, pageSize);
                if ("PENDING".equals(status)) {
                    List<ServiceOrder> filtered = pendingResult.getRecords().stream()
                        .filter(order -> "PENDING".equals(order.getStatus()))
                        .collect(java.util.stream.Collectors.toList());
                    result = new PageResult<>(filtered, pendingResult.getTotal(), pendingResult.getCurrent(), pendingResult.getSize());
                } else if ("APPROVED".equals(status)) {
                    List<ServiceOrder> filtered = pendingResult.getRecords().stream()
                        .filter(order -> "APPROVED".equals(order.getStatus()))
                        .collect(java.util.stream.Collectors.toList());
                    result = new PageResult<>(filtered, pendingResult.getTotal(), pendingResult.getCurrent(), pendingResult.getSize());
                } else {
                    result = pendingResult;
                }
            } else {
                // 其他状态需要从数据库查询
                result = orderService.getOrdersByStatusPage(status, pageNum, pageSize);
            }
        } else {
            // 查询所有订单
            result = orderService.getAllOrdersPage(pageNum, pageSize);
        }
        return Result.success(result);
    }
    
    /**
     * 审核订单
     */
    @Operation(summary = "审核订单", description = "派单员审核订单")
    @PutMapping("/review")
    public Result<?> reviewOrder(@RequestParam Long orderId, 
                                @RequestParam Boolean approved) {
        boolean success = orderService.reviewOrder(orderId, approved);
        if (success) {
            return Result.success(approved ? "审核通过" : "审核驳回");
        }
        return Result.error("审核失败");
    }
    
    /**
     * 获取可用于派单的服务员列表（包含评分）
     */
    @Operation(summary = "获取可用于派单的服务员列表", description = "获取可用于派单的服务员列表，包含评分，支持按评分排序")
    @GetMapping("/assign/workers")
    public Result<?> getAvailableWorkers(@RequestParam Long orderId,
                                        @RequestParam(defaultValue = "false") Boolean sortByRating) {
        java.util.List<org.example.homeservice_platform.dto.WorkerWithRatingDTO> workers = 
            orderService.getAvailableWorkersForAssign(orderId, sortByRating);
        return Result.success(workers);
    }
    
    /**
     * 派单
     */
    @Operation(summary = "派单", description = "派单员派单给服务员（workerId为null时自动派单）")
    @PutMapping("/assign")
    public Result<?> assignOrder(@RequestParam Long orderId,
                                @RequestParam(required = false) Long workerId) {
        boolean success = orderService.assignOrder(orderId, workerId);
        if (success) {
            return Result.success("派单成功");
        }
        return Result.error("派单失败");
    }
    
    /**
     * 服务员接单
     */
    @Operation(summary = "接单", description = "服务员接受派单")
    @PutMapping("/accept")
    public Result<?> acceptOrder(@RequestParam Long orderId,
                                @RequestParam Long workerId) {
        boolean success = orderService.acceptOrder(orderId, workerId);
        if (success) {
            return Result.success("接单成功");
        }
        return Result.error("接单失败");
    }
    
    /**
     * 完成订单
     */
    @Operation(summary = "完成订单", description = "服务员完成服务")
    @PutMapping("/complete")
    public Result<?> completeOrder(@RequestParam Long orderId) {
        boolean success = orderService.completeOrder(orderId);
        if (success) {
            return Result.success("订单已完成");
        }
        return Result.error("操作失败");
    }
    
    /**
     * 拒绝订单
     */
    @Operation(summary = "拒绝订单", description = "服务员拒绝派单员分配的订单")
    @PutMapping("/reject")
    public Result<?> rejectOrder(@RequestParam Long orderId,
                                 @RequestParam Long workerId) {
        boolean success = orderService.rejectOrder(orderId, workerId);
        if (success) {
            return Result.success("已拒绝订单，订单将重新进入派单池");
        }
        return Result.error("拒绝失败");
    }
    
    /**
     * 支付订单
     */
    @Operation(summary = "支付订单", description = "客户支付订单（模拟支付）")
    @PostMapping("/pay")
    public Result<?> payOrder(@RequestParam Long orderId,
                             @RequestParam BigDecimal amount) {
        boolean success = orderService.payOrder(orderId, amount);
        if (success) {
            return Result.success("支付成功");
        }
        return Result.error("支付失败");
    }
}
