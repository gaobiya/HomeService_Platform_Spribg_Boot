package org.example.homeservice_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.homeservice_platform.model.Withdraw;

/**
 * 提现记录 Mapper
 * @author system
 */
@Mapper
public interface WithdrawMapper extends BaseMapper<Withdraw> {
}
