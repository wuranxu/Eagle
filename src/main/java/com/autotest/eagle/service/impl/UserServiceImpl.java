package com.autotest.eagle.service.impl;

import com.autotest.eagle.dto.UserDto;
import com.autotest.eagle.entity.User;
import com.autotest.eagle.enums.Role;
import com.autotest.eagle.mapper.UserMapper;
import com.autotest.eagle.middleware.Jwt;
import com.autotest.eagle.service.UserService;
import com.autotest.eagle.utils.Sha1Util;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 * @author wuranxu
 * @date 2020/8/31 3:00 下午
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getUserByToken(String token) {
        Long userId = Jwt.getUserId(token);
        if (userId == null) {
            return null;
        }
        return userMapper.selectById(userId);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> listUser() {
        return userMapper.selectList(null);
    }

    @Override
    public Boolean isSuperAdmin(Long id) {
        User user = userMapper.selectById(id);
        return user != null && user.getRole() == Role.SuperAdmin;
    }

    @Override
    public Boolean forbiddenUser(Long id) {
        User user = new User();
        user.setDisabled(true);
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id);
        return userMapper.update(user, wrapper) > 0;
    }

    @Override
    public Boolean registerUser(User user, String ip) throws NoSuchAlgorithmException {
        user.setPassword(Sha1Util.encode(user.getPassword()));
        user.setCreateTime(new Date());
        user.setLastLoginTime(new Date());
        user.setLastLoginIp(ip);
        user.setDisabled(false);
        int insert = userMapper.insert(user);
        return insert > 0;
    }

    @Override
    public User login(UserDto user, String ip) throws Exception {
        String pwd = Sha1Util.encode(user.getPassword());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<User> eq = wrapper.lambda().eq(User::getName, user.getName()).eq(User::getPassword, pwd);
        User usr = userMapper.selectOne(eq);
        if (usr == null) {
            return null;
        }
        if (usr.getDisabled()) {
            throw new Exception("用户已被禁用");
        }
        usr.setLastLoginIp(ip);
        usr.setLastLoginTime(new Date());
        userMapper.updateById(usr);
        return usr;
    }
}
