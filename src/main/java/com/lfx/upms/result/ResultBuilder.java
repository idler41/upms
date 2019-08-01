package com.lfx.upms.result;

import com.github.pagehelper.Page;
import com.lfx.upms.enums.ResultEnum;
import com.lfx.upms.util.BeanCopierUtil;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@SuppressWarnings("unchecked")
@UtilityClass
public class ResultBuilder {

    public static WebResult<Void> buildSuccess() {
        return buildSuccess(null);
    }

    public static <T> WebResult<T> buildSuccess(T t) {
        return new WebResult<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), t);
    }

    public static <T> WebResult<T> buildFailed(ResultEnum resultEnum) {
        return buildFailed(resultEnum.getCode(), resultEnum.getMessage(), null);
    }

    public static <T> WebResult<T> buildFailed(ResultEnum resultEnum, String message) {
        return buildFailed(resultEnum.getCode(), message, null);
    }

    public static <T> WebResult<T> buildFailed(int code, String message) {
        return new WebResult(code, message, null);
    }

    public static <T> WebResult<T> buildFailed(int code, String message, T data) {
        return new WebResult(code, message, data);
    }

    public static <T> WebPageResult<T> buildPageSuccess(Page<T> page) {
        return new WebPageResult(
                ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(),
                page.getResult(), page.getPageNum(), page.getPageSize(),
                page.getTotal(), page.getPages()
        );
    }

    public static <S, T> WebPageResult<T> buildPageSuccess(Page<S> page, Class<T> clazz) {
        if (page == null) {
            throw new IllegalArgumentException();
        }

        WebPageResult<T> webPageResult = new WebPageResult<>();

        webPageResult.setCode(ResultEnum.SUCCESS.getCode());
        webPageResult.setMessage(ResultEnum.SUCCESS.getMessage());
        webPageResult.setPageNum(page.getPageNum());
        webPageResult.setPageSize(page.getPageSize());
        webPageResult.setTotal(page.getTotal());
        webPageResult.setPages(page.getPages());

        List<S> pageResult = page.getResult();
        if (!pageResult.isEmpty()) {
            webPageResult.setData(BeanCopierUtil.copyList(pageResult, clazz));
        }
        return webPageResult;
    }
}
