package com.censoft.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.censoft.common.annotation.LoginUser;
import com.censoft.common.auth.annotation.HasPermissions;
import com.censoft.common.constant.UserConstants;
import com.censoft.common.core.controller.BaseController;
import com.censoft.common.core.domain.R;
import com.censoft.common.log.annotation.OperLog;
import com.censoft.common.log.enums.BusinessType;
import com.censoft.common.utils.RandomUtil;
import com.censoft.system.domain.SysUser;
import com.censoft.system.service.ISysMenuService;
import com.censoft.system.service.ISysUserService;
import com.censoft.system.util.PasswordUtil;

import java.util.Map;

/**
 * 用户 提供者
 *
 * @author censoft
 * @date 2019-05-20
 */
@RestController
@RequestMapping("user")
public class SysUserController extends BaseController
{
    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysMenuService sysMenuService;

    /**
     * 查询用户
     */
    @GetMapping("get/{userId}")
    public SysUser get(@PathVariable("userId") Long userId)
    {
        return sysUserService.selectUserById(userId);
    }

    @GetMapping("info")
    public SysUser info(@LoginUser SysUser sysUser)
    {
        if(sysUser.getUserId()==0L){
            sysUser.setButtons(sysMenuService.selectPermsByUserId(0L));
        }else{
            sysUser.setButtons(sysMenuService.selectPermsByUserId(sysUser.getUserId()));
        }
        return sysUser;
    }

    /**
     * 查询用户
     */
    @GetMapping("find/{username}")
    public SysUser findByUsername(@PathVariable("username") String username)
    {
        return sysUserService.selectUserByLoginName(username);
    }


    @PostMapping("getInsideUserById")
    public Map getInsideUserById(@RequestParam("userId") Long userId){
        return sysUserService.getInsideUserById(userId);
    }
    /**
     * 查询用户列表
     */
    @GetMapping("list")
    public R list(SysUser sysUser)
    {
        startPage();
        return result(sysUserService.selectUserList(sysUser));
    }

    /**
     * 新增保存用户
     */
    @PostMapping("save")
    @OperLog(title = "用户管理", businessType = BusinessType.INSERT)
    public R addSave(@RequestBody SysUser sysUser)
    {
        if (UserConstants.USER_NAME_NOT_UNIQUE.equals(sysUserService.checkLoginNameUnique(sysUser.getLoginName())))
        {
            return R.error("新增用户'" + sysUser.getLoginName() + "'失败，登录账号已存在");
        }
        /*else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(sysUserService.checkPhoneUnique(sysUser)))
        {
            return R.error("新增用户'" + sysUser.getLoginName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(sysUserService.checkEmailUnique(sysUser)))
        {
            return R.error("新增用户'" + sysUser.getLoginName() + "'失败，邮箱账号已存在");
        }*/
        sysUser.setSalt(RandomUtil.randomStr(6));
        sysUser.setPassword(
                PasswordUtil.encryptPassword(sysUser.getLoginName(), sysUser.getPassword(), sysUser.getSalt()));
        sysUser.setCreateBy(getLoginName());
        sysUser.setDelFlag("0");
        return toAjax(sysUserService.insertUser(sysUser));
    }

    /**
     * 修改保存用户
     */
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("update")
    public R editSave(@RequestBody SysUser sysUser)
    {
        if (null != sysUser.getUserId() && SysUser.isAdmin(sysUser.getUserId()))
        {
            return R.error("不允许修改超级管理员用户");
        }
       /* else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(sysUserService.checkPhoneUnique(sysUser)))
        {
            return R.error("修改用户'" + sysUser.getLoginName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(sysUserService.checkEmailUnique(sysUser)))
        {
            return R.error("修改用户'" + sysUser.getLoginName() + "'失败，邮箱账号已存在");
        }*/
        return toAjax(sysUserService.updateUser(sysUser));
    }

    /**
     * 修改保存用户
     */
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("updateInsideUser")
    public R updateInsideUser(@RequestBody SysUser sysUser)
    {
        if (null != sysUser.getUserId() && SysUser.isAdmin(sysUser.getUserId()))
        {
            return R.error("不允许修改超级管理员用户");
        }
       /* else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(sysUserService.checkPhoneUnique(sysUser)))
        {
            return R.error("修改用户'" + sysUser.getLoginName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(sysUserService.checkEmailUnique(sysUser)))
        {
            return R.error("修改用户'" + sysUser.getLoginName() + "'失败，邮箱账号已存在");
        }*/
        return toAjax(sysUserService.updateInsideUser(sysUser));
    }

    /**
     * 修改用户信息
     * @param sysUser
     * @return
     * @author censoft
     */
    @HasPermissions("system:user:edit")
    @PostMapping("update/info")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    public R updateInfo(@RequestBody SysUser sysUser)
    {
        return toAjax(sysUserService.updateUserInfo(sysUser));
    }

    /**
     * 记录登陆信息
     * @param sysUser
     * @return
     * @author censoft
     */
    @PostMapping("update/login")
    public R updateLoginRecord(@RequestBody SysUser sysUser)
    {
        return toAjax(sysUserService.updateUser(sysUser));
    }

    @HasPermissions("system:user:resetPwd")
    @OperLog(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    public R resetPwdSave(@RequestBody SysUser user)
    {
        user.setSalt(RandomUtil.randomStr(6));
        user.setPassword(PasswordUtil.encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
        return toAjax(sysUserService.resetUserPwd(user));
    }

    /**
     * 修改状态
     * @param sysUser
     * @return
     * @author censoft
     */
    @HasPermissions("system:user:edit")
    @PostMapping("status")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    public R status(@RequestBody SysUser sysUser)
    {
        return toAjax(sysUserService.changeStatus(sysUser));
    }

    /**
     * 删除用户
     * @throws Exception
     */
    @HasPermissions("system:user:remove")
    @OperLog(title = "用户管理", businessType = BusinessType.DELETE)
    @PostMapping("remove")
    public R remove(String ids) throws Exception
    {
        return toAjax(sysUserService.deleteUserByIds(ids));
    }
}
