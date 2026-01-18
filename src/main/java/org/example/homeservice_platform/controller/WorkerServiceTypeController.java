package org.example.homeservice_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.homeservice_platform.common.Result;
import org.example.homeservice_platform.dto.WorkerServiceTypeSetDTO;
import org.example.homeservice_platform.service.WorkerServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务员服务类型控制器
 * @author system
 */
@Tag(name = "服务员服务类型模块", description = "服务员服务类型管理功能")
@RestController
@RequestMapping("/api/worker/service-type")
public class WorkerServiceTypeController {
    
    @Autowired
    private WorkerServiceTypeService workerServiceTypeService;
    
    /**
     * 设置服务员服务类型
     */
    @Operation(summary = "设置服务类型", description = "批量设置服务员可提供的服务类型")
    @PostMapping("/set")
    public Result<?> setWorkerServiceTypes(@Valid @RequestBody WorkerServiceTypeSetDTO dto) {
        boolean success = workerServiceTypeService.setWorkerServiceTypes(dto.getWorkerId(), dto.getServiceTypes());
        if (success) {
            return Result.success("设置成功");
        }
        return Result.error("设置失败");
    }
    
    /**
     * 获取服务员的服务类型列表
     */
    @Operation(summary = "获取服务类型列表", description = "查询服务员可提供的服务类型")
    @GetMapping("/{workerId}")
    public Result<List<String>> getWorkerServiceTypes(@PathVariable Long workerId) {
        List<String> serviceTypes = workerServiceTypeService.getWorkerServiceTypes(workerId);
        return Result.success(serviceTypes);
    }
    
    /**
     * 删除服务员的服务类型
     */
    @Operation(summary = "删除服务类型", description = "删除服务员指定的服务类型")
    @DeleteMapping("/{workerId}/{serviceType}")
    public Result<?> removeWorkerServiceType(@PathVariable Long workerId,
                                             @PathVariable String serviceType) {
        boolean success = workerServiceTypeService.removeWorkerServiceType(workerId, serviceType);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
