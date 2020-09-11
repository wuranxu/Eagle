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
import java.util.List;

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
            return Response.error("注册失败, 用户邮箱/手机号/用户名重复");
        } catch (Exception e) {
            log.error("business => 用户服务: 注册用户失败, error: " + e);
            return Response.error("注册失败");
        }
        if (!result) {
            return Response.error("注册失败");
        }
        user.setToken(Jwt.createToken(user.getId(), user.getName()));
        return Response.success(user, "注册成功");
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
                return Response.error("账号或密码错误");
            }
            String token = Jwt.createToken(data.getId(), data.getName());
            if (token == null) {
                return Response.error("登录失败");
            }
            data.setToken(token);
        } catch (Exception e) {
            log.error("business => 用户服务 登录失败, error: " + e.getMessage());
            return Response.error(e.getMessage());
        }
        return Response.success(data, "登录成功");
    }

    @GetMapping("/forbidden/{userId}")
    @Permission(Role.SuperAdmin)
    public Response forbidden(@PathVariable("userId") String userId) {
        try {
            Long value = Long.valueOf(userId);
            Boolean result = userService.forbiddenUser(value);
            if (result) {
                return Response.error("禁用失败");
            }
            return Response.success("禁用成功");
        } catch (NumberFormatException e) {
            return Response.error("传入用户id不合法");
        }
    }

    @GetMapping("/list")
    @Permission(Role.Guest)
    public Response listUser() {
        List<User> users = userService.listUser();
        return Response.success(users, "操作成功");
    }
}
