package org.example.homeservice_platform.service;

import java.util.List;

/**
 * 服务员服务类型服务接口
 * @author system
 */
public interface WorkerServiceTypeService {
    
    /**
     * 设置服务员服务类型（批量）
     * @param workerId 服务员ID
     * @param serviceTypes 服务类型列表
     * @return 是否成功
     */
    boolean setWorkerServiceTypes(Long workerId, List<String> serviceTypes);
    
    /**
     * 获取服务员的服务类型列表
     * @param workerId 服务员ID
     * @return 服务类型列表
     */
    List<String> getWorkerServiceTypes(Long workerId);
    
    /**
     * 删除服务员的服务类型
     * @param workerId 服务员ID
     * @param serviceType 服务类型
     * @return 是否成功
     */
    boolean removeWorkerServiceType(Long workerId, String serviceType);
}
