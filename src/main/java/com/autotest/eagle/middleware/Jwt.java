package com.autotest.eagle.middleware;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuranxu
 * @date 2020/8/31 2:18 下午
 */
@Slf4j
public class Jwt {
    private static final String SECRET_KEY = "EagleTest";
    private static final int EXPIRED_TIME = 48 * 60 * 1000;

    // 生成jwt token
    public static String createToken(Long userId, String username) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRED_TIME);
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("username", username);
            return JWT.create().withClaim("data", data).withExpiresAt(date).sign(algorithm);
        } catch (Exception e) {
            log.error(String.format("business => 用户管理, 生成token失败. error: %s", e.getMessage()));
        }
        return null;
    }

    private static Map<String, Object> parseToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            Verification jwtVerifier = JWT.require(algorithm);
            DecodedJWT jwt = jwtVerifier.build().verify(token);
            return jwt.getClaim("data").asMap();
        } catch (Exception e) {
            return null;
        }
    }

    public static Long getUserId(String token) {
        Map<String, Object> data = parseToken(token);
        if (data == null) {
            return null;
        }
        return (Long) data.get("userId");
    }

    public static String getUsername(String token) {
        Map<String, Object> data = parseToken(token);
        if (data == null) {
            return null;
        }
        return (String) data.get("username");
    }
}
