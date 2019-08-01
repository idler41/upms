package com.lfx.upms.util;

import lombok.experimental.UtilityClass;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@UtilityClass
public class ObjenesisUtil {

    private static final Objenesis OBJENESIS = new ObjenesisStd();

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz) {
        ObjectInstantiator oi = OBJENESIS.getInstantiatorOf(clazz);
        return (T) oi.newInstance();
    }
}
