package com.lfx.upms.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-09 11:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResourcePermission extends Resource {
    private List<Long> permissionIds;
}
