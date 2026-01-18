package org.example.homeservice_platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 家政服务预约与派单平台主应用类
 * @author system
 */
@SpringBootApplication
@MapperScan("org.example.homeservice_platform.mapper")
public class HomeServicePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeServicePlatformApplication.class, args);
    }

}
