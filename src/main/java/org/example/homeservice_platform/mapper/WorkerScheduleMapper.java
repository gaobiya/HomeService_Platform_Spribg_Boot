package org.example.homeservice_platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.homeservice_platform.model.WorkerSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务员日程Mapper接口
 * @author system
 */
@Mapper
public interface WorkerScheduleMapper extends BaseMapper<WorkerSchedule> {
}
