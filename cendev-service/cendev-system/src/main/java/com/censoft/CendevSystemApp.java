package com.censoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.censoft.system.annotation.EnableCenFeignClients;

import tk.mybatis.spring.annotation.MapperScan;

/**
 * 启动程序
 * 
 * @author censoft
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaClient
@EnableCenFeignClients
@MapperScan("com.censoft.*.mapper")
public class CendevSystemApp
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(CendevSystemApp.class, args);
    }
}