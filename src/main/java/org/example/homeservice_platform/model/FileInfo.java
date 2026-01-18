package org.example.homeservice_platform.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息实体类
 * @author system
 */
@Data
@TableName("file_info")
public class FileInfo {
    
    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 上传用户ID
     */
    private Long userId;
    
    /**
     * 文件类型：avatar-头像, qualification-资质
     */
    private String fileType;
    
    /**
     * 文件存储路径
     */
    private String filePath;
    
    /**
     * 原始文件名
     */
    private String fileName;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 上传时间
     */
    private LocalDateTime uploadedAt;
}
