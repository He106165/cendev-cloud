package com.censoft.system.feign;

import com.censoft.common.constant.ServiceNameConstants;
import com.censoft.system.feign.factory.RemoteMenuFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

/**
 * 菜单 Feign服务层
 * 
 * @author censoft
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteMenuFallbackFactory.class)
public interface RemoteMenuService
{
    @GetMapping("menu/perms/{userId}")
    public Set<String> selectPermsByUserId(@PathVariable("userId") Long userId);
}
