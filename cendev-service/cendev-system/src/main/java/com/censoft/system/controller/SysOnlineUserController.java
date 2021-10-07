/**
 * Copyright (C), 2018-2019, 中关村科技软件股份有限公司
 * FileName: SysOnlineUserController
 * Author:   XG-X1
 * Date:     2019/11/8 14:32
 * Description: 获取在线用户
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.censoft.system.controller;

import com.censoft.common.auth.annotation.HasPermissions;
import com.censoft.common.core.controller.BaseController;
import com.censoft.common.core.domain.R;
import com.censoft.system.domain.SysUser;
import com.censoft.system.service.impl.SysOnlineUserRedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 〈一句话功能简述〉<br> 
 * 〈获取在线用户〉
 *
 * @author XG-X1
 * @create 2019/11/8
 * @since 1.0.0
 */
@RestController
@RequestMapping("onlineUser")
public class SysOnlineUserController extends BaseController {

    @Autowired
    private SysOnlineUserRedisServiceImpl sysOnlineUserRedisServiceImpl;
    /**
     * 查询在线用户列表
     */
    @HasPermissions("monitor:online:list")
    @RequestMapping("list")
    public R list(SysUser sysUser)
    {
        startPage();
        return result(sysOnlineUserRedisServiceImpl.selectOnlineUserList(sysUser));
    }
    /**
     * 踢除用户
     */
    @HasPermissions("monitor:online:forceLogout")
    @GetMapping("remove")
    public R remove(String userId)
    {
        return toAjax(sysOnlineUserRedisServiceImpl.deleteOnlineUserByUserId(userId));
    }
//    public R remove(String token)
//    {
//        return toAjax(sysOnlineUserRedisServiceImpl.deleteOnlineUserByToken(token));
//    }

}