package com.autotest.eagle.service;

import com.autotest.eagle.dto.UserDto;
import com.autotest.eagle.entity.User;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author wuranxu
 * @date 2020/8/31 3:02 下午
 */
public interface UserService {
    User getUserByToken(String token);

    User getUserById(Long id);

    List<User> listUser();

    Boolean isSuperAdmin(Long id);

    Boolean forbiddenUser(Long id);

    Boolean registerUser(User user, String ip) throws NoSuchAlgorithmException;

    User login(UserDto user, String ip) throws Exception;
}
