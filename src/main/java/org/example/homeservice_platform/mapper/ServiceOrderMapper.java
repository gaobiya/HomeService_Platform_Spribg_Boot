package org.example.homeservice_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.homeservice_platform.model.ServiceOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务订单Mapper接口
 * @author system
 */
@Mapper
public interface ServiceOrderMapper extends BaseMapper<ServiceOrder> {
}
