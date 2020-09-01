package com.autotest.eagle.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wuranxu
 * @date 2020/9/1 2:45 下午
 */
@Getter
@AllArgsConstructor
public enum ProjRole implements IEnum<Integer> {
    MEMBER(0, "组员"),
    OWNER(1, "组长");

    private int value;
    private String desc;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
