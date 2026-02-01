package org.example.homeservice_platform.service;

import org.example.homeservice_platform.dto.PageResult;
import org.example.homeservice_platform.dto.UserLoginDTO;
import org.example.homeservice_platform.model.UserInfo;

import java.math.BigDecimal;

/**
 * 用户服务接口
 * @author system
 */
public interface UserService {
    
    /**
     * 用户登录（账号密码方式）
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
     * 创建用户
     * @param userInfo 用户信息
     * @return 用户信息
     */
    UserInfo createUser(UserInfo userInfo);
    
    /**
     * 用户注册（账号密码方式）
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @param role 角色
     * @return 用户信息
     */
    UserInfo register(String username, String password, String phone, String role);
    
    /**
     * 账号密码登录（客户和服务员）
     * @param username 用户名或手机号
     * @param password 密码
     * @param role 角色
     * @return 用户信息和token
     */
    Object accountLogin(String username, String password, String role);
    
    /**
     * 获取用户列表（支持角色筛选和搜索）
     * @param role 角色筛选（可选）
     * @param keyword 搜索关键词（用户名或手机号，可选）
     * @return 用户列表
     */
    java.util.List<UserInfo> getUserList(String role, String keyword);
    
    /**
     * 获取用户列表（分页，支持角色筛选和搜索）
     * @param role 角色筛选（可选）
     * @param keyword 搜索关键词（用户名或手机号，可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<UserInfo> getUserListPage(String role, String keyword, Long pageNum, Long pageSize);
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long userId);
    
    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 服务员提现（扣减余额并记录提现申请，简单版不做真实打款）
     * @param workerId 服务员ID
     * @param amount 提现金额
     * @return 是否成功
     */
    boolean withdraw(Long workerId, BigDecimal amount);
}
