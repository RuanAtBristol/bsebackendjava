# 数据库初始化

-- 创建库
create database if not exists bse_temp;

-- 切换库
use bse_temp;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
) comment '用户' collate = utf8mb4_unicode_ci;

INSERT INTO user (userAccount, userPassword, userName)
VALUES ('bse', 'bse', 'bse');

create table if not exists chart
(
    id         bigint auto_increment comment 'id' primary key,
    goal       varchar(256)                       null comment '分析目标',
    `name`     varchar(128)                       null comment '图表名称',
    chartData  text null comment 'chart data',
    chartType  varchar(128)                       null comment '图表类型',
    genChart   text null comment '生成的图表数据',
    genResult  text null comment '生成的分析结论',
    userId      bigint                            null comment '创建图标的用户的id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)comment '图表信息表' collate = utf8mb4_unicode_ci;

