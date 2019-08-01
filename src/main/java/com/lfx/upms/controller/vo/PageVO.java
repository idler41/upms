package com.lfx.upms.controller.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-07-23 21:03
 */
@Data
public class PageVO {

    @Min(value = 0)
    @NotNull
    private Integer pageNum;

    @Range(min = 0, max = 100)
    @NotNull
    private Integer pageSize;
}
