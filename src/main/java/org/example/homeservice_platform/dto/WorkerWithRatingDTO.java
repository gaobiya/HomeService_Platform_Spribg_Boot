package org.example.homeservice_platform.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 服务员信息DTO（包含评分）
 * @author system
 */
@Data
public class WorkerWithRatingDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 角色
     */
    private String role;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 用户头像路径
     */
    private String avatarUrl;
    
    /**
     * 平均评分
     */
    private Double averageRating;
    
    /**
     * 评价数量
     */
    private Integer ratingCount;
}
