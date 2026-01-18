package org.example.homeservice_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.homeservice_platform.common.Result;
import org.example.homeservice_platform.model.OrderRating;
import org.example.homeservice_platform.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评价控制器
 * @author system
 */
@Tag(name = "评价模块", description = "订单评价功能")
@RestController
@RequestMapping("/api/order/rate")
public class RatingController {
    
    @Autowired
    private RatingService ratingService;
    
    /**
     * 创建评价
     */
    @Operation(summary = "创建评价", description = "对订单进行评价")
    @PostMapping
    public Result<?> createRating(@RequestParam Long orderId,
                                  @RequestParam Long raterId,
                                  @RequestParam Long rateeId,
                                  @RequestParam Integer rating,
                                  @RequestParam(required = false) String comment) {
        boolean success = ratingService.createRating(orderId, raterId, rateeId, rating, comment);
        if (success) {
            return Result.success("评价成功");
        }
        return Result.error("评价失败");
    }
    
    /**
     * 获取订单评价列表
     */
    @Operation(summary = "获取订单评价", description = "查询订单的所有评价")
    @GetMapping("/order/{orderId}")
    public Result<List<OrderRating>> getOrderRatings(@PathVariable Long orderId) {
        List<OrderRating> ratings = ratingService.getOrderRatings(orderId);
        return Result.success(ratings);
    }
    
    /**
     * 获取用户评价列表
     */
    @Operation(summary = "获取用户评价", description = "查询用户收到的所有评价")
    @GetMapping("/user/{userId}")
    public Result<List<OrderRating>> getUserRatings(@PathVariable Long userId) {
        List<OrderRating> ratings = ratingService.getUserRatings(userId);
        return Result.success(ratings);
    }
}
