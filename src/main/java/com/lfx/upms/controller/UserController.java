package com.lfx.upms.controller;

import com.github.pagehelper.Page;
import com.lfx.upms.controller.excel.UserModel;
import com.lfx.upms.controller.valid.InsertGroup;
import com.lfx.upms.controller.valid.UpdateGroup;
import com.lfx.upms.controller.valid.UpdateSelectiveGroup;
import com.lfx.upms.controller.vo.GrantUserVO;
import com.lfx.upms.controller.vo.PageVO;
import com.lfx.upms.controller.vo.ResourceNode;
import com.lfx.upms.controller.vo.UserCountVO;
import com.lfx.upms.controller.vo.UserInfoVO;
import com.lfx.upms.controller.vo.UserVO;
import com.lfx.upms.domain.Resource;
import com.lfx.upms.domain.User;
import com.lfx.upms.domain.UserExample;
import com.lfx.upms.enums.ResourceEnum;
import com.lfx.upms.enums.ResultEnum;
import com.lfx.upms.result.ResultBuilder;
import com.lfx.upms.result.WebPageResult;
import com.lfx.upms.result.WebResult;
import com.lfx.upms.service.ResourceService;
import com.lfx.upms.service.UserService;
import com.lfx.upms.util.BeanCopierUtil;
import com.lfx.upms.util.DateUtil;
import com.lfx.upms.util.ExcelUtil;
import com.lfx.upms.util.TreeUtil;
import com.lfx.upms.util.WebConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Api(value = "User Controller", tags = "操作用户相关接口")
@RequestMapping("/user")
@RestController
public class UserController {

    private final UserService userService;

    private final ResourceService resourceService;

    private final PasswordEncoder passwordEncoder;

    private final SessionDAO sessionDAO;

    @Autowired
    public UserController(
            UserService userService,
            ResourceService resourceService,
            PasswordEncoder passwordEncoder,
            SessionDAO sessionDAO
    ) {
        this.userService = userService;
        this.resourceService = resourceService;
        this.passwordEncoder = passwordEncoder;
        this.sessionDAO = sessionDAO;
    }

    @ApiOperation(value = "获取单个用户")
    @GetMapping("/{id}")
    @RequiresPermissions("user:retrieve")
    public WebResult<UserVO> selectByPrimaryKey(@PathVariable Long id) {
        User user = userService.selectByPrimaryKey(id);
        return ResultBuilder.buildSuccess(BeanCopierUtil.copy(user, UserVO.class));
    }

    @ApiOperation(value = "更新单个用户，没有值的字段都设置为NULL")
    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermissions("user:update")
    public WebResult<Void> updateByPrimaryKey(@Validated(value = UpdateGroup.class) @RequestBody UserVO userVO) {
        User user = BeanCopierUtil.copy(userVO, User.class);
        userService.updateByPrimaryKey(user);
        return ResultBuilder.buildSuccess();
    }

    @ApiOperation(value = "更新单个用户，不更新值为NULL的字段")
    @PostMapping(value = "/updateSelective", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermissions("user:update")
    public WebResult<Void> updateByPrimaryKeySelective(
            @Validated(value = UpdateSelectiveGroup.class) @RequestBody UserVO userVO
    ) {
        User user = BeanCopierUtil.copy(userVO, User.class);
        userService.updateByPrimaryKeySelective(user);
        return ResultBuilder.buildSuccess();
    }

    @ApiOperation(value = "分页查询用户")
    @GetMapping(value = "/page")
    @RequiresPermissions("user:retrieve")
    public WebPageResult<UserVO> selectByPagination(
            @Validated PageVO pageVO, @Validated UserVO userVO
    ) {
        UserExample userExample = buildPageExample(userVO);
        Integer pageNum = pageVO.getPageNum();
        Integer pageSize = pageVO.getPageSize();
        Page<User> page = (Page<User>) userService.selectByPagination(pageNum, pageSize, userExample);
        return ResultBuilder.buildPageSuccess(page, UserVO.class);
    }

    private UserExample buildPageExample(UserVO userVO) {
        String nickname = userVO.getNickname();
        Integer gender = userVO.getGender();
        String mobile = userVO.getMobile();

        if (nickname == null && gender == null && mobile == null) {
            return null;
        }

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();

        if (StringUtils.isNoneBlank(nickname)) {
            criteria.andNicknameEqualTo(nickname);
        }

        if (StringUtils.isNoneBlank(mobile)) {
            criteria.andMobileEqualTo(mobile);
        }

        if (gender != null) {
            criteria.andGenderEqualTo(gender);
        }
        return example;
    }

    @ApiOperation(value = "给用户授权角色")
    @PostMapping(value = "/grant", produces = MediaType.APPLICATION_JSON_VALUE)
    @RequiresPermissions("user:grant")
    public WebResult<Void> grant(@Validated @RequestBody GrantUserVO grantUserVo, HttpServletRequest request) {
        Long doGrantUser = (Long) request.getAttribute(WebConstants.CURRENT_USER_ID);
        Long doGrantTime = System.currentTimeMillis();
        Long userId = grantUserVo.getUserId();
        List<Long> roleIds = grantUserVo.getRoleIds();
        userService.grant(userId, roleIds, doGrantUser, doGrantTime);
        return ResultBuilder.buildSuccess();
    }

    @ApiOperation(value = "excel导出用户数据")
    @GetMapping(value = "/export")
    @RequiresPermissions("user:export")
    public void upload(HttpServletResponse response) throws IOException {
        List<User> dataList = userService.selectByExample(null);
        List<UserModel> result = new ArrayList<>(dataList.size());
        // todo easyexcel 后期有根据枚举值的自动转换，正式版发布后更改 参考 https://github.com/alibaba/easyexcel/pull/329
        String[] gender = {"男", "女"};
        String[] frozen = {"正常", "冻结"};
        String[] disable = {"正常", "禁用"};
        for (User user : dataList) {
            result.add(new UserModel(user, gender, frozen, disable));
        }

        String fileName = "用户-" + DateUtil.getShortFormatOfLine();
        ExcelUtil.export(fileName, result, response);
    }

    @ApiOperation(value = "用户注册接口")
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResult<Void> register(
            @Validated(value = InsertGroup.class) @RequestBody UserVO userVO
    ) {
        String username = userVO.getUsername();
        User userExisting = userService.selectOneByUsername(username);
        if (userExisting != null) {
            return ResultBuilder.buildFailed(ResultEnum.REPEAT_DATA, "用户已经存在,不能重复注册");
        }
        String rawPassword = passwordEncoder.encode(userVO.getPassword());
        userVO.setPassword(rawPassword);
        User user = BeanCopierUtil.copy(userVO, User.class);
        user.setCreateTime(System.currentTimeMillis());
        userService.insertSelective(user);
        return ResultBuilder.buildSuccess();
    }

    @ApiOperation(value = "获取账号信息")
    @GetMapping("/info")
    public WebResult<UserInfoVO> getInfo(HttpServletRequest request) {

        // 1. 查询用户基本信息
        Long userId = (Long) request.getAttribute(WebConstants.CURRENT_USER_ID);
        User user = userService.selectByPrimaryKey(userId);

        // 2. 查询用户资源信息
        List<Resource> resourceList = resourceService.selectByUserId(user.getId());
        Set<String> resourceKey = new HashSet<>();
        List<ResourceNode> menuTree = new ArrayList<>();
        for (Resource resource : resourceList) {
            if (ResourceEnum.CONTENT.match(resource.getType())) {
                resourceKey.add(resource.getResourceKey());
            } else {
                menuTree.add(BeanCopierUtil.copy(resource, ResourceNode.class));
            }
        }
        return ResultBuilder.buildSuccess(new UserInfoVO(user.getNickname(), user.getAvatar(), TreeUtil.buildTree(menuTree), resourceKey));
    }

    @ApiOperation(value = "用户运营统计数据")
    @GetMapping(value = "/count")
    public WebResult<UserCountVO> countUser() {
        Long userAmount = userService.countByExample(null);
        Integer onlineUser = sessionDAO.getActiveSessions().size();
        Integer registerUser = countRegisteredUserYesterday();
        UserCountVO userCountVo = new UserCountVO(userAmount, onlineUser, registerUser);
        return ResultBuilder.buildSuccess(userCountVo);
    }

    private Integer countUser;
    private LocalDate lastCountDate;
    private AtomicBoolean countLock = new AtomicBoolean();

    private Integer countRegisteredUserYesterday() {
        LocalDate now = LocalDate.now();

        // 如果统计数据过期，则重新加载
        if (lastCountDate == null || now.compareTo(lastCountDate) != 0) {
            if (countLock.compareAndSet(false, true)) {
                try {
                    long startTime = DateUtil.getStartOfDate(now.minusDays(1));
                    long endTime = DateUtil.getStartOfDate(now);
                    countUser = userService.countRegisteredUser(startTime, endTime);
                    lastCountDate = now;
                } finally {
                    countLock.compareAndSet(true, false);
                }
            }
        }
        return countUser;
    }

}
