/**
 * Copyright (C), 2018-2019, 中关村科技软件股份有限公司
 * FileName: SysOnlineUserRedisServiceImpl
 * Author:   XG-X1
 * Date:     2019/11/8 14:51
 * Description: 查询系统在线用户Redis
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.censoft.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.censoft.common.constant.Constants;
import com.censoft.common.log.publish.PublishFactory;
import com.censoft.common.redis.util.RedisUtils;
import com.censoft.system.domain.SysUser;
import com.censoft.system.service.ISysOnlineUserRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈查询系统在线用户Redis〉
 *
 * @author XG-X1
 * @create 2019/11/8
 * @since 1.0.0
 */
@Service
public class SysOnlineUserRedisServiceImpl implements ISysOnlineUserRedisService {
    @Autowired
    private RedisUtils redis;
    @Override
    public List<SysUser> selectOnlineUserList(SysUser sysUser) {
        List<SysUser> userList = new ArrayList<>();
        //从redis模糊匹配所有用户
        List<Object> jsonList= redis.getList(Constants.ACCESS_TOKEN );
        for (Object perm : jsonList)
        {
            SysUser tmpSysUser=JSONObject.toJavaObject((JSONObject)perm,SysUser.class);
            if(sysUser.getLoginName()!=null&&sysUser.getLoginName().equals(tmpSysUser.getLoginName()))
            {
                userList.add(tmpSysUser);
            }else if(sysUser.getLoginName()==null)
            {
                userList.add(tmpSysUser);
            }
        }
        return userList;
    }

    @Override
    public int deleteOnlineUserByToken(String token) {
        SysUser sysUser=redis.get(Constants.ACCESS_TOKEN + token, SysUser.class);
        if(sysUser!=null) {
            redis.delete(Constants.ACCESS_TOKEN + token);
            redis.delete(Constants.ACCESS_USERID + sysUser.getUserId());
            redis.delete(Constants.USER_PERMS + ":"+sysUser.getUserId());
            //记录踢除记录
            PublishFactory.recordLogininfor(sysUser.getLoginName(), Constants.KICK_LOGOUT, "强制退出");
            return 0;
        }
        return 1;
    }
    @Override
    public boolean deleteOnlineUserByUserId(String userId) {
        String token = redis.get(Constants.ACCESS_USERID + userId);
        if(token!=null) {
            SysUser sysUser=redis.get(Constants.ACCESS_TOKEN + token, SysUser.class);
            redis.delete(Constants.ACCESS_TOKEN + token);
            redis.delete(Constants.ACCESS_USERID + userId);
            redis.delete(Constants.USER_PERMS + ":"+userId);
            //记录踢除记录
            PublishFactory.recordLogininfor(sysUser.getLoginName(), Constants.KICK_LOGOUT, "强制退出");
            return true;
        }
        return false;
    }

}