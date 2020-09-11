package com.autotest.eagle.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * @author wuranxu
 * @date 2020/8/31 2:36 下午
 */

@AllArgsConstructor
@Getter
@Setter
public class Response {
    private int code;
    private Object data;
    private String message;
    private static final String FORBIDDEN = "对不起, 你没有权限";
    private static final String LOGIN_REQUIRED = "请先登录...";
    private static final String EXPIRED = "身份认证已过期";
    private static final String LOGIN_FORBIDDEN = "用户已被封禁, 请联系管理员";

    public static Response build(int code, Object data, String message) {
        return new Response(code, data, message);
    }

    public static Response notLogin() {
        return new Response(HttpStatus.UNAUTHORIZED.value(), null, LOGIN_REQUIRED);
    }

    public static Response notAllowed() {
        return new Response(HttpStatus.FORBIDDEN.value(), null, LOGIN_FORBIDDEN);
    }


    public static Response expired() {
        return new Response(HttpStatus.UNAUTHORIZED.value(), null, EXPIRED);
    }


    public static Response success(Object data, String msg) {
        return new Response(HttpStatus.OK.value(), data, msg);
    }

    public static Response success(String msg) {
        return new Response(HttpStatus.OK.value(), null, msg);
    }

    public static Response error(Object data, String msg) {
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), data, msg);
    }

    public static Response error(String msg) {
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, msg);
    }

    public static Response forbidden() {
        return new Response(HttpStatus.FORBIDDEN.value(), null, FORBIDDEN);
    }
}
