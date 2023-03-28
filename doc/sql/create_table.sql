create table user_center.user
(
    id           bigint auto_increment comment '用户id'
        primary key,
    username     varchar(256)                                                                  null comment '用户昵称',
    userAccount  varchar(256)                                                                  null comment '用户账号',
    userRole     tinyint       default 0                                                       not null comment '角色（0 表示普通用户，1表示管理员）',
    avatarUrl    varchar(1024) default 'https://avatars.githubusercontent.com/u/102940794?v=4' null comment '用户头像链接',
    gender       tinyint                                                                       null comment '性别（0---女    1---男）',
    userPassword varchar(512)                                                                  null comment '密码',
    phone        varchar(128)                                                                  null comment '电话',
    email        varchar(512)                                                                  null comment '邮箱',
    userStatus   int           default 0                                                       not null comment '状态（0表示正常）',
    createTime   datetime      default CURRENT_TIMESTAMP                                       null comment '创建日期',
    updateTime   datetime      default CURRENT_TIMESTAMP                                       null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint       default 0                                                       not null comment '删除状态（逻辑删除 0 正常 1 删除）',
    tags         varchar(1024)                                                                 null comment '用户专属标签列表（json）'
)
    comment '用户表';

