package com.censoft.system.resolver;

import javax.servlet.http.HttpServletRequest;

import com.censoft.common.log.enums.OperatorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.censoft.common.annotation.LoginUser;
import com.censoft.common.constant.Constants;
import com.censoft.system.domain.SysUser;
import com.censoft.system.service.ISysUserService;

/**
 * 有@LoginUser注解的方法参数，注入当前登录用户
 */
@Configuration
public class LoginUserHandlerResolver implements HandlerMethodArgumentResolver
{
    @Autowired
    private ISysUserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter)
    {
        return parameter.getParameterType().isAssignableFrom(SysUser.class)
                && parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
            NativeWebRequest nativeWebRequest, WebDataBinderFactory factory) throws Exception
    {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        // 获取用户ID
        Long userid = Long.valueOf(request.getHeader(Constants.CURRENT_ID));
        String  userType =request.getHeader("userType");

        if (userid == null && userType==null)
        {
            return null;
        }
        if(userType.equals(OperatorType.EDUCATION.toString()) || userType.equals(OperatorType.FCENTER.toString()) || userType.equals(OperatorType.GRID.toString())){
            SysUser sysUser = userService.selectUserById(0L);
            String loginName = request.getHeader("current_username");
            sysUser.setUserName(loginName);
            sysUser.setLoginName(loginName);
            return sysUser;
        }
        return userService.selectUserById(userid);
    }
}
