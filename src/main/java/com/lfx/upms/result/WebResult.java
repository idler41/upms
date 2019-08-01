package com.lfx.upms.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebResult<T> implements Serializable {

    /**
     * 状态码
     */
    private int code;

    /**
     * 状态码对应信息
     */
    private String message;

    /**
     * 数据
     */
    private T data;
}
