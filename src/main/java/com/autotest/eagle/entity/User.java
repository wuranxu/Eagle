package com.autotest.eagle.entity;

import com.autotest.eagle.enums.Role;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author wuranxu
 * @date 2020/8/31 3:03 下午
 */
@Data
@TableName("t_eagle_user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("email")
    @NotNull(message = "用户邮箱不能为空")
    private String email;

    @TableField("nickname")
    @NotNull(message = "用户昵称不能为空")
    private String nickname;

    @TableField("password")
    @NotNull(message = "用户密码不能为空")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @TableField("name")
    @NotNull(message = "用户名不能为空")
    private String name;

    @TableField("tel")
    @NotNull(message = "用户手机号不能为空")
    private String tel;

    @TableField("role")
    private Role role;

    @TableField("create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField("last_login_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    @TableField("last_login_ip")
    private String lastLoginIp;

    @TableField("avatar")
    private String avatar;

    @TableField("disabled")
    private Boolean disabled;

    @TableField(exist = false)
    private String token;


}
