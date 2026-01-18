package org.example.homeservice_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.homeservice_platform.model.OrderRating;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单评价Mapper接口
 * @author system
 */
@Mapper
public interface OrderRatingMapper extends BaseMapper<OrderRating> {
}
