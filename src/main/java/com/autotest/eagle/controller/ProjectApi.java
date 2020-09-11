package com.autotest.eagle.controller;

import com.autotest.eagle.annotation.Permission;
import com.autotest.eagle.dto.Response;
import com.autotest.eagle.entity.Project;
import com.autotest.eagle.entity.ProjectRole;
import com.autotest.eagle.entity.User;
import com.autotest.eagle.enums.Role;
import com.autotest.eagle.exceptions.ForbiddenException;
import com.autotest.eagle.service.ProjectService;
import com.autotest.eagle.utils.RequestUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuranxu
 * @date 2020/9/1 11:04 上午
 */
@RestController
@RequestMapping("/api/project")
@Slf4j
public class ProjectApi {
    @Resource
    private ProjectService projectService;

    @PostMapping("/insert")
    @Permission(Role.SuperAdmin)
    public Response insertProject(HttpServletRequest request, @Valid @RequestBody Project project, BindingResult results) {
        Response err = RequestUtil.validate(results);
        if (err != null) {
            return err;
        }
        Boolean result;
        try {
            User user = RequestUtil.getUser(request);
            result = projectService.insertProject(project, user.getId());
        } catch (DuplicateKeyException e) {
            return Response.error("创建失败, 该项目已存在");
        } catch (Exception e) {
            log.error("项目服务->新增项目失败, error: " + e.getMessage());
            return Response.error(e.getMessage());
        }
        if (!result) {
            return Response.error("创建失败");
        }
        return Response.success(project, "创建成功");
    }

    @GetMapping("/{projectId}")
    @Permission(Role.Guest)
    public Response queryProject(HttpServletRequest request, @PathVariable String projectId) {
        Project project;
        try {
            Long projId = Long.valueOf(projectId);
            User user = RequestUtil.getUser(request);
            project = projectService.queryProjectById(projId, user.getId());
        } catch (ForbiddenException e) {
            return Response.forbidden();
        } catch (Exception e) {
            log.error("项目服务->获取项目失败, error: " + e.getMessage());
            return Response.error(e.getMessage());
        }
        return Response.success(project, "操作成功");
    }

    @PostMapping("/update")
    @Permission(Role.Guest)
    public Response updateProject(HttpServletRequest request, @Valid @RequestBody Project project, BindingResult results) {
        Response err = RequestUtil.validate(results);
        if (err != null) {
            return err;
        }
        Boolean result;
        try {
            User user = RequestUtil.getUser(request);
            result = projectService.updateProject(project, user.getId());
        } catch (DuplicateKeyException e) {
            return Response.error("修改失败, 已存在该项目名");
        } catch (Exception e) {
            log.error("项目服务->修改项目失败, error: " + e.getMessage());
            return Response.error(e.getMessage());
        }
        if (!result) {
            return Response.error("修改失败");
        }
        return Response.success("修改成功");
    }


    @GetMapping("/list")
    @Permission(Role.Guest)
    public Response listProject(HttpServletRequest request) {
        IPage page = RequestUtil.getPage(request);
        User user = RequestUtil.getUser(request);
        String projectName = request.getParameter("projectName");
        IPage<Project> data;
        try {
            if (user.getRole() == Role.SuperAdmin) {
                data = projectService.listProject(page, user.getId(), projectName);
            } else {
                data = projectService.listProjectByUser(page, user.getId(), projectName);
            }
        } catch (Exception e) {
            return Response.error(new ArrayList<>(), e.getMessage());
        }
        return Response.success(data, "请求成功");
    }

    @PostMapping("/upload")
    @Permission
    public Response uploadProjectFile(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("project_id") String projectId) {
        if (file.isEmpty()) {
            return Response.error("上传失败, 文件为空");
        }
        User user = RequestUtil.getUser(request);
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith("jpg") && !filename.endsWith("jpeg")
                && !filename.endsWith("png") && !filename.endsWith("gif"))) {
            return Response.error("上传失败, 图片格式错误");
        }
        int index = filename.lastIndexOf('.');
        String suffix = filename.substring(index + 1);
        Boolean result;
        try {
            String name = String.format("%s.%s", projectId, suffix);
            result = projectService.uploadProjectPic(name, file.getInputStream());
            projectService.updateProjectAvatar(Long.valueOf(projectId), user.getId(), name);
        } catch (ForbiddenException e) {
            return Response.forbidden();
        } catch (Exception e) {
            log.error("上传图片失败, error: " + e.getMessage());
            return Response.error("上传图片失败");
        }
        if (!result) {
            return Response.error("上传图片失败");
        }
        return Response.success("上传图片成功");
    }

    @GetMapping("/{projectId}/members")
    @Permission
    public Response listProjectRole(@PathVariable String projectId) {
        try {
            Long projId = Long.valueOf(projectId);
            List<ProjectRole> projectRoles = projectService.listProjectRole(projId);
            return Response.success(projectRoles, "操作成功");
        } catch (Exception e) {
            log.error("项目服务->获取项目失败, error: " + e.getMessage());
            return Response.error(new ArrayList<>(), e.getMessage());
        }
    }

    @PostMapping("/role/insert")
    @Permission
    public Response insertProjectRole(HttpServletRequest request, @RequestBody ProjectRole role) {
        try {
            User user = RequestUtil.getUser(request);
            if (projectService.insertProjectMember(user.getId(), role)) {
                return Response.success("添加成功");
            }
            return Response.error("添加失败");
        } catch (DuplicateKeyException e) {
            return Response.error("添加失败, 用户已存在");
        } catch (ForbiddenException e) {
            return Response.forbidden();
        } catch (Exception e) {
            log.error("项目服务->添加项目角色失败, error: " + e.getMessage());
            return Response.error(new ArrayList<>(), e.getMessage());
        }
    }

    @PostMapping("/role/update")
    @Permission
    public Response updateProjectRole(HttpServletRequest request, @RequestBody ProjectRole role) {
        try {
            User user = RequestUtil.getUser(request);
            if (projectService.updateProjectMember(user.getId(), role)) {
                return Response.success("更新成功");
            }
            return Response.error("更新失败");
        } catch (ForbiddenException e) {
            return Response.forbidden();
        } catch (Exception e) {
            log.error("项目服务->修改项目角色失败, error: " + e.getMessage());
            return Response.error(new ArrayList<>(), e.getMessage());
        }
    }

    @PostMapping("/role/delete")
    @Permission
    public Response deleteProjectRole(HttpServletRequest request, @RequestBody ProjectRole role) {
        try {
            User user = RequestUtil.getUser(request);
            if (projectService.deleteProjectMember(user.getId(), role)) {
                return Response.success("删除成功");
            }
            return Response.error("删除失败");
        } catch (ForbiddenException e) {
            return Response.forbidden();
        } catch (Exception e) {
            log.error("项目服务->删除项目角色失败, error: " + e.getMessage());
            return Response.error(e.getMessage());
        }
    }
}
