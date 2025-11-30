SET NAMES utf8mb4;
SET character_set_connection = utf8mb4;
SET collation_connection = utf8mb4_unicode_ci;

-- ----------------------------
-- 梦幻校园数据库设计
-- 基于北洋TT框架
-- ----------------------------

-- ----------------------------
-- 1、二手商品分类表
-- ----------------------------
drop table if exists dc_secondhand_category;
create table dc_secondhand_category (
  category_id       bigint(20)      not null auto_increment    comment '分类ID',
  category_name     varchar(50)     not null                   comment '分类名称',
  category_icon     varchar(100)    default ''                 comment '分类图标',
  sort_order        int(4)          default 0                  comment '显示顺序',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (category_id)
) engine=innodb auto_increment=1 comment = '二手商品分类表';

-- ----------------------------
-- 初始化-二手商品分类表数据
-- ----------------------------
insert into dc_secondhand_category values(1, '教材', '', 1, '0', 'admin', sysdate(), '', null, '');
insert into dc_secondhand_category values(2, '电子产品', '', 2, '0', 'admin', sysdate(), '', null, '');
insert into dc_secondhand_category values(3, '生活用品', '', 3, '0', 'admin', sysdate(), '', null, '');
insert into dc_secondhand_category values(4, '其他', '', 4, '0', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 2、二手商品表
-- ----------------------------
drop table if exists dc_secondhand_goods;
create table dc_secondhand_goods (
  goods_id          bigint(20)      not null auto_increment    comment '商品ID',
  user_id           bigint(20)      not null                   comment '发布用户ID',
  category_id       bigint(20)      not null                   comment '分类ID',
  goods_title       varchar(200)    not null                   comment '商品标题',
  goods_desc        text                                       comment '商品描述',
  goods_price       decimal(10,2)   not null                   comment '商品价格',
  original_price    decimal(10,2)   default null               comment '原价',
  goods_status      char(1)         default '0'                comment '商品状态（0待售 1已售 2已下架）',
  contact_way       varchar(50)    default ''                 comment '联系方式',
  location          varchar(200)   default ''                 comment '交易地点',
  view_count        int(11)         default 0                  comment '浏览次数',
  like_count         int(11)         default 0                  comment '点赞数',
  collect_count      int(11)         default 0                  comment '收藏数',
  images             varchar(2000)   default null               comment '商品图片（逗号分隔的URL）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (goods_id),
  key idx_user_id (user_id),
  key idx_category_id (category_id),
  key idx_goods_status (goods_status),
  key idx_create_time (create_time)
) engine=innodb auto_increment=1 comment = '二手商品表';

-- ----------------------------
-- 3、二手商品图片表
-- ----------------------------
drop table if exists dc_secondhand_images;
create table dc_secondhand_images (
  image_id          bigint(20)      not null auto_increment    comment '图片ID',
  goods_id          bigint(20)      not null                   comment '商品ID',
  image_url         varchar(500)    not null                   comment '图片地址',
  image_order       int(4)          default 0                  comment '图片排序',
  create_time       datetime                                   comment '创建时间',
  primary key (image_id),
  key idx_goods_id (goods_id)
) engine=innodb auto_increment=1 comment = '二手商品图片表';

-- ----------------------------
-- 4、二手商品收藏表
-- ----------------------------
drop table if exists dc_secondhand_favorite;
create table dc_secondhand_favorite (
  favorite_id       bigint(20)      not null auto_increment    comment '收藏ID',
  user_id           bigint(20)      not null                   comment '用户ID',
  goods_id          bigint(20)      not null                   comment '商品ID',
  create_time       datetime                                   comment '创建时间',
  primary key (favorite_id),
  unique key uk_user_goods (user_id, goods_id),
  key idx_user_id (user_id),
  key idx_goods_id (goods_id)
) engine=innodb auto_increment=1 comment = '二手商品收藏表';

-- ----------------------------
-- 5、二手交易订单表
-- ----------------------------
drop table if exists dc_secondhand_order;
create table dc_secondhand_order (
  order_id          bigint(20)      not null auto_increment    comment '订单ID',
  goods_id          bigint(20)      not null                   comment '商品ID',
  seller_id         bigint(20)      not null                   comment '卖家ID',
  buyer_id          bigint(20)      not null                   comment '买家ID',
  order_price       decimal(10,2)   not null                   comment '订单价格',
  order_status      char(1)         default '0'                comment '订单状态（0待支付 1已支付 2已完成 3已取消）',
  pay_time          datetime                                   comment '支付时间',
  complete_time     datetime                                   comment '完成时间',
  cancel_reason     varchar(500)   default null               comment '取消原因',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (order_id),
  key idx_goods_id (goods_id),
  key idx_seller_id (seller_id),
  key idx_buyer_id (buyer_id),
  key idx_order_status (order_status)
) engine=innodb auto_increment=1 comment = '二手交易订单表';

-- ----------------------------
-- 6、跑腿任务表
-- ----------------------------
drop table if exists dc_errand_task;
create table dc_errand_task (
  task_id           bigint(20)      not null auto_increment    comment '任务ID',
  user_id           bigint(20)      not null                   comment '发布用户ID',
  task_type         varchar(20)     not null                   comment '任务类型（代取/代买/代送/其他）',
  task_title        varchar(200)    not null                   comment '任务标题',
  task_desc         text                                       comment '任务描述',
  start_location    varchar(200)   not null                   comment '起点位置',
  end_location      varchar(200)    not null                   comment '终点位置',
  task_reward       decimal(10,2)   not null                   comment '任务报酬',
  time_requirement  varchar(200)   default ''                 comment '时间要求',
  task_status       char(1)         default '0'                comment '任务状态（0待接单 1进行中 2已完成 3已取消）',
  accepter_id       bigint(20)      default null               comment '接单人ID',
  accept_time       datetime                                   comment '接单时间',
  complete_time     datetime                                   comment '完成时间',
  view_count        int(11)         default 0                  comment '浏览次数',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (task_id),
  key idx_user_id (user_id),
  key idx_task_status (task_status),
  key idx_task_type (task_type),
  key idx_create_time (create_time)
) engine=innodb auto_increment=1 comment = '跑腿任务表';

-- ----------------------------
-- 7、论坛分类表
-- ----------------------------
drop table if exists dc_forum_category;
create table dc_forum_category (
  category_id       bigint(20)      not null auto_increment    comment '分类ID',
  category_name     varchar(50)     not null                   comment '分类名称',
  category_icon     varchar(100)    default ''                 comment '分类图标',
  sort_order        int(4)          default 0                  comment '显示顺序',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (category_id)
) engine=innodb auto_increment=1 comment = '论坛分类表';

-- ----------------------------
-- 初始化-论坛分类表数据
-- ----------------------------
insert into dc_forum_category values(1, '学习交流', '', 1, '0', 'admin', sysdate(), '', null, '');
insert into dc_forum_category values(2, '生活分享', '', 2, '0', 'admin', sysdate(), '', null, '');
insert into dc_forum_category values(3, '校园活动', '', 3, '0', 'admin', sysdate(), '', null, '');
insert into dc_forum_category values(4, '求助问答', '', 4, '0', 'admin', sysdate(), '', null, '');
insert into dc_forum_category values(5, '其他', '', 5, '0', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 8、论坛帖子表
-- ----------------------------
drop table if exists dc_forum_post;
create table dc_forum_post (
  post_id           bigint(20)      not null auto_increment    comment '帖子ID',
  user_id           bigint(20)      not null                   comment '发布用户ID',
  category_id       bigint(20)      not null                   comment '分类ID',
  post_title        varchar(200)    not null                   comment '帖子标题',
  post_content      text            not null                   comment '帖子内容',
  images            varchar(2000)   default ''                 comment '图片URLs（逗号分隔）',
  view_count        int(11)         default 0                  comment '浏览次数',
  like_count        int(11)         default 0                  comment '点赞数',
  comment_count     int(11)         default 0                  comment '评论数',
  is_top            char(1)         default '0'                comment '是否置顶（0否 1是）',
  is_hot            char(1)         default '0'                comment '是否热门（0否 1是）',
  status            char(1)         default '0'                comment '状态（0正常 1关闭）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (post_id),
  key idx_user_id (user_id),
  key idx_category_id (category_id),
  key idx_is_top (is_top),
  key idx_is_hot (is_hot),
  key idx_create_time (create_time)
) engine=innodb auto_increment=1 comment = '论坛帖子表';

-- ----------------------------
-- 9、论坛帖子图片表
-- ----------------------------
drop table if exists dc_forum_images;
create table dc_forum_images (
  image_id          bigint(20)      not null auto_increment    comment '图片ID',
  post_id           bigint(20)      not null                   comment '帖子ID',
  image_url         varchar(500)    not null                   comment '图片地址',
  image_order       int(4)          default 0                  comment '图片排序',
  create_time       datetime                                   comment '创建时间',
  primary key (image_id),
  key idx_post_id (post_id)
) engine=innodb auto_increment=1 comment = '论坛帖子图片表';

-- ----------------------------
-- 10、论坛评论表
-- ----------------------------
drop table if exists dc_forum_comment;
create table dc_forum_comment (
  comment_id        bigint(20)      not null auto_increment    comment '评论ID',
  post_id           bigint(20)      not null                   comment '帖子ID',
  user_id           bigint(20)      not null                   comment '评论用户ID',
  parent_id         bigint(20)      default 0                  comment '父评论ID（0表示顶级评论）',
  comment_content   text            not null                   comment '评论内容',
  like_count        int(11)         default 0                  comment '点赞数',
  status            char(1)         default '0'                comment '状态（0正常 1删除）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  primary key (comment_id),
  key idx_post_id (post_id),
  key idx_user_id (user_id),
  key idx_parent_id (parent_id),
  key idx_create_time (create_time)
) engine=innodb auto_increment=1 comment = '论坛评论表';

-- ----------------------------
-- 11、论坛点赞表
-- ----------------------------
drop table if exists dc_forum_like;
create table dc_forum_like (
  like_id           bigint(20)      not null auto_increment    comment '点赞ID',
  user_id           bigint(20)      not null                   comment '用户ID',
  target_type       char(1)         not null                   comment '目标类型（1帖子 2评论）',
  target_id         bigint(20)      not null                   comment '目标ID',
  create_time       datetime                                   comment '创建时间',
  primary key (like_id),
  unique key uk_user_target (user_id, target_type, target_id),
  key idx_target (target_type, target_id)
) engine=innodb auto_increment=1 comment = '论坛点赞表';

-- ----------------------------
-- 12、消息表
-- ----------------------------
drop table if exists dc_message;
create table dc_message (
  message_id        bigint(20)      not null auto_increment    comment '消息ID',
  sender_id         bigint(20)      default 0                  comment '发送者ID（0表示系统消息）',
  receiver_id       bigint(20)      not null                   comment '接收者ID',
  message_type      varchar(20)     not null                   comment '消息类型（system系统通知 trade交易消息 comment评论回复）',
  message_title     varchar(200)   default ''                 comment '消息标题',
  message_content   text            not null                   comment '消息内容',
  related_id        bigint(20)      default null               comment '关联ID（商品ID/任务ID/帖子ID等）',
  related_type      varchar(20)    default null               comment '关联类型',
  is_read           char(1)         default '0'                comment '是否已读（0未读 1已读）',
  read_time         datetime                                   comment '阅读时间',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_time       datetime                                   comment '创建时间',
  primary key (message_id),
  key idx_receiver_id (receiver_id),
  key idx_message_type (message_type),
  key idx_is_read (is_read),
  key idx_create_time (create_time)
) engine=innodb auto_increment=1 comment = '消息表';

-- ----------------------------
-- 13、用户信用评分表
-- ----------------------------
drop table if exists dc_user_credit;
create table dc_user_credit (
  credit_id         bigint(20)      not null auto_increment    comment '信用ID',
  user_id           bigint(20)      not null                   comment '用户ID',
  credit_score      decimal(5,2)    default 5.00               comment '信用评分（0-5分）',
  total_deals       int(11)         default 0                  comment '总交易次数',
  good_deals        int(11)         default 0                  comment '好评次数',
  bad_deals         int(11)         default 0                  comment '差评次数',
  update_time       datetime                                   comment '更新时间',
  primary key (credit_id),
  unique key uk_user_id (user_id)
) engine=innodb auto_increment=1 comment = '用户信用评分表';

-- ----------------------------
-- 14、交易评价表
-- ----------------------------
drop table if exists dc_trade_evaluate;
create table dc_trade_evaluate (
  evaluate_id       bigint(20)      not null auto_increment    comment '评价ID',
  order_id          bigint(20)      not null                   comment '订单ID',
  evaluator_id      bigint(20)      not null                   comment '评价人ID',
  evaluated_id      bigint(20)      not null                   comment '被评价人ID',
  evaluate_type     char(1)         not null                   comment '评价类型（1买家评价卖家 2卖家评价买家）',
  score             int(1)          not null                   comment '评分（1-5分）',
  evaluate_content  varchar(500)   default ''                 comment '评价内容',
  create_time       datetime                                   comment '创建时间',
  primary key (evaluate_id),
  key idx_order_id (order_id),
  key idx_evaluated_id (evaluated_id)
) engine=innodb auto_increment=1 comment = '交易评价表';

-- ----------------------------
-- 15、聊天会话表
-- ----------------------------
drop table if exists dc_chat_session;
create table dc_chat_session (
  session_id         bigint(20)      not null auto_increment    comment '会话ID',
  user1_id          bigint(20)      not null                   comment '用户1ID',
  user2_id          bigint(20)      not null                   comment '用户2ID',
  last_message      text                                       comment '最后一条消息',
  last_message_time datetime                                   comment '最后消息时间',
  user1_unread      int(11)         default 0                  comment '用户1未读数',
  user2_unread      int(11)         default 0                  comment '用户2未读数',
  user1_hidden      char(1)         default '0'                comment '用户1是否隐藏会话',
  user2_hidden      char(1)         default '0'                comment '用户2是否隐藏会话',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_time       datetime                                   comment '创建时间',
  update_time       datetime                                   comment '更新时间',
  primary key (session_id),
  unique key uk_users (user1_id, user2_id),
  key idx_user1_id (user1_id),
  key idx_user2_id (user2_id),
  key idx_update_time (update_time)
) engine=innodb auto_increment=1 comment = '聊天会话表';

-- ----------------------------
-- 16、聊天消息表
-- ----------------------------
drop table if exists dc_chat_message;
create table dc_chat_message (
  message_id        bigint(20)      not null auto_increment    comment '消息ID',
  session_id       bigint(20)      not null                   comment '会话ID',
  sender_id        bigint(20)      not null                   comment '发送者ID',
  receiver_id      bigint(20)      not null                   comment '接收者ID',
  message_content  text            not null                   comment '消息内容',
  message_type     varchar(20)     default 'text'             comment '消息类型（text文本 image图片）',
  is_read          char(1)         default '0'                comment '是否已读（0未读 1已读）',
  read_time        datetime                                   comment '阅读时间',
  del_flag         char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_time      datetime                                   comment '创建时间',
  primary key (message_id),
  key idx_session_id (session_id),
  key idx_sender_id (sender_id),
  key idx_receiver_id (receiver_id),
  key idx_create_time (create_time)
) engine=innodb auto_increment=1 comment = '聊天消息表';

-- ============================================
-- 系统菜单配置
-- ============================================
-- 添加内容管理模块菜单
-- 执行此SQL后，刷新页面即可在侧边栏看到"内容管理"菜单

-- 插入"内容管理"父菜单
INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '内容管理', 0, 5, 'dreamcampus', NULL, '', '', 1, 0, 'M', '0', '0', '', 'guide', 'admin', NOW(), '', NULL, '梦幻校园内容管理'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '内容管理' AND parent_id = 0
);

-- 获取刚插入的父菜单ID
SET @parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '内容管理' AND parent_id = 0 LIMIT 1);

-- 插入"二手商品管理"子菜单
INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '二手商品', @parent_id, 1, 'secondhand', 'dreamcampus/secondhand/index', '', '', 1, 0, 'C', '0', '0', 'dreamcampus:secondhand:list', 'shopping', 'admin', NOW(), '', NULL, '二手商品管理菜单'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '二手商品' AND parent_id = @parent_id
);

-- 插入"跑腿任务管理"子菜单
INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '跑腿任务', @parent_id, 2, 'errand', 'dreamcampus/errand/index', '', '', 1, 0, 'C', '0', '0', 'dreamcampus:errand:list', 'star', 'admin', NOW(), '', NULL, '跑腿任务管理菜单'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '跑腿任务' AND parent_id = @parent_id
);

-- 插入"论坛帖子管理"子菜单（如果不存在）
INSERT INTO sys_menu(menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '论坛帖子', @parent_id, 3, 'forum', 'dreamcampus/forum/index', '', '', 1, 0, 'C', '0', '0', 'dreamcampus:forum:list', 'message', 'admin', NOW(), '', NULL, '论坛帖子管理菜单'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_name = '论坛帖子' AND parent_id = @parent_id
);

-- 为超级管理员角色分配菜单权限（如果角色ID为1）
INSERT INTO sys_role_menu(role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_name IN ('内容管理', '二手商品', '跑腿任务', '论坛帖子')
AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = sys_menu.menu_id
);

-- ============================================
-- 系统配置
-- ============================================
-- 开启用户注册功能
-- 说明：此脚本用于开启系统用户注册功能
-- 执行后，用户可以通过前端注册页面进行注册

-- 方法1：如果配置项已存在，直接更新
UPDATE sys_config 
SET config_value = 'true' 
WHERE config_key = 'sys.account.registerUser';

-- 方法2：如果配置项不存在，则插入新配置
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'true', 'Y', 'admin', sysdate(), '是否开启注册用户功能（true开启，false关闭）'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_config WHERE config_key = 'sys.account.registerUser'
);

