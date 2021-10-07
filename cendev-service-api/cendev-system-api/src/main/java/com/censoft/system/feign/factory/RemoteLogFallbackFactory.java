package com.censoft.system.feign.factory;

import org.springframework.stereotype.Component;

import com.censoft.system.domain.SysLogininfor;
import com.censoft.system.domain.SysOperLog;
import com.censoft.system.feign.RemoteLogService;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RemoteLogFallbackFactory implements FallbackFactory<RemoteLogService>
{
    @Override
    public RemoteLogService create(Throwable throwable)
    {
        log.error(throwable.getMessage());
        return new RemoteLogService()
        {
            @Override
            public void insertOperlog(SysOperLog operLog)
            {
            }

            @Override
            public void insertLoginlog(SysLogininfor logininfor)
            {
            }
        };
    }
}
