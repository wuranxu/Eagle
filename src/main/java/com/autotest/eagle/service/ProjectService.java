package com.autotest.eagle.service;

import com.autotest.eagle.entity.Project;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author wuranxu
 * @date 2020/9/1 11:27 上午
 */
public interface ProjectService {

    Boolean insertProject(Project project, Long user) throws Exception;

    Boolean updateProject(Project project, Long user) throws Exception;

    Boolean deleteProject(Long projectId, Long user);

    IPage<Project> listProject(IPage<Project> page, Long user, String name);

    IPage<Project> listProjectByUser(IPage<Project> page, Long user, String name);
}
