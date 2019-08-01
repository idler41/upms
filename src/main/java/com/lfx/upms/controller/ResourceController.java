package com.lfx.upms.controller;

import com.github.pagehelper.Page;
import com.lfx.upms.controller.valid.InsertGroup;
import com.lfx.upms.controller.valid.UpdateSelectiveGroup;
import com.lfx.upms.controller.vo.PageVO;
import com.lfx.upms.controller.vo.ResourceNode;
import com.lfx.upms.controller.vo.ResourceVO;
import com.lfx.upms.domain.Resource;
import com.lfx.upms.domain.ResourceExample;
import com.lfx.upms.enums.ResourceEnum;
import com.lfx.upms.enums.ResultEnum;
import com.lfx.upms.result.ResultBuilder;
import com.lfx.upms.result.WebPageResult;
import com.lfx.upms.result.WebResult;
import com.lfx.upms.service.ResourceService;
import com.lfx.upms.util.BeanCopierUtil;
import com.lfx.upms.util.TreeNode;
import com.lfx.upms.util.TreeUtil;
import com.lfx.upms.util.WebConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 21:27:44
 */
@Api(value = "Resource Controller", tags = "操作前端资源相关接口")
@RequestMapping("/resource")
@RestController
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @ApiOperation(value = "获取单个前端资源")
    @GetMapping("/{id}")
    @RequiresPermissions("resource:retrieve")
    public WebResult<ResourceVO> selectByPrimaryKey(@PathVariable Long id) {
        Resource resource = resourceService.selectByPrimaryKey(id);
        return ResultBuilder.buildSuccess(BeanCopierUtil.copy(resource, ResourceVO.class));
    }

    private AtomicBoolean resourceTreeLock = new AtomicBoolean();

    @ApiOperation(value = "添加单个前端资源")
    @PostMapping(value = "/insert", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermissions("resource:create")
    public WebResult<Long> insert(
            @Validated(value = InsertGroup.class) @RequestBody ResourceVO resourceVO, HttpServletRequest request
    ) {
        if (!resourceTreeLock.compareAndSet(false, true)) {
            return ResultBuilder.buildFailed(ResultEnum.CONCURRENT_OPERATE, "发现有多个用户的并发操作，请稍后刷新页面重新操作");
        }

        try {
            // 规则校验: 节点类型与父节点类型
            Integer currentType = resourceVO.getType();
            Long parentId = resourceVO.getParentId();

            boolean isParentRoot = TreeNode.isRootId(parentId);
            Resource parentToUpdate = isParentRoot ? null : resourceService.selectByPrimaryKey(parentId);
            if (!isParentRoot && parentToUpdate == null) {
                return ResultBuilder.buildFailed(ResultEnum.ILLEGAL_DATA, "找不到资源, id=" + parentId);
            }
            Integer parentType = isParentRoot ? ResourceNode.getRootType() : parentToUpdate.getType();

            String error = checkNodeType(currentType, parentType);
            if (error != null) {
                return ResultBuilder.buildFailed(HttpStatus.BAD_REQUEST.value(), error);
            }

            // 1. 设置待插入节点level与parentId + 插入新节点 2. 父节点是叶子节点 => 更新父节点为非叶子节点
            Resource resourceToInsert = BeanCopierUtil.copy(resourceVO, Resource.class);
            if (isParentRoot) {
                resourceToInsert.setResourceLevel(TreeNode.ROOT_LEVEL + 1);
                resourceToInsert.setParentId(TreeNode.ROOT_ID);
            } else {
                resourceToInsert.setResourceLevel(parentToUpdate.getResourceLevel() + 1);
                if (parentToUpdate.getLeaf() == 1) {
                    parentToUpdate.setLeaf(0);
                }
            }

            Long userId = (Long) request.getAttribute(WebConstants.CURRENT_USER_ID);
            resourceToInsert.setLeaf(1);
            resourceToInsert.setCreateUser(userId);
            resourceToInsert.setCreateTime(System.currentTimeMillis());

            resourceService.mixedInsert(resourceToInsert, resourceVO.getPermissionIds(), parentToUpdate);
            return ResultBuilder.buildSuccess(resourceToInsert.getId());
        } finally {
            resourceTreeLock.compareAndSet(true, false);
        }
    }

    @ApiOperation(value = "更新单个前端资源，不更新值为NULL的字段")
    @PostMapping(value = "/updateSelective", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermissions("resource:update")
    public WebResult<Void> updateByPrimaryKeySelective(
            @Validated(value = UpdateSelectiveGroup.class) @RequestBody ResourceVO resourceVO, HttpServletRequest request
    ) {
        if (!resourceTreeLock.compareAndSet(false, true)) {
            return ResultBuilder.buildFailed(ResultEnum.CONCURRENT_OPERATE, "发现有多个用户的并发操作，请稍后刷新页面重新操作");
        }

        // 1. 更新节点信息
        //      1.1 有子节点  => 所有子节点level重新计算
        // 2. 父节点有更新   => 更新节点leve = 新的父节点level + 1
        //      2.1 老的父节点只有一个子节点 => 更新老的父节点为叶子节点
        //      2.2 新的父节点为叶子节点 => 更新新的父节点为非叶子节点
        try {
            // 如果父节点设置为null，则赋值根节点默认value
            if (resourceVO.getParentId() == null) {
                resourceVO.setParentId(TreeNode.ROOT_ID);
            }
            Long parentId = resourceVO.getParentId();
            Long id = resourceVO.getId();

            List<ResourceNode> tree = resourceService.selectTreeWithPermission();
            ResourceNode currentNode = TreeUtil.getTreeNode(tree, id);
            assert currentNode != null;
            ResourceNode oldParentNode = currentNode.getParentNode();

            Long oldParentId = oldParentNode == null ? TreeNode.ROOT_ID : oldParentNode.getId();
            List<Resource> listToUpdate = new ArrayList<>();
            // 父节点有更新
            if (!oldParentId.equals(parentId)) {
                ResourceNode newParentNode = null;
                Integer newParentType;
                if (TreeNode.isRootId(parentId)) {
                    newParentType = ResourceNode.getRootType();
                } else {
                    newParentNode = TreeUtil.getTreeNode(tree, parentId);
                    assert newParentNode != null;
                    newParentType = newParentNode.getType();
                }

                // 规则校验: 节点类型与父节点类型
                Integer currentType = currentNode.getType();
                String error = checkNodeType(currentType, newParentType);
                if (error != null) {
                    return ResultBuilder.buildFailed(HttpStatus.BAD_REQUEST.value(), error);
                }

                // 新的父节点资源叶子节点 => 更改为非叶子节点
                if (newParentNode != null && newParentNode.getLeaf() == 1) {
                    newParentNode.setLeaf(0);
                    Resource resourceToUpdate = new Resource();
                    resourceToUpdate.setId(newParentNode.getId());
                    resourceToUpdate.setLeaf(newParentNode.getLeaf());
                    listToUpdate.add(resourceToUpdate);
                }

                // 旧的父节点非叶子节点且只有一个子节点 => 更改为叶子节点
                if (oldParentNode != null && oldParentNode.getLeaf() == 0 && oldParentNode.getChildren() != null && oldParentNode.getChildren().size() == 1) {
                    oldParentNode.setLeaf(1);
                    Resource resourceToUpdate = new Resource();
                    resourceToUpdate.setId(oldParentNode.getId());
                    resourceToUpdate.setLeaf(oldParentNode.getLeaf());
                    listToUpdate.add(resourceToUpdate);
                }

                int currentLevel = newParentNode == null ? TreeNode.ROOT_LEVEL + 1 : (newParentNode.getLevel() + 1);
                resourceVO.setResourceLevel(currentLevel);

                // 有子节点 => 更新所有子节点的level
                if (!CollectionUtils.isEmpty(currentNode.getChildren())) {
                    try {
                        updateChildren(currentNode.getChildren(), parentId, currentLevel, listToUpdate);
                    } catch (IllegalArgumentException e) {
                        return ResultBuilder.buildFailed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
                    }
                }
            }
            Resource resourceToUpdate = BeanCopierUtil.copy(resourceVO, Resource.class);
            // 更新当前节点
            listToUpdate.add(resourceToUpdate);
            Long doGrantUser = (Long) request.getAttribute(WebConstants.CURRENT_USER_ID);
            Long doGrantTime = System.currentTimeMillis();
            List<Long> permissionIds = resourceVO.getPermissionIds();
            resourceService.mixedUpdate(listToUpdate, doGrantUser, doGrantTime, permissionIds);
            return ResultBuilder.buildSuccess();
        } finally {
            resourceTreeLock.compareAndSet(true, false);
        }
    }

    private String checkNodeType(Integer currentType, Integer parentType) {
        ResourceEnum resourceEnum = ResourceEnum.valueOf(currentType);

        String error;
        // 父节点有更改 => 启动校验流程 + 批量更新
        switch (resourceEnum) {
            // 节点类型: DIRECTORY => 父节点类型: DIRECTORY
            case DIRECTORY:
                error = ResourceEnum.DIRECTORY.match(parentType) ? null : "可展开目录的父节点类型只能为根目录或者可展开目录";
                break;

            // 节点类型: PAGE => 父节点类型: DIRECTORY
            case PAGE:
                error = ResourceEnum.DIRECTORY.match(parentType) ? null : "可打开页面的父节点类型只能为根目录或者可展开目录";
                break;
            // 节点类型: CONTENT => 父节点类型: PAGE
            case CONTENT:
                error = ResourceEnum.PAGE.match(parentType) ? null : "其它类型的父节点类型只能为可打开页面";
                break;
            default:
                error = "不支持当前节点类型";
        }
        return error;
    }

    private void updateChildren(List<ResourceNode> children, Long newParentId, int level, List<Resource> listToUpdate) {
        for (ResourceNode child : children) {
            if (child.getId().equals(newParentId)) {
                throw new IllegalArgumentException("新的父节点资源id与递归子节时的id相同");
            }
            Resource resourceToUpdate = new Resource();
            resourceToUpdate.setId(child.getId());
            resourceToUpdate.setResourceLevel(level + 1);
            listToUpdate.add(resourceToUpdate);
            if (!CollectionUtils.isEmpty(child.getChildren())) {
                updateChildren(child.getChildren(), newParentId, resourceToUpdate.getResourceLevel(), listToUpdate);
            }
        }
    }

    @ApiOperation(value = "删除单个前端资源")
    @PostMapping(value = "/delete/{id}")
    @RequiresPermissions("resource:delete")
    public WebResult<Void> deleteByPrimaryKey(@PathVariable Long id) {
        if (!resourceTreeLock.compareAndSet(false, true)) {
            return ResultBuilder.buildFailed(ResultEnum.CONCURRENT_OPERATE, "发现有多个用户的并发操作，请稍后刷新页面重新操作");
        }
        try {
            List<ResourceNode> tree = resourceService.selectTreeWithPermission();
            String errorMsg = null;
            ResourceNode currentNode = TreeUtil.getTreeNode(tree, id);
            if (currentNode == null) {
                return ResultBuilder.buildFailed(ResultEnum.CONCURRENT_OPERATE, "该资源不存在, id=" + id);
            }
            if (!CollectionUtils.isEmpty(currentNode.getChildren())) {
                errorMsg = "不能删除，该资源还存在子节点";
            } else if (ResourceEnum.CONTENT.match(currentNode.getType()) && resourceService.isRelateToPermission(id)) {
                // 是否有权限关联
                errorMsg = "有权限与该资源关联,请资源回收该权限";
            } else if (resourceService.isRelateToRole(id)) {
                // 是否有角色关联
                errorMsg = "有角色与该资源关联,请角色回收该资源";
            }

            if (errorMsg != null) {
                return ResultBuilder.buildFailed(HttpStatus.BAD_REQUEST.value(), errorMsg);
            }
            resourceService.deleteByPrimaryKey(id);
            return ResultBuilder.buildSuccess();
        } finally {
            resourceTreeLock.compareAndSet(true, false);
        }
    }

    @ApiOperation(value = "分页查询前端资源")
    @GetMapping(value = "/page")
    @RequiresPermissions("resource:retrieve")
    public WebPageResult<ResourceVO> selectByPagination(
            @Validated PageVO pageVO, @Validated ResourceVO resourceVO
    ) {
        ResourceExample resourceExample = buildPageExample(resourceVO);
        Integer pageNum = pageVO.getPageNum();
        Integer pageSize = pageVO.getPageSize();
        Page<Resource> page = (Page<Resource>) resourceService.selectByPagination(pageNum, pageSize, resourceExample);
        return ResultBuilder.buildPageSuccess(page, ResourceVO.class);
    }

    private ResourceExample buildPageExample(ResourceVO resourceVO) {
        return null;
    }

    @ApiOperation(value = "查询树型结构的所有资源")
    @GetMapping(value = "/tree")
    @RequiresPermissions("resource:retrieve")
    public WebResult<List<ResourceNode>> selectTree() {
        List<ResourceNode> data = resourceService.selectTreeWithPermission();
        return ResultBuilder.buildSuccess(data);
    }

    @ApiOperation(value = "根据单个角色ID查询叶子节点资源")
    @GetMapping(value = "/leaf-role/{roleId}")
    @RequiresPermissions("resource:retrieve")
    public WebResult<List<Long>> selectLeafIdsByRoleId(@PathVariable Long roleId) {
        List<Long> leafIds = resourceService.selectLeafIdsByRoleIds(roleId);
        return ResultBuilder.buildSuccess(leafIds);
    }

    @ApiOperation(value = "根据角色ID数组查询叶子节点资源")
    @GetMapping(value = "/leaf-roles")
    @RequiresPermissions("resource:retrieve")
    public WebResult<List<Long>> selectLeafIdsByRoleIds(@RequestParam(name = "roleIds[]") Long[] roleIds) {
        List<Long> leafIds = resourceService.selectLeafIdsByRoleIds(roleIds);
        return ResultBuilder.buildSuccess(leafIds);
    }

}
