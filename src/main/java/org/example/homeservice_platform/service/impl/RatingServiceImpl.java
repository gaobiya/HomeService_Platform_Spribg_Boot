package org.example.homeservice_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.homeservice_platform.common.BusinessException;
import org.example.homeservice_platform.dto.PageResult;
import org.example.homeservice_platform.dto.RatingDTO;
import org.example.homeservice_platform.mapper.OrderRatingMapper;
import org.example.homeservice_platform.mapper.ServiceOrderMapper;
import org.example.homeservice_platform.mapper.UserInfoMapper;
import org.example.homeservice_platform.model.OrderRating;
import org.example.homeservice_platform.model.ServiceOrder;
import org.example.homeservice_platform.model.UserInfo;
import org.example.homeservice_platform.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    
    @Autowired
    private UserInfoMapper userInfoMapper;
    
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
        
        // 判断评价人是客户还是服务员
        boolean isCustomerRating = raterId.equals(order.getCustomerId());
        boolean isWorkerRating = order.getWorkerId() != null && raterId.equals(order.getWorkerId());
        
        // 如果是服务员评价客户，需要检查订单是否已支付
        if (isWorkerRating && order.getPaid() == 0) {
            throw new BusinessException(400, "订单未支付，无法评价");
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
    
    @Override
    public PageResult<OrderRating> getAllRatingsPage(Long pageNum, Long pageSize) {
        Page<OrderRating> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OrderRating> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OrderRating::getCreatedAt);
        Page<OrderRating> result = ratingMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public PageResult<RatingDTO> getAllRatingsWithUsernamePage(Long pageNum, Long pageSize) {
        // 先查询评价列表
        Page<OrderRating> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OrderRating> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OrderRating::getCreatedAt);
        Page<OrderRating> result = ratingMapper.selectPage(page, wrapper);
        
        // 转换为DTO并填充用户名
        List<RatingDTO> ratingDTOList = result.getRecords().stream().map(rating -> {
            RatingDTO dto = new RatingDTO();
            dto.setId(rating.getId());
            dto.setOrderId(rating.getOrderId());
            dto.setRaterId(rating.getRaterId());
            dto.setRateeId(rating.getRateeId());
            dto.setRating(rating.getRating());
            dto.setComment(rating.getComment());
            dto.setCreatedAt(rating.getCreatedAt());
            
            // 查询评价人用户名
            UserInfo rater = userInfoMapper.selectById(rating.getRaterId());
            if (rater != null) {
                dto.setRaterUsername(rater.getUsername());
            }
            
            // 查询被评价人用户名
            UserInfo ratee = userInfoMapper.selectById(rating.getRateeId());
            if (ratee != null) {
                dto.setRateeUsername(ratee.getUsername());
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        return new PageResult<>(ratingDTOList, result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public Double getUserAverageRating(Long userId) {
        List<OrderRating> ratings = getUserRatings(userId);
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }
        double sum = ratings.stream().mapToInt(OrderRating::getRating).sum();
        return sum / ratings.size();
    }
}
