package com.autotest.eagle.mapper;

import com.autotest.eagle.entity.Project;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.*;

/**
 * @author wuranxu
 * @date 2020/9/1 11:30 上午
 */
public interface ProjectMapper extends BaseMapper<Project> {

    @Select("select * from t_eagle_project where id in (SELECT project_id FROM t_eagle_project_role where user_id = #{userId}) and project_name like '%${projectName}%'")
    IPage<Project> listProjectByUser(IPage<Project> page, @Param("userId") Long userId, @Param("projectName") String projectName);
}
