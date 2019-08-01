package com.lfx.upms.controller.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-04-28 09:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PermissionModel extends BaseRowModel {

    /**
     * 权限标识
     */
    @ExcelProperty(value = "权限标识", index = 0)
    private String permissionKey;

    /**
     * 权限描述
     */
    @ExcelProperty(value = "权限描述", index = 1)
    private String permissionDesc;

}
