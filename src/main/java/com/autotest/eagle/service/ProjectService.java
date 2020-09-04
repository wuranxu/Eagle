package com.autotest.eagle.service;

import com.autotest.eagle.entity.Project;
import com.autotest.eagle.enums.ProjRole;
import com.autotest.eagle.exceptions.ForbiddenException;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.InputStream;

/**
 * @author wuranxu
 * @date 2020/9/1 11:27 上午
 */
public interface ProjectService {

    Boolean insertProject(Project project, Long user) throws Exception;

    Boolean updateProject(Project project, Long user) throws Exception;

    Boolean updateProjectAvatar(Long id, Long user, String filename) throws Exception;

    Boolean deleteProject(Long projectId, Long user);

    IPage<Project> listProject(IPage<Project> page, Long user, String name);

    IPage<Project> listProjectByUser(IPage<Project> page, Long user, String name);

    Project queryProjectById(Long id, Long user) throws ForbiddenException;

    // 删除项目成员
    Boolean deleteProjectMember(Long projectId, Long user);

    // 添加项目成员
    Boolean insertProjectMember(Long projectId, Long user, ProjRole role);

    // 修改项目成员
    Boolean updateProjectMember(Long projectId, Long user, ProjRole role);

    // 上传项目图片
    Boolean uploadProjectPic(String filename, InputStream inputStream);
}
