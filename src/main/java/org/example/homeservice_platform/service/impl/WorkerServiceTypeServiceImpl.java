package org.example.homeservice_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.homeservice_platform.common.BusinessException;
import org.example.homeservice_platform.mapper.UserInfoMapper;
import org.example.homeservice_platform.mapper.WorkerServiceTypeMapper;
import org.example.homeservice_platform.model.UserInfo;
import org.example.homeservice_platform.model.WorkerServiceType;
import org.example.homeservice_platform.service.WorkerServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务员服务类型服务实现类
 * @author system
 */
@Slf4j
@Service
public class WorkerServiceTypeServiceImpl implements WorkerServiceTypeService {
    
    @Autowired
    private WorkerServiceTypeMapper workerServiceTypeMapper;
    
    @Autowired
    private UserInfoMapper userInfoMapper;
    
    // 有效的服务类型
    private static final String[] VALID_SERVICE_TYPES = {"cleaning", "repair", "cooking", "babysitting"};
    
    @Override
    @Transactional
    public boolean setWorkerServiceTypes(Long workerId, List<String> serviceTypes) {
        // 验证服务员是否存在且角色正确
        UserInfo worker = userInfoMapper.selectById(workerId);
        if (worker == null || !"worker".equals(worker.getRole())) {
            throw new BusinessException(400, "服务员不存在");
        }
        
        // 验证服务类型有效性
        for (String serviceType : serviceTypes) {
            if (!isValidServiceType(serviceType)) {
                throw new BusinessException(400, "无效的服务类型: " + serviceType);
            }
        }
        
        // 先删除该服务员的所有服务类型
        LambdaQueryWrapper<WorkerServiceType> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(WorkerServiceType::getWorkerId, workerId);
        workerServiceTypeMapper.delete(deleteWrapper);
        
        // 批量插入新的服务类型
        if (serviceTypes != null && !serviceTypes.isEmpty()) {
            for (String serviceType : serviceTypes) {
                WorkerServiceType workerServiceType = new WorkerServiceType();
                workerServiceType.setWorkerId(workerId);
                workerServiceType.setServiceType(serviceType);
                workerServiceType.setCreatedAt(LocalDateTime.now());
                workerServiceType.setUpdatedAt(LocalDateTime.now());
                workerServiceTypeMapper.insert(workerServiceType);
            }
        }
        
        return true;
    }
    
    @Override
    public List<String> getWorkerServiceTypes(Long workerId) {
        LambdaQueryWrapper<WorkerServiceType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkerServiceType::getWorkerId, workerId);
        List<WorkerServiceType> list = workerServiceTypeMapper.selectList(wrapper);
        return list.stream()
                .map(WorkerServiceType::getServiceType)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public boolean removeWorkerServiceType(Long workerId, String serviceType) {
        LambdaQueryWrapper<WorkerServiceType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkerServiceType::getWorkerId, workerId)
               .eq(WorkerServiceType::getServiceType, serviceType);
        return workerServiceTypeMapper.delete(wrapper) > 0;
    }
    
    /**
     * 验证服务类型是否有效
     */
    private boolean isValidServiceType(String serviceType) {
        for (String validType : VALID_SERVICE_TYPES) {
            if (validType.equals(serviceType)) {
                return true;
            }
        }
        return false;
    }
}
