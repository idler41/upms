package com.lfx.upms.captcha;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-07-21 15:13
 */
public interface CaptchaManager {

    /**
     * 生成验证码图片
     *
     * @param clientId 客户端唯一标识
     * @return 验证码图片字节数组
     */
    byte[] createImageBytes(String clientId);

    /**
     * 判定客户端是否符合生成验证码条件
     *
     * @param clientId 客户端唯一标识
     * @return true: 需要生成验证码 false: 不需要生成验证码
     */
    boolean needVerify(String clientId);

    /**
     * 校验客户端验证码
     *
     * @param clientId 客户端唯一标识
     * @param text     验证码文本
     * @return true: 验证码相同 false: 验证码不同
     */
    boolean verify(String clientId, String text);

    /**
     * 清空客户端验证码相关缓存
     * @param clientId 客户端唯一标识
     * @return
     */
    void clear(String clientId);

    /**
     * 登录失败统计次数加1
     * @param clientId 客户端唯一标识
     * @return
     */
    void increaseLoginFailedTimes(String clientId);


}
