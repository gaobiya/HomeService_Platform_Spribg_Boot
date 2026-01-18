package org.example.homeservice_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.homeservice_platform.common.BusinessException;
import org.example.homeservice_platform.dto.UserLoginDTO;
import org.example.homeservice_platform.mapper.UserInfoMapper;
import org.example.homeservice_platform.model.UserInfo;
import org.example.homeservice_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户服务实现类
 * @author system
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserInfoMapper userInfoMapper;
    
    @Override
    public Object login(UserLoginDTO loginDTO) {
        // 微信小程序登录逻辑（简化版，实际应该调用微信API）
        // 这里使用code模拟，实际项目中需要通过code获取openid
        String code = loginDTO.getCode();
        String role = loginDTO.getRole();
        
        if (code == null || code.isEmpty()) {
            throw new BusinessException(400, "微信登录code不能为空");
        }
        
        // 模拟通过code获取openid（实际应该调用微信API）
        String openid = "wx_" + code.substring(0, Math.min(10, code.length()));
        
        // 查询用户是否存在
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getPhone, openid); // 使用phone字段存储openid
        UserInfo user = userInfoMapper.selectOne(wrapper);
        
        // 如果用户不存在，创建新用户
        if (user == null) {
            user = new UserInfo();
            user.setUsername("用户" + System.currentTimeMillis());
            user.setRole(role);
            user.setPhone(openid);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userInfoMapper.insert(user);
        }
        
        // 生成token（简化版，实际应该使用JWT）
        String token = UUID.randomUUID().toString().replace("-", "");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("role", user.getRole());
        result.put("username", user.getUsername());
        
        return result;
    }
    
    @Override
    public Object dispatcherLogin(String username, String password) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUsername, username)
               .eq(UserInfo::getRole, "dispatcher");
        UserInfo user = userInfoMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        // 密码验证（简单明文比较，仅用于演示）
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (!password.equals(user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        String token = UUID.randomUUID().toString().replace("-", "");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("role", user.getRole());
        result.put("username", user.getUsername());
        
        return result;
    }
    
    @Override
    public UserInfo getUserById(Long userId) {
        return userInfoMapper.selectById(userId);
    }
    
    @Override
    public boolean updateUser(UserInfo userInfo) {
        userInfo.setUpdatedAt(LocalDateTime.now());
        return userInfoMapper.updateById(userInfo) > 0;
    }
    
    @Override
    public UserInfo createUser(UserInfo userInfo) {
        userInfo.setCreatedAt(LocalDateTime.now());
        userInfo.setUpdatedAt(LocalDateTime.now());
        userInfoMapper.insert(userInfo);
        return userInfo;
    }
}
