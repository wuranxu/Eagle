package com.autotest.eagle.aop;

/**
 * @author wuranxu
 * @date 2020/8/31 1:53 下午
 */

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.autotest.eagle.annotation.Permission;
import com.autotest.eagle.dto.Response;
import com.autotest.eagle.entity.User;
import com.autotest.eagle.enums.Role;
import com.autotest.eagle.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author wuranxu
 * @date 2020/7/20 3:12 下午
 */
@Aspect
@Component
public class PermissionAspect {

    private static final String FORBIDDEN = "对不起, 你没有足够的权限";
    private static final String EXPIRED = "身份认证已过期";

    @Resource
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    @Resource
    private UserService userService;


    private Response setResponse() {
        return Response.build(403, null, FORBIDDEN);
    }


    private Response expired() {
        return Response.build(401, null, EXPIRED);
    }

    //这里使用环绕通知切入自定义的注解即可
    @Around("@annotation(com.autotest.eagle.annotation.Permission)")
    public Object point(ProceedingJoinPoint point) {

        try {
            //1 获取当前执行的方法信息
            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            Method method = methodSignature.getMethod();

            //2 获取方法上的注解,如果有权限注解就验证
            Permission annotation = method.getAnnotation(Permission.class);

            if (annotation != null) {
                Role value = annotation.value();
                String token = request.getHeader("token");
                if (StringUtils.isEmpty(token)) {
                    return setResponse();
                }
                User user = userService.getUserByToken(token);
                request.setAttribute("user", user);
                if (user == null || user.getDisabled() || user.getRole().getValue() < value.getValue()) {
                    return setResponse();
                }
                return point.proceed();
            }

        } catch (TokenExpiredException e) {
            return expired();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
