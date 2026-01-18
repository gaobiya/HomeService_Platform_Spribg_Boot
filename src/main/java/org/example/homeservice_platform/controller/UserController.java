package org.example.homeservice_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.homeservice_platform.common.Result;
import org.example.homeservice_platform.dto.UserLoginDTO;
import org.example.homeservice_platform.model.UserInfo;
import org.example.homeservice_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * @author system
 */
@Tag(name = "用户模块", description = "用户登录、注册、个人信息管理")
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录（微信小程序）
     */
    @Operation(summary = "用户登录", description = "微信小程序用户登录")
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        Object result = userService.login(loginDTO);
        return Result.success("登录成功", result);
    }
    
    /**
     * 派单员登录（后台系统）
     */
    @Operation(summary = "派单员登录", description = "后台管理系统派单员登录")
    @PostMapping("/dispatcher/login")
    public Result<?> dispatcherLogin(@RequestParam String username, @RequestParam String password) {
        Object result = userService.dispatcherLogin(username, password);
        return Result.success("登录成功", result);
    }
    
    /**
     * 获取用户信息
     */
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    @GetMapping("/info")
    public Result<UserInfo> getUserInfo(@RequestParam Long userId) {
        UserInfo user = userService.getUserById(userId);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        return Result.success(user);
    }
    
    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息", description = "更新用户个人信息")
    @PutMapping("/update")
    public Result<?> updateUser(@RequestBody UserInfo userInfo) {
        boolean success = userService.updateUser(userInfo);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }
}
