package org.example.homeservice_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.homeservice_platform.common.BusinessException;
import org.example.homeservice_platform.mapper.OrderRatingMapper;
import org.example.homeservice_platform.mapper.ServiceOrderMapper;
import org.example.homeservice_platform.model.OrderRating;
import org.example.homeservice_platform.model.ServiceOrder;
import org.example.homeservice_platform.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价服务实现类
 * @author system
 */
@Slf4j
@Service
public class RatingServiceImpl implements RatingService {
    
    @Autowired
    private OrderRatingMapper ratingMapper;
    
    @Autowired
    private ServiceOrderMapper orderMapper;
    
    @Override
    @Transactional
    public boolean createRating(Long orderId, Long raterId, Long rateeId, Integer rating, String comment) {
        // 验证订单是否存在且已完成
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new BusinessException(400, "订单未完成，无法评价");
        }
        
        // 检查是否已经评价过
        LambdaQueryWrapper<OrderRating> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderRating::getOrderId, orderId)
               .eq(OrderRating::getRaterId, raterId);
        OrderRating existing = ratingMapper.selectOne(wrapper);
        
        if (existing != null) {
            throw new BusinessException(400, "您已经评价过此订单");
        }
        
        // 验证评分范围
        if (rating < 1 || rating > 5) {
            throw new BusinessException(400, "评分必须在1-5之间");
        }
        
        OrderRating orderRating = new OrderRating();
        orderRating.setOrderId(orderId);
        orderRating.setRaterId(raterId);
        orderRating.setRateeId(rateeId);
        orderRating.setRating(rating);
        orderRating.setComment(comment);
        orderRating.setCreatedAt(LocalDateTime.now());
        
        return ratingMapper.insert(orderRating) > 0;
    }
    
    @Override
    public List<OrderRating> getOrderRatings(Long orderId) {
        LambdaQueryWrapper<OrderRating> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderRating::getOrderId, orderId)
               .orderByDesc(OrderRating::getCreatedAt);
        return ratingMapper.selectList(wrapper);
    }
    
    @Override
    public List<OrderRating> getUserRatings(Long userId) {
        LambdaQueryWrapper<OrderRating> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderRating::getRateeId, userId)
               .orderByDesc(OrderRating::getCreatedAt);
        return ratingMapper.selectList(wrapper);
    }
}
