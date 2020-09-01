package com.autotest.eagle.controller;

import com.autotest.eagle.annotation.Permission;
import com.autotest.eagle.dto.Response;
import com.autotest.eagle.dto.UserDto;
import com.autotest.eagle.entity.User;
import com.autotest.eagle.enums.Role;
import com.autotest.eagle.middleware.Jwt;
import com.autotest.eagle.service.UserService;
import com.autotest.eagle.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author wuranxu
 * @date 2020/8/31 3:16 下午
 */
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserApi {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Response registerUser(HttpServletRequest request, @Valid @RequestBody User user, BindingResult results) {
        Response err = RequestUtil.validate(results);
        if (err != null) {
            return err;
        }
        Boolean result;
        try {
            String ip = RequestUtil.getIpAddress(request);
            result = userService.registerUser(user, ip);
        } catch (DuplicateKeyException e) {
            return Response.build(503, null, "注册失败, 该用户已存在");
        } catch (Exception e) {
            log.error("business => 用户服务: 注册用户失败, error: " + e);
            return Response.build(503, null, "注册失败");
        }
        if (!result) {
            return Response.build(503, null, "注册失败");
        }
        return Response.build(200, user, "注册成功");
    }

    @PostMapping("/login")
    public Response login(HttpServletRequest request, @Valid @RequestBody UserDto user, BindingResult results) {
        Response err = RequestUtil.validate(results);
        if (err != null) {
            return err;
        }
        User data;
        try {
            String ip = RequestUtil.getIpAddress(request);
            data = userService.login(user, ip);
            if (data == null) {
                return Response.build(501, null, "账号或密码错误");
            }
            String token = Jwt.createToken(data.getId(), data.getName());
            if (token == null) {
                return Response.build(500, null, "登录失败");
            }
            data.setToken(token);
        } catch (Exception e) {
            log.error("business => 用户服务 登录失败, error: " + e.getMessage());
            return Response.build(500, null, e.getMessage());
        }
        return Response.build(200, data, "登录成功");
    }

    @GetMapping("/forbidden/{userId}")
    @Permission(Role.SuperAdmin)
    public Response forbidden(@PathVariable("userId") String userId) {
        try {
            Long value = Long.valueOf(userId);
            Boolean result = userService.forbiddenUser(value);
            if (result) {
                return Response.build(500, null, "禁用失败");
            }
            return Response.build(200, null, "禁用成功");
        } catch (NumberFormatException e) {
            return Response.build(501, null, "传入用户id不合法");
        }
    }
}
