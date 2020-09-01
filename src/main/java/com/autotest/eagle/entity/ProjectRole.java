package com.autotest.eagle.entity;

import com.autotest.eagle.enums.ProjRole;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    private Long projectId;

    @TableField("user_id")
    private Long userId;

    @TableField("project_role")
    private ProjRole projRole;
}
