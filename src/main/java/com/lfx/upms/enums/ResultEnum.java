package com.lfx.upms.enums;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
public enum ResultEnum {

    /**
     * 账号不存在
     */
    UNKNOWN_ACCOUNT(20000, "账号不存在"),

    /**
     * 登录密码错误
     */
    INCORRECT_CREDENTIALS(20001, "登录密码错误"),

    /**
     * 用户已被锁定
     */
    LOCKED_ACCOUNT(20003, "用户已被锁定"),

    /**
     * 用户已被禁用
     */
    DISABLED_ACCOUNT(20004, "用户已被禁用"),

    /**
     * 需要验证码
     */
    CHECKED_CODE_NEED(20005, "需要验证码"),

    /**
     * 验证码错误
     */
    CHECKED_CODE_ERROR(20006, "验证码错误"),

    /**
     * 验证码功能没有开启
     */
    CHECKED_CODE_DISABLE(20007, "验证码功能没有开启"),

    /**
     * 无权限访问
     */
    NO_PERMISSION(20100, "无权限访问"),

    /**
     * 数据重复异常
     */
    REPEAT_DATA(30000, "数据重复异常"),

    /**
     * 存在异常数据
     */
    ILLEGAL_DATA(30100, "存在异常数据"),

    /**
     * 并发操作
     */
    CONCURRENT_OPERATE(40001, "并发操作"),

    /**
     * 响应成功
     */
    SUCCESS(200, null),

    /**
     * 响应失败：未知异常
     */
    UNKNOWN_ERROR(500, "未知异常");

    private int code;
    private String message;

    ResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
