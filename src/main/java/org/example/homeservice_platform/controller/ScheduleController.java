package org.example.homeservice_platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.homeservice_platform.common.Result;
import org.example.homeservice_platform.dto.ScheduleAddDTO;
import org.example.homeservice_platform.model.ServiceOrder;
import org.example.homeservice_platform.model.WorkerSchedule;
import org.example.homeservice_platform.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日程管理控制器
 * @author system
 */
@Tag(name = "日程管理模块", description = "服务员日程管理功能")
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    
    @Autowired
    private ScheduleService scheduleService;
    
    /**
     * 添加可服务时间段
     */
    @Operation(summary = "添加日程", description = "添加服务员可服务时间段")
    @PostMapping("/add")
    public Result<?> addSchedule(@Valid @RequestBody ScheduleAddDTO dto) {
        boolean success = scheduleService.addSchedule(dto.getWorkerId(), dto.getStartTime(), dto.getEndTime());
        if (success) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }
    
    /**
     * 删除日程
     */
    @Operation(summary = "删除日程", description = "删除服务员指定的日程")
    @DeleteMapping("/{scheduleId}")
    public Result<?> deleteSchedule(@PathVariable Long scheduleId,
                                    @RequestParam Long workerId) {
        boolean success = scheduleService.deleteSchedule(scheduleId, workerId);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
    
    /**
     * 获取服务员的日程列表
     */
    @Operation(summary = "获取日程列表", description = "查询服务员的所有可服务时间段")
    @GetMapping("/worker/{workerId}")
    public Result<List<WorkerSchedule>> getWorkerSchedules(@PathVariable Long workerId) {
        List<WorkerSchedule> schedules = scheduleService.getWorkerSchedules(workerId);
        return Result.success(schedules);
    }
    
    /**
     * 获取服务员已安排的订单
     */
    @Operation(summary = "获取已安排订单", description = "查询服务员已安排的订单（用于查看时间冲突）")
    @GetMapping("/worker/{workerId}/orders")
    public Result<List<ServiceOrder>> getWorkerScheduledOrders(@PathVariable Long workerId) {
        List<ServiceOrder> orders = scheduleService.getWorkerScheduledOrders(workerId);
        return Result.success(orders);
    }
}
