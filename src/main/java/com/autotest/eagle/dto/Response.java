package com.autotest.eagle.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

    public static Response build(int code, Object data, String message) {
        return new Response(code, data, message);
    }
}
