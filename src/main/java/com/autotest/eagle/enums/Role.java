package com.autotest.eagle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * @author wuranxu
 * @date 2020/8/31 2:00 下午
 */
@Getter
@AllArgsConstructor
public enum Role implements IEnum<Integer> {
    Guest(0, "访客"),
    ConfigAdmin(1, "管理员"),
    SuperAdmin(2, "超级管理员");

    private int value;
    private String desc;

    @Override
    public Integer getValue() {
        return this.value;
    }
}

