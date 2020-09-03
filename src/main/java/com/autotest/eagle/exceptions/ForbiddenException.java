package com.autotest.eagle.exceptions;

/**
 * @author wuranxu
 * @date 2020/9/3 11:37 上午
 */
public class ForbiddenException extends Exception {

    public ForbiddenException() {
        super("对不起, 你没有权限");
    }
}
