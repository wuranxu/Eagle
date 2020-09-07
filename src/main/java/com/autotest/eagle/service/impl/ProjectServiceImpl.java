package com.autotest.eagle.service.impl;

import com.autotest.eagle.entity.Project;
import com.autotest.eagle.entity.ProjectRole;
import com.autotest.eagle.entity.User;
import com.autotest.eagle.enums.ProjRole;
import com.autotest.eagle.enums.Role;
import com.autotest.eagle.exceptions.ForbiddenException;
import com.autotest.eagle.mapper.ProjectMapper;
import com.autotest.eagle.mapper.ProjectRoleMapper;
import com.autotest.eagle.middleware.OssClient;
import com.autotest.eagle.service.ProjectService;
import com.autotest.eagle.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
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

    @Autowired
    private OssClient oss;

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
        return true;
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

    public Project update(Project project, Long user) {
        project.setUpdater(user);
        project.setUpdateTime(new Date());
        return project;
    }

    @Override
    public Boolean updateProjectAvatar(Long id, Long user, String filename) throws Exception {
        Project project = projectMapper.selectById(id);
        if (!Objects.equals(project.getOwner(), user) && !userService.isSuperAdmin(user)) {
            throw new ForbiddenException();
        }
        update(project, user);
        project.setAvatar(filename);
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
        return projectMapper.listProjectByUser(page, user, name);
    }

    @Override
    public Project queryProjectById(Long id, Long user) throws Exception {
        if (isOwnerOrAdmin(id, user)) {
            return projectMapper.selectById(id);
        }
        QueryWrapper<ProjectRole> query = new QueryWrapper<>();
        ProjectRole projectRole = projectRoleMapper.selectOne(query.lambda().eq(ProjectRole::getProjectId, id).eq(ProjectRole::getUserId, user));
        if (projectRole == null) {
            throw new ForbiddenException();
        }
        return projectMapper.selectById(id);
    }

    @Override
    public Boolean deleteProjectMember(Long user, Long projectId, Long uid) throws Exception {
        if (!isOwnerOrAdmin(projectId, user) && !isAdminForProject(projectId, user)) {
            throw new ForbiddenException();
        }
        QueryWrapper<ProjectRole> query = new QueryWrapper<>();
        return projectRoleMapper.delete(query.lambda().eq(ProjectRole::getProjectId, projectId).eq(ProjectRole::getUserId, uid)) > 0;
    }

    public boolean isOwnerOrAdmin(Long projectId, Long user) throws Exception {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new Exception("项目不存在");
        }
        return userService.isSuperAdmin(user) || Objects.equals(user, project.getOwner());
    }

    public boolean isAdminForProject(Long projectId, Long user) {
        QueryWrapper<ProjectRole> query = new QueryWrapper<>();
        ProjectRole projectRole = projectRoleMapper.selectOne(query.lambda().eq(ProjectRole::getProjectId, projectId).eq(ProjectRole::getUserId, user));
        return projectRole != null && projectRole.getProjRole() == ProjRole.ADMIN;
    }

    @Override
    public Boolean insertProjectMember(Long user, ProjectRole role) throws Exception {
        Project project = projectMapper.selectById(role.getProjectId());
        if (project == null) {
            throw new Exception("项目不存在");
        }
        if (Objects.equals(project.getOwner(), role.getUserId())) {
            throw new Exception("不能添加组长");
        }
        if (!userService.isSuperAdmin(user) && Objects.equals(user, project.getOwner()) && !isAdminForProject(role.getProjectId(), user)) {
            throw new ForbiddenException();
        }
        return projectRoleMapper.insert(role) > 0;
    }

    @Override
    public Boolean updateProjectMember(Long user, ProjectRole role) throws Exception {
        if (!isOwnerOrAdmin(role.getProjectId(), user) && !isAdminForProject(role.getProjectId(), user)) {
            throw new ForbiddenException();
        }
        return projectRoleMapper.updateById(role) > 0;
    }

    @Override
    public Boolean uploadProjectPic(String filename, InputStream inputStream) {
        return oss.uploadFile(filename, inputStream);
    }

    @Override
    public List<ProjectRole> listProjectRole(Long projectId) {
        QueryWrapper<ProjectRole> query = new QueryWrapper<>();
        return projectRoleMapper.selectList(query.lambda().eq(ProjectRole::getProjectId, projectId));
    }
}
