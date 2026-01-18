package org.example.homeservice_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.homeservice_platform.common.Result;
import org.example.homeservice_platform.dto.ChangePasswordDTO;
import org.example.homeservice_platform.dto.UserLoginDTO;
import org.example.homeservice_platform.dto.UserRegisterDTO;
import org.example.homeservice_platform.model.UserInfo;
import org.example.homeservice_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;
    
    /**
     * 用户注册（账号密码方式）
     */
    @Operation(summary = "用户注册", description = "客户或服务员账号注册")
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        UserInfo user = userService.register(
            registerDTO.getUsername(),
            registerDTO.getPassword(),
            registerDTO.getPhone(),
            registerDTO.getRole()
        );
        return Result.success("注册成功", user);
    }
    
    /**
     * 用户登录（账号密码方式）
     */
    @Operation(summary = "用户登录", description = "客户或服务员账号密码登录（小程序使用）")
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
    
    /**
     * 获取用户列表（支持角色筛选和搜索）
     */
    @Operation(summary = "获取用户列表", description = "获取用户列表，支持按角色筛选和搜索")
    @GetMapping("/list")
    public Result<List<UserInfo>> getUserList(@RequestParam(required = false) String role,
                                               @RequestParam(required = false) String keyword) {
        List<UserInfo> users = userService.getUserList(role, keyword);
        return Result.success(users);
    }
    
    /**
     * 根据ID获取用户详情
     */
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @GetMapping("/{userId}")
    public Result<UserInfo> getUserById(@PathVariable Long userId) {
        UserInfo user = userService.getUserById(userId);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        return Result.success(user);
    }
    
    /**
     * 删除用户
     */
    @Operation(summary = "删除用户", description = "删除用户（需要检查是否有未完成订单）")
    @DeleteMapping("/{userId}")
    public Result<?> deleteUser(@PathVariable Long userId) {
        boolean success = userService.deleteUser(userId);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败，该用户可能有未完成的订单");
    }
    
    /**
     * 上传头像
     */
    @Operation(summary = "上传头像", description = "上传用户头像")
    @PostMapping("/upload-avatar")
    public Result<?> uploadAvatar(@RequestParam Long userId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        
        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath + "/avatars");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
            String filename = UUID.randomUUID().toString() + extension;
            String filePath = uploadPath + "/avatars/" + filename;
            
            // 保存文件
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());
            
            // 更新用户头像URL
            UserInfo user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 相对路径，用于前端访问
            String avatarUrl = "/uploads/avatars/" + filename;
            user.setAvatarUrl(avatarUrl);
            userService.updateUser(user);
            
            Map<String, Object> result = new HashMap<>();
            result.put("avatarUrl", avatarUrl);
            
            return Result.success("上传成功", result);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @Operation(summary = "修改密码", description = "修改用户密码")
    @PutMapping("/change-password")
    public Result<?> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        boolean success = userService.changePassword(
            changePasswordDTO.getUserId(),
            changePasswordDTO.getOldPassword(),
            changePasswordDTO.getNewPassword()
        );
        if (success) {
            return Result.success("密码修改成功");
        }
        return Result.error("密码修改失败，原密码错误");
    }
}
