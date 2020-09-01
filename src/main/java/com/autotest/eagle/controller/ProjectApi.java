package com.autotest.eagle.controller;

import com.autotest.eagle.annotation.Permission;
import com.autotest.eagle.dto.Response;
import com.autotest.eagle.entity.Project;
import com.autotest.eagle.entity.User;
import com.autotest.eagle.enums.Role;
import com.autotest.eagle.service.ProjectService;
import com.autotest.eagle.utils.RequestUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;

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
            return Response.build(503, null, "创建失败, 该项目已存在");
        } catch (Exception e) {
            log.error("项目服务->新增项目失败, error: " + e.getMessage());
            return Response.build(501, null, e.getMessage());
        }
        if (!result) {
            return Response.build(501, null, "创建失败");
        }
        return Response.build(200, project, "创建成功");
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
            return Response.build(503, null, "修改失败, 已存在该项目名");
        } catch (Exception e) {
            log.error("项目服务->修改项目失败, error: " + e.getMessage());
            return Response.build(501, null, e.getMessage());
        }
        if (!result) {
            return Response.build(501, null, "修改失败");
        }
        return Response.build(200, project, "修改成功");
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
            return Response.build(501, new ArrayList<>(), e.getMessage());
        }
        return Response.build(200, data, "请求成功");
    }
}
