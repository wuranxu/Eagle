package com.autotest.eagle.entity;

import com.autotest.eagle.enums.ProjRole;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wuranxu
 * @date 2020/9/1 2:40 下午
 */

@Data
@TableName("t_eagle_project_role")
public class ProjectRole {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("project_id")
    @NotNull(message = "项目id不能为空")
    private Long projectId;

    @TableField("user_id")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @TableField("project_role")
    @NotNull(message = "角色不能为空")
    private ProjRole projRole;
}
