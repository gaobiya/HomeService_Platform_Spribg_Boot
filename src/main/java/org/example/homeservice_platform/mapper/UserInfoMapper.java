package org.example.homeservice_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.homeservice_platform.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息Mapper接口
 * @author system
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
