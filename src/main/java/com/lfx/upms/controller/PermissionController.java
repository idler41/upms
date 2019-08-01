package com.lfx.upms.controller;

import com.github.pagehelper.Page;
import com.lfx.upms.controller.excel.PermissionModel;
import com.lfx.upms.controller.valid.InsertGroup;
import com.lfx.upms.controller.valid.UpdateGroup;
import com.lfx.upms.controller.valid.UpdateSelectiveGroup;
import com.lfx.upms.controller.vo.PageVO;
import com.lfx.upms.controller.vo.PermissionVO;
import com.lfx.upms.domain.Permission;
import com.lfx.upms.domain.PermissionExample;
import com.lfx.upms.result.ResultBuilder;
import com.lfx.upms.result.WebPageResult;
import com.lfx.upms.result.WebResult;
import com.lfx.upms.service.PermissionService;
import com.lfx.upms.util.BeanCopierUtil;
import com.lfx.upms.util.DateUtil;
import com.lfx.upms.util.ExcelUtil;
import com.lfx.upms.util.WebConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 20:26:02
 */
@Api(value = "Permission Controller", tags = "操作权限相关接口")
@RequestMapping("/permission")
@RestController
public class PermissionController {

    private final PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @ApiOperation(value = "获取单个权限")
    @GetMapping("/{id}")
    @RequiresPermissions("permission:retrieve")
    public WebResult<PermissionVO> selectByPrimaryKey(@PathVariable Long id) {
        Permission permission = permissionService.selectByPrimaryKey(id);
        return ResultBuilder.buildSuccess(BeanCopierUtil.copy(permission, PermissionVO.class));
    }

    @ApiOperation(value = "添加单个权限")
    @PostMapping(value = "/insert", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermissions("permission:create")
    public WebResult<Long> insert(
            @Validated(value = InsertGroup.class) @RequestBody PermissionVO permissionVO,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute(WebConstants.CURRENT_USER_ID);
        Permission permission = BeanCopierUtil.copy(permissionVO, Permission.class);
        permission.setCreateUser(userId);
        permission.setCreateTime(System.currentTimeMillis());
        permissionService.insertSelective(permission);
        return ResultBuilder.buildSuccess(permission.getId());
    }

    @ApiOperation(value = "更新单个权限，没有值的字段都设置为NULL")
    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermissions("permission:update")
    public WebResult<Void> updateByPrimaryKey(
            @Validated(value = UpdateGroup.class) @RequestBody PermissionVO permissionVO
    ) {
        Permission permission = BeanCopierUtil.copy(permissionVO, Permission.class);
        permissionService.updateByPrimaryKey(permission);
        return ResultBuilder.buildSuccess();
    }

    @ApiOperation(value = "更新单个权限，不更新值为NULL的字段")
    @PostMapping(value = "/updateSelective", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermissions("permission:update")
    public WebResult<Void> updateByPrimaryKeySelective(
            @Validated(value = UpdateSelectiveGroup.class) @RequestBody PermissionVO permissionVO
    ) {
        Permission permission = BeanCopierUtil.copy(permissionVO, Permission.class);
        permissionService.updateByPrimaryKeySelective(permission);
        return ResultBuilder.buildSuccess();
    }

    @ApiOperation(value = "删除单个权限")
    @PostMapping(value = "/delete/{id}")
    @RequiresPermissions("permission:delete")
    public WebResult<Void> deleteByPrimaryKey(@PathVariable Long id) {

        String errorMsg = checkBeforeDelete(id);
        if (errorMsg != null) {
            return ResultBuilder.buildFailed(HttpStatus.BAD_REQUEST.value(), errorMsg);
        }

        permissionService.deleteByPrimaryKey(id);
        return ResultBuilder.buildSuccess();
    }

    @ApiOperation(value = "批量删除权限")
    @PostMapping(value = "/delete/batch")
    @RequiresPermissions("permission:delete")
    public WebResult<Void> batchDelete(@RequestBody Long[] idList) {
        if (idList == null || idList.length == 0) {
            return ResultBuilder.buildSuccess();
        }

        String errorMsg = checkBeforeDelete(idList);
        if (errorMsg != null) {
            return ResultBuilder.buildFailed(HttpStatus.BAD_REQUEST.value(), errorMsg);
        }

        PermissionExample permissionExample = new PermissionExample();
        permissionExample.createCriteria().andIdIn(Arrays.asList(idList));
        permissionService.deleteByExample(permissionExample);
        return ResultBuilder.buildSuccess();
    }

    private String checkBeforeDelete(Long... idList) {
        if (permissionService.isRelateToResource(idList)) {
            return "有资源与该权限关联,请资源回收该权限";
        }
        if (permissionService.isRelateToRole(idList)) {
            return "有角色与该权限关联,请角色回收该权限";
        }
        return null;
    }

    @ApiOperation(value = "分页查询权限")
    @GetMapping(value = "/page")
    @RequiresPermissions("permission:retrieve")
    public WebPageResult<PermissionVO> selectByPagination(
            @Validated PageVO pageVO, @Validated PermissionVO permissionVO,
            @RequestParam(name = "orderDesc", required = false) Boolean orderDesc
    ) {
        Integer pageNum = pageVO.getPageNum();
        Integer pageSize = pageVO.getPageSize();
        PermissionExample permissionExample = buildPageExample(permissionVO, orderDesc);
        Page<Permission> page = (Page<Permission>) permissionService.selectByPagination(pageNum, pageSize, permissionExample);
        return ResultBuilder.buildPageSuccess(page, PermissionVO.class);
    }

    private PermissionExample buildPageExample(PermissionVO permissionVO, Boolean orderDesc) {
        String key = permissionVO.getPermissionKey();
        String label = permissionVO.getLabel();

        if (key == null && label == null && orderDesc == null) {
            return null;
        }

        PermissionExample example = new PermissionExample();
        PermissionExample.Criteria criteria = example.createCriteria();

        if (StringUtils.isNotBlank(key)) {
            criteria.andPermissionKeyLike(key + "%");
        }

        if (StringUtils.isNotBlank(label)) {
            criteria.andLabelLike(label + "%");
        }

        if (orderDesc != null) {
            example.setOrderByClause("create_time " + (orderDesc ? "desc" : "asc"));
        }

        return example;
    }

    @ApiOperation(value = "excel导出权限数据")
    @GetMapping(value = "/export")
    @RequiresPermissions("permission:export")
    public void upload(HttpServletResponse response) throws IOException {
        List<Permission> dataList = permissionService.selectByExample(null);
        List<PermissionModel> result = new ArrayList<>(dataList.size());
        for (Permission permission : dataList) {
            result.add(new PermissionModel(permission.getPermissionKey(), permission.getLabel()));
        }
        String fileName = "权限-" + DateUtil.getShortFormatOfLine();
        ExcelUtil.export(fileName, result, response);
    }

    @ApiOperation(value = "查询所有权限")
    @GetMapping(value = "/all")
    @RequiresPermissions("permission:retrieve")
    public WebResult<List<PermissionVO>> selectAll() {
        List<Permission> data = permissionService.selectByExample(null);
        List<PermissionVO> result = BeanCopierUtil.copyList(data, PermissionVO.class);
        return ResultBuilder.buildSuccess(result);
    }

    @ApiOperation(value = "查询指定角色的所有权限")
    @GetMapping(value = "/role/{roleId}")
    @RequiresPermissions("permission:retrieve")
    public WebResult<List<Long>> selectByRoleId(@PathVariable Long roleId) {
        List<Long> permissionIds = permissionService.selectIdsByRoleId(roleId);
        return ResultBuilder.buildSuccess(permissionIds);
    }
}
