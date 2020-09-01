package com.autotest.eagle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author wuranxu
 * @date 2020/9/1 11:06 上午
 */
@Data
@TableName("t_eagle_project")
public class Project {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("project_name")
    @NotNull(message = "项目名称不能为空")
    private String projectName;

    @TableField("gitlab_url")
    @NotNull(message = "关联gitlab不能为空")
    private String gitlabUrl;

    @TableField("description")
    private String description;

    @TableField("owner")
    @NotNull(message = "项目负责人不能为空")
    private Long owner;

    @TableField("creator")
    private Long creator;

    @TableField("updater")
    private Long updater;

    @TableField("create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField("update_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableField("disabled")
    private Boolean disabled;

    @TableField(exist = false)
    private ProjectRole projectRole;
}
