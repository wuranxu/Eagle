package com.autotest.eagle.service.impl;

import com.autotest.eagle.entity.Project;
import com.autotest.eagle.entity.ProjectRole;
import com.autotest.eagle.entity.User;
import com.autotest.eagle.enums.ProjRole;
import com.autotest.eagle.enums.Role;
import com.autotest.eagle.mapper.ProjectMapper;
import com.autotest.eagle.mapper.ProjectRoleMapper;
import com.autotest.eagle.service.ProjectService;
import com.autotest.eagle.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @author wuranxu
 * @date 2020/9/1 11:30 上午
 */
@Service("projectService")
public class ProjectServiceImpl implements ProjectService {

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private ProjectRoleMapper projectRoleMapper;

    @Resource
    private UserService userService;

    @Override
    public Boolean insertProject(Project project, Long user) throws Exception {
        project.setCreateTime(new Date());
        project.setUpdateTime(new Date());
        project.setCreator(user);
        project.setUpdater(user);
        project.setDisabled(false);
        if (projectMapper.insert(project) == 0) {
            throw new Exception("添加项目失败");
        }
        // 自动将管理员设置为项目组长
        ProjectRole role = new ProjectRole();
        role.setProjectId(project.getId());
        role.setProjRole(ProjRole.OWNER);
        role.setUserId(project.getOwner());
        return projectRoleMapper.insert(role) > 0;
    }

    @Override
    public Boolean updateProject(Project project, Long user) throws Exception {
        User updater = userService.getUserById(user);
        if (project.getId() == null) {
            throw new Exception("请传入project_id");
        }
        if (updater.getRole().getValue() < Role.SuperAdmin.getValue() && !Objects.equals(updater.getId(), project.getOwner())) {
            throw new Exception("你没有权限修改");
        }
        project.setUpdater(updater.getId());
        project.setUpdateTime(new Date());
        return projectMapper.updateById(project) > 0;
    }

    @Override
    public Boolean deleteProject(Long projectId, Long user) {
        Project project = new Project();
        project.setUpdater(user);
        project.setUpdateTime(new Date());
        UpdateWrapper<Project> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", projectId);
        return projectMapper.update(project, wrapper) > 0;
    }

    @Override
    public IPage<Project> listProject(IPage<Project> page, Long user, String name) {
        QueryWrapper<Project> query = new QueryWrapper<>();
        LambdaQueryWrapper<Project> wrapper = query.lambda().like(!Strings.isEmpty(name), Project::getProjectName, name).orderByDesc(Project::getId);
        return projectMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<Project> listProjectByUser(IPage<Project> page, Long user, String name) {
        QueryWrapper<Project> query = new QueryWrapper<>();
        LambdaQueryWrapper<Project> wrapper = query.lambda().like(!Strings.isEmpty(name), Project::getProjectName, name).orderByDesc(Project::getId);
        return projectMapper.listProjectByUser(page, user, wrapper);
    }
}
