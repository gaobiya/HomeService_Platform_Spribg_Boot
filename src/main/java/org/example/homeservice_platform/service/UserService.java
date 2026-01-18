package org.example.homeservice_platform.service;

import org.example.homeservice_platform.dto.UserLoginDTO;
import org.example.homeservice_platform.model.UserInfo;

/**
 * 用户服务接口
 * @author system
 */
public interface UserService {
    
    /**
     * 用户登录（微信小程序）
     * @param loginDTO 登录信息
     * @return 用户信息和token
     */
    Object login(UserLoginDTO loginDTO);
    
    /**
     * 派单员登录（后台系统）
     * @param username 用户名
     * @param password 密码
     * @return 用户信息和token
     */
    Object dispatcherLogin(String username, String password);
    
    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfo getUserById(Long userId);
    
    /**
     * 更新用户信息
     * @param userInfo 用户信息
     * @return 是否成功
     */
    boolean updateUser(UserInfo userInfo);
    
    /**
     * 创建用户（微信登录时自动创建）
     * @param userInfo 用户信息
     * @return 用户信息
     */
    UserInfo createUser(UserInfo userInfo);
}
