package org.example.homeservice_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.homeservice_platform.common.Result;
import org.example.homeservice_platform.dto.PageResult;
import org.example.homeservice_platform.dto.RatingCreateDTO;
import org.example.homeservice_platform.dto.RatingDTO;
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
    public Result<?> createRating(@Valid @RequestBody RatingCreateDTO ratingDTO) {
        boolean success = ratingService.createRating(
            ratingDTO.getOrderId(),
            ratingDTO.getRaterId(),
            ratingDTO.getRateeId(),
            ratingDTO.getRating(),
            ratingDTO.getComment()
        );
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
    
    /**
     * 获取所有评价列表（分页，包含用户名）- 后台管理使用
     */
    @Operation(summary = "获取所有评价列表", description = "后台管理查看所有评价，支持分页，包含用户名")
    @GetMapping("/list/all/page")
    public Result<PageResult<RatingDTO>> getAllRatingsPage(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize) {
        PageResult<RatingDTO> result = ratingService.getAllRatingsWithUsernamePage(pageNum, pageSize);
        return Result.success(result);
    }
}
