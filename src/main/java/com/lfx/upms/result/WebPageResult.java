package com.lfx.upms.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebPageResult<T> implements Serializable {

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
    private List<T> data;

    /**
     * 当前页
     */
    private int pageNum;

    /**
     * 每页的数量
     */
    private int pageSize;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private int pages;
}
