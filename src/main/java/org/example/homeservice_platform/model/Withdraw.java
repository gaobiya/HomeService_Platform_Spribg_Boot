package org.example.homeservice_platform.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录实体（简单版：仅记录申请，线下打款）
 * @author system
 */
@Data
@TableName("withdraw")
public class Withdraw {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 服务员ID */
    private Long workerId;
    /** 提现金额 */
    private BigDecimal amount;
    /** 状态：PENDING-待打款, DONE-已打款 */
    private String status;
    /** 申请时间 */
    private LocalDateTime createdAt;
}
