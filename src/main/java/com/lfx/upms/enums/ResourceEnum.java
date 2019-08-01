package com.lfx.upms.enums;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-07-15 09:46
 */
public enum ResourceEnum {

    /**
     * 可下拉目录
     */
    DIRECTORY(0),

    /**
     * 可点击页面
     */
    PAGE(1),

    /**
     * 其它页面元素如按钮/列表等
     */
    CONTENT(2);

    int code;

    ResourceEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ResourceEnum valueOf(int code) {
        for (ResourceEnum resourceEnum : values()) {
            if (resourceEnum.getCode() == code) {
                return resourceEnum;
            }
        }
        throw new RuntimeException("找不到对应的资源类型, code = " + code);
    }

    public static int getRootResourceEnum() {
        return DIRECTORY.code;
    }

    public boolean match(int code) {
        return this.code == code;
    }
}
