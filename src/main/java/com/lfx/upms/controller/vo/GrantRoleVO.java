package com.lfx.upms.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-08 13:38
 */
@Data
public class GrantRoleVO {

    @NotNull
    private Long roleId;
    private List<Long> resourceIds;
    private List<Long> permissionIds;

}
