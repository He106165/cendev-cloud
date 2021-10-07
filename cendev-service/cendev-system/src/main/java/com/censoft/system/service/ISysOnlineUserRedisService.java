package com.censoft.system.service;

import com.censoft.system.domain.SysUser;

import java.util.List;

public interface ISysOnlineUserRedisService {

    /**
     * 查询系统在线用户
     *
     * @return 用户合计
     */
    public List<SysUser> selectOnlineUserList(SysUser sysUser);
    /**
     * 批量踢除用户
     *
     * @param token 需要踢除的用户的token
     * @return 结果
     */
    public int deleteOnlineUserByToken(String token);
    /**
     * 批量踢除用户
     *
     * @param userId 需要踢除的用户的userid
     * @return 结果
     */
    public boolean deleteOnlineUserByUserId(String userId);


}
