package com.lfx.upms.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-08 13:45
 */
@Data
public class GrantUserVO {

    @NotNull
    private Long userId;

    @Size(max = 20, message = "一个用户拥有角色数不能超过20个")
    private List<Long> roleIds;

}