package com.autotest.eagle.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wuranxu
 * @date 2020/8/31 5:52 下午
 */
@Data
public class UserDto {
    @NotNull(message = "用户名不能为空")
    private String name;

    @NotNull(message = "用户密码不能为空")
    private String password;

}
