# 简介

upms是一个基于RBAC(Resource-Based Access Control)模型实现的按钮级别的细粒度权限管理系统。它是一个前后端分离的系统，用户可动态配置角色后台api权限 + 管理前端目录/按钮/表格等元素的显示和隐藏。

## upms-server

upms-server是upms系统的后端实现，基于springboot2.1.X、mybatis、shiro、ehcache开发。


## 系统功能

1. 资源管理
	- 后端权限与资源进行绑定(如：按钮/表格等前端元素绑定后台api权限)
	- 资源树管理(目录管理 + 页面前端元素 管理)

2. 角色管理
	- CRUD
	- 导出
	- 角色授权 (前端目录授权 + 后台api授权，可自动绑定前端元素绑定的api权限)

3. 用户管理
	- 用户注册/登录/登出
	- 用户授权
	- 用户Excel导出

4. 权限管理
	- CRUD
	- 权限Excel导出
	
## 项目截图

### 角色授权

<img src="screenshots/grant-role.gif" />

### 用户授权

<img src="screenshots/grant-user.gif" />

### 重新登录

<img src="screenshots/relogin.gif" />

### 资源绑定权限



## 在线体验

1. 登录页面每次加载都会随机生成一个用户名，范围为user1 ~ user2000。user后面的数字奇数为管理员，偶数为普通用户
2. 本系统登录为单用户登陆，如正常操作时提示登录异常，一般是有其他人登录了该账户，请重新登录。
3. 授权操作时，为了不影响他人体验，最好自己新增角色，然后对该角色授权。

所有用户登录密码都是123456。演示地址：[http://47.98.170.77](http://47.98.170.77?_blank)

## 广告

Hi拼购，云主机低至199元/年<a href="https://www.aliyun.com/acts/hi-group-buying?userCode=fdg6btmo">https://promotion.aliyun.com/ntms/act/enterprise-discount.html?userCode=fdg6btmo</a>