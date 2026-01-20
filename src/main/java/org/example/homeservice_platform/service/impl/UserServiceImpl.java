package org.example.homeservice_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.homeservice_platform.common.BusinessException;
import org.example.homeservice_platform.dto.UserLoginDTO;
import org.example.homeservice_platform.mapper.ServiceOrderMapper;
import org.example.homeservice_platform.mapper.UserInfoMapper;
import org.example.homeservice_platform.model.ServiceOrder;
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
    
    @Autowired
    private ServiceOrderMapper orderMapper;
    
    @Override
    public Object login(UserLoginDTO loginDTO) {
        // 账号密码登录
        return accountLogin(loginDTO.getUsername(), loginDTO.getPassword(), loginDTO.getRole());
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
    
    @Override
    public UserInfo register(String username, String password, String phone, String role) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<UserInfo> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(UserInfo::getUsername, username);
        UserInfo existUser = userInfoMapper.selectOne(usernameWrapper);
        if (existUser != null) {
            throw new BusinessException(400, "用户名已存在");
        }
        
        // 检查手机号是否已存在
        LambdaQueryWrapper<UserInfo> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(UserInfo::getPhone, phone);
        existUser = userInfoMapper.selectOne(phoneWrapper);
        if (existUser != null) {
            throw new BusinessException(400, "手机号已被注册");
        }
        
        // 创建新用户
        UserInfo user = new UserInfo();
        user.setUsername(username);
        user.setPassword(password); // 明文存储（根据需求文档）
        user.setPhone(phone);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userInfoMapper.insert(user);
        
        return user;
    }
    
    @Override
    public Object accountLogin(String username, String password, String role) {
        // 查询用户（支持用户名或手机号登录）
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(UserInfo::getUsername, username).or().eq(UserInfo::getPhone, username))
               .eq(UserInfo::getRole, role);
        UserInfo user = userInfoMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        // 验证密码（明文比较，根据需求文档）
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (!password.equals(user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        // 生成token
        String token = UUID.randomUUID().toString().replace("-", "");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("role", user.getRole());
        result.put("username", user.getUsername());
        
        return result;
    }
    
    @Override
    public java.util.List<UserInfo> getUserList(String role, String keyword) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 角色筛选
        if (role != null && !role.isEmpty()) {
            wrapper.eq(UserInfo::getRole, role);
        }
        
        // 关键词搜索（用户名或手机号）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(UserInfo::getUsername, keyword)
                            .or()
                            .like(UserInfo::getPhone, keyword));
        }
        
        wrapper.orderByDesc(UserInfo::getCreatedAt);
        return userInfoMapper.selectList(wrapper);
    }
    
    @Override
    public org.example.homeservice_platform.dto.PageResult<UserInfo> getUserListPage(String role, String keyword, Long pageNum, Long pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserInfo> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 角色筛选
        if (role != null && !role.isEmpty()) {
            wrapper.eq(UserInfo::getRole, role);
        }
        
        // 关键词搜索（用户名或手机号）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(UserInfo::getUsername, keyword)
                            .or()
                            .like(UserInfo::getPhone, keyword));
        }
        
        wrapper.orderByDesc(UserInfo::getCreatedAt);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<UserInfo> result = userInfoMapper.selectPage(page, wrapper);
        return new org.example.homeservice_platform.dto.PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public boolean deleteUser(Long userId) {
        // 检查用户是否存在
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        // 检查是否有未完成的订单
        LambdaQueryWrapper<ServiceOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.and(w -> w.eq(ServiceOrder::getCustomerId, userId)
                            .or()
                            .eq(ServiceOrder::getWorkerId, userId))
                   .in(ServiceOrder::getStatus, "PENDING", "APPROVED", "IN_PROGRESS");
        long orderCount = orderMapper.selectCount(orderWrapper);
        
        if (orderCount > 0) {
            throw new BusinessException(400, "该用户有未完成的订单，无法删除");
        }
        
        return userInfoMapper.deleteById(userId) > 0;
    }
    
    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        // 验证原密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BusinessException(400, "用户未设置密码");
        }
        if (!oldPassword.equals(user.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }
        
        // 更新密码
        user.setPassword(newPassword);
        user.setUpdatedAt(LocalDateTime.now());
        return userInfoMapper.updateById(user) > 0;
    }
}
