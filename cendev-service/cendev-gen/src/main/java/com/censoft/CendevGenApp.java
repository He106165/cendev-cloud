package com.censoft;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.censoft.system.annotation.EnableCenFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.censoft.*.mapper")
@EnableCenFeignClients
public class CendevGenApp
{
    public static void main(String[] args)
    {
        SpringApplication.run(CendevGenApp.class, args);
    }
}
