package com.lfx.upms.controller;

import com.github.pagehelper.Page;
import com.lfx.upms.controller.valid.InsertGroup;
import com.lfx.upms.controller.valid.UpdateGroup;
import com.lfx.upms.controller.valid.UpdateSelectiveGroup;
import com.lfx.upms.controller.vo.GrantRoleVO;
import com.lfx.upms.controller.vo.PageVO;
import com.lfx.upms.controller.vo.RoleVO;
import com.lfx.upms.domain.Role;
import com.lfx.upms.domain.RoleExample;
import com.lfx.upms.result.ResultBuilder;
import com.lfx.upms.result.WebPageResult;
import com.lfx.upms.result.WebResult;
import com.lfx.upms.service.RoleService;
import com.lfx.upms.service.UserService;
import com.lfx.upms.util.BeanCopierUtil;
import com.lfx.upms.util.WebConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 21:27:44
 */
@Api(value = "Role Controller", tags = "操作角色相关接口")
@RequestMapping("/role")
@RestController
@Slf4j
public class RoleController {

    private final RoleService roleService;

    private final UserService userService;

    private final AuthorizingRealm authorizingRealm;
    @Autowired
    public RoleController(RoleService roleService, UserService userService, AuthorizingRealm authorizingRealm) {
        this.roleService = roleService;
        this.userService = userService;
        this.authorizingRealm = authorizingRealm;
    }

    @ApiOperation(value = "获取单个角色")
    @GetMapping("/{id}")
    @RequiresPermissions("role:retrieve")
    public WebResult<RoleVO> selectByPrimaryKey(@PathVariable Long id) {
        Role role = roleService.selectByPrimaryKey(id);
        return ResultBuilder.buildSuccess(BeanCopierUtil.copy(role, RoleVO.class));
    }

    @ApiOperation(value = "添加单个角色")
    @PostMapping(value = "/insert", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermissions("role:create")
    public WebResult<Long> insert(
            @Validated(value = InsertGroup.class) @RequestBody RoleVO roleVO, HttpServletRequest request
    ) {
        Role role = BeanCopierUtil.copy(roleVO, Role.class);
        Long userId = (Long) request.getAttribute(WebConstants.CURRENT_USER_ID);
        role.setCreateUser(userId);
        role.setCreateTime(System.currentTimeMillis());
        roleService.insertSelective(role);
        return ResultBuilder.buildSuccess(role.getId());
    }

    @ApiOperation(value = "更新单个角色，没有值的字段都设置为NULL")
    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermissions("role:update")
    public WebResult<Void> updateByPrimaryKey(@Validated(value = UpdateGroup.class) @RequestBody RoleVO roleVO) {
        Role role = BeanCopierUtil.copy(roleVO, Role.class);
        roleService.updateByPrimaryKey(role);
        return ResultBuilder.buildSuccess();
    }

    @ApiOperation(value = "更新单个角色，不更新值为NULL的字段")
    @PostMapping(value = "/updateSelective", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermissions("role:update")
    public WebResult<Void> updateByPrimaryKeySelective(
            @Validated(value = UpdateSelectiveGroup.class) @RequestBody RoleVO roleVO
    ) {
        Role role = BeanCopierUtil.copy(roleVO, Role.class);
        roleService.updateByPrimaryKeySelective(role);
        return ResultBuilder.buildSuccess();
    }

    @ApiOperation(value = "删除单个角色")
    @PostMapping(value = "/delete/{id}")
    @RequiresPermissions("role:delete")
    public WebResult<Void> deleteByPrimaryKey(@PathVariable Long id) {
        String errorMsg = checkBeforeDelete(id);
        if (errorMsg != null) {
            return ResultBuilder.buildFailed(HttpStatus.BAD_REQUEST.value(), errorMsg);
        }
        roleService.deleteByPrimaryKey(id);
        return ResultBuilder.buildSuccess();
    }

    @ApiOperation(value = "批量删除角色")
    @PostMapping(value = "/delete/batch")
    @RequiresPermissions("role:delete")
    public WebResult<Void> batchDelete(@RequestBody Long[] idList) {
        if (idList == null || idList.length == 0) {
            return ResultBuilder.buildSuccess();
        }
        String errorMsg = checkBeforeDelete(idList);
        if (errorMsg != null) {
            return ResultBuilder.buildFailed(HttpStatus.BAD_REQUEST.value(), errorMsg);
        }

        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andIdIn(Arrays.asList(idList));
        roleService.deleteByExample(roleExample);
        return ResultBuilder.buildSuccess();
    }

    private String checkBeforeDelete(Long... idList) {
        if (roleService.isRelateToUser(idList)) {
            return "有资源与该权限关联,请资源回收该权限";
        }
        if (roleService.isRelateToResource(idList)) {
            return "有资源与该权限关联,请资源回收该权限";
        }
        if (roleService.isRelateToPermission(idList)) {
            return "有资源与该权限关联,请资源回收该权限";
        }
        return null;
    }

    @ApiOperation(value = "分页查询角色")
    @GetMapping(value = "/page")
    @RequiresPermissions("role:retrieve")
    public WebPageResult<RoleVO> selectByPagination(@Validated PageVO pageVO, @Validated RoleVO roleVO) {
        RoleExample roleExample = buildPageExample(roleVO);
        Integer pageNum = pageVO.getPageNum();
        Integer pageSize = pageVO.getPageSize();
        Page<Role> page = (Page<Role>) roleService.selectByPagination(pageNum, pageSize, roleExample);
        return ResultBuilder.buildPageSuccess(page, RoleVO.class);
    }

    private RoleExample buildPageExample(RoleVO roleVO) {
        return null;
    }

    @ApiOperation(value = "查询所有角色")
    @GetMapping(value = "/all")
    @RequiresPermissions("role:retrieve")
    public WebResult<List<RoleVO>> selectAllRole() {
        List<Role> roleList = roleService.selectByExample(null);
        List<RoleVO> result = BeanCopierUtil.copyList(roleList, RoleVO.class);
        return ResultBuilder.buildSuccess(result);
    }

    @ApiOperation(value = "查询某个用户的所有角色")
    @GetMapping(value = "/user/{userId}")
    @RequiresPermissions("role:retrieve")
    public WebResult<List<Long>> selectByUserId(@PathVariable Long userId) {
        List<Long> userOfRoleIds = roleService.selectRoleIdsByUserId(userId);
        return ResultBuilder.buildSuccess(userOfRoleIds);
    }

    @ApiOperation(value = "给角色授权权限")
    @PostMapping(value = "/grant", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermissions("role:grant")
    @SuppressWarnings("unchecked")
    public WebResult<Void> grant(
            @Validated @RequestBody GrantRoleVO grantRoleVO, HttpServletRequest request
    ) {
        Long doGrantUser = (Long) request.getAttribute(WebConstants.CURRENT_USER_ID);
        Long doGrantTime = System.currentTimeMillis();
        Long roleId = grantRoleVO.getRoleId();
        List<Long> resourceIds = grantRoleVO.getResourceIds();
        List<Long> permissionIds = grantRoleVO.getPermissionIds();
        roleService.grant(roleId, resourceIds, permissionIds, doGrantUser, doGrantTime);
        // todo 清除该角色所有用户的权限缓存
        // 查询该角色所有用户
        List<String> usernameList = userService.selectUsernameByRoleId(roleId);
        if (log.isInfoEnabled()) {
            log.info("角色【{}】授权成功，清除所有关联用户的权限缓存【{}】", roleId, StringUtils.join(usernameList, ","));
        }
        Cache cache = authorizingRealm.getAuthorizationCache();
        for (String username : usernameList) {
            cache.remove(username);
        }
        return ResultBuilder.buildSuccess();
    }

}
