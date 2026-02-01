package org.example.homeservice_platform.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息实体类
 * @author system
 */
@Data
@TableName("user_info")
public class UserInfo {
    
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户昵称
     */
    private String username;
    
    /**
     * 用户角色：customer-客户, worker-服务员, dispatcher-派单员
     */
    private String role;
    
    /**
     * 密码（派单员使用）
     */
    private String password;
    
    /**
     * 用户头像路径
     */
    private String avatarUrl;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 服务员余额（仅服务员使用，客户支付成功后入账）
     */
    private BigDecimal balance;
    
    /**
     * 注册时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
