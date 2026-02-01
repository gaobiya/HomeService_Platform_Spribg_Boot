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
    
    /**
     * 获取所有评价列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    org.example.homeservice_platform.dto.PageResult<OrderRating> getAllRatingsPage(Long pageNum, Long pageSize);
    
    /**
     * 获取所有评价列表（分页，包含用户名）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果（包含用户名）
     */
    org.example.homeservice_platform.dto.PageResult<org.example.homeservice_platform.dto.RatingDTO> getAllRatingsWithUsernamePage(Long pageNum, Long pageSize);
    
    /**
     * 获取用户的平均评分
     * @param userId 用户ID
     * @return 平均评分（如果没有评价则返回0.0）
     */
    Double getUserAverageRating(Long userId);
}
