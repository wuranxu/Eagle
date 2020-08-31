package com.autotest.eagle.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

/**
 * @author wuranxu
 * @date 2020/8/31 3:48 下午
 */
public class RequestUtil {
    //获取客户端IP地址
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (inet == null) {
                    return ip;
                }
                ip = inet.getHostAddress();
            }
        }
        // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    // 获取分页参数
    public static IPage getPage(HttpServletRequest request) {
        long current = 1;
        long size = 8;
        String page = request.getParameter("page");
        if (StringUtils.isNotEmpty(page)) {
            Long p = Long.valueOf(page);
            if (p > 0) {
                current = p;
            }
        }
        String pageSize = request.getParameter("size");
        if (StringUtils.isNotEmpty(pageSize)) {
            Long l = Long.valueOf(pageSize);
            if (l > 0) {
                size = l;
            }
        }
        return new Page(current, size);
    }
}
