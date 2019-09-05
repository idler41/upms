# 简介

upms是一个基于RBAC(Resource-Based Access Control)模型实现的按钮级别的细粒度权限管理系统。

upms是一个前后端分离的系统。前端基于vue-element-admin二次开发，后端基于基于springboot2.1.X、mybatis、shiro、ehcache实现。

系统优点：

- 动态管理用户权限，修改用户权限即时生效
- 后台api权限可与表格/按钮等元素动态绑定，同时管理前端权限与后台权限


## 代码结构

```java
├── docker								// docker compose 服务编排
├── screenshots
├── sql									// 建表与初始化sql
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── lfx
│       │           └── upms
│       │               ├── captcha					// 验证码管理
│       │               ├── config
│       │               ├── controller
│       │               │   ├── excel
│       │               │   ├── valid					// 自定义校验
│       │               │   └── vo
│       │               ├── domain
│       │               ├── enums
│       │               ├── exception
│       │               │   └── handler					// 全局异常处理
│       │               ├── mapper
│       │               │   └── base
│       │               ├── result
│       │               ├── service
│       │               │   ├── base
│       │               │   └── impl
│       │               ├── shiro
│       │               │   ├── aop					// 自定义shiro权限校验handler
│       │               │   ├── filter					// 自定义shiro filter
│       │               │   ├── listener				// 用户认证监听
│       │               │   ├── matcher					// 自定义密码校验
│       │               │   ├── realm
│       │               │   └── session					// 自定义session管理
│       │               └── util
│       ├── resources
│       │   ├── com
│       │   │   └── lfx
│       │   │       └── upms
│       │   │           └── mapper
│       │   ├── dev							// 远程开发环境配置
│       │   ├── local							// 本地开发环境配置
│       │   └── prod							// 生产环境配置

├── Dockerfile								// docker 镜像制作文件
├── Jenkinsfile								// jenkins pipeline 自动化构建
├── RECORD-07-17-19-9-13-47-AM.jmx					// jmeter 登录并发测试脚本

```

## upms-server

### 登录管理

自定义了RestLoginFilter，RestLogoutFilter，RestUserFilter。登录成功或失败，不重定向，而是返回json数据。

本系统支持同时存储多种加密方式的密码，密码加密由spring boot的PasswordEncoder处理。

缓存中存储了userId => sessionId，ssessionId => session对象的kv值，用于实现单用户登陆功能。本系统用ehcache的读写锁方式，保证了并发情况下单用户登录的正常处理逻辑。

```java
/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-04-27 18:35
 */
@Slf4j
public class EhCacheKickoutListener implements AuthenticationListener {

    private final boolean kickoutEnable;

    private final Ehcache sessionCache;

    private final Ehcache kickoutCache;

    public EhCacheKickoutListener(boolean kickoutEnable, Ehcache sessionCache, Ehcache kickoutCache) {
        this.kickoutEnable = kickoutEnable;
        this.sessionCache = sessionCache;
        this.kickoutCache = kickoutCache;
    }

    @Override
    public void onSuccess(AuthenticationToken token, AuthenticationInfo info) {
        if (!kickoutEnable) {
            return;
        }

        WebSubject subject = (WebSubject) SecurityUtils.getSubject();
        HttpServletRequest request = (HttpServletRequest) subject.getServletRequest();
        Long userId = (Long) request.getAttribute(WebConstants.CURRENT_USER_ID);
        kickoutCache.acquireWriteLockOnKey(userId);
        try {
            // 1. 查询上一次登录的sessionId，如果有值则根据该sessionId删除缓存中的session
            Element element = kickoutCache.get(userId);
            if (element != null) {
                String sessionId = (String) element.getObjectValue();
                if (sessionId != null) {
                    sessionCache.remove(sessionId);
                }
            }

            // 2. 放入新的sessionId
            kickoutCache.put(new Element(userId, subject.getSession().getId()));
        } finally {
            kickoutCache.releaseWriteLockOnKey(userId);
        }
    }

    @Override
    public void onFailure(AuthenticationToken token, AuthenticationException ae) {
        // do nothing
    }

    @Override
    public void onLogout(PrincipalCollection principals) {
        // do nothing
    }

}
```

### session管理

shiro中以session对象的形式存储在缓存中。用户登录成功后，shiro会频繁的从缓存中加载session对象读取对象中的数据。假如session不是存储在本地，频繁的网络操作会影响系统性能。

所以本系统自定义了WebSessionManager，每次从缓存中加载了session对象后，将session对象放入HttpServletRequest中。session更新时，删除HttpServletRequest的session对象。

shiro的session存储在redis中有以下缺点：

- 修改session中的一个字段，需要加载整个session对象。不能高效使用redis中hash命令
- SimpleSession对象有startTimestamp、stopTimestamp、lastAccessTime等字段，shiro中的filter每次拦截请求都会更新这些字段用于管理session过期。而redis中存储session只需要expire操作就可管理session过期功能


### 权限管理

shiro以spring aop方式实现权限验证。AopAllianceAnnotationsAuthorizingMethodInterceptor默认提供了5个拦截器处理不同的权限注解。PermissionAnnotationMethodInterceptor拦截器以PermissionAnnotationHandler处理权限验证功能。

PermissionAnnotationHandler调用subject.checkPermission实现权限验证。

shiro的session管理在redis中存在缺陷 => 不希望shiro管理session => 不使用shiro认证模块 => 权限验证时，subject将得不到正确解析

按照以上逻辑，本系统自定义了NoSubjectPermissionAnnotationHandler，直接通过securityManager.checkPermissions的方式进行权限验证。

## 资源(resource)

upms将前端权限关联的元素统称为资源。如：侧边栏的目录树、表格、按钮等。

资源可与后台api权限进行关联。授权时，勾选该资源可自动关联后台api接口权限。
	
## 系统截图

### 角色授权

<img src="screenshots/grant-role.gif" />

### 用户授权

<img src="screenshots/grant-user.gif" />

### 重新登录

<img src="screenshots/relogin.gif" />


## 在线体验

1. 登录页面每次加载都会随机生成一个用户名，范围为user1 ~ user2000。user后面的数字奇数为管理员，偶数为普通用户
2. 本系统登录为单用户登陆，如正常操作时提示登录异常，一般是有其他人登录了该账户，请重新登录。
3. 授权操作时，为了不影响他人体验，最好自己新增角色，然后对该角色授权。

所有用户登录密码都是123456。演示地址：[http://47.98.170.77](http://47.98.170.77?_blank)

## 系统潜在问题

用户关联的权限集合存储在缓存中。如果系统每天平均有M个用户在线，每个用户平均有N条权限，则缓存中存储M * N条权限。N数量越多，占用缓存空间也越大，所以应尽量减少N的数量。

shiro支持复合权限(如："user:insert,update")，可减少用户关联权限个数，消除上述潜在问题。

shiro虽然支持解析验证复合权限功能，但本系统前后端处理授权逻辑对复合权限支持不完善，下一个版本会完善该功能。

## TODO

1. 权限相关缓存提供ehcache、redis两种存储方式
2. 自定义filter实现用户认证功能，只保留shiro的权限验证功能
3. 优化前后端授权逻辑，完善对复合权限(如："user:insert,update")的授权管理

## 广告

Hi拼购，云主机低至199元/年<a href="https://www.aliyun.com/acts/hi-group-buying?userCode=fdg6btmo">https://promotion.aliyun.com/ntms/act/enterprise-discount.html?userCode=fdg6btmo</a>
