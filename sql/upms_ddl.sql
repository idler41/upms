CREATE DATABASE IF NOT EXISTS `upms` DEFAULT CHARSET UTF8 COLLATE UTF8_GENERAL_CI;

USE `upms`;

-- ----------------------------
-- Table structure for upms_user
-- ----------------------------
CREATE TABLE IF NOT EXISTS `upms_user` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(20) NOT NULL COMMENT '用户名',
  `password` VARCHAR(128) NOT NULL COMMENT '登陆密码',
  `nickname` VARCHAR(20) NULL DEFAULT NULL COMMENT '用户昵称',
  `mobile` VARCHAR(20) NULL DEFAULT NULL COMMENT '手机号码',
  `email` VARCHAR(64)  NULL DEFAULT NULL COMMENT '邮箱地址',
  `avatar` VARCHAR(128)  NULL DEFAULT NULL COMMENT '用户头像',
  `gender` TINYINT(1) NULL DEFAULT 0 COMMENT '性别',
  `birthday` BIGINT(20) NULL DEFAULT NULL COMMENT '出生日期：时分秒默认为凌晨',
  `create_user` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `create_time` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '注册时间',
  `reset_pwd_time` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '上次密码重置时间',

  `frozen` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否被冻结：0 否 1 是',
  `disable` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否被禁用：0 否 1 是',

  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_username`(`username`)
)  ENGINE=INNODB DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI COMMENT '用户表';

-- ----------------------------
-- Table structure for upms_role
-- ----------------------------
CREATE TABLE IF NOT EXISTS `upms_role` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_key` VARCHAR(128) NOT NULL COMMENT '角色标识',
  `label` VARCHAR(32) NOT NULL COMMENT '角色标签',

  `admin` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否超级管理员：0 否 1 是',

  `create_user` BIGINT(20) UNSIGNED NOT NULL COMMENT '创建人',
  `create_time` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '创建时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_key`(`role_key`)

)  ENGINE=INNODB DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI COMMENT '角色表';

-- ----------------------------
-- Table structure for upms_permission
-- ----------------------------
CREATE TABLE IF NOT EXISTS `upms_permission` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,

  `permission_key` VARCHAR(255) NOT NULL COMMENT '权限标识',
  `label` VARCHAR(64) NOT NULL COMMENT '权限标签',

  `create_user` BIGINT(20) UNSIGNED NOT NULL COMMENT '创建人',
  `create_time` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '创建时间',

  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_key`(`permission_key`)
)  ENGINE=INNODB DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI COMMENT '权限表';

-- ----------------------------
-- Table structure for upms_resource
-- ----------------------------
CREATE TABLE IF NOT EXISTS `upms_resource` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,

  `parent_id` BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '父级资源ID',
  `resource_key` VARCHAR(32) NOT NULL COMMENT '资源标识',
  `label` VARCHAR(32) NOT NULL COMMENT '资源描述',
  `icon` VARCHAR(128)  NULL DEFAULT NULL COMMENT '资源图标',
  `resource_level` INT(11) UNSIGNED NULL DEFAULT NULL DEFAULT 0 COMMENT '资源层级',
  `level_order` INT(11) UNSIGNED NULL DEFAULT NULL COMMENT '层级顺序',
  `leaf` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否为叶子节点：0 否 1 是',

  `type` TINYINT(1) NULL DEFAULT 0 COMMENT '资源类型: 0 可展开目录 1 可打开页面 2 其它页面元素如按钮/列表等',

  `create_user` BIGINT(20) UNSIGNED NOT NULL COMMENT '创建人',
  `create_time` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '创建时间',

  PRIMARY KEY (`id`),
  KEY `idx_parent_id`(`parent_id`),
  UNIQUE KEY `udx_key`(`resource_key`)
)  ENGINE=INNODB DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI COMMENT '前端资源表';


-- ----------------------------
-- Table structure for upms_user_role
-- ----------------------------
CREATE TABLE IF NOT EXISTS `upms_user_role` (
  `user_id` BIGINT(20) UNSIGNED NOT NULL COMMENT '用户ID',
  `role_id` BIGINT(20) UNSIGNED NOT NULL COMMENT '角色ID',

  `create_user` BIGINT(20) UNSIGNED NOT NULL COMMENT '授权人',
  `create_time` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '授权时间',

  PRIMARY KEY (`user_id`, `role_id`),
  KEY `idx_role_id`(`role_id`)
)  ENGINE=INNODB DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI COMMENT '用户-角色关联表';

-- ----------------------------
-- Table structure for upms_role_permission
-- ----------------------------
CREATE TABLE IF NOT EXISTS `upms_role_permission` (
  `role_id` BIGINT(20) UNSIGNED NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT(20) UNSIGNED NOT NULL COMMENT '权限ID',

  `create_user` BIGINT(20) UNSIGNED NOT NULL COMMENT '授权人',
  `create_time` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '授权时间',

  PRIMARY KEY (`role_id`, `permission_id`),
  KEY `idx_permission_id`(`permission_id`)
)  ENGINE=INNODB DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI COMMENT '角色-权限关联表';


-- ----------------------------
-- Table structure for upms_role_resource
-- ----------------------------
CREATE TABLE IF NOT EXISTS `upms_role_resource` (
  `role_id` BIGINT(20) UNSIGNED NOT NULL COMMENT '角色ID',
  `resource_id` BIGINT(20) UNSIGNED NOT NULL COMMENT '资源ID',

  `create_user` BIGINT(20) UNSIGNED NOT NULL COMMENT '授权人',
  `create_time` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '授权时间',

  PRIMARY KEY (`role_id`, `resource_id`),
  KEY `idx_resource_id`(`resource_id`)
)  ENGINE=INNODB DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI COMMENT '角色-资源关联表';


-- ----------------------------
-- Table structure for upms_resource_permission
-- ----------------------------
CREATE TABLE IF NOT EXISTS `upms_resource_permission` (
  `resource_id` BIGINT(20) UNSIGNED NOT NULL,
  `permission_id` BIGINT(20) UNSIGNED NOT NULL,

  `create_user` BIGINT(20) UNSIGNED NOT NULL COMMENT '授权人',
  `create_time` BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT '授权时间',

  PRIMARY KEY (`resource_id`, `permission_id`),
  KEY `idx_role_id`(`permission_id`)
)  ENGINE=INNODB DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI COMMENT '资源-权限关联表';

