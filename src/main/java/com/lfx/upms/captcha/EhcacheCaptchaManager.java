package com.lfx.upms.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-07-21 09:39
 */
public class EhcacheCaptchaManager extends AbstractCaptchaManager {

    private final Ehcache captchaCache;

    private final int maxTimes;

    public EhcacheCaptchaManager(DefaultKaptcha defaultKaptcha, Ehcache captchaCache, int maxTimes, String formatName) {
        super(defaultKaptcha, formatName);
        this.captchaCache = captchaCache;
        this.maxTimes = maxTimes;
    }

    private String timesPrefix = "code:times:";

    private String textPrefix = "text:times:";

    @Override
    protected void saveText(String clientId, String text) {
        String cacheTextKey = getCacheTextKey(clientId);
        captchaCache.acquireWriteLockOnKey(cacheTextKey);
        try {
            captchaCache.put(new Element(cacheTextKey, text));
        } finally {
            captchaCache.releaseWriteLockOnKey(cacheTextKey);
        }
    }

    @Override
    public boolean verify(String clientId, String text) {
        String cacheTextKey = getCacheTextKey(clientId);
        captchaCache.acquireReadLockOnKey(cacheTextKey);
        try {
            Element element = captchaCache.get(cacheTextKey);
            if (element == null) {
                throw new IllegalStateException("验证码校验时发生异常，缓存中校验码为null");
            }
            return text.equals(element.getObjectValue());
        } finally {
            captchaCache.releaseReadLockOnKey(cacheTextKey);
        }
    }

    @Override
    public boolean needVerify(String clientId) {
        String cacheTimesKey = getCacheTimesKey(clientId);
        captchaCache.acquireReadLockOnKey(cacheTimesKey);
        try {
            Element element = captchaCache.get(cacheTimesKey);
            return element != null && (Integer) element.getObjectValue() >= maxTimes;
        } finally {
            captchaCache.releaseReadLockOnKey(cacheTimesKey);
        }
    }

    @Override
    public void clear(String clientId) {
        String cacheTimesKey = getCacheTimesKey(clientId);
        captchaCache.acquireWriteLockOnKey(cacheTimesKey);
        try {
            captchaCache.remove(cacheTimesKey);
        } finally {
            captchaCache.releaseWriteLockOnKey(cacheTimesKey);
        }

        String cacheTextKey = getCacheTextKey(clientId);
        captchaCache.acquireWriteLockOnKey(cacheTextKey);
        try {
            captchaCache.remove(cacheTextKey);
        } finally {
            captchaCache.releaseWriteLockOnKey(cacheTextKey);
        }
    }

    @Override
    public void increaseLoginFailedTimes(String clientId) {
        String cacheTimesKey = getCacheTimesKey(clientId);
        captchaCache.acquireWriteLockOnKey(cacheTimesKey);
        try {
            Element element = captchaCache.get(cacheTimesKey);
            Integer cacheVal = 0;
            if (element != null) {
                Integer val = (Integer) element.getObjectValue();
                if (val != null) {
                    cacheVal = val;
                }
            }
            Integer loginFailedTimes = cacheVal + 1;
            captchaCache.put(new Element(cacheTimesKey, loginFailedTimes));
        } finally {
            captchaCache.releaseWriteLockOnKey(cacheTimesKey);
        }
    }

    private String getCacheTimesKey(String clientId) {
        return timesPrefix + clientId;
    }

    private String getCacheTextKey(String clientId) {
        return textPrefix + clientId;
    }
}
