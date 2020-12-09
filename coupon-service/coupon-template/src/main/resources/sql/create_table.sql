CREATE TABLE `coupon_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `available` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是可用状态',
  `expired` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否过期',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '优惠券名称',
  `logo` varchar(256) NOT NULL DEFAULT '' COMMENT '优惠券logo',
  `intro` varchar(256) NOT NULL DEFAULT '' COMMENT '优惠券描述',
  `category` varchar(64) NOT NULL DEFAULT '' COMMENT '优惠券分类',
  `product_line` int(11) NOT NULL DEFAULT '0' COMMENT '产品线',
  `coupon_count` int(11) NOT NULL DEFAULT '0' COMMENT '总数',
  `create_time` datetime NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建用户',
  `template_key` varchar(128) NOT NULL DEFAULT '' COMMENT '优惠券模板的编码',
  `target` int(11) NOT NULL DEFAULT '0' COMMENT '目标用户',
  `rule` varchar(1024) NOT NULL DEFAULT '' COMMENT '优惠券规则',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `idx_category` (`category`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='优惠券模板表';

