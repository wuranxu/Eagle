package com.autotest.eagle.annotation;

/**
 * @author wuranxu
 * @date 2020/8/31 1:59 下午
 */
import com.autotest.eagle.enums.Role;

import java.lang.annotation.*;

/**
 * @author wuranxu
 * @date 2020/7/20 3:09 下午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {
    Role value() default Role.Guest;
}

