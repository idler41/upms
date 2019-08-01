package com.lfx.upms.util;

import lombok.experimental.UtilityClass;
import net.sf.cglib.beans.BeanCopier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@SuppressWarnings("unchecked")
@UtilityClass
public class BeanCopierUtil {

    public static <D> D copy(Object srcObj, Class<D> targetClazz) {
        return srcObj == null ? null : copy(srcObj, ObjenesisUtil.newInstance(targetClazz));
    }

    public static <D> D copy(Object srcObj, D targetObj) {
        if (srcObj == null) {
            return null;
        }
        if (targetObj == null) {
            throw new IllegalArgumentException("targetObj can't be null");
        }
        BeanCopier beanCopier = BeanCopier.create(srcObj.getClass(), targetObj.getClass(), false);
        beanCopier.copy(srcObj, targetObj, null);
        return targetObj;
    }

    public static <T> List<T> copyList(List<?> srcList, Class<T> targetClazz) {
        if (srcList == null) {
            return null;
        }

        if (srcList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        if (targetClazz == null) {
            throw new IllegalArgumentException("targetClazz can't be null");
        }

        List<T> result = new ArrayList<>(srcList.size());
        for (Object srcObj : srcList) {
            result.add(copy(srcObj, targetClazz));
        }

        return result;
    }
}
