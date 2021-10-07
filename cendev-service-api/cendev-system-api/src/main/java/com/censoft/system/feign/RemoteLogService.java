package com.censoft.system.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.censoft.common.constant.ServiceNameConstants;
import com.censoft.system.domain.SysLogininfor;
import com.censoft.system.domain.SysOperLog;
import com.censoft.system.feign.factory.RemoteLogFallbackFactory;

/**
 * 日志Feign服务层
 * 
 * @author censoft
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteLogFallbackFactory.class)
public interface RemoteLogService
{
    @PostMapping("operLog/save")
    public void insertOperlog(@RequestBody SysOperLog operLog);

    @PostMapping("logininfor/save")
    public void insertLoginlog(@RequestBody SysLogininfor logininfor);
}
