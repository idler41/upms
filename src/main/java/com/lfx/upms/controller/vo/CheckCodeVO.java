package com.lfx.upms.controller.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-04-25 16:42
 */
@Data
public class CheckCodeVO implements Serializable {

    private String code;
    private Integer failedTimes;

    public void increaseTimes() {
        failedTimes++;
    }
}
