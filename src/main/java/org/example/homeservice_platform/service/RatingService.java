package org.example.homeservice_platform.service;

import org.example.homeservice_platform.model.OrderRating;

import java.util.List;

/**
 * 评价服务接口
 * @author system
 */
public interface RatingService {
    
    /**
     * 创建评价
     * @param orderId 订单ID
     * @param raterId 评价人ID
     * @param rateeId 被评价人ID
     * @param rating 评分
     * @param comment 评价内容
     * @return 是否成功
     */
    boolean createRating(Long orderId, Long raterId, Long rateeId, Integer rating, String comment);
    
    /**
     * 获取订单的所有评价
     * @param orderId 订单ID
     * @return 评价列表
     */
    List<OrderRating> getOrderRatings(Long orderId);
    
    /**
     * 获取用户的评价列表（作为被评价人）
     * @param userId 用户ID
     * @return 评价列表
     */
    List<OrderRating> getUserRatings(Long userId);
}
