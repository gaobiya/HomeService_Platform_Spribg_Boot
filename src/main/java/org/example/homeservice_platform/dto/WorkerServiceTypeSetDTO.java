package org.example.homeservice_platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 设置服务员服务类型DTO
 * @author system
 */
@Data
public class WorkerServiceTypeSetDTO {
    
    @NotNull(message = "服务员ID不能为空")
    private Long workerId;
    
    @NotNull(message = "服务类型列表不能为空")
    private List<String> serviceTypes;
}
