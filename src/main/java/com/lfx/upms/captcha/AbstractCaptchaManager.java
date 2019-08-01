package com.lfx.upms.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-07-21 10:16
 */
public abstract class AbstractCaptchaManager implements CaptchaManager {

    private final DefaultKaptcha defaultKaptcha;

    private final String formatName;

    public AbstractCaptchaManager(DefaultKaptcha defaultKaptcha, String formatName) {
        this.defaultKaptcha = defaultKaptcha;
        this.formatName = formatName;
    }

    @Override
    public byte[] createImageBytes(String clientId) {
        String text = defaultKaptcha.createText();
        BufferedImage challenge = defaultKaptcha.createImage(text);

        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(challenge, formatName, jpegOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        saveText(clientId, text);

        return jpegOutputStream.toByteArray();
    }

    /**
     * 保存客户端验证码，
     *
     * @param clientId 客户端唯一标识
     * @param text     验证码文本
     */
    protected abstract void saveText(String clientId, String text);

}
