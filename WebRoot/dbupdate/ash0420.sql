/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50027
Source Host           : localhost:3306
Source Database       : ash

Target Server Type    : MYSQL
Target Server Version : 50027
File Encoding         : 65001

Date: 2015-04-20 02:20:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `ab_appraise`
-- ----------------------------
DROP TABLE IF EXISTS `ab_appraise`;
CREATE TABLE `ab_appraise` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) default NULL COMMENT '用户ID',
  `mer_id` varchar(32) default NULL COMMENT '商户ID',
  `order_id` varchar(32) default NULL COMMENT '订单ID',
  `qdr_id` varchar(32) default NULL COMMENT '接单人ID',
  `datetime` varchar(50) default NULL COMMENT '评价时间',
  `type` varchar(1) default NULL COMMENT '评价类型 1 好评 2中评 3差评',
  `content` varchar(255) default NULL,
  `dync_mah` int(11) default NULL COMMENT '动态评分-描述相符',
  `dync_ser` int(11) default NULL COMMENT '动态评分-服务态度',
  `dync_spd` int(11) default NULL COMMENT '动态评分- 发货速度',
  `dync_val` float(11,1) default NULL,
  `private` varchar(1) default '1' COMMENT '是否匿名 1公开 2匿名',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_appraise
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_appraise_img`
-- ----------------------------
DROP TABLE IF EXISTS `ab_appraise_img`;
CREATE TABLE `ab_appraise_img` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `image` longblob NOT NULL COMMENT '上传图片',
  `img_url` varchar(255) NOT NULL COMMENT '图片路径',
  `app_id` varchar(32) NOT NULL COMMENT '评价ID',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_appraise_img
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_card`
-- ----------------------------
DROP TABLE IF EXISTS `ab_card`;
CREATE TABLE `ab_card` (
  `id` varchar(32) NOT NULL,
  `card_number` varchar(16) NOT NULL COMMENT '卡号',
  `type_id` varchar(32) NOT NULL COMMENT '卡类别ID',
  `status` varchar(1) NOT NULL default '1' COMMENT '状态 0 无效 1 有效',
  `active` varchar(1) default '0' COMMENT '激活状态 0 未激活 1激活',
  `purchase_status` varchar(1) default '0' COMMENT '购买状态 0 未购买 1已购买',
  `sale_type` varchar(1) default NULL COMMENT '销售途径 1线上，2线下',
  `expire_time` varchar(50) default NULL COMMENT '过期时间',
  `create_time` varchar(50) default NULL COMMENT '制卡时间',
  `active_time` varchar(50) default NULL COMMENT '激活时间',
  `rmoney` decimal(12,2) default NULL COMMENT '会员卡余额',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_card
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_card_expense`
-- ----------------------------
DROP TABLE IF EXISTS `ab_card_expense`;
CREATE TABLE `ab_card_expense` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `order_id` varchar(32) NOT NULL COMMENT '订单ID',
  `card_id` varchar(32) default NULL COMMENT '会员卡ID',
  `money` decimal(12,2) default NULL COMMENT '抵扣费用',
  `status` varchar(1) NOT NULL COMMENT '状态 1抵扣成功 0抵扣失败',
  `remark` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_card_expense
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_card_type`
-- ----------------------------
DROP TABLE IF EXISTS `ab_card_type`;
CREATE TABLE `ab_card_type` (
  `id` varchar(32) NOT NULL,
  `name` varchar(50) default NULL COMMENT '会员卡名称',
  `title` varchar(255) default NULL COMMENT '卡说明',
  `type` varchar(1) default NULL COMMENT '卡类型 1 送餐卡 2 消费卡',
  `status` varchar(1) default NULL COMMENT '卡类型状态 0. 有效 1无效',
  `money` decimal(12,2) default NULL COMMENT '卡面值',
  `price` decimal(12,2) default NULL COMMENT '购买金额',
  `valid_days` int(11) default NULL COMMENT '有效天数',
  `image` longblob COMMENT '会员卡图片',
  `img_url` varchar(255) default NULL COMMENT '会员卡图片路径',
  `create_time` varchar(50) default NULL COMMENT '创建时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_card_type
-- ----------------------------
INSERT INTO `ab_card_type` VALUES ('4630723c5c3840248072cef65eff3aff', '6000元老板vip卡', '充6000元老板vip卡赠10%购物礼金，实际金额6600元。', '2', '1', '6600.00', '6000.00', '100', null, 'res/css/ab/images/04.gif', '2015-03-06 16:47:37');
INSERT INTO `ab_card_type` VALUES ('4e78e89a7e36439dab6b5f69e755cd8b', 'VIP半年卡', '仅<b>66</b>元，畅享<b>185</b>天无限次免送餐费特权。<br> 送<b>30元</b>红包！', '1', '1', null, '66.00', '100', null, 'res/css/ab/images/card2.jpg', '2015-03-06 16:38:43');
INSERT INTO `ab_card_type` VALUES ('6dbe6a6e233f41dca24557fec1161354', 'VIP年卡', '仅<b>99</b>元，畅享<b>365</b>天无限次免送餐费特权。<br> 送<b>50元</b>红包！', '1', '1', null, '99.00', '365', null, 'res/css/ab/images/card1.jpg', '2015-03-04 21:43:18');
INSERT INTO `ab_card_type` VALUES ('75b930eb5bb447159af499fd32a49c87', '200元VIP老板卡', '充200元老板vip卡赠6%购物礼金', '2', '1', '212.00', '200.00', '100', null, 'res/css/ab/images/01.gif', '2015-03-06 16:43:36');
INSERT INTO `ab_card_type` VALUES ('b4a61e4f905f482da6318c088bde67f8', '500元老板vip卡', '充500元老板卡赠8%购物礼金，实际金额为540元。', '2', '1', '540.00', '500.00', '100', null, 'res/css/ab/images/02.gif', '2015-03-06 16:45:23');
INSERT INTO `ab_card_type` VALUES ('c0aa7b63f8164529a548823d0372b1f0', '1000元老板卡', '充1000元老板vip卡赠10%购物礼金，实际金额为1100元。', '2', '1', '1100.00', '1000.00', '100', null, 'res/css/ab/images/03.gif', '2015-03-06 16:46:24');
INSERT INTO `ab_card_type` VALUES ('eab0a3c9318049599b0454d17adb8cfd', 'VIP季卡', '仅<b>29</b>元，畅享<b>90</b>天无限次免送餐费特权。<br> 送<b>10元</b>红包！', '1', '1', null, '29.00', '100', null, 'res/css/ab/images/card3.jpg', '2015-03-06 16:39:28');

-- ----------------------------
-- Table structure for `ab_card_user`
-- ----------------------------
DROP TABLE IF EXISTS `ab_card_user`;
CREATE TABLE `ab_card_user` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `card_id` varchar(32) NOT NULL COMMENT '会员卡ID',
  `p_datetime` varchar(50) default NULL COMMENT '购买时间',
  `p_way` char(1) default NULL COMMENT '支付方式[0-充值直扣、1-支付宝、2-线下购买]',
  `remark` varchar(500) default NULL COMMENT '备注',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_card_user
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_cityarea`
-- ----------------------------
DROP TABLE IF EXISTS `ab_cityarea`;
CREATE TABLE `ab_cityarea` (
  `id` varchar(32) NOT NULL COMMENT '编码',
  `mc` varchar(50) default NULL COMMENT '名称',
  `type_id` char(1) default NULL COMMENT '类型[0-城市、1-商业圈]',
  `p_id` varchar(32) default NULL COMMENT '上级ID',
  `p_name` varchar(50) default NULL COMMENT '上级名称',
  `ccm` int(11) default NULL COMMENT '层次码',
  `seq_num` int(11) default NULL COMMENT '排列顺序',
  `is_display` char(1) default NULL COMMENT '是否显示',
  `remark` varchar(200) default NULL COMMENT '备注',
  `user_id` varchar(32) default NULL COMMENT '管理员ID',
  `user_mc` varchar(50) default NULL COMMENT '管理员姓名',
  `tc_shenhe` char(1) default NULL COMMENT '物流订单是否需要审核（1-需要、0或null不需要）',
  PRIMARY KEY  (`id`),
  KEY `FK_sys_city_01` (`p_id`),
  CONSTRAINT `FK_sys_city_01` FOREIGN KEY (`p_id`) REFERENCES `ab_cityarea` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_cityarea[城市区域信息表]';

-- ----------------------------
-- Records of ab_cityarea
-- ----------------------------
INSERT INTO `ab_cityarea` VALUES ('100', '北京', '0', 'ROOT', null, '1', '1', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('101', '上海', '0', 'ROOT', null, '0', '2', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('102', '天津', '0', 'ROOT', null, '0', '3', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('103', '重庆', '0', 'ROOT', null, '0', '4', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105', '黑龙江', '0', 'ROOT', null, '0', '5', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105100', '哈尔滨', '0', '105', null, '1', '1', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105101', '齐齐哈尔', '0', '105', null, '1', '2', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105102', '牡丹江', '0', '105', null, '1', '3', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105103', '佳木斯', '0', '105', null, '1', '4', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105104', '大庆', '0', '105', null, '1', '5', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105105', '伊春', '0', '105', null, '1', '6', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105106', '鸡西', '0', '105', null, '1', '7', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105107', '鹤岗', '0', '105', null, '1', '8', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105108', '双鸭山', '0', '105', null, '1', '9', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105109', '七台河', '0', '105', null, '1', '10', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105110', '绥化', '0', '105', null, '1', '11', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105111', '黑河', '0', '105', null, '1', '12', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('105112', '大兴安岭地区', '0', '105', null, '1', '13', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('106', '辽宁', '0', 'ROOT', null, '0', '6', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('107', '吉林', '0', 'ROOT', null, '0', '7', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('108', '河北', '0', 'ROOT', null, '0', '8', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('109', '河南', '0', 'ROOT', null, '0', '9', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('109100', '郑州', '0', '109', null, '1', '1', '1', null, '15eb5bd8ec544237a1dd63ae567332b8', '张明', null);
INSERT INTO `ab_cityarea` VALUES ('109100100', '二七万达商圈', '1', '109100', null, '2', '1', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('109100101', '紫荆山商圈', '1', '109100', null, '2', '2', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('109100102', '国贸360商圈', '1', '109100', null, '2', '3', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('109100103', '二七商圈', '1', '109100', null, '2', '4', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('109100104', '火车站商圈', '1', '109100', null, '2', '5', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('109100105', '碧沙岗商圈', '1', '109100', null, '2', '6', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('109101', '开封', '0', '109', null, '1', '2', '1', null, '9ffd1868f8f04151b547e5b10592b298', '王亮', null);
INSERT INTO `ab_cityarea` VALUES ('109102', '信阳', '0', '109', null, '1', '3', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('110', '湖北', '0', 'ROOT', null, '0', '10', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('111', '湖南', '0', 'ROOT', null, '0', '11', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('111100', '长沙', '0', '111', null, '1', '1', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('112', '山东', '0', 'ROOT', null, '0', '12', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('113', '山西', '0', 'ROOT', null, '0', '13', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('114', '陕西', '0', 'ROOT', null, '0', '14', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('115', '安徽', '0', 'ROOT', null, '0', '15', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('116', '浙江', '0', 'ROOT', null, '0', '16', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('117', '江苏', '0', 'ROOT', null, '0', '17', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('118', '福建', '0', 'ROOT', null, '0', '18', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('119', '广东', '0', 'ROOT', null, '0', '19', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('120', '海南', '0', 'ROOT', null, '0', '20', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('121', '四川', '0', 'ROOT', null, '0', '21', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('122', '云南', '0', 'ROOT', null, '0', '22', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('123', '贵州', '0', 'ROOT', null, '0', '23', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('124', '青海', '0', 'ROOT', null, '0', '24', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('125', '甘肃', '0', 'ROOT', null, '0', '25', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('126', '江西', '0', 'ROOT', null, '0', '26', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('127', '台湾', '0', 'ROOT', null, '0', '27', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('128', '内蒙古', '0', 'ROOT', null, '0', '28', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('129', '宁夏', '0', 'ROOT', null, '0', '29', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('130', '新疆', '0', 'ROOT', null, '0', '30', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('131', '西藏', '0', 'ROOT', null, '0', '31', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('132', '广西', '0', 'ROOT', null, '0', '32', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('133', '香港', '0', 'ROOT', null, '0', '33', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('134', '澳门', '0', 'ROOT', null, '0', '34', '1', null, null, null, null);
INSERT INTO `ab_cityarea` VALUES ('ROOT', '区域分类', null, null, null, '0', '0', '1', null, null, null, null);

-- ----------------------------
-- Table structure for `ab_cityarea_subject`
-- ----------------------------
DROP TABLE IF EXISTS `ab_cityarea_subject`;
CREATE TABLE `ab_cityarea_subject` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `city_id` varchar(32) default NULL COMMENT '城市ID',
  `subject_id` varchar(32) default NULL COMMENT '分类ID',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_cityarea_subject_02` (`city_id`),
  KEY `FK_ab_cityarea_subject_01` (`subject_id`),
  CONSTRAINT `FK_ab_cityarea_subject_01` FOREIGN KEY (`subject_id`) REFERENCES `ab_subject` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_ab_cityarea_subject_02` FOREIGN KEY (`city_id`) REFERENCES `ab_cityarea` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_cityarea_subject[城市区域分类]';

-- ----------------------------
-- Records of ab_cityarea_subject
-- ----------------------------
INSERT INTO `ab_cityarea_subject` VALUES ('001a7667b5d0469aaf2b476fa824496c', '109', '10061006');
INSERT INTO `ab_cityarea_subject` VALUES ('013a97c096924814a3592acd456a8a08', '105100', '10031002');
INSERT INTO `ab_cityarea_subject` VALUES ('01f3cf83f5354d76ac633643acb4415d', '109100', '10091003');
INSERT INTO `ab_cityarea_subject` VALUES ('03294060c542492cad6bde7c2d6003db', '105100', '10111005');
INSERT INTO `ab_cityarea_subject` VALUES ('039daaf4b737455b80d7a02272dd3a7a', '109100', '10131002');
INSERT INTO `ab_cityarea_subject` VALUES ('03d20b6a5f064d3ba2360c4ab99d66b1', '105', '10011009');
INSERT INTO `ab_cityarea_subject` VALUES ('042dfbbbd47d4e88be5b745755db2298', '105100', '10061008');
INSERT INTO `ab_cityarea_subject` VALUES ('05a2e2d54b99426b9dac7b8c97b35ee4', '102', '10011009');
INSERT INTO `ab_cityarea_subject` VALUES ('05e75b570b4e40c79767cbf98f49513f', '109', '10161005');
INSERT INTO `ab_cityarea_subject` VALUES ('0712ddbc082f4ea6aa00b3cc6b919e07', '109100', '10111002');
INSERT INTO `ab_cityarea_subject` VALUES ('07c76cb11f7a4bc5acd3638772028df6', '109', '1018');
INSERT INTO `ab_cityarea_subject` VALUES ('095dc93f45f049bb9e347661919abaab', '101', '10011004');
INSERT INTO `ab_cityarea_subject` VALUES ('0ae5acb0268b475987c8b9f3297bf06c', '109', '10161002');
INSERT INTO `ab_cityarea_subject` VALUES ('0b5b11a1053949e78143a261ac85c7c9', '109', '10021001');
INSERT INTO `ab_cityarea_subject` VALUES ('0be5ff2a74b34a4293cfc96bdfddc421', '109', '10201001');
INSERT INTO `ab_cityarea_subject` VALUES ('0d39c99dad1144a6b1ddc75ad20ccda7', '109100', '100110011001');
INSERT INTO `ab_cityarea_subject` VALUES ('0fc0f9feb77149fa83ace58bac8e34f9', '105100', '10201002');
INSERT INTO `ab_cityarea_subject` VALUES ('1119633c765f4b27a15a5b51da4472a2', '109100', '10071002');
INSERT INTO `ab_cityarea_subject` VALUES ('130beb69d3ed4587bc43ca00f73d7a8a', '109100', '1007');
INSERT INTO `ab_cityarea_subject` VALUES ('14b33f3ef3bd4867beac756b3fea8406', '102', '10011002');
INSERT INTO `ab_cityarea_subject` VALUES ('14f9bdb913574cf993a6a64831ce9f88', '109100', '100110011000');
INSERT INTO `ab_cityarea_subject` VALUES ('1545fc40dec14d57a41e22cfc64a8e87', '105100', '10071002');
INSERT INTO `ab_cityarea_subject` VALUES ('154b2331629642d3a4dbbeb57783875a', '109', '10151004');
INSERT INTO `ab_cityarea_subject` VALUES ('16289f6367284d91852963f6936f0132', '105100', '10151001');
INSERT INTO `ab_cityarea_subject` VALUES ('1920c6944feb445fb5c490ba9cf8f86a', '109', '1015');
INSERT INTO `ab_cityarea_subject` VALUES ('1c37a5d2d5eb4f38b72bbf1088b079e5', '102', '10011004');
INSERT INTO `ab_cityarea_subject` VALUES ('1c868f843dfb4e06b1735e3f47003030', '109', '1008');
INSERT INTO `ab_cityarea_subject` VALUES ('1ce21bbd7aed4114ac02ce0f5cb85aea', '109100', '1003');
INSERT INTO `ab_cityarea_subject` VALUES ('1d70366303f846128dfc81621e05c5fb', '105100', '10141003');
INSERT INTO `ab_cityarea_subject` VALUES ('1d84afb233ea4ddf90f8d9dda178787b', '101', '10011005');
INSERT INTO `ab_cityarea_subject` VALUES ('1d850159cf14418b80d6e200fa09cf6e', '109100', '10021003');
INSERT INTO `ab_cityarea_subject` VALUES ('1fef743350674746b1639013ce52ae23', '109', '10191003');
INSERT INTO `ab_cityarea_subject` VALUES ('218377c54b464b12b3d7a6bd63f02eb8', '105100', '10031006');
INSERT INTO `ab_cityarea_subject` VALUES ('21ebdd655420415896105ee672e033f8', '105100', '10061007');
INSERT INTO `ab_cityarea_subject` VALUES ('2253935747634992b479a6be8cf71c64', '109100', '10061005');
INSERT INTO `ab_cityarea_subject` VALUES ('23a99e49b9c04c8f8545978b3deee49f', '109100', '1005');
INSERT INTO `ab_cityarea_subject` VALUES ('23d325a52253465ba2cc77d3a75ff358', '109100', '10031002');
INSERT INTO `ab_cityarea_subject` VALUES ('242dc6744df248059460523199bb3002', '105', '10011005');
INSERT INTO `ab_cityarea_subject` VALUES ('24b39e98ebeb4b2597b969c575b82e1b', '105100', '10021003');
INSERT INTO `ab_cityarea_subject` VALUES ('24bc5050d1494562973a1344d2406ba2', '105100', '10061002');
INSERT INTO `ab_cityarea_subject` VALUES ('26151fbf5dec490aa09ac23340907256', '109100', '1011');
INSERT INTO `ab_cityarea_subject` VALUES ('266e0420bd0b4cc5a9c0f936448bb6b9', '109', '10201003');
INSERT INTO `ab_cityarea_subject` VALUES ('26a91cccf9ed4b19963fbf249757f584', '105100', '10151004');
INSERT INTO `ab_cityarea_subject` VALUES ('26d92d92a8e94322b3e8dbf23b4275ec', '109100', '10021002');
INSERT INTO `ab_cityarea_subject` VALUES ('270d8ec382414c979b3e70f7de561c99', '105100', '10011009');
INSERT INTO `ab_cityarea_subject` VALUES ('272d00db31354c70a832bf0fae85c0e4', '105100', '10111003');
INSERT INTO `ab_cityarea_subject` VALUES ('274fcac2971749cfa71ed32d1092ed4d', '109', '10171002');
INSERT INTO `ab_cityarea_subject` VALUES ('2aa80d34942441949ffd3eff9d2da80e', '102', '10011003');
INSERT INTO `ab_cityarea_subject` VALUES ('2c2ad5bcc8464b5084a40a1d37e86a5c', '105', '10011006');
INSERT INTO `ab_cityarea_subject` VALUES ('2c9c2825aa4044c59a4563c8d0f17991', '109100', '10011002');
INSERT INTO `ab_cityarea_subject` VALUES ('2d4d98927d234741b6391b0d1700372c', '109100', '10051001');
INSERT INTO `ab_cityarea_subject` VALUES ('31a6640426f8474ebf41d606eb84fd62', '109100', '10191003');
INSERT INTO `ab_cityarea_subject` VALUES ('33695f7d04db4d4cb563d25dadab4f25', '109', '10161004');
INSERT INTO `ab_cityarea_subject` VALUES ('34586801db5f43f89282e811b14931ee', '109', '1017');
INSERT INTO `ab_cityarea_subject` VALUES ('34ee5e09187347398164239ab5db33bd', '109100', '10201001');
INSERT INTO `ab_cityarea_subject` VALUES ('36a8d72b076f4172a30e5b85b1f15940', '109100', '10081002');
INSERT INTO `ab_cityarea_subject` VALUES ('37d8c472f27c440bbe080d8e2fd43cc0', '109100', '1016');
INSERT INTO `ab_cityarea_subject` VALUES ('39abf179464f4713aff8ca011e4d11f5', '105100', '10171002');
INSERT INTO `ab_cityarea_subject` VALUES ('3a58d2070bca4127b55464111185004e', '109', '10071002');
INSERT INTO `ab_cityarea_subject` VALUES ('3e074967369042b1b75ebd42866f983b', '105', '10011007');
INSERT INTO `ab_cityarea_subject` VALUES ('3ea3e2bacf1749ad9d8a7f9da17ec5c6', '105100', '10161002');
INSERT INTO `ab_cityarea_subject` VALUES ('42dceac93a9843afb12edd0389beb20c', '105100', '10161004');
INSERT INTO `ab_cityarea_subject` VALUES ('4459c333960d426db249ef0ba6aa6334', '109100', '10151002');
INSERT INTO `ab_cityarea_subject` VALUES ('455a360957444148a8c78de28acf9646', '105100', '10091002');
INSERT INTO `ab_cityarea_subject` VALUES ('467b941670c744cf812da6f624e3aef5', '105100', '10011005');
INSERT INTO `ab_cityarea_subject` VALUES ('46ce9748bfd9422aa2f3929dbe49a6c4', '109', '1007');
INSERT INTO `ab_cityarea_subject` VALUES ('4737a218d2df409d9e59e472f885af70', '109100', '10151001');
INSERT INTO `ab_cityarea_subject` VALUES ('47982acf35f64975875e4ff142c3a8fe', '102', '10011007');
INSERT INTO `ab_cityarea_subject` VALUES ('498ab62f42904622b68d297b458fb149', '105100', '10051001');
INSERT INTO `ab_cityarea_subject` VALUES ('4b1d500d8a3b4ffeba521ef0a01586b4', '109', '1003');
INSERT INTO `ab_cityarea_subject` VALUES ('4b1d86ccf3c44c30b5255db755549b4e', '101', '10011006');
INSERT INTO `ab_cityarea_subject` VALUES ('4b421edc7f8646469de4cd6565247398', '105100', '10061001');
INSERT INTO `ab_cityarea_subject` VALUES ('4b6cf0567af04bcf95a7d37a257d2f56', '105100', '1018');
INSERT INTO `ab_cityarea_subject` VALUES ('4c7d7fe134564508b7f306ae6cfef6c7', '109', '10031006');
INSERT INTO `ab_cityarea_subject` VALUES ('4c964dc348094c329132618186140f44', '105100', '10161003');
INSERT INTO `ab_cityarea_subject` VALUES ('4e7181ece3974f8dabfdca3361352a6c', '105100', '10071003');
INSERT INTO `ab_cityarea_subject` VALUES ('5001279ff94c4cfe9ad108c4f19ce116', '105100', '10151003');
INSERT INTO `ab_cityarea_subject` VALUES ('5125d432f7674aa5804f951e80d615b5', '109', '1001');
INSERT INTO `ab_cityarea_subject` VALUES ('516e2bb3c5494d6f983cf47d245bf5a9', '105100', '1015');
INSERT INTO `ab_cityarea_subject` VALUES ('522250a9f95842a59046b586e8b6638f', '109100', '1015');
INSERT INTO `ab_cityarea_subject` VALUES ('53257f3bedde4182a9329f13dba8da01', '105100', '10121002');
INSERT INTO `ab_cityarea_subject` VALUES ('53287cfb45184311ae197915bc8c5118', '109', '10061001');
INSERT INTO `ab_cityarea_subject` VALUES ('551d5c1317674c99b21ca060c6defc65', '105100', '10011003');
INSERT INTO `ab_cityarea_subject` VALUES ('559193abdc46403db868546cb1612dd5', '109100', '1006');
INSERT INTO `ab_cityarea_subject` VALUES ('56f5722846ac4be19b6d8b4fedd27d2e', '109100', '1002');
INSERT INTO `ab_cityarea_subject` VALUES ('5714b45b62714b6aa44e1ec34245d811', '105100', '10071001');
INSERT INTO `ab_cityarea_subject` VALUES ('5820077ca8544d3fa8015a039a9f9d97', '105100', '10011004');
INSERT INTO `ab_cityarea_subject` VALUES ('593d7815473f4d7c85e0c18f1f6b2da5', '105', '10011003');
INSERT INTO `ab_cityarea_subject` VALUES ('596fdd44b5c34065a9eec10cfd930d6c', '109100', '10031001');
INSERT INTO `ab_cityarea_subject` VALUES ('5a7f8fb55e2d4495963ed772fdf53532', '109100', '10141003');
INSERT INTO `ab_cityarea_subject` VALUES ('5ac0b8132b2b40dcbb8c23c3b1df6eaa', '102', '10011005');
INSERT INTO `ab_cityarea_subject` VALUES ('5b576e1421fa41feaf9ad93aacb71df3', '105100', '10111002');
INSERT INTO `ab_cityarea_subject` VALUES ('5c055a9fa76c46f9aa5710ece79f970d', '109100', '10061001');
INSERT INTO `ab_cityarea_subject` VALUES ('5c9ae36d254c405ca2bfed4627fc677a', '105100', '10011008');
INSERT INTO `ab_cityarea_subject` VALUES ('5dc6a67963c548bc92e90197e2c707c6', '109100', '10121003');
INSERT INTO `ab_cityarea_subject` VALUES ('5dd37435bafa46528443d7c97d167b77', '109100', '1019');
INSERT INTO `ab_cityarea_subject` VALUES ('5e23057fa95a4674b044aedde6073560', '109100', '10031007');
INSERT INTO `ab_cityarea_subject` VALUES ('5e5a30239ec24f57b494c290802446c4', '105100', '1004');
INSERT INTO `ab_cityarea_subject` VALUES ('5f2624a9a8884a24854d88ced5da1042', '105100', '1002');
INSERT INTO `ab_cityarea_subject` VALUES ('5f9cd63c2be84312b972403b9a04949c', '109', '10171003');
INSERT INTO `ab_cityarea_subject` VALUES ('5fd194659f9844e1ad3b825520236aab', '101', '10011007');
INSERT INTO `ab_cityarea_subject` VALUES ('61ed29ee3f4740709a5a3b5ba544743e', '109', '10021002');
INSERT INTO `ab_cityarea_subject` VALUES ('6349b2b723bb467393d85a006886f1b1', '109100', '10131001');
INSERT INTO `ab_cityarea_subject` VALUES ('640d7131f1c1450789ef3e97d2697d5a', '105100', '10061003');
INSERT INTO `ab_cityarea_subject` VALUES ('644a88e7a5884f4095cd199942de97c0', '101', '10011008');
INSERT INTO `ab_cityarea_subject` VALUES ('66c2b22ac8b64c2681501d8b9b145b5b', '109', '10031004');
INSERT INTO `ab_cityarea_subject` VALUES ('6879996db4a941b191f590f751bc9469', '105100', '1012');
INSERT INTO `ab_cityarea_subject` VALUES ('69eb0e40c69a465192d36d1ba8aebf12', '102', '10011006');
INSERT INTO `ab_cityarea_subject` VALUES ('69f3873f515845d3b041744eae43bb5c', '109100', '10061003');
INSERT INTO `ab_cityarea_subject` VALUES ('6a1d684e5d5d48dcae1037247cd35bc8', '105100', '10091001');
INSERT INTO `ab_cityarea_subject` VALUES ('6aa92f15a2eb4b87b98111b6de96338d', '109100', '10121002');
INSERT INTO `ab_cityarea_subject` VALUES ('6afeec4e14ad44f189ac84603b0c158a', '109100', '10061002');
INSERT INTO `ab_cityarea_subject` VALUES ('6b458a4bae7a4893b5dc44f2b4b18218', '101', '1001');
INSERT INTO `ab_cityarea_subject` VALUES ('6bde70edb6234538a01ee5788542363d', '109', '10141002');
INSERT INTO `ab_cityarea_subject` VALUES ('6be1db00c1c6461c9454a97b14d7b12f', '105100', '10171001');
INSERT INTO `ab_cityarea_subject` VALUES ('6cae4f23dbdf46fa984e0f28a234cbad', '105100', '10191003');
INSERT INTO `ab_cityarea_subject` VALUES ('6d2e68420b374c62be9d1312e1a819b7', '105100', '1006');
INSERT INTO `ab_cityarea_subject` VALUES ('6e21719333d2484b83ed8fd5b3134865', '109100', '10151004');
INSERT INTO `ab_cityarea_subject` VALUES ('6f2e57793cc74c40922b678b751b4b1a', '105100', '10171003');
INSERT INTO `ab_cityarea_subject` VALUES ('6fa6158f9fbe4e2bb25b06b944ca28ce', '105100', '1017');
INSERT INTO `ab_cityarea_subject` VALUES ('6fc6d6c88c084accbae02f6c2caf23a7', '109100', '10201003');
INSERT INTO `ab_cityarea_subject` VALUES ('6ffbc6b0ca3f4a5d8da253da97fe2257', '109100', '1020');
INSERT INTO `ab_cityarea_subject` VALUES ('702c5e164e554e9e8f16ad8fe57e56de', '109', '10021004');
INSERT INTO `ab_cityarea_subject` VALUES ('706a77a87d954168b075b4d6007e9974', '102', '10011001');
INSERT INTO `ab_cityarea_subject` VALUES ('71ba0d4ffd0d4886812ffcaa8b664782', '109100', '10161003');
INSERT INTO `ab_cityarea_subject` VALUES ('7257ce1c5b4f449c8735e8ed15295451', '109', '10061005');
INSERT INTO `ab_cityarea_subject` VALUES ('7385aae6574d415599c99aceb6cfbd22', '105100', '10181001');
INSERT INTO `ab_cityarea_subject` VALUES ('7524976421e54120b6173dd600320c2e', '105100', '1014');
INSERT INTO `ab_cityarea_subject` VALUES ('75766bd5352849a0a4ee365ab1e83d82', '105100', '10061004');
INSERT INTO `ab_cityarea_subject` VALUES ('7597d5b798a143feab5b9a56363509c9', '109', '10031003');
INSERT INTO `ab_cityarea_subject` VALUES ('761e1134513e4940968867e895e737e7', '105', '10011004');
INSERT INTO `ab_cityarea_subject` VALUES ('7651fb4b6240444bb7f9e47446b8c194', '109', '1016');
INSERT INTO `ab_cityarea_subject` VALUES ('792677e34236443e889d10f9ab2364ef', '105', '10011002');
INSERT INTO `ab_cityarea_subject` VALUES ('795b7dd281f348e3a0e8a8efcfef2e76', '109', '10191001');
INSERT INTO `ab_cityarea_subject` VALUES ('7a8f9079f3eb4cbda68322b2598f7b54', '109100', '10071001');
INSERT INTO `ab_cityarea_subject` VALUES ('7b40e0f85e064b81ada2194e0fc151ae', '109100', '1008');
INSERT INTO `ab_cityarea_subject` VALUES ('7cc6975e9b3845b597700761e36655c2', '109', '10161001');
INSERT INTO `ab_cityarea_subject` VALUES ('7ce7da4a4dae4f529e450cccc0665f2d', '105100', '10121004');
INSERT INTO `ab_cityarea_subject` VALUES ('7e292d8beef346849d1452921bca674d', '105100', '10201001');
INSERT INTO `ab_cityarea_subject` VALUES ('7eb3fa227f7c4b1fad1ce3b6aec43f21', '105100', '10021004');
INSERT INTO `ab_cityarea_subject` VALUES ('829bb71af7894e9c8fef6a091f6a3f55', '109100', '1009');
INSERT INTO `ab_cityarea_subject` VALUES ('83d275af12514ec98c3efb85f56a49b0', '109', '10191002');
INSERT INTO `ab_cityarea_subject` VALUES ('83e977b5b518422ea0909f72107a223d', '105100', '1007');
INSERT INTO `ab_cityarea_subject` VALUES ('83f44f1bcd0943d392ea1fb6a650d8dc', '109100', '10141001');
INSERT INTO `ab_cityarea_subject` VALUES ('854fd1b2ab6d4575ad338392ef151379', '109', '1004');
INSERT INTO `ab_cityarea_subject` VALUES ('87a7acf8e0c2437aa37655c23d7bd1c0', '109', '10061004');
INSERT INTO `ab_cityarea_subject` VALUES ('8ac060c48dc64e15be89532f0a5ecc9d', '109', '1006');
INSERT INTO `ab_cityarea_subject` VALUES ('8d506d9288404cebaa56f4ce2371c444', '109100', '10061007');
INSERT INTO `ab_cityarea_subject` VALUES ('8d8d29be74fc4ecf85821898617fe4ee', '109100', '10111003');
INSERT INTO `ab_cityarea_subject` VALUES ('8e5977b5d4164dc3b69df64840ef1513', '109', '10141003');
INSERT INTO `ab_cityarea_subject` VALUES ('8e818986c7194831ab30be8f7fc5d63c', '109100', '10161002');
INSERT INTO `ab_cityarea_subject` VALUES ('8f20500b18334814be94d120ade348d1', '105100', '10021001');
INSERT INTO `ab_cityarea_subject` VALUES ('8f453da5f86245d7b0ae8b6fa208e7e3', '109100', '100110011002');
INSERT INTO `ab_cityarea_subject` VALUES ('917aae8cc2ca471f8ecacc40be64c7d0', '109100', '10181001');
INSERT INTO `ab_cityarea_subject` VALUES ('930a8704f8884367bf942e64e691c673', '109100', '10161004');
INSERT INTO `ab_cityarea_subject` VALUES ('9329a1e72c4440dd90f6776628b4877d', '109100', '1018');
INSERT INTO `ab_cityarea_subject` VALUES ('9340a25258e14b29be008ab993642b50', '105', '10011001');
INSERT INTO `ab_cityarea_subject` VALUES ('937a4b9fbeb84104afb4bd5b44a684ef', '109', '10151002');
INSERT INTO `ab_cityarea_subject` VALUES ('93ea9bf695a04cb6825d4856b582938b', '105100', '10091003');
INSERT INTO `ab_cityarea_subject` VALUES ('9560b1e456b145018382e6ccfbb675c6', '109100', '10121004');
INSERT INTO `ab_cityarea_subject` VALUES ('973be0a740f842a18e8ae7e5cf2b9711', '109', '10011001');
INSERT INTO `ab_cityarea_subject` VALUES ('986ce53ddb92423e9cae015aa8916bbd', '109100', '1014');
INSERT INTO `ab_cityarea_subject` VALUES ('99c4ac2bd6d54a9f9f77199012e29a60', '105100', '1001');
INSERT INTO `ab_cityarea_subject` VALUES ('9c4407ceb8654d8ba86e818977af6c2c', '105100', '10131001');
INSERT INTO `ab_cityarea_subject` VALUES ('9c553b5585714bef83e9e1b6cd50eaf7', '102', '10011008');
INSERT INTO `ab_cityarea_subject` VALUES ('a1d2f86272c84771a3e58badbfbcec49', '105100', '10121001');
INSERT INTO `ab_cityarea_subject` VALUES ('a2564706fe12436daba2ad7ab8ab008a', '109100', '10061008');
INSERT INTO `ab_cityarea_subject` VALUES ('a39a3470cc78479f8d4d507d3c54d33f', '105100', '10201003');
INSERT INTO `ab_cityarea_subject` VALUES ('a493f9c0520f4d3a9209c0e3c80ddb60', '109100', '1004');
INSERT INTO `ab_cityarea_subject` VALUES ('a4b1927b22a64aaabfee5bb15d51ce0a', '105100', '10141001');
INSERT INTO `ab_cityarea_subject` VALUES ('a53e3e75fa1b49078180b1cd968a047d', '101', '10011009');
INSERT INTO `ab_cityarea_subject` VALUES ('a611b69586e94ee9b9718ca02bc34c59', '105100', '1008');
INSERT INTO `ab_cityarea_subject` VALUES ('a6bb270283a446d5afc10e3f213c2363', '109', '10141001');
INSERT INTO `ab_cityarea_subject` VALUES ('a781923d8a354f45a2a7a8f1c4d427af', '109100', '10191001');
INSERT INTO `ab_cityarea_subject` VALUES ('aa858b27247b4e11aceb3429cb9fb697', '105100', '10011001');
INSERT INTO `ab_cityarea_subject` VALUES ('ad91d77f362d4ed7ab38b7a17c15774c', '109', '10031005');
INSERT INTO `ab_cityarea_subject` VALUES ('ae75a33b5b3a4a0ca4d047e23bf963f5', '109100', '10091002');
INSERT INTO `ab_cityarea_subject` VALUES ('af1820855bcc40fbaa61269425b5def5', '109100', '10091001');
INSERT INTO `ab_cityarea_subject` VALUES ('afacd004a5014f4a94294b16e7c8c95a', '109100', '10081001');
INSERT INTO `ab_cityarea_subject` VALUES ('b1c2357b5e66451291f389297dc74d4b', '105100', '10031007');
INSERT INTO `ab_cityarea_subject` VALUES ('b3ddf5435287452b93aa836811adbafb', '109', '10061007');
INSERT INTO `ab_cityarea_subject` VALUES ('b59154d2936a4bc78380f7f219f9fd18', '109', '10081002');
INSERT INTO `ab_cityarea_subject` VALUES ('b8647024d65c4e32aa74cb9e9f01242c', '109', '10071001');
INSERT INTO `ab_cityarea_subject` VALUES ('bad28f39172542ed83b8cdb6a406c2e6', '105100', '10011002');
INSERT INTO `ab_cityarea_subject` VALUES ('bba21b1c7f9249caba74a96e1ad42b50', '109', '10161003');
INSERT INTO `ab_cityarea_subject` VALUES ('bc598828b0cc440ea6740151107fbe68', '105100', '10061006');
INSERT INTO `ab_cityarea_subject` VALUES ('bd11857ecea7464086fbc15251fb3c86', '109100', '1013');
INSERT INTO `ab_cityarea_subject` VALUES ('be828963309040c09370b02c09a59cd7', '105', '10011008');
INSERT INTO `ab_cityarea_subject` VALUES ('bfc3a586e39d478694983eb7ee63d53c', '109', '1019');
INSERT INTO `ab_cityarea_subject` VALUES ('bfe7fbc7293f4430979b31e367e6763b', '109', '10171001');
INSERT INTO `ab_cityarea_subject` VALUES ('c02a4105009041a4a25c031fa3442e64', '109100', '10031004');
INSERT INTO `ab_cityarea_subject` VALUES ('c0bd6d6dac2e428f9dd8c0b8f3a85acf', '109100', '10011003');
INSERT INTO `ab_cityarea_subject` VALUES ('c1a38743540c407ba5e12e3f43ba935b', '109100', '10151003');
INSERT INTO `ab_cityarea_subject` VALUES ('c279b7e52dd841ecbee02e7c77e8d885', '105100', '10031001');
INSERT INTO `ab_cityarea_subject` VALUES ('c2a1e1cfb3d74c179a3ee06d4eec59a8', '105100', '10151002');
INSERT INTO `ab_cityarea_subject` VALUES ('c40abfe534c74f51b693c1f6f31bdec6', '105100', '10031003');
INSERT INTO `ab_cityarea_subject` VALUES ('c5f6ad4121c7424fa74e6f48d69300e1', '105100', '10031004');
INSERT INTO `ab_cityarea_subject` VALUES ('c67926efb10c45be84fec000c8938c8b', '105100', '10011006');
INSERT INTO `ab_cityarea_subject` VALUES ('c6d4abd581f1400e9487d890acb20ccd', '109100', '10111004');
INSERT INTO `ab_cityarea_subject` VALUES ('c74262eff58e4f31beea5d9d0f9c3e9f', '109', '1014');
INSERT INTO `ab_cityarea_subject` VALUES ('c775956b8d4144fa81014babba913938', '109100', '10141002');
INSERT INTO `ab_cityarea_subject` VALUES ('c7e093b1e3ec4a55b450bc115756033e', '109', '10061003');
INSERT INTO `ab_cityarea_subject` VALUES ('c7e473fc2f4e4f87859cbfbc36cd02cc', '109', '10031002');
INSERT INTO `ab_cityarea_subject` VALUES ('c9fdffbf3f414108882a458eb441aa1c', '109100', '10201002');
INSERT INTO `ab_cityarea_subject` VALUES ('cacc8a21678441c193d3fd51f643c373', '105100', '10081002');
INSERT INTO `ab_cityarea_subject` VALUES ('cb1aaa5009f54bbb88af6b08d7b398f8', '109100', '1012');
INSERT INTO `ab_cityarea_subject` VALUES ('cb4c62265115422bb9d5767c05b54328', '109100', '10111005');
INSERT INTO `ab_cityarea_subject` VALUES ('cb754b6228fc407db743489549a4b731', '105100', '1011');
INSERT INTO `ab_cityarea_subject` VALUES ('cd9b144a4eb2460e8738730de4aadcae', '105100', '1003');
INSERT INTO `ab_cityarea_subject` VALUES ('d0c4a5ce7eca4b6abd10ddda05c90215', '109', '10081001');
INSERT INTO `ab_cityarea_subject` VALUES ('d0f6b2cb90784e5ba931746dd585298b', '109100', '10161001');
INSERT INTO `ab_cityarea_subject` VALUES ('d12bf881ae6c4388be86ed12cb5457a4', '105100', '10081003');
INSERT INTO `ab_cityarea_subject` VALUES ('d1e5d4810152472d9357c15c792531e3', '105100', '1013');
INSERT INTO `ab_cityarea_subject` VALUES ('d33f47f3286741c9b36f79273439e037', '105100', '10141002');
INSERT INTO `ab_cityarea_subject` VALUES ('d36bf63ee8a540839d01864716848855', '109', '10011002');
INSERT INTO `ab_cityarea_subject` VALUES ('d4f8c0f808664a16a045a8a65f7c83d8', '105100', '10161001');
INSERT INTO `ab_cityarea_subject` VALUES ('d5e92b6318d54796a3a4b31cb779deed', '101', '10011001');
INSERT INTO `ab_cityarea_subject` VALUES ('d5f27ea8bb2d40c18a07fba1c8b0a4e3', '109', '10201002');
INSERT INTO `ab_cityarea_subject` VALUES ('d7bf6efc012145d3a685b225e46608ae', '105100', '10131002');
INSERT INTO `ab_cityarea_subject` VALUES ('d7ddce76e03d457888e34770277bd99a', '109', '10161006');
INSERT INTO `ab_cityarea_subject` VALUES ('d879911c536349338fe1c245209fbc7a', '105100', '10011007');
INSERT INTO `ab_cityarea_subject` VALUES ('d8dbeb09a3ac4b8c944e7821b14981d6', '101', '10011003');
INSERT INTO `ab_cityarea_subject` VALUES ('d94c378bc8904c788e1c6358357264f1', '109100', '10021001');
INSERT INTO `ab_cityarea_subject` VALUES ('da6258547ca64a2d92891f018db05ddf', '109100', '10011001');
INSERT INTO `ab_cityarea_subject` VALUES ('daafe7a16dab4a7a899fcd2a3cabb2ac', '109', '1005');
INSERT INTO `ab_cityarea_subject` VALUES ('dc981f5306ae4c378cf1b10b393eae23', '105100', '10061005');
INSERT INTO `ab_cityarea_subject` VALUES ('dd5411ef583f4372948f285b0316a0ab', '105100', '10161006');
INSERT INTO `ab_cityarea_subject` VALUES ('ddc3c1dd703148f3aa4f0f5a510b977b', '105100', '10031005');
INSERT INTO `ab_cityarea_subject` VALUES ('de1bf928121041a4be25823e305d98b3', '109100', '10111001');
INSERT INTO `ab_cityarea_subject` VALUES ('de3c991a87ae419cb2cedf85d8c6de75', '109100', '10081003');
INSERT INTO `ab_cityarea_subject` VALUES ('de828d17e4c044c8a30166802aa377ea', '109100', '10071003');
INSERT INTO `ab_cityarea_subject` VALUES ('deb9ae7ee6f34e7a9ccc837d24a929e1', '105100', '10121003');
INSERT INTO `ab_cityarea_subject` VALUES ('def0bf0280f542bb8fa3a1991df7cc05', '109100', '10161006');
INSERT INTO `ab_cityarea_subject` VALUES ('df563d5eca3e45729c6396768e9a9dd5', '105100', '10191001');
INSERT INTO `ab_cityarea_subject` VALUES ('dfef8426f00a428cb70fde0a39e10a65', '109100', '10191002');
INSERT INTO `ab_cityarea_subject` VALUES ('e0d4a65c4b9340089f857a5d56c5da32', '109100', '1001');
INSERT INTO `ab_cityarea_subject` VALUES ('e2bb7f1dd33240d18edcca0d39e6d79a', '109', '1020');
INSERT INTO `ab_cityarea_subject` VALUES ('e2f3d5b9a7ca4c51ab35dd266944f7c5', '105100', '10161005');
INSERT INTO `ab_cityarea_subject` VALUES ('e4015e9953bd4171bd8d6308b4a2f943', '109', '1002');
INSERT INTO `ab_cityarea_subject` VALUES ('e407a581988d4149adce1fb28c5c69af', '105100', '10191002');
INSERT INTO `ab_cityarea_subject` VALUES ('e5423c34dcaf4b6399e230411e028bd9', '109', '10071003');
INSERT INTO `ab_cityarea_subject` VALUES ('e57237103b2f4465b43dfd28e80b0c63', '109', '10021003');
INSERT INTO `ab_cityarea_subject` VALUES ('e5a25244248944a68131636f7340dd5e', '105100', '1019');
INSERT INTO `ab_cityarea_subject` VALUES ('e6b0dfef895e421f9df7621e52e52e6d', '109100', '10061004');
INSERT INTO `ab_cityarea_subject` VALUES ('e8ad91df284745118e87eabf47c347af', '105100', '1020');
INSERT INTO `ab_cityarea_subject` VALUES ('e8bd7fcaa5f042da80493017b974b82d', '101', '10011002');
INSERT INTO `ab_cityarea_subject` VALUES ('e9679391535e4e29b608cc0ac9e37fca', '105100', '10021002');
INSERT INTO `ab_cityarea_subject` VALUES ('ea543fbfe7894c25b9d957f200a13760', '109', '10061008');
INSERT INTO `ab_cityarea_subject` VALUES ('ea89fadbf9274c7bb05c2c6d1ff55d44', '109', '10151003');
INSERT INTO `ab_cityarea_subject` VALUES ('ebdd92c82d134fe4bf4032118eedfe51', '105100', '1016');
INSERT INTO `ab_cityarea_subject` VALUES ('eca9bb1259104797aca71c38397ff518', '109', '10151001');
INSERT INTO `ab_cityarea_subject` VALUES ('ecd26814f16147e8aaf6f6d6ba8fc63e', '105100', '10081001');
INSERT INTO `ab_cityarea_subject` VALUES ('ed1d93532018461a961d651015fb0f2b', '105100', '10111004');
INSERT INTO `ab_cityarea_subject` VALUES ('ed5489b36b2543dab130f38546bc6882', '109', '10081003');
INSERT INTO `ab_cityarea_subject` VALUES ('eeb910c4d84541e9a89ba2f7c2b49b6f', '109', '10051001');
INSERT INTO `ab_cityarea_subject` VALUES ('efc12647554c413394886a6b215924f4', '109100', '10061006');
INSERT INTO `ab_cityarea_subject` VALUES ('f0934498074a4da7b0b2ec4538b88f8c', '109', '10181001');
INSERT INTO `ab_cityarea_subject` VALUES ('f452a73981a04e6faaa3c4bc69537b99', '102', '1001');
INSERT INTO `ab_cityarea_subject` VALUES ('f6949add94af45619f6210d740975adb', '105100', '10111001');
INSERT INTO `ab_cityarea_subject` VALUES ('f6ef77412bfa435ba7b04bc6bc57d6ef', '109100', '10021004');
INSERT INTO `ab_cityarea_subject` VALUES ('f88068986e7b4816815f29552d45d2a3', '109', '10061002');
INSERT INTO `ab_cityarea_subject` VALUES ('f8dc432e58b148d989ff4803b806472e', '109100', '10121001');
INSERT INTO `ab_cityarea_subject` VALUES ('fa9cc30d046f42cbaf152cc01e65419e', '109', '10031001');
INSERT INTO `ab_cityarea_subject` VALUES ('fc1ef50970df41feb2dcd7363b1f52dc', '109100', '10161005');
INSERT INTO `ab_cityarea_subject` VALUES ('fd5a608bcd534133899067413bbbde77', '105', '1001');
INSERT INTO `ab_cityarea_subject` VALUES ('fde57e1fe5594fe9909e8d5c79cf2625', '109', '10031007');
INSERT INTO `ab_cityarea_subject` VALUES ('fe6dcbb718cd486c82a152d541e9a5b8', '105100', '1009');
INSERT INTO `ab_cityarea_subject` VALUES ('ff2a56844ccd4bcb8a5596615adc3c43', '105100', '1005');

-- ----------------------------
-- Table structure for `ab_fmcar`
-- ----------------------------
DROP TABLE IF EXISTS `ab_fmcar`;
CREATE TABLE `ab_fmcar` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `car_no` varchar(16) default NULL,
  `driver` varchar(32) default NULL,
  `mobile` varchar(32) default NULL,
  `length` varchar(32) default NULL,
  `type` varchar(32) default NULL,
  `is_locate` tinyint(4) default NULL,
  `location` varchar(32) default NULL,
  `location_time` datetime default NULL,
  `state` varchar(16) default NULL,
  `is_net` tinyint(4) default NULL,
  `is_protect` tinyint(4) default NULL,
  `recommend_no` varchar(32) default NULL,
  `remark` varchar(500) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_fmcar[熟车信息表]';

-- ----------------------------
-- Records of ab_fmcar
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_fmcar_city`
-- ----------------------------
DROP TABLE IF EXISTS `ab_fmcar_city`;
CREATE TABLE `ab_fmcar_city` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `car_id` varchar(32) default NULL,
  `city_name` varchar(32) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_fmcar_city[熟车常跑城市对应表]';

-- ----------------------------
-- Records of ab_fmcar_city
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_fmcar_order`
-- ----------------------------
DROP TABLE IF EXISTS `ab_fmcar_order`;
CREATE TABLE `ab_fmcar_order` (
  `id` varchar(32) NOT NULL,
  `start_province_code` varchar(100) default NULL,
  `start_province_name` varchar(100) default NULL,
  `start_city_code` varchar(100) default NULL,
  `start_city_name` varchar(100) default NULL,
  `start_county_code` varchar(100) default NULL,
  `start_county_name` varchar(100) default NULL,
  `start_addr` varchar(100) default NULL,
  `arr_province_code` varchar(100) default NULL,
  `arr_province_name` varchar(100) default NULL,
  `arr_city_code` varchar(100) default NULL,
  `arr_city_name` varchar(100) default NULL,
  `arr_county_code` varchar(100) default NULL,
  `arr_county_name` varchar(100) default NULL,
  `arr_addr` varchar(100) default NULL,
  `car_length` varchar(100) default NULL,
  `car_type` varchar(100) default NULL,
  `load_time` datetime default NULL,
  `mobile` varchar(100) default NULL,
  `remark` varchar(500) default NULL,
  `status` varchar(100) default NULL,
  `create_id` varchar(32) default NULL,
  `create_time` datetime default NULL,
  `car_id` varchar(32) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_fmcar_order[熟车订单信息表]';

-- ----------------------------
-- Records of ab_fmcar_order
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_fmcar_order_car`
-- ----------------------------
DROP TABLE IF EXISTS `ab_fmcar_order_car`;
CREATE TABLE `ab_fmcar_order_car` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `car_id` varchar(32) default NULL,
  `order_id` varchar(32) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_fmcar_order_car[熟车订单对应表]';

-- ----------------------------
-- Records of ab_fmcar_order_car
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_fmcar_user`
-- ----------------------------
DROP TABLE IF EXISTS `ab_fmcar_user`;
CREATE TABLE `ab_fmcar_user` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `car_id` varchar(32) default NULL,
  `user_id` varchar(32) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_fmcar_user[熟车-用户对应表]';

-- ----------------------------
-- Records of ab_fmcar_user
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_integral_exchange`
-- ----------------------------
DROP TABLE IF EXISTS `ab_integral_exchange`;
CREATE TABLE `ab_integral_exchange` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `goods_id` varchar(32) NOT NULL COMMENT '积分商品ID',
  `user_id` varchar(32) NOT NULL COMMENT '兑换用户ID',
  `count` int(11) default NULL COMMENT '兑换数量',
  `rect` varchar(50) default NULL COMMENT '收件人',
  `rect_adrs` varchar(255) default NULL COMMENT '收件人地址',
  `rect_mobile` varchar(50) default NULL COMMENT '收件人电话',
  `datetime` varchar(50) default NULL COMMENT '兑换时间',
  `send_status` varchar(1) default '0' COMMENT '发货状态 0 未发货 1发货',
  `send_time` varchar(50) default NULL COMMENT '发货时间',
  `send_type` varchar(50) default NULL COMMENT '快递名称',
  `send_number` varchar(50) default NULL COMMENT '快递单号',
  `remark` varchar(255) default NULL COMMENT '备注',
  `pay_type` varchar(1) default NULL COMMENT '支付方式 1 充值直扣，2 支付宝，3 餐到付款, 4 无支付',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_integral_exchange
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_integral_goods`
-- ----------------------------
DROP TABLE IF EXISTS `ab_integral_goods`;
CREATE TABLE `ab_integral_goods` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `title` varchar(50) default NULL COMMENT '商品标题信息',
  `image` longblob COMMENT '商户图片',
  `img_url` varchar(100) default NULL,
  `score` int(11) default NULL COMMENT '所需积分',
  `money` float(18,2) default '0.00' COMMENT '商户金额',
  `limit` int(11) default NULL COMMENT '每人限制兑换数量',
  `count` int(11) default NULL COMMENT '商品数量',
  `status` varchar(1) default NULL COMMENT '状态 0 无效 1 有效',
  `starttime` varchar(50) default NULL COMMENT '兑换开始日期',
  `endtime` varchar(50) default NULL COMMENT '兑换结束日期',
  `description` varchar(255) default NULL COMMENT '商品详情',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_integral_goods
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_integral_score`
-- ----------------------------
DROP TABLE IF EXISTS `ab_integral_score`;
CREATE TABLE `ab_integral_score` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `type` varchar(2) NOT NULL COMMENT '积分变化类型  ',
  `value` int(11) default NULL COMMENT '分值',
  `datetime` varchar(50) default NULL COMMENT '积分变动时间',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `fid` varchar(32) default NULL COMMENT '外联ID',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_integral_score
-- ----------------------------
INSERT INTO `ab_integral_score` VALUES ('05181874be074cc7b021a3a1775df05f', '02', '2', '2015-02-11 10:41:16', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('06dbc71756ac41fcb0adbd21ed7f648b', '02', '2', '2015-04-02 00:04:00', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('13f491df8b7c4718b36ddad523735c90', '01', '30', '2015-04-16 14:44:11', 'bb9e145d6d32452b9e1ceeea92abe023', '');
INSERT INTO `ab_integral_score` VALUES ('16033f008f7c4dc79b203954dc9e4f97', '02', '2', '2015-03-25 15:50:06', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('1af590e55b3e4eee918907c699b3baf5', '02', '2', '2015-02-10 11:34:44', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('1ec83c4de55f43bd9cfa8ae05f141664', '02', '2', '2015-04-18 21:57:14', '981f1d141c5a4f2f93ab3dd58e8b7874', '');
INSERT INTO `ab_integral_score` VALUES ('2153a2a416dd45cc8786c3845c38af3d', '02', '2', '2015-03-29 00:30:45', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('24ae02f5b391424dafaf8a05d6b2291c', '01', '30', '2015-03-18 22:07:50', 'bc3bba3a61424f8a846dc1e589852a6a', '');
INSERT INTO `ab_integral_score` VALUES ('26656e32e84f434a9863a744a5966103', '02', '2', '2015-04-08 09:34:30', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('2d112a5c43e6444b8b98f5e9f9bba230', '06', '70', '2015-04-02 00:20:01', '3587e3ef3971402db97584b6ade9d999', '7b16fb494791470d96cefd0cc800b0a5');
INSERT INTO `ab_integral_score` VALUES ('2d91c09803fa4657850e46e3322b9d5a', '02', '2', '2015-04-19 23:07:41', '981f1d141c5a4f2f93ab3dd58e8b7874', '');
INSERT INTO `ab_integral_score` VALUES ('2f0eef6ab3834d0abedfdd5542420a6a', '01', '30', '2015-04-16 14:48:39', '981f1d141c5a4f2f93ab3dd58e8b7874', '');
INSERT INTO `ab_integral_score` VALUES ('33535edc02e64553b5039d0ab51790c5', '02', '2', '2015-03-19 09:56:08', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('38d24fd17dbe4fa68aa6354ceba655e0', '02', '2', '2015-03-26 23:27:30', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('39cfe14933694e7eaadb7c91533490f5', '06', '70', '2015-04-02 00:19:29', '3587e3ef3971402db97584b6ade9d999', '7b16fb494791470d96cefd0cc800b0a5');
INSERT INTO `ab_integral_score` VALUES ('3fcb6e21503a4a6aa73536fddee5f9b0', '02', '2', '2015-04-20 00:28:03', '981f1d141c5a4f2f93ab3dd58e8b7874', '');
INSERT INTO `ab_integral_score` VALUES ('4885bfbe85774e9b96dedd829447f45a', '06', '70', '2015-04-02 00:21:23', '3587e3ef3971402db97584b6ade9d999', '7b16fb494791470d96cefd0cc800b0a5');
INSERT INTO `ab_integral_score` VALUES ('4bc1b7826b8e47c6968c9ffea0498b8f', '02', '2', '2015-04-12 18:17:06', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('53e15c9273ea41fdb788fd76647643aa', '02', '2', '2015-02-15 00:04:08', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('58063851958d44188f746a39897738bc', '02', '2', '2015-03-23 00:17:06', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('639389badcc2485aa42c8badb1eb4f66', '06', '70', '2015-04-02 01:02:32', '3587e3ef3971402db97584b6ade9d999', '7b16fb494791470d96cefd0cc800b0a5');
INSERT INTO `ab_integral_score` VALUES ('6e700cbfda7f426da5e15877c91cf55d', '01', '30', '2015-02-12 11:56:02', '284d2c1d0de345f09ed7063a38e8f3d4', '');
INSERT INTO `ab_integral_score` VALUES ('74b0c714ec324c0690eddd3d1cb77215', '06', '80', '2015-04-16 01:34:19', '3587e3ef3971402db97584b6ade9d999', 'ba5a654a356e450ebc02aed641675be8');
INSERT INTO `ab_integral_score` VALUES ('76a489cde58c4de1bb2a602143cced58', '06', '95', '2015-04-16 01:34:19', '3587e3ef3971402db97584b6ade9d999', 'a0498b74f7a547699d26ac4206307b31');
INSERT INTO `ab_integral_score` VALUES ('7c48df1e11e94d408731154f9802d7ec', '06', '117', '2015-04-17 23:49:36', '981f1d141c5a4f2f93ab3dd58e8b7874', 'ca40ed1733754ff38e7524bcbf05fbe0');
INSERT INTO `ab_integral_score` VALUES ('7fc603731500452c864ea5640dae6cdd', '06', '70', '2015-04-02 00:59:14', '3587e3ef3971402db97584b6ade9d999', 'a229d66a8b194213ae224941c98e07e2');
INSERT INTO `ab_integral_score` VALUES ('87c4fd6f276e4f54ad23460a2ac5c0d7', '02', '2', '2015-04-16 01:21:44', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('8b96f63ff2274d16b217810a0616c565', '02', '2', '2015-04-05 00:28:07', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('8c003b2fde464571a5cec8c99277cdfe', '02', '2', '2015-03-22 00:48:08', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('902919d696014a1ab41131ce856ded7b', '01', '30', '2015-02-12 14:51:04', 'c00b8293bfb3477d8984ec29ef3c75eb', '');
INSERT INTO `ab_integral_score` VALUES ('9ca3bdd48d714ada8c4024d2acd1a21e', '01', '30', '2015-04-08 11:08:22', '744a3a58182e4599ae37bdf1518813cb', '');
INSERT INTO `ab_integral_score` VALUES ('9d69cc9312aa47cbb7a2af32ee660b8b', '06', '122', '2015-04-17 23:49:36', '981f1d141c5a4f2f93ab3dd58e8b7874', '49c6fd05fbca45e0955b4e8c9cd13231');
INSERT INTO `ab_integral_score` VALUES ('9feec1e46d9647bf9fdd76b769226eab', '06', '70', '2015-04-02 00:18:49', '3587e3ef3971402db97584b6ade9d999', '7b16fb494791470d96cefd0cc800b0a5');
INSERT INTO `ab_integral_score` VALUES ('a5ee5ed3a01343d2b8e93f4073e23a45', '02', '2', '2015-04-15 21:55:35', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('a6549d2b3388453f8d07fcf18de67e6e', '02', '2', '2015-04-19 23:13:18', 'bb9e145d6d32452b9e1ceeea92abe023', '');
INSERT INTO `ab_integral_score` VALUES ('abd13d71fdda4a568cb8b580bcb86442', '02', '2', '2015-04-04 22:49:54', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('acd0025a14474546acf0bda72afff2a3', '06', '60', '2015-04-18 22:39:29', '981f1d141c5a4f2f93ab3dd58e8b7874', 'ad7d29af656a4b07823695ff6d70b2ea');
INSERT INTO `ab_integral_score` VALUES ('b12372cdb6cf4688a52bdadea06cf5c0', '06', '70', '2015-04-02 00:25:16', '3587e3ef3971402db97584b6ade9d999', '7b16fb494791470d96cefd0cc800b0a5');
INSERT INTO `ab_integral_score` VALUES ('b786f605d4ff4a1a93b3b2f3c637ba6b', '06', '90', '2015-04-18 23:16:29', '981f1d141c5a4f2f93ab3dd58e8b7874', '3b081fb9a34c4637a480247b1c0d0e94');
INSERT INTO `ab_integral_score` VALUES ('cc4f43fb7f2f4322b13f803e71f30827', '06', '202', '2015-04-20 02:16:19', '981f1d141c5a4f2f93ab3dd58e8b7874', '6b77a5bf8d074d118b29c6124bc91053');
INSERT INTO `ab_integral_score` VALUES ('d70eea812c0f491d94b3f6016a2da2f1', '06', '70', '2015-04-02 00:23:51', '3587e3ef3971402db97584b6ade9d999', '7b16fb494791470d96cefd0cc800b0a5');
INSERT INTO `ab_integral_score` VALUES ('d7227b19f2104cab931496c2aca1cbf4', '01', '30', '2015-02-12 13:41:46', 'a015b1deaadf4ec483c6a14b792f451b', '');
INSERT INTO `ab_integral_score` VALUES ('ded3fa1daf1441ce8c4113b9ac50f7eb', '06', '70', '2015-04-16 01:34:19', '3587e3ef3971402db97584b6ade9d999', 'a229d66a8b194213ae224941c98e07e2');
INSERT INTO `ab_integral_score` VALUES ('e341a1a05bdf4a0b851a6ec4ca4c1d94', '02', '2', '2015-02-14 00:06:50', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('e561eb66aef54ca48cc4f1c43944d3b1', '02', '2', '2015-03-20 23:15:08', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('e61d4c136fd141d0a4d43a3e748a04ef', '06', '70', '2015-04-16 01:34:19', '3587e3ef3971402db97584b6ade9d999', '7b16fb494791470d96cefd0cc800b0a5');
INSERT INTO `ab_integral_score` VALUES ('e9e04fd74c734aaeb5cb46e5fc0a9c38', '06', '60', '2015-04-16 01:34:19', '3587e3ef3971402db97584b6ade9d999', 'f8847ba9d5ff45809e74a6c2e0a49293');
INSERT INTO `ab_integral_score` VALUES ('f42205ec58c54307870f2b7e367ebe93', '02', '2', '2015-03-06 22:56:23', '3587e3ef3971402db97584b6ade9d999', '');
INSERT INTO `ab_integral_score` VALUES ('f9cea2ac637245be992623eed4ea52f1', '06', '70', '2015-04-02 00:21:21', '3587e3ef3971402db97584b6ade9d999', '7b16fb494791470d96cefd0cc800b0a5');
INSERT INTO `ab_integral_score` VALUES ('feef0a595e374f07af6ed531d2d076c1', '06', '70', '2015-04-02 00:17:30', '3587e3ef3971402db97584b6ade9d999', '7b16fb494791470d96cefd0cc800b0a5');
INSERT INTO `ab_integral_score` VALUES ('ff06bcf0ca014e788412e383f54b57d5', '06', '70', '2015-04-02 00:12:53', '3587e3ef3971402db97584b6ade9d999', '7b16fb494791470d96cefd0cc800b0a5');

-- ----------------------------
-- Table structure for `ab_merchant`
-- ----------------------------
DROP TABLE IF EXISTS `ab_merchant`;
CREATE TABLE `ab_merchant` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `mc` varchar(50) default NULL COMMENT '商家名称',
  `subject_id` varchar(32) default NULL COMMENT '一级分类ID',
  `subject_name` varchar(50) default NULL COMMENT '一级分类名称',
  `is_type` char(1) default NULL COMMENT '类型[0-货物类、1-服务类]',
  `city_id` varchar(32) default NULL COMMENT '所属城市',
  `city_name` varchar(50) default NULL COMMENT '所属城市名称',
  `area_id` varchar(32) default NULL COMMENT '所属商业区',
  `area_name` varchar(50) default NULL COMMENT '所属商业区名称',
  `yzbm` varchar(6) default NULL COMMENT '邮政编码',
  `xxdz` varchar(100) default NULL COMMENT '详细地址',
  `mobile` varchar(50) default NULL COMMENT '手机号',
  `qq` varchar(50) default NULL COMMENT 'QQ号码',
  `weibo` varchar(50) default NULL COMMENT '微博',
  `weixin` varchar(50) default NULL COMMENT '微信',
  `rate` varchar(20) default '0' COMMENT '客户评级',
  `grade` varchar(20) default '0' COMMENT '服务评级',
  `credit` int(11) default NULL COMMENT '商户信用等级',
  `brief` text COMMENT '商家简介',
  `lng` varchar(20) default NULL COMMENT '经度',
  `lat` varchar(20) default NULL COMMENT '维度',
  `mapbusiness` varchar(100) default NULL COMMENT '地图的地点信息',
  `worktime` varchar(50) default NULL COMMENT '工作时间',
  `is_recommend` char(1) default '0' COMMENT '是否推荐[0-不推荐、1-推荐]',
  `tjkssj` varchar(20) default NULL COMMENT '推荐开始时间',
  `tjjzsj` varchar(20) default NULL COMMENT '推荐截止时间',
  `img_url` varchar(100) default NULL COMMENT '效果图',
  `thumbnail_url` varchar(100) default NULL COMMENT '缩略图',
  `logo` varchar(100) default NULL COMMENT '企业LOGO',
  `sfzs` char(1) default '0' COMMENT '是否自送[0-否、1-是]',
  `zdpsje` decimal(12,2) default NULL COMMENT '最低配送金额',
  `chk_fgzrxd` char(1) default '0' COMMENT '非工作日是否允许下单',
  `chk_tgfp` char(1) default '0' COMMENT '是否选择提供发票',
  `tgfp` char(1) default NULL COMMENT '提供发票[0-否、1-是]',
  `chk_yhhd` char(1) default '0' COMMENT '是否选择优惠活动',
  `mds` decimal(12,2) default NULL COMMENT '买多少',
  `jds` decimal(12,2) default NULL COMMENT '减多少',
  `chk_sdsj` char(1) default '0' COMMENT '是否选择送达时间',
  `sdsj` varchar(20) default NULL COMMENT '送达时间（分钟）',
  `cspf` varchar(20) default NULL COMMENT '超时赔付',
  `seq_num` int(11) default '0' COMMENT '推荐排序',
  `business_status` char(1) default '1' COMMENT '营业状态[0-已打烊、1-营业中、2-休息中]',
  `check_status` char(1) default '0' COMMENT '审核状态[0-未审核、1-审核通过、2-审核不通过]',
  `is_display` char(1) default '1' COMMENT '是否显示[0-不显示、1-显示]',
  `create_time` varchar(20) default NULL COMMENT '注册时间',
  `user_id` varchar(32) default NULL COMMENT '用户ID',
  `avgprice` decimal(12,2) default '0.00' COMMENT '平均消费费用[订单完成时计算]',
  `avgptf` decimal(12,2) default '0.00' COMMENT '平均跑腿费[订单完成时计算]',
  `avghour` int(11) default '0' COMMENT '用时时长[订单完成时计算]',
  `zxl` int(11) default '0' COMMENT '总销售量[订单完成时计算]',
  `hps` int(11) default '0' COMMENT '好评数[订单完成时计算]',
  `views_day` int(11) default NULL COMMENT '日访问数',
  `comments_day` int(11) default NULL COMMENT '日评论数',
  `views_month` int(11) default NULL COMMENT '日下载数',
  `comments_month` int(11) default NULL COMMENT '日顶数',
  `comments_total` int(11) default NULL COMMENT '总评论数',
  `view_total` int(11) default '0' COMMENT '点击量',
  `remark` varchar(500) default NULL COMMENT '备注',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_merchant[商家信息表]';

-- ----------------------------
-- Records of ab_merchant
-- ----------------------------
INSERT INTO `ab_merchant` VALUES ('25c7cb3315d449bfba173df2a70f972a', '望江南', '1001', '我要点餐', '0', '109100', '郑州', '109100100', '二七万达商圈', null, '郑州市二七区郑州汽车客运总站', '13673386255', null, null, null, '0', '0', null, null, '113.661272', '34.726396', '郑州汽车客运总站', null, '0', null, null, '2b7bb090bb4347e098567908c3fd803a.jpg', null, null, '0', null, '0', '0', null, '0', null, null, '0', null, null, '0', '1', '1', '1', '2015-04-16 14:53:04', '25c7cb3315d449bfba173df2a70f972a', '0.00', '0.00', '0', '0', '0', null, null, null, null, null, '0', null);
INSERT INTO `ab_merchant` VALUES ('63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '1001', '我要点餐', '0', '109100', '郑州', '109100101', '紫荆山商圈', '123444', '郑州市管城回族区紫荆山路/东大街(路口)', '13673386255', '1231341234', '12313241234', '12341234', '0', '0', null, '秦淮人家秦淮人家秦淮人家秦淮人家', '113.688532', '34.754895', '紫荆山路/东大街(路口)', null, '1', '2015-04-10 21:36:18', '2015-07-24 21:36:21', '2b7bb090bb4347e098567908c3fd803a.jpg', null, null, '0', '0.00', '1', '1', '1', '1', '100.00', '10.00', '1', '50', '2', '0', '1', '1', '1', '2015-04-16 18:26:54', '63bd3c719f1d4be98a8a396855408c73', '0.00', '0.00', '0', '0', '0', null, null, null, null, null, '0', null);
INSERT INTO `ab_merchant` VALUES ('9526be72c83b41c9895b1fd6fd7cf562', '九九鸭脖', '1001', '我要点餐', '0', '109100', '郑州', '109100100', '二七万达商圈', '236426', '郑州市金水区金水路/未来路(路口)', '13673386255', '423426346', 'weibqe8234', 'ywer273', '0', '0', null, '九九鸭脖九九鸭脖', '113.711341', '34.769248', '金水路/未来路(路口)', null, '1', '2015-04-17 22:43:14', '2015-05-29 22:43:16', '2b7bb090bb4347e098567908c3fd803a.jpg', null, null, '1', '2.00', '0', '1', '0', '1', '100.00', '8.00', '0', null, null, '0', '1', '1', '1', '2015-04-17 22:14:26', '9526be72c83b41c9895b1fd6fd7cf562', '0.00', '0.00', '0', '0', '0', null, null, null, null, null, '0', null);
INSERT INTO `ab_merchant` VALUES ('a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府', '1001', '我要点餐', '0', '109100', '郑州', '109100100', '二七万达商圈', null, '郑州市二七区长江路/碧云路(路口)', '13673386255', '23', null, null, '0', '0', null, null, '113.671612', '34.71616', '长江路/碧云路(路口)', null, '1', '2015-04-20 00:27:04', '2015-04-30 00:27:05', '2b7bb090bb4347e098567908c3fd803a.jpg', null, null, '0', '100.00', '1', '1', '0', '0', null, null, '1', null, null, '0', '1', '1', '1', '2015-04-17 22:06:22', 'a7b25e9592514cc5bd78511af3dfdbe1', '0.00', '0.00', '0', '0', '0', null, null, null, null, null, '0', null);

-- ----------------------------
-- Table structure for `ab_merchant_comment`
-- ----------------------------
DROP TABLE IF EXISTS `ab_merchant_comment`;
CREATE TABLE `ab_merchant_comment` (
  `id` varchar(32) NOT NULL COMMENT '评论ID',
  `mid` varchar(32) NOT NULL COMMENT '商家ID',
  `rate` int(11) default NULL COMMENT '信用评级',
  `grade` int(11) default NULL COMMENT '服务评级',
  `title` varchar(255) default NULL COMMENT '标题',
  `content` longtext COMMENT '评论内容',
  `createtime` varchar(20) default NULL COMMENT '评论时间',
  `ip` varchar(20) default NULL COMMENT '评论IP',
  `userid` varchar(32) default NULL COMMENT '评论人ID',
  `username` varchar(50) default NULL COMMENT '评论人名称',
  `remark` varchar(500) default NULL COMMENT '备注',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_merchant_comment_01` (`mid`),
  CONSTRAINT `FK_ab_merchant_comment_01` FOREIGN KEY (`mid`) REFERENCES `ab_merchant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_merchant_comment[商家内容评论]';

-- ----------------------------
-- Records of ab_merchant_comment
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_merchant_img`
-- ----------------------------
DROP TABLE IF EXISTS `ab_merchant_img`;
CREATE TABLE `ab_merchant_img` (
  `id` varchar(32) NOT NULL COMMENT '图片ID',
  `mid` varchar(32) default NULL COMMENT '商家ID',
  `title` varchar(255) default NULL COMMENT '标题',
  `type_name` varchar(255) default NULL COMMENT '类型名称',
  `seq_num` int(11) default NULL COMMENT '顺序号',
  `lager` varchar(255) default NULL COMMENT '大图',
  `thumbnail` varchar(255) default NULL COMMENT '缩略图',
  `scrid` varchar(32) default NULL COMMENT '上传人ID',
  `scrmc` varchar(50) default NULL COMMENT '上传人名称',
  `scsj` varchar(20) default NULL COMMENT '上传时间',
  `description` varchar(200) default NULL COMMENT '描述',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_merchant_img_01` (`mid`),
  CONSTRAINT `FK_ab_merchant_img_01` FOREIGN KEY (`mid`) REFERENCES `ab_merchant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_merchant_img[图片信息表]';

-- ----------------------------
-- Records of ab_merchant_img
-- ----------------------------
INSERT INTO `ab_merchant_img` VALUES ('07bcbd7f4cc94f72a76cc657e6de9d72', '63bd3c719f1d4be98a8a396855408c73', null, '.jpg', null, 'dd2bf8014689430492639be5b6c22c8e.jpg', null, '63bd3c719f1d4be98a8a396855408c73', null, '2015-04-17 21:28:50', null);
INSERT INTO `ab_merchant_img` VALUES ('5121692a29e243dca287e4c11a038035', '63bd3c719f1d4be98a8a396855408c73', null, '.jpg', null, 'f209e530431144a9aa44d9d5f8d8c4dd.jpg', null, '63bd3c719f1d4be98a8a396855408c73', null, '2015-04-17 21:28:50', null);
INSERT INTO `ab_merchant_img` VALUES ('8a20ed92b4354bd1a37155676a0fe3ad', '9526be72c83b41c9895b1fd6fd7cf562', null, '.jpg', null, '32a91d12797747b0a6a0055f92c0515e.jpg', null, '9526be72c83b41c9895b1fd6fd7cf562', null, '2015-04-17 22:39:51', null);
INSERT INTO `ab_merchant_img` VALUES ('8fab95bc4cc2446e9e8f39678aeb3edc', '63bd3c719f1d4be98a8a396855408c73', null, '.jpg', null, '15eea455ddd846a79795f755ba3a6c3d.jpg', null, '63bd3c719f1d4be98a8a396855408c73', null, '2015-04-17 21:28:50', null);

-- ----------------------------
-- Table structure for `ab_merchant_leave`
-- ----------------------------
DROP TABLE IF EXISTS `ab_merchant_leave`;
CREATE TABLE `ab_merchant_leave` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `mer_id` varchar(32) default NULL COMMENT '商家ID',
  `qdr_id` varchar(32) default NULL,
  `order_id` varchar(32) NOT NULL,
  `datetime` varchar(50) NOT NULL COMMENT '留言时间',
  `content` varchar(255) NOT NULL COMMENT '留言内容',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_merchant_leave
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_merchant_product`
-- ----------------------------
DROP TABLE IF EXISTS `ab_merchant_product`;
CREATE TABLE `ab_merchant_product` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `city_id` varchar(32) default NULL COMMENT '城市ID',
  `city_name` varchar(100) default NULL COMMENT '城市名称',
  `area_id` varchar(32) default NULL COMMENT '商圈ID',
  `area_name` varchar(100) default NULL COMMENT '商圈名称',
  `subject_id` varchar(50) default NULL COMMENT '一级分类ID',
  `subject_name` varchar(50) default NULL COMMENT '一级分类名称',
  `sub_id` varchar(32) default NULL COMMENT '二级分类ID',
  `sub_name` varchar(50) default NULL COMMENT '二级分类名称',
  `thr_id` varchar(32) default NULL COMMENT '三级分类ID',
  `thr_name` varchar(50) default NULL COMMENT '三级分类名称',
  `is_type` char(1) default NULL COMMENT '类型[0-货物类、1-服务类]',
  `mc` varchar(50) default NULL COMMENT '商家名称',
  `img_url` varchar(100) default NULL COMMENT '效果图',
  `thumbnail_url` varchar(100) default NULL COMMENT '缩略图',
  `salenum` int(11) default '0' COMMENT '销售总量',
  `productinfo` text COMMENT '商品介绍',
  `price` decimal(10,2) default '0.00' COMMENT '价格',
  `mdprice` decimal(10,2) default '0.00' COMMENT '门店价',
  `ptf` decimal(10,2) default '0.00' COMMENT '跑腿费',
  `yhje` decimal(10,2) default '0.00' COMMENT '优惠金额',
  `yhkssj` varchar(20) default NULL COMMENT '赠送开始时间',
  `yhjzsj` varchar(20) default NULL COMMENT '优惠截止时间',
  `jf` int(11) default '0' COMMENT '赠送积分',
  `zt` char(1) default '0' COMMENT '审核状态[0-未审核、1-已审核]',
  `sfsj` char(1) default '0' COMMENT '是否上架',
  `sysfxs` char(1) default '0' COMMENT '首页是否显示[0-否、1-是]',
  `yj` decimal(10,2) default '0.00' COMMENT '佣金',
  `mid` varchar(32) NOT NULL COMMENT '商家ID',
  `mname` varchar(100) default NULL COMMENT '商家名称',
  `seq_num` int(11) default '0' COMMENT '商品排序',
  `createtime` varchar(20) default NULL COMMENT '创建时间',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_merchant_product_01` (`mid`),
  CONSTRAINT `FK_ab_merchant_product_01` FOREIGN KEY (`mid`) REFERENCES `ab_merchant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_merchant_product[商家商品]';

-- ----------------------------
-- Records of ab_merchant_product
-- ----------------------------
INSERT INTO `ab_merchant_product` VALUES ('00891be559cf4ac0ab629a1a82d6c67f', '109100', '郑州', '109100100', '二七万达商圈', '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', '0', '麻辣豆腐', 'f83b4a4fc16347c79de6985c09ab48ab.jpg', 'f83b4a4fc16347c79de6985c09ab48ab-thumbnail.jpg', '0', '麻辣豆腐麻辣豆腐麻辣豆腐', '22.00', '24.00', '2.00', '0.00', null, null, '0', '1', '1', '1', '2.00', '9526be72c83b41c9895b1fd6fd7cf562', '九九鸭脖', '1', '2015-04-17 22:41:27');
INSERT INTO `ab_merchant_product` VALUES ('46c880b53505436f80f38568b0a9c71f', '109100', '郑州', '109100100', '二七万达商圈', '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', '0', '酱牛肉', 'b26d969d517c4ecbbbe0c31ba27b85d3.jpg', 'b26d969d517c4ecbbbe0c31ba27b85d3-thumbnail.jpg', '0', '3434', '25.00', '28.00', '4.00', '0.00', null, null, '0', '1', '1', '1', '0.00', 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府', '0', '2015-04-20 00:26:05');
INSERT INTO `ab_merchant_product` VALUES ('6c4007d9e81b424894c5c50ca24ee050', '109100', '郑州', '109100100', '二七万达商圈', '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', '0', '黄焖鸡', '2b84a2ff04264c309982dcf4edfd5cc3.jpg', '2b84a2ff04264c309982dcf4edfd5cc3-thumbnail.jpg', '0', null, '15.00', '18.00', '2.00', '0.00', null, null, '0', '1', '1', '1', '0.00', 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府', '0', '2015-04-20 00:23:51');
INSERT INTO `ab_merchant_product` VALUES ('a2e735c9013f46caaa0535683c7d32a9', '109100', '郑州', '109100100', '二七万达商圈', '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', '0', '酱牛肉', 'f08175d1592849b3ae32a7ff0466d47b.jpg', 'f08175d1592849b3ae32a7ff0466d47b-thumbnail.jpg', '0', '3422345', '40.00', '50.00', '5.00', '0.00', null, null, '0', '1', '1', '1', '0.00', 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府', '0', '2015-04-20 00:22:30');
INSERT INTO `ab_merchant_product` VALUES ('ca91afb739b44fcda7b117dd45d40d8f', '109100', '郑州', '109100101', '紫荆山商圈', '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', '0', '剁椒鱼头', 'f16344f1393d48d7b482da5e44b24dc1.jpg', 'f16344f1393d48d7b482da5e44b24dc1-thumbnail.jpg', '0', '剁椒鱼头剁椒鱼头剁椒鱼头', '30.00', '33.00', '2.00', '0.00', null, null, '0', '1', '1', '1', '5.00', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '1', '2015-04-17 21:33:39');
INSERT INTO `ab_merchant_product` VALUES ('ccce1c0292b94cf6a7919145fefeb9dd', '109100', '郑州', '109100101', '紫荆山商圈', '1001', '我要点餐', '10011001', '早餐', '100110011000', '主餐', '0', '地三鲜', 'dfd3ec045fa747dc9dc658cf7f316c78.jpg', 'dfd3ec045fa747dc9dc658cf7f316c78-thumbnail.jpg', '0', '地三鲜', '18.00', '20.00', '2.00', '0.00', null, null, '0', '1', '1', '1', '5.00', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '2', '2015-04-17 21:31:11');

-- ----------------------------
-- Table structure for `ab_merchant_reply`
-- ----------------------------
DROP TABLE IF EXISTS `ab_merchant_reply`;
CREATE TABLE `ab_merchant_reply` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `leave_id` varchar(32) NOT NULL COMMENT '留言ID',
  `content` varchar(255) NOT NULL COMMENT '回复内容',
  `datetime` varchar(50) NOT NULL COMMENT '回复时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_merchant_reply
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_merchant_yysj`
-- ----------------------------
DROP TABLE IF EXISTS `ab_merchant_yysj`;
CREATE TABLE `ab_merchant_yysj` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `mid` varchar(32) NOT NULL COMMENT '商家ID',
  `mname` varchar(100) default NULL COMMENT '商家名称',
  `xq` tinyint(4) default NULL COMMENT '星期',
  `sbsj` varchar(20) default NULL COMMENT '上班时间',
  `xbsj` varchar(20) default NULL COMMENT '下班时间',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_merchant_yysj_01` (`mid`),
  CONSTRAINT `FK_ab_merchant_yysj_01` FOREIGN KEY (`mid`) REFERENCES `ab_merchant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_merchant_yysj[商家-营业时间]';

-- ----------------------------
-- Records of ab_merchant_yysj
-- ----------------------------
INSERT INTO `ab_merchant_yysj` VALUES ('d25c613cc5354b2180939262d7da215c', '9526be72c83b41c9895b1fd6fd7cf562', null, '5', '00:10:02', '23:55:50');
INSERT INTO `ab_merchant_yysj` VALUES ('d36c1eab3cc64e94832b8bdbf8605df7', '9526be72c83b41c9895b1fd6fd7cf562', null, '5', '00:00:00', '23:55:50');

-- ----------------------------
-- Table structure for `ab_merchant_zjfw`
-- ----------------------------
DROP TABLE IF EXISTS `ab_merchant_zjfw`;
CREATE TABLE `ab_merchant_zjfw` (
  `id` varchar(32) NOT NULL,
  `userid` varchar(32) default NULL,
  `mid` varchar(32) default NULL,
  `cjsj` varchar(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_merchant_zjfw
-- ----------------------------
INSERT INTO `ab_merchant_zjfw` VALUES ('0149661b2b6348a2a3fb4bd9748dbed4', 'e444adc7896b41e5939143a26b34e393', 'b18147d5101f435a833f2b2b5240ffb3', '2015-04-08 13:41:31');
INSERT INTO `ab_merchant_zjfw` VALUES ('248b0ac8a27740dcb777966bb8d69074', '63bd3c719f1d4be98a8a396855408c73', '63bd3c719f1d4be98a8a396855408c73', '2015-04-19 22:25:59');
INSERT INTO `ab_merchant_zjfw` VALUES ('4b51c302c5b14b42925fb78bd878e23a', '981f1d141c5a4f2f93ab3dd58e8b7874', '63bd3c719f1d4be98a8a396855408c73', '2015-04-19 23:56:36');
INSERT INTO `ab_merchant_zjfw` VALUES ('820cb01024f14c0dbacc5f6ac3e3770d', '63bd3c719f1d4be98a8a396855408c73', '25c7cb3315d449bfba173df2a70f972a', '2015-04-19 22:26:16');
INSERT INTO `ab_merchant_zjfw` VALUES ('8ff461d900344da981e2b03c5b0fe707', '9526be72c83b41c9895b1fd6fd7cf562', '63bd3c719f1d4be98a8a396855408c73', '2015-04-17 22:49:38');
INSERT INTO `ab_merchant_zjfw` VALUES ('a2f16736ffe94d0086abcb8b383fff5e', '63bd3c719f1d4be98a8a396855408c73', '9526be72c83b41c9895b1fd6fd7cf562', '2015-04-19 22:34:12');
INSERT INTO `ab_merchant_zjfw` VALUES ('b7fe4709b32944de850f813708c16ae9', '3587e3ef3971402db97584b6ade9d999', 'b18147d5101f435a833f2b2b5240ffb3', '2015-03-29 01:24:53');
INSERT INTO `ab_merchant_zjfw` VALUES ('ba680b5028be4cacb1a80d4355e01939', '981f1d141c5a4f2f93ab3dd58e8b7874', '25c7cb3315d449bfba173df2a70f972a', '2015-04-18 22:02:44');
INSERT INTO `ab_merchant_zjfw` VALUES ('bd1634ff62484016b73fdfe94bbc5188', '3587e3ef3971402db97584b6ade9d999', 'ebc93ec42823416db8e8be84f4b99d3b', '2015-04-02 00:45:50');
INSERT INTO `ab_merchant_zjfw` VALUES ('c0a1d743020e41889100358a82973908', '63bd3c719f1d4be98a8a396855408c73', 'a7b25e9592514cc5bd78511af3dfdbe1', '2015-04-19 22:31:07');
INSERT INTO `ab_merchant_zjfw` VALUES ('cd700a89c4234ae990ab6a1381d3a7f2', 'b18147d5101f435a833f2b2b5240ffb3', 'b18147d5101f435a833f2b2b5240ffb3', '2015-04-12 18:10:39');
INSERT INTO `ab_merchant_zjfw` VALUES ('f3e164eb8a494b67a51f1a31bf1d2d9d', '9526be72c83b41c9895b1fd6fd7cf562', '9526be72c83b41c9895b1fd6fd7cf562', '2015-04-17 22:46:28');

-- ----------------------------
-- Table structure for `ab_order`
-- ----------------------------
DROP TABLE IF EXISTS `ab_order`;
CREATE TABLE `ab_order` (
  `id` varchar(32) NOT NULL COMMENT '订单ID',
  `sn` varchar(20) default NULL COMMENT '订单编号',
  `subject_id` int(11) default NULL COMMENT '分类ID',
  `subject_name` varchar(50) default NULL COMMENT '分类名称',
  `is_type` char(1) default NULL COMMENT '类型[0-货物类、1-服务类]',
  `city_id` varchar(32) default NULL COMMENT '城市ID',
  `city_name` varchar(100) default NULL COMMENT '城市名称',
  `area_id` varchar(32) default NULL COMMENT '区域ID',
  `area_name` varchar(100) default NULL COMMENT '区域名称',
  `yt` char(1) default NULL COMMENT '用途[0-犒劳自己、1-赠送他人]',
  `mid` varchar(32) default NULL COMMENT '商家ID',
  `mname` varchar(100) default NULL COMMENT '商家名称',
  `sjjd` varchar(20) default NULL COMMENT '商家地理经度',
  `sjwd` varchar(20) default NULL COMMENT '商家地理纬度',
  `mapbusiness` varchar(50) default NULL COMMENT '商家地图位置',
  `spzj` decimal(10,2) default '0.00' COMMENT '商品总价',
  `ddzje` decimal(10,2) default '0.00' COMMENT '订单总金额',
  `yhje` decimal(10,2) default '0.00' COMMENT '优惠金额',
  `cjlfjf` decimal(10,2) default '0.00' COMMENT '超距离附加费',
  `sdsj` varchar(20) default NULL COMMENT '送达时间',
  `zsjf` int(11) default NULL COMMENT '赠送积分',
  `zfzt` char(1) default '0' COMMENT '支付状态[0-未支付、1-已支付]',
  `zffs` char(1) default '0' COMMENT '支付方式 1 充值直扣，2 支付宝，3 餐到付款, 4 无支付',
  `zfsj` varchar(20) default NULL COMMENT '支付时间',
  `ddzt` char(1) default '0' COMMENT '订单状态[0-购物车、1-已提交、2-取货中、3-送货中、4-已送达、5-已签收、6-拒单、7-已退单、8-撤单]',
  `yzbm` varchar(6) default NULL COMMENT '收货邮政编码',
  `shdz` varchar(50) default NULL COMMENT '收货通讯地址',
  `lxr` varchar(50) default NULL COMMENT '收货联系人',
  `lxrdh` varchar(50) default NULL COMMENT '收货人联系电话',
  `dzsfqr` char(1) default '0' COMMENT '地址是否确认[0-未确认、1-已确认]',
  `dzqrsj` varchar(20) default NULL COMMENT '地址确认时间',
  `dzqrip` varchar(20) default NULL COMMENT '地址确认IP',
  `shdzjd` varchar(50) default NULL COMMENT '送货地址地图精度',
  `shdzwd` varchar(50) default NULL COMMENT '送货地址地图纬度',
  `sfdb` char(1) default '0' COMMENT '是否打包[0-否、1-是]',
  `dbyq` varchar(200) default NULL COMMENT '打包要求',
  `dbbh` varchar(32) default NULL COMMENT '打包编号',
  `dbsj` varchar(20) default NULL COMMENT '打包时间',
  `shzt` char(1) default '0' COMMENT '打包是否审核[0-待审、1-已审核]',
  `dbspzj` decimal(14,2) default '0.00' COMMENT '打包后商品总价',
  `dbptf` decimal(14,2) default '0.00' COMMENT '打包后跑腿费',
  `dbcjlfjf` decimal(14,2) default '0.00' COMMENT '打包后超距离附加费',
  `dbyhje` decimal(14,2) default '0.00' COMMENT '打包后优惠金额',
  `dbddzje` decimal(14,2) default '0.00' COMMENT '打包后订单总金额',
  `memo` varchar(200) default NULL COMMENT '特殊要求',
  `jl` varchar(20) default NULL COMMENT '距离',
  `ptf` decimal(10,2) default '0.00' COMMENT '跑腿费金额',
  `mcptf` char(1) default '0' COMMENT '是否免除腿费[0-否、1-是]',
  `mcptfsm` varchar(200) default NULL COMMENT '免费跑腿费说明',
  `xdsj` varchar(20) default NULL COMMENT '下单时间',
  `xdrid` varchar(32) default NULL COMMENT '下单人ID',
  `xdrmc` varchar(50) default NULL COMMENT '下单人名称',
  `xdrdh` varchar(50) default NULL COMMENT '下单人电话',
  `ddcxsj` varchar(20) default NULL COMMENT '订单撤销时间',
  `ddcxrid` varchar(32) default NULL COMMENT '订单撤销人',
  `ddcxrmc` varchar(50) default NULL COMMENT '订单撤销人名称',
  `psfs` char(1) default NULL COMMENT '配送方式[0-商家自送、1-外部配送]',
  `qdsj` varchar(20) default NULL COMMENT '抢单时间',
  `qdrid` varchar(32) default NULL COMMENT '接单人ID',
  `qdrname` varchar(50) default NULL COMMENT '接单人名称',
  `jdrjd` varchar(20) default NULL COMMENT '接单人地理经度',
  `jdrwd` varchar(20) default NULL COMMENT '接单人地理纬度',
  `shsj` varchar(20) default NULL COMMENT '收货时间',
  `shqrrid` varchar(32) default NULL COMMENT '收货确认人ID',
  `shqrrmc` varchar(50) default NULL COMMENT '收货确认人名称',
  `yqsdsj` varchar(20) default NULL COMMENT '要求送达时间',
  `rzm` varchar(20) default NULL COMMENT '认证码',
  `is_account` char(1) default NULL COMMENT '是否结算[0-未结算、1-已结算]',
  `per` decimal(10,2) default '0.00' COMMENT '佣金比例',
  `accountmoney` decimal(10,2) default '0.00' COMMENT '结算金额',
  `accountdate` varchar(20) default NULL COMMENT '结算时间',
  `accountid` varchar(32) default NULL COMMENT '结算人ID',
  `accountname` varchar(50) default NULL COMMENT '结算人名称',
  `accountremark` varchar(200) default NULL COMMENT '结算人备注',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_order[订单信息表]';

-- ----------------------------
-- Records of ab_order
-- ----------------------------
INSERT INTO `ab_order` VALUES ('13bb14c1e40042ccbded31b6cae9ae00', '542136879', '1001', '我要点餐', '0', '109100', '郑州', '109100100', '二七万达商圈', null, '9526be72c83b41c9895b1fd6fd7cf562', '九九鸭脖', '113.711341', '34.769248', '金水路/未来路(路口)', '66.00', '68.00', '0.00', '0.00', null, '0', '0', '0', null, '0', null, '郑州市管城回族区紫荆山路/东大街(路口)', '秦淮人家', '13673386255', '0', null, null, null, null, '0', null, null, null, '0', '0.00', '0.00', '0.00', '0.00', '0.00', null, null, '2.00', '0', null, '2015-04-19 22:31:12', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '13673386255', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '0.00', '0.00', null, null, null, null);
INSERT INTO `ab_order` VALUES ('3b081fb9a34c4637a480247b1c0d0e94', '643178925', '1001', '我要点餐', '0', '109100', '郑州', '109100101', '紫荆山商圈', null, '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '113.688532', '34.754895', '紫荆山路/东大街(路口)', '90.00', '90.00', '0.00', '0.00', null, '0', '0', '3', null, '1', null, '郑州而且沃尔秦莞尔', '张明', '1326612365', '0', null, null, null, null, null, null, '4a88dcb47dee4b5ba14ec79283cbece6', null, '0', '0.00', '0.00', '0.00', '0.00', '0.00', '的机器沃尔全文而', null, '0.00', '0', null, '2015-04-18 23:16:29', '981f1d141c5a4f2f93ab3dd58e8b7874', null, '13673386255', null, null, null, '0', null, null, null, null, null, null, null, null, '2015-04-18 23:16:27', 'afncmb', null, '0.00', '0.00', null, null, null, null);
INSERT INTO `ab_order` VALUES ('49c6fd05fbca45e0955b4e8c9cd13231', '671948352', '1001', '我要点餐', '0', '109100', '郑州', '109100101', '紫荆山商圈', null, '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '113.688532', '34.754895', '紫荆山路/东大街(路口)', '242.00', '239.50', '18.00', '0.00', null, '0', '0', '3', null, '1', null, '河南省郑州市二七区红云路2号院', '张明', '13627372362', '0', null, null, null, null, null, null, 'd99b6b598ddf4b4b97006f0eaef1f3ce', null, '0', '0.00', '0.00', '0.00', '0.00', '0.00', '阿斯顿发放的', null, '15.50', '0', null, '2015-04-17 23:49:36', '981f1d141c5a4f2f93ab3dd58e8b7874', null, '13673386255', null, null, null, '1', null, null, null, null, null, null, null, null, '2015-04-17 23:49:29', '7k8t3e', null, '0.00', '0.00', null, null, null, null);
INSERT INTO `ab_order` VALUES ('6b77a5bf8d074d118b29c6124bc91053', '758316492', '1001', '我要点餐', '0', '109100', '郑州', '109100100', '二七万达商圈', null, 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府', '113.671612', '34.71616', '长江路/碧云路(路口)', '195.00', '202.00', '0.00', '3.00', null, '0', '0', '3', null, '1', null, '郑州市二七区万达国际影城(二七店)', '系统管理员', '1123412341134', '0', null, null, null, null, null, null, 'b4ae7909fd7a400eb739e235514523d4', null, '0', '0.00', '0.00', '0.00', '0.00', '0.00', '123123414', null, '4.00', '0', null, '2015-04-20 02:16:19', '981f1d141c5a4f2f93ab3dd58e8b7874', '用户亮亮', '13673386255', null, null, null, '1', null, null, null, null, null, null, null, null, '2015-04-24 02:16:16', '389caz', null, '0.00', '0.00', null, null, null, null);
INSERT INTO `ab_order` VALUES ('7e7bb17948144cad855840990000fa6d', '134756829', '1001', '我要点餐', '0', '109100', '郑州', '109100101', '紫荆山商圈', null, '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '113.688532', '34.754895', '紫荆山路/东大街(路口)', '60.00', '62.00', '0.00', '0.00', null, '0', '0', '0', null, '0', null, '郑州市管城回族区紫荆山路/东大街(路口)', '秦淮人家', '13673386255', '0', null, null, null, null, '0', null, null, null, '0', '0.00', '0.00', '0.00', '0.00', '0.00', null, null, '2.00', '0', null, '2015-04-19 22:27:51', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '13673386255', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '0.00', '0.00', null, null, null, null);
INSERT INTO `ab_order` VALUES ('ad7d29af656a4b07823695ff6d70b2ea', '674859231', '1001', '我要点餐', '0', '109100', '郑州', '109100101', '紫荆山商圈', null, '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '113.688532', '34.754895', '紫荆山路/东大街(路口)', '60.00', '60.00', '0.00', '0.00', null, '0', '0', '3', null, '1', null, '2345', '2345', '2345', '0', null, null, null, null, null, null, '00dd4ebc1d9f470a8be54495f0602c76', null, '0', '0.00', '0.00', '0.00', '0.00', '0.00', '2345', null, '0.00', '0', null, '2015-04-18 22:39:29', '981f1d141c5a4f2f93ab3dd58e8b7874', null, '13673386255', null, null, null, '1', null, null, null, null, null, null, null, null, '2015-04-18 22:39:27', '5b42yv', null, '0.00', '0.00', null, null, null, null);
INSERT INTO `ab_order` VALUES ('ca40ed1733754ff38e7524bcbf05fbe0', '461325798', '1001', '我要点餐', '0', '109100', '郑州', '109100100', '二七万达商圈', null, '9526be72c83b41c9895b1fd6fd7cf562', '九九鸭脖', '113.711341', '34.769248', '金水路/未来路(路口)', '242.00', '239.50', '18.00', '0.00', null, '0', '0', '3', null, '1', null, '河南省郑州市二七区红云路2号院', '张明', '13627372362', '0', null, null, null, null, null, null, '234127fcebd04479936fdae067a7222b', null, '0', '0.00', '0.00', '0.00', '0.00', '0.00', '阿斯顿发放的', null, '15.50', '0', null, '2015-04-17 23:49:36', '981f1d141c5a4f2f93ab3dd58e8b7874', null, '13673386255', null, null, null, '1', null, null, null, null, null, null, null, null, '2015-04-17 23:49:29', '7k8t3e', null, '0.00', '0.00', null, null, null, null);

-- ----------------------------
-- Table structure for `ab_order_chargeback`
-- ----------------------------
DROP TABLE IF EXISTS `ab_order_chargeback`;
CREATE TABLE `ab_order_chargeback` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `xdr_id` varchar(32) default NULL COMMENT '下单人ID',
  `order_id` varchar(32) default NULL COMMENT '订单ID',
  `mer_id` varchar(32) default NULL COMMENT '商户ID',
  `qdr_id` varchar(32) default NULL,
  `status` varchar(1) default NULL COMMENT '仲裁状态 1. 申请仲裁 2 仲裁中 3仲裁完成',
  `cb_status` varchar(2) default NULL COMMENT '退单流程状态',
  `apply_id` varchar(32) default NULL COMMENT '申请人ID',
  `apply_type` varchar(1) default NULL COMMENT '申请人类型 1 用户 2商户 3业务员',
  `apply_desc` varchar(255) default NULL COMMENT '退单申请描述',
  `apply_time` varchar(50) default NULL COMMENT '退单申请时间',
  `apply_img_url` varchar(255) default NULL COMMENT '图片路径',
  `apply_img` longblob COMMENT '用户申请图片',
  `rep_desc` varchar(255) default NULL COMMENT '回复内容',
  `rep_time` varchar(50) default NULL COMMENT '回复时间',
  `rep_img_url` varchar(255) default NULL COMMENT '图片路径',
  `rep_img` longblob COMMENT '商户回复图片',
  `judge_id` varchar(32) default NULL,
  `judge_desc` varchar(255) default NULL COMMENT '仲裁描述',
  `judge_time` varchar(50) default NULL COMMENT '申请仲裁时间',
  `result_desc` varchar(255) default NULL COMMENT '结果描述',
  `result_time` varchar(50) default NULL COMMENT '结果时间',
  `service_id` varchar(32) default NULL COMMENT '客服ID',
  `back_money` decimal(10,2) default '0.00' COMMENT ' 退单金额',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_order_chargeback
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_order_chargeback_item`
-- ----------------------------
DROP TABLE IF EXISTS `ab_order_chargeback_item`;
CREATE TABLE `ab_order_chargeback_item` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `cbid` varchar(32) NOT NULL COMMENT '退单ID',
  `uid` varchar(32) default NULL COMMENT '补充人ID',
  `content` varchar(255) default NULL COMMENT '补充内容',
  `img_url` varchar(255) default NULL COMMENT '图片路径',
  `image` longblob COMMENT '图片',
  `type` varchar(1) NOT NULL COMMENT '补充人类型 1 用户 2商户',
  `datetime` varchar(50) default NULL COMMENT '补充时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_order_chargeback_item
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_order_item`
-- ----------------------------
DROP TABLE IF EXISTS `ab_order_item`;
CREATE TABLE `ab_order_item` (
  `id` varchar(32) NOT NULL COMMENT '订单明细ID',
  `orderid` varchar(32) NOT NULL COMMENT '订单ID',
  `createtime` varchar(20) default NULL COMMENT '创建时间',
  `modifytime` varchar(20) default NULL COMMENT '修改时间',
  `subject_id` varchar(50) default NULL COMMENT '一级分类ID',
  `subject_name` varchar(50) default NULL COMMENT '一级分类名称',
  `sub_id` varchar(32) default NULL COMMENT '二级分类ID',
  `sub_name` varchar(50) default NULL COMMENT '二级分类名称',
  `thr_id` varchar(32) default NULL COMMENT '三级分类ID',
  `thr_name` varchar(50) default NULL COMMENT '三级分类名称',
  `is_type` char(1) default NULL COMMENT '类型[0-货物类、1-服务类]',
  `itemid` varchar(32) default NULL COMMENT '商品ID',
  `itemname` varchar(50) default NULL COMMENT '商家名称',
  `img_url` varchar(100) default NULL COMMENT '效果图',
  `thumbnail_url` varchar(100) default NULL COMMENT '缩略图',
  `quantity` int(11) default NULL COMMENT '销售总量',
  `price` decimal(10,2) default NULL COMMENT '价格',
  `totalmoney` decimal(10,2) default NULL COMMENT '金额',
  `seq_num` int(11) default '0' COMMENT '商品排序',
  `ptf` decimal(10,2) default '0.00' COMMENT '跑腿费',
  `yj` decimal(10,2) default '0.00' COMMENT '佣金',
  `mid` varchar(32) default '0' COMMENT '商家ID',
  `mname` varchar(100) default NULL COMMENT '商家名称',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_order_item_01` (`orderid`),
  CONSTRAINT `FK_ab_order_item_01` FOREIGN KEY (`orderid`) REFERENCES `ab_order` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_order_item[订单明细]';

-- ----------------------------
-- Records of ab_order_item
-- ----------------------------
INSERT INTO `ab_order_item` VALUES ('027ebdac1b124c5cb0f17685492b2ed3', 'ca40ed1733754ff38e7524bcbf05fbe0', '2015-04-17 23:10:08', null, '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', null, '00891be559cf4ac0ab629a1a82d6c67f', '麻辣豆腐', 'f83b4a4fc16347c79de6985c09ab48ab.jpg', 'f83b4a4fc16347c79de6985c09ab48ab-thumbnail.jpg', '5', '22.00', '110.00', '0', '0.00', '0.00', '9526be72c83b41c9895b1fd6fd7cf562', '九九鸭脖');
INSERT INTO `ab_order_item` VALUES ('1c91bc3e39d54f9d904528d622dde428', '49c6fd05fbca45e0955b4e8c9cd13231', '2015-04-17 23:07:52', null, '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', null, 'ca91afb739b44fcda7b117dd45d40d8f', '剁椒鱼头', 'f16344f1393d48d7b482da5e44b24dc1.jpg', 'f16344f1393d48d7b482da5e44b24dc1-thumbnail.jpg', '2', '30.00', '60.00', '0', '0.00', '0.00', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家');
INSERT INTO `ab_order_item` VALUES ('487cda16c72f4fb6bac54939ac4db8a8', '3b081fb9a34c4637a480247b1c0d0e94', '2015-04-18 22:56:20', null, '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', null, 'ca91afb739b44fcda7b117dd45d40d8f', '剁椒鱼头', 'f16344f1393d48d7b482da5e44b24dc1.jpg', 'f16344f1393d48d7b482da5e44b24dc1-thumbnail.jpg', '3', '30.00', '90.00', '0', '0.00', '0.00', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家');
INSERT INTO `ab_order_item` VALUES ('58ce12ccc6db47448c3e4eac1ed234ae', '49c6fd05fbca45e0955b4e8c9cd13231', '2015-04-17 23:07:44', null, '1001', '我要点餐', '10011001', '早餐', '100110011000', '主餐', null, 'ccce1c0292b94cf6a7919145fefeb9dd', '地三鲜', 'dfd3ec045fa747dc9dc658cf7f316c78.jpg', 'dfd3ec045fa747dc9dc658cf7f316c78-thumbnail.jpg', '4', '18.00', '72.00', '0', '0.00', '0.00', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家');
INSERT INTO `ab_order_item` VALUES ('7fbe34644f9c4aab93da815f788260af', '6b77a5bf8d074d118b29c6124bc91053', '2015-04-20 00:38:28', null, '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', null, '6c4007d9e81b424894c5c50ca24ee050', '黄焖鸡', '2b84a2ff04264c309982dcf4edfd5cc3.jpg', '2b84a2ff04264c309982dcf4edfd5cc3-thumbnail.jpg', '3', '15.00', '45.00', '0', '4.00', '0.00', 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府');
INSERT INTO `ab_order_item` VALUES ('97146f31108649a8a9a5db340cb66c9c', '7e7bb17948144cad855840990000fa6d', '2015-04-19 22:27:51', null, '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', null, 'ca91afb739b44fcda7b117dd45d40d8f', '剁椒鱼头', 'f16344f1393d48d7b482da5e44b24dc1.jpg', 'f16344f1393d48d7b482da5e44b24dc1-thumbnail.jpg', '2', '30.00', '60.00', '0', '2.00', '0.00', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家');
INSERT INTO `ab_order_item` VALUES ('a38709a0bff841adaf88ccca357e7db2', 'ad7d29af656a4b07823695ff6d70b2ea', '2015-04-18 22:20:04', null, '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', null, 'ca91afb739b44fcda7b117dd45d40d8f', '剁椒鱼头', 'f16344f1393d48d7b482da5e44b24dc1.jpg', 'f16344f1393d48d7b482da5e44b24dc1-thumbnail.jpg', '2', '30.00', '60.00', '0', '0.00', '0.00', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家');
INSERT INTO `ab_order_item` VALUES ('d847ae3253964588882a080aaba83451', '6b77a5bf8d074d118b29c6124bc91053', '2015-04-20 00:38:18', null, '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', null, '46c880b53505436f80f38568b0a9c71f', '酱牛肉', 'b26d969d517c4ecbbbe0c31ba27b85d3.jpg', 'b26d969d517c4ecbbbe0c31ba27b85d3-thumbnail.jpg', '6', '25.00', '150.00', '0', '4.00', '0.00', 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府');
INSERT INTO `ab_order_item` VALUES ('e10b8d3ce8cd4e508dfa44a0a9ff1a12', '13bb14c1e40042ccbded31b6cae9ae00', '2015-04-19 22:31:12', null, '1001', '我要点餐', '10011002', '午餐/晚餐', '100110021001', '川菜', null, '00891be559cf4ac0ab629a1a82d6c67f', '麻辣豆腐', 'f83b4a4fc16347c79de6985c09ab48ab.jpg', 'f83b4a4fc16347c79de6985c09ab48ab-thumbnail.jpg', '3', '22.00', '66.00', '0', '2.00', '0.00', '9526be72c83b41c9895b1fd6fd7cf562', '九九鸭脖');

-- ----------------------------
-- Table structure for `ab_order_itemchargeoff`
-- ----------------------------
DROP TABLE IF EXISTS `ab_order_itemchargeoff`;
CREATE TABLE `ab_order_itemchargeoff` (
  `id` varchar(32) NOT NULL COMMENT '退单ID',
  `orderid` varchar(32) NOT NULL COMMENT '订单ID',
  `qdrid` varchar(32) default NULL COMMENT '抢单人ID',
  `qdrname` varchar(50) default NULL COMMENT '抢单人名称',
  `reason` varchar(200) default NULL COMMENT '退单原因',
  `mid` varchar(32) default NULL COMMENT '商家ID',
  `mname` varchar(100) default NULL COMMENT '商家名称',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_order_itemchargeoff_01` (`orderid`),
  CONSTRAINT `FK_ab_order_itemchargeoff_01` FOREIGN KEY (`orderid`) REFERENCES `ab_order` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_order_itemchargeoff[订单退单明细]';

-- ----------------------------
-- Records of ab_order_itemchargeoff
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_order_position`
-- ----------------------------
DROP TABLE IF EXISTS `ab_order_position`;
CREATE TABLE `ab_order_position` (
  `id` varchar(32) NOT NULL COMMENT '地图位置标志',
  `orderid` varchar(32) NOT NULL COMMENT '订单ID',
  `mid` varchar(32) default NULL COMMENT '商家ID',
  `mname` varchar(100) default NULL COMMENT '商家名称',
  `shrid` varchar(32) default NULL COMMENT '送货人ID',
  `shrmc` varchar(50) default NULL COMMENT '送货人名称',
  `jd` varchar(20) default NULL COMMENT '经度',
  `wd` varchar(20) default NULL COMMENT '维度',
  `dqsj` varchar(20) default NULL COMMENT '当前时间',
  `zt` char(1) default '0' COMMENT '状态[0-离线，1-在线，2-空闲，3-忙碌，4-收工回家]',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_order_position_01` (`orderid`),
  CONSTRAINT `FK_ab_order_position_01` FOREIGN KEY (`orderid`) REFERENCES `ab_order` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_order_position[订单当前位置]';

-- ----------------------------
-- Records of ab_order_position
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_order_quote`
-- ----------------------------
DROP TABLE IF EXISTS `ab_order_quote`;
CREATE TABLE `ab_order_quote` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `order_sn` varchar(150) NOT NULL COMMENT '订单编号',
  `uid` varchar(50) NOT NULL COMMENT '报价司机编号',
  `quote_money` float NOT NULL COMMENT ' 报价金额 ',
  `create_time` varchar(20) NOT NULL COMMENT '创建时间 ',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='招标订单报价保存信息';

-- ----------------------------
-- Records of ab_order_quote
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_order_sms`
-- ----------------------------
DROP TABLE IF EXISTS `ab_order_sms`;
CREATE TABLE `ab_order_sms` (
  `id` varchar(96) default NULL,
  `orderid` varchar(96) default NULL,
  `shrdh` varchar(150) default NULL,
  `yzm` varchar(150) default NULL,
  `createtime` varchar(60) default NULL,
  `timelong` decimal(16,0) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ab_order_sms
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_order_state_img`
-- ----------------------------
DROP TABLE IF EXISTS `ab_order_state_img`;
CREATE TABLE `ab_order_state_img` (
  `pk_ab_order_img` varchar(32) NOT NULL COMMENT '主键',
  `fk_order_sn` varchar(150) NOT NULL COMMENT '订单编号',
  `uid` varchar(50) NOT NULL COMMENT '司机编号',
  `order_state` float NOT NULL COMMENT ' 订单状态 ',
  `order_img` float NOT NULL COMMENT '拍照图片保存路径',
  `create_time` varchar(20) NOT NULL COMMENT '创建时间 ',
  PRIMARY KEY  (`pk_ab_order_img`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单状态图片';

-- ----------------------------
-- Records of ab_order_state_img
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_product_comment`
-- ----------------------------
DROP TABLE IF EXISTS `ab_product_comment`;
CREATE TABLE `ab_product_comment` (
  `id` varchar(32) NOT NULL COMMENT '评论ID',
  `pid` varchar(32) NOT NULL COMMENT '商品ID',
  `title` varchar(255) default NULL COMMENT '标题',
  `content` text COMMENT '评论内容',
  `createtime` varchar(20) default NULL COMMENT '评论时间',
  `ip` varchar(20) default NULL COMMENT '评论IP',
  `userid` varchar(32) default NULL COMMENT '评论人ID',
  `username` varchar(50) default NULL COMMENT '评论人名称',
  `remark` varchar(500) default NULL COMMENT '备注',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_product_comment_01` (`pid`),
  CONSTRAINT `FK_ab_product_comment_01` FOREIGN KEY (`pid`) REFERENCES `ab_merchant_product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_product_comment[商品内容评论]';

-- ----------------------------
-- Records of ab_product_comment
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_product_img`
-- ----------------------------
DROP TABLE IF EXISTS `ab_product_img`;
CREATE TABLE `ab_product_img` (
  `id` varchar(32) NOT NULL COMMENT '图片ID',
  `f_id` varchar(32) default NULL COMMENT '外键ID',
  `title` varchar(255) default NULL COMMENT '标题',
  `type_name` varchar(255) default NULL COMMENT '类型名称',
  `seq_num` int(11) default NULL COMMENT '顺序号',
  `lager` varchar(255) default NULL COMMENT '大图',
  `thumbnail` varchar(255) default NULL COMMENT '缩略图',
  `scrid` varchar(32) default NULL COMMENT '上传人ID',
  `scrmc` varchar(50) default NULL COMMENT '上传人名称',
  `scsj` varchar(20) default NULL COMMENT '上传时间',
  `description` varchar(200) default NULL COMMENT '描述',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_product_img_01` (`f_id`),
  CONSTRAINT `FK_ab_product_img_01` FOREIGN KEY (`f_id`) REFERENCES `ab_merchant_product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_product_img[图片信息表]';

-- ----------------------------
-- Records of ab_product_img
-- ----------------------------
INSERT INTO `ab_product_img` VALUES ('00e9f8b803c84fc2919232d8874af9bc', '00891be559cf4ac0ab629a1a82d6c67f', null, '.jpg', null, '05d1b244ba704e28b159eee361ffbe0b.jpg', '05d1b244ba704e28b159eee361ffbe0b-thumbnail.jpg', '9526be72c83b41c9895b1fd6fd7cf562', '九九鸭脖', '2015-04-17 22:41:27', null);
INSERT INTO `ab_product_img` VALUES ('0adc56f38b504eb2b7bf52fe1850d8b3', 'a2e735c9013f46caaa0535683c7d32a9', null, '.jpg', null, '51cdcc0d34bd4b0099a0e4ef4412e6fc.jpg', '51cdcc0d34bd4b0099a0e4ef4412e6fc-thumbnail.jpg', 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府', '2015-04-20 00:22:30', null);
INSERT INTO `ab_product_img` VALUES ('0bbfec31014742caaf981a604796d658', 'ccce1c0292b94cf6a7919145fefeb9dd', null, '.jpg', null, 'fc06b399683c45a28ec02f24df6ae4de.jpg', 'fc06b399683c45a28ec02f24df6ae4de-thumbnail.jpg', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '2015-04-17 21:31:11', null);
INSERT INTO `ab_product_img` VALUES ('163bb422da654e1d9eca907fd7942e4e', 'ccce1c0292b94cf6a7919145fefeb9dd', null, '.jpg', null, 'e7311494fd0243fe8f6c5bd8610c0708.jpg', 'e7311494fd0243fe8f6c5bd8610c0708-thumbnail.jpg', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '2015-04-17 21:31:11', null);
INSERT INTO `ab_product_img` VALUES ('21641bacedcf4383bec6d5e454f4443d', '46c880b53505436f80f38568b0a9c71f', null, '.jpg', null, 'cac62961df6f4695ba9cecb63d29aabd.jpg', 'cac62961df6f4695ba9cecb63d29aabd-thumbnail.jpg', 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府', '2015-04-20 00:26:05', null);
INSERT INTO `ab_product_img` VALUES ('241341aa5bed43ffb5e4c3dfffe03507', 'ccce1c0292b94cf6a7919145fefeb9dd', null, '.jpg', null, '8ef69e521eff4e879c1e72589e83850a.jpg', '8ef69e521eff4e879c1e72589e83850a-thumbnail.jpg', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '2015-04-17 21:31:11', null);
INSERT INTO `ab_product_img` VALUES ('4b56a2b3698241b7a606fd30af1f4725', '00891be559cf4ac0ab629a1a82d6c67f', null, '.jpg', null, 'c940a35320c248739c47c5797da5240c.jpg', 'c940a35320c248739c47c5797da5240c-thumbnail.jpg', '9526be72c83b41c9895b1fd6fd7cf562', '九九鸭脖', '2015-04-17 22:41:27', null);
INSERT INTO `ab_product_img` VALUES ('551c8b2e0f4d4bdea0d71b211601623f', 'a2e735c9013f46caaa0535683c7d32a9', null, '.jpg', null, 'c37579f3cb8545f589f9800d12d9e047.jpg', 'c37579f3cb8545f589f9800d12d9e047-thumbnail.jpg', 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府', '2015-04-20 00:22:30', null);
INSERT INTO `ab_product_img` VALUES ('5846287181714e2ab389435037de22b2', 'a2e735c9013f46caaa0535683c7d32a9', null, '.jpg', null, '560a28ca04c84a7682e22f66da93fbb6.jpg', '560a28ca04c84a7682e22f66da93fbb6-thumbnail.jpg', 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府', '2015-04-20 00:22:30', null);
INSERT INTO `ab_product_img` VALUES ('62b00040f9604106aced60ba6b3e7301', '00891be559cf4ac0ab629a1a82d6c67f', null, '.jpg', null, '03ae3961abdd48b68bbc100558b990fe.jpg', '03ae3961abdd48b68bbc100558b990fe-thumbnail.jpg', '9526be72c83b41c9895b1fd6fd7cf562', '九九鸭脖', '2015-04-17 22:41:27', null);
INSERT INTO `ab_product_img` VALUES ('6c1dcde05c40448bad068bf1d42b438c', 'ca91afb739b44fcda7b117dd45d40d8f', null, '.jpg', null, '3b57f2ec1ea54f1d8a8d459c6dbad27b.jpg', '3b57f2ec1ea54f1d8a8d459c6dbad27b-thumbnail.jpg', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '2015-04-17 21:33:39', null);
INSERT INTO `ab_product_img` VALUES ('6f053cd5e8104928b6c4224f28d313db', '6c4007d9e81b424894c5c50ca24ee050', null, '.jpg', null, '4b8bb7f4c0604ec9b0ceef62a661bc58.jpg', '4b8bb7f4c0604ec9b0ceef62a661bc58-thumbnail.jpg', 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府', '2015-04-20 00:23:51', null);
INSERT INTO `ab_product_img` VALUES ('6fea4b95c6be4c4da4b92b15b598519b', '6c4007d9e81b424894c5c50ca24ee050', null, '.jpg', null, 'e3567f6f058043738f1d98fe38e51dc7.jpg', 'e3567f6f058043738f1d98fe38e51dc7-thumbnail.jpg', 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府', '2015-04-20 00:23:51', null);
INSERT INTO `ab_product_img` VALUES ('c86843136f2e4453bb71f63309d1e3ff', 'ca91afb739b44fcda7b117dd45d40d8f', null, '.jpg', null, 'a00a106c144a40b9ac6d3d463a6254ce.jpg', 'a00a106c144a40b9ac6d3d463a6254ce-thumbnail.jpg', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '2015-04-17 21:33:39', null);
INSERT INTO `ab_product_img` VALUES ('dadeb86b15104d64825aeb2648837194', '46c880b53505436f80f38568b0a9c71f', null, '.jpg', null, 'daeee3c094c94c188dc02339a6cc2f63.jpg', 'daeee3c094c94c188dc02339a6cc2f63-thumbnail.jpg', 'a7b25e9592514cc5bd78511af3dfdbe1', '华豫食府', '2015-04-20 00:26:05', null);
INSERT INTO `ab_product_img` VALUES ('e849de3ba2ec4917be2c59c4e6ae1eb2', 'ca91afb739b44fcda7b117dd45d40d8f', null, '.jpg', null, 'cfc03f4f48a245d99bdf79595d93ce6f.jpg', 'cfc03f4f48a245d99bdf79595d93ce6f-thumbnail.jpg', '63bd3c719f1d4be98a8a396855408c73', '秦淮人家', '2015-04-17 21:33:39', null);

-- ----------------------------
-- Table structure for `ab_shywyjl`
-- ----------------------------
DROP TABLE IF EXISTS `ab_shywyjl`;
CREATE TABLE `ab_shywyjl` (
  `id` varchar(32) NOT NULL COMMENT '订单明细ID',
  `mid` varchar(32) NOT NULL COMMENT '商家ID',
  `mname` varchar(100) default NULL COMMENT '商家名称',
  `cityid` varchar(32) default NULL COMMENT '所属城市ID',
  `citymc` varchar(50) default NULL COMMENT '所属城市名称',
  `subject_id` varchar(50) default NULL COMMENT '一级分类ID',
  `subject_name` varchar(50) default NULL COMMENT '一级分类名称',
  `sjjd` varchar(20) default NULL COMMENT '商家地理经度',
  `sjwd` varchar(20) default NULL COMMENT '商家地理纬度',
  `ywyid` varchar(32) default NULL COMMENT '业务员ID',
  `ywymc` varchar(50) default NULL COMMENT '业务员名称',
  `ywyjd` varchar(20) default NULL COMMENT '商家地理经度',
  `ywywd` varchar(20) default NULL COMMENT '商家地理纬度',
  `lzjl` decimal(10,2) default NULL COMMENT '两者距离[单位：米]',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_shywyjl_01` (`mid`),
  KEY `FK_ab_shywyjl_02` (`ywyid`),
  CONSTRAINT `FK_ab_shywyjl_01` FOREIGN KEY (`mid`) REFERENCES `ab_merchant` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_ab_shywyjl_02` FOREIGN KEY (`ywyid`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_shywyjl[商户业务员距离表]';

-- ----------------------------
-- Records of ab_shywyjl
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_sj_position`
-- ----------------------------
DROP TABLE IF EXISTS `ab_sj_position`;
CREATE TABLE `ab_sj_position` (
  `id` varchar(32) NOT NULL COMMENT '地图位置标志',
  `sjid` varchar(32) default NULL COMMENT '司机ID',
  `sjmc` varchar(50) default NULL COMMENT '司机名称',
  `jd` varchar(20) default NULL COMMENT '经度',
  `wd` varchar(20) default NULL COMMENT '维度',
  `dqsj` varchar(20) default NULL COMMENT '当前时间',
  `zt` char(1) default '0' COMMENT '状态[0-离线，1-在线，2-空闲，3-忙碌，4-收工回家]'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_order_position[订单当前位置]';

-- ----------------------------
-- Records of ab_sj_position
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_subject`
-- ----------------------------
DROP TABLE IF EXISTS `ab_subject`;
CREATE TABLE `ab_subject` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `mc` varchar(50) default NULL COMMENT '名称',
  `ccm` int(11) default NULL COMMENT '层次',
  `p_id` varchar(32) default NULL COMMENT '上级ID',
  `is_enable` char(1) default '1' COMMENT '是否禁用[0-禁用、1-可用]',
  `is_display` char(1) default '1' COMMENT '是否显示[0-隐藏、1-显示]',
  `is_type` char(1) default NULL COMMENT '类型[0-货物类、1-服务类]',
  `seq_num` int(11) default NULL COMMENT '排序号',
  `ddesc` text COMMENT '分类描述',
  `class_path` varchar(200) default NULL COMMENT '分类地址',
  `remark` varchar(500) default NULL COMMENT '备注',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_subject_01` (`p_id`),
  CONSTRAINT `FK_ab_subject_01` FOREIGN KEY (`p_id`) REFERENCES `ab_subject` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_subject[分类信息表]';

-- ----------------------------
-- Records of ab_subject
-- ----------------------------
INSERT INTO `ab_subject` VALUES ('1001', '我要点餐', '1', 'ROOT', '1', '1', '0', '1', '营业员', 'shiwu.png', '一样');
INSERT INTO `ab_subject` VALUES ('10011001', '早餐', '2', '1001', '1', '1', '0', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('100110011000', '主餐', '3', '10011001', '0', '1', '0', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('100110011001', '副餐', '3', '10011001', '1', '1', '0', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('100110011002', '饮料', '3', '10011001', '1', '1', '0', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('10011002', '午餐/晚餐', '2', '1001', '1', '1', '0', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('100110021000', '酒水', '3', '10011002', '1', '1', '0', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('100110021001', '川菜', '3', '10011002', '1', '1', '0', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10011003', '下午茶', '2', '1001', '1', '1', '0', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('10011004', '蛋糕/搞点', '2', '1001', '1', '1', '0', '4', null, null, null);
INSERT INTO `ab_subject` VALUES ('10011005', '半成品/熟食', '2', '1001', '1', '1', '0', '5', null, null, null);
INSERT INTO `ab_subject` VALUES ('10011006', '民间风味小吃', '2', '1001', '1', '1', '0', '6', null, null, null);
INSERT INTO `ab_subject` VALUES ('10011007', '阿姨私房菜', '2', '1001', '1', '1', '0', '7', null, null, null);
INSERT INTO `ab_subject` VALUES ('10011008', '沙拉', '2', '1001', '1', '1', '0', '8', null, null, null);
INSERT INTO `ab_subject` VALUES ('10011009', '披萨', '2', '1001', '1', '1', '0', '9', null, null, null);
INSERT INTO `ab_subject` VALUES ('1002', '我要零食', '1', 'ROOT', '0', '1', '0', '2', null, 'lingshi.png', null);
INSERT INTO `ab_subject` VALUES ('10021001', '干果类', '2', '1002', '1', '1', '0', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10021002', '果脯类', '2', '1002', '1', '1', '0', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10021003', '肉食类', '2', '1002', '1', '1', '0', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('10021004', '进口零食', '2', '1002', '1', '1', '0', '4', null, null, null);
INSERT INTO `ab_subject` VALUES ('1003', '特色生活', '1', 'ROOT', '0', '1', '0', '3', null, 'shenghuo.png', null);
INSERT INTO `ab_subject` VALUES ('10031001', '小妖面膜', '2', '1003', '1', '1', '0', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10031002', '美肤用户', '2', '1003', '1', '1', '0', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10031003', '美妆用品', '2', '1003', '1', '1', '0', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('10031004', '情趣用品', '2', '1003', '1', '1', '0', '6', null, null, null);
INSERT INTO `ab_subject` VALUES ('10031005', '保健', '2', '1003', '1', '1', '0', '5', null, null, null);
INSERT INTO `ab_subject` VALUES ('10031006', '亲子用品', '2', '1003', '1', '1', '0', '4', null, null, null);
INSERT INTO `ab_subject` VALUES ('10031007', '鲜花', '2', '1003', '1', '1', '0', '7', null, null, null);
INSERT INTO `ab_subject` VALUES ('1004', '年货特供', '1', 'ROOT', '0', '1', '0', '4', null, 'nianhuo.png', null);
INSERT INTO `ab_subject` VALUES ('1005', '产地直供', '1', 'ROOT', '0', '1', '0', '5', null, 'changdi.png', null);
INSERT INTO `ab_subject` VALUES ('10051001', '阳澄湖大闸蟹', '2', '1005', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('1006', '生活服务', '1', 'ROOT', '1', '1', '1', '7', null, 'shui.png', null);
INSERT INTO `ab_subject` VALUES ('10061001', '我要搬家', '2', '1006', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10061002', '我要送水', '2', '1006', '1', '1', '1', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('10061003', '我要装修', '2', '1006', '1', '1', '1', '4', null, null, null);
INSERT INTO `ab_subject` VALUES ('10061004', '我要保洁', '2', '1006', '1', '1', '1', '5', null, null, null);
INSERT INTO `ab_subject` VALUES ('10061005', '我要保姆', '2', '1006', '1', '1', '1', '6', null, null, null);
INSERT INTO `ab_subject` VALUES ('10061006', '疏通管道', '2', '1006', '1', '1', '1', '7', null, null, null);
INSERT INTO `ab_subject` VALUES ('10061007', '我要开锁', '2', '1006', '1', '1', '1', '8', null, null, null);
INSERT INTO `ab_subject` VALUES ('10061008', '风水起名', '2', '1006', '1', '1', '1', '9', null, null, null);
INSERT INTO `ab_subject` VALUES ('1007', '上门维修', '1', 'ROOT', '1', '1', '1', '9', null, 'weixiu.png', null);
INSERT INTO `ab_subject` VALUES ('10071001', '为电脑维修', '2', '1007', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10071002', '家电维修', '2', '1007', '1', '1', '1', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10071003', '代送维修', '2', '1007', '1', '1', '1', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('1008', '医疗服务', '1', 'ROOT', '0', '1', '1', '14', null, 'yiliao.png', null);
INSERT INTO `ab_subject` VALUES ('10081001', '我要送药', '2', '1008', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10081002', '我要挂号', '2', '1008', '1', '1', '1', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10081003', '我要按摩', '2', '1008', '1', '1', '1', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('1009', '我要家教', '1', 'ROOT', '0', '1', '1', '16', null, 'jiaoshi.png', null);
INSERT INTO `ab_subject` VALUES ('10091001', '小学', '2', '1009', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10091002', '初中', '2', '1009', '1', '1', '1', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10091003', '高中', '2', '1009', '1', '1', '1', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('1011', '汽车服务', '1', 'ROOT', '0', '1', '1', '17', null, 'qiche.png', null);
INSERT INTO `ab_subject` VALUES ('10111001', '我要代驾', '2', '1011', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10111002', '我要租赁', '2', '1011', '1', '1', '1', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10111003', '我要陪练', '2', '1011', '1', '1', '1', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('10111004', '我要洗车', '2', '1011', '1', '1', '1', '4', null, null, null);
INSERT INTO `ab_subject` VALUES ('10111005', '汽车美容', '2', '1011', '1', '1', '1', '5', null, null, null);
INSERT INTO `ab_subject` VALUES ('1012', '法律服务', '1', 'ROOT', '0', '1', '1', '18', null, 'falv.png', null);
INSERT INTO `ab_subject` VALUES ('10121001', '写合同', '2', '1012', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10121002', '写状纸', '2', '1012', '1', '1', '1', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10121003', '股份纠纷', '2', '1012', '1', '1', '1', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('10121004', '我要离婚', '2', '1012', '1', '1', '1', '4', null, null, null);
INSERT INTO `ab_subject` VALUES ('1013', '其他分类', '1', 'ROOT', '0', '0', '1', '19', null, null, null);
INSERT INTO `ab_subject` VALUES ('10131001', '找关系', '2', '1013', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10131002', '我要贷款', '2', '1013', '1', '1', '1', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('1014', '宠物社区', '1', 'ROOT', '0', '1', '0', '5', null, 'chongwu.png', null);
INSERT INTO `ab_subject` VALUES ('10141001', '宠物照料', '2', '1014', '1', '1', '0', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10141002', '我要狗粮', '2', '1014', '1', '1', '0', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10141003', '我要猫粮', '2', '1014', '1', '1', '0', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('1015', '企业服务', '1', 'ROOT', '1', '1', '1', '10', null, 'qiye.png', null);
INSERT INTO `ab_subject` VALUES ('10151001', '代理注册', '2', '1015', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10151002', '代记账', '2', '1015', '1', '1', '1', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10151003', '代跑政府', '2', '1015', '1', '1', '1', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('10151004', '代宣传', '2', '1015', '1', '1', '1', '4', null, null, null);
INSERT INTO `ab_subject` VALUES ('1016', '跑腿服务', '1', 'ROOT', '0', '1', '1', '13', null, 'chongwu.png', null);
INSERT INTO `ab_subject` VALUES ('10161001', '接送服务', '2', '1016', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10161002', '送礼服务', '2', '1016', '1', '1', '1', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10161003', '代缴服务', '2', '1016', '1', '1', '1', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('10161004', '同城递送', '2', '1016', '1', '1', '1', '4', null, null, null);
INSERT INTO `ab_subject` VALUES ('10161005', '调查服务', '2', '1016', '1', '1', '0', '5', null, null, null);
INSERT INTO `ab_subject` VALUES ('10161006', '代发宣传单', '2', '1016', '1', '1', '1', '6', null, null, null);
INSERT INTO `ab_subject` VALUES ('1017', '私人定制', '1', 'ROOT', '0', '0', '1', '6', null, null, null);
INSERT INTO `ab_subject` VALUES ('10171001', '养生定制', '2', '1017', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10171002', '减微定制', '2', '1017', '1', '1', '1', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10171003', '美容定制', '2', '1017', '1', '1', '0', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('1018', '同城快递', '1', 'ROOT', '0', '1', '1', '6', null, 'kuaidi.png', null);
INSERT INTO `ab_subject` VALUES ('10181001', '我要发货', '2', '1018', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('1019', '房产服务', '1', 'ROOT', '0', '1', '1', '8', null, 'fangchan.png', null);
INSERT INTO `ab_subject` VALUES ('10191001', '二手房', '2', '1019', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10191002', '我要租房', '2', '1019', '1', '1', '0', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10191003', '租办公室', '2', '1019', '1', '1', '1', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('1020', '美丽到家', '1', 'ROOT', '0', '1', '1', '15', null, 'meijia.png', null);
INSERT INTO `ab_subject` VALUES ('10201001', '我要美甲', '2', '1020', '1', '1', '1', '1', null, null, null);
INSERT INTO `ab_subject` VALUES ('10201002', '我要写真', '2', '1020', '1', '1', '1', '2', null, null, null);
INSERT INTO `ab_subject` VALUES ('10201003', '我要摄影', '2', '1020', '1', '1', '1', '3', null, null, null);
INSERT INTO `ab_subject` VALUES ('ROOT', '分类首页', '0', null, '1', '1', null, '0', null, null, null);

-- ----------------------------
-- Table structure for `ab_subject_merchant`
-- ----------------------------
DROP TABLE IF EXISTS `ab_subject_merchant`;
CREATE TABLE `ab_subject_merchant` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `m_id` varchar(32) default NULL COMMENT '商家ID',
  `subject_id` varchar(32) default NULL COMMENT '分类ID',
  `srvmoney` double(8,2) default '0.00' COMMENT '跑腿费',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_subject_merchant_01` (`subject_id`),
  KEY `FK_ab_subject_merchant_02` (`m_id`),
  CONSTRAINT `FK_ab_subject_merchant_01` FOREIGN KEY (`subject_id`) REFERENCES `ab_subject` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_ab_subject_merchant_02` FOREIGN KEY (`m_id`) REFERENCES `ab_merchant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_subject_merchant[商家分类信息表]';

-- ----------------------------
-- Records of ab_subject_merchant
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_subject_product`
-- ----------------------------
DROP TABLE IF EXISTS `ab_subject_product`;
CREATE TABLE `ab_subject_product` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `subject_id` varchar(32) NOT NULL COMMENT '类别ID',
  `mc` varchar(100) default NULL COMMENT '名称',
  `price` decimal(10,2) default '0.00' COMMENT '单价',
  `remark` varchar(200) default NULL COMMENT '备注',
  `order_num` int(11) default '0' COMMENT '排序号码',
  `img_url` varchar(50) default NULL COMMENT '图片地址',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_subject_product[服务类商品的商品分类]';

-- ----------------------------
-- Records of ab_subject_product
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_subject_product_order`
-- ----------------------------
DROP TABLE IF EXISTS `ab_subject_product_order`;
CREATE TABLE `ab_subject_product_order` (
  `pid` varchar(32) NOT NULL COMMENT '商品id',
  `oid` varchar(32) NOT NULL COMMENT '订单ID',
  `num` int(11) default '0' COMMENT '数量'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_subject_product_order[服务类商品订单中间表]';

-- ----------------------------
-- Records of ab_subject_product_order
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_tc_config`
-- ----------------------------
DROP TABLE IF EXISTS `ab_tc_config`;
CREATE TABLE `ab_tc_config` (
  `id` int(11) NOT NULL auto_increment COMMENT '主键',
  `type` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL COMMENT '名称，如：车辆种类、货物种类',
  `key` varchar(100) default NULL COMMENT '关键词',
  `value` varchar(500) default NULL COMMENT '值',
  `desc` varchar(1000) default NULL COMMENT '描述',
  `listorder` int(11) default NULL COMMENT '排序值',
  `city_id` varchar(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_tc_config[同城快递配置管理]';

-- ----------------------------
-- Records of ab_tc_config
-- ----------------------------
INSERT INTO `ab_tc_config` VALUES ('1', '01', '小面包', '起步价', '50', '50元', '2', '109100');
INSERT INTO `ab_tc_config` VALUES ('2', '01', '小面包', '运输体积', '1.8立方米以内', null, '2', '109100');
INSERT INTO `ab_tc_config` VALUES ('3', '01', '小面包', '长宽高', '170*110*100cm', null, '2', '109100');
INSERT INTO `ab_tc_config` VALUES ('4', '01', '小面包', '重量限制', '600KG', null, '2', '109100');
INSERT INTO `ab_tc_config` VALUES ('5', '01', '小面包', '超公里费', '4', '4元每公里', '2', '109100');
INSERT INTO `ab_tc_config` VALUES ('6', '01', '小面包', '夜间服务', '25', '25元一次', '2', '109100');
INSERT INTO `ab_tc_config` VALUES ('8', '01', '金杯车', '起步价', '70', '70元', '3', '109100');
INSERT INTO `ab_tc_config` VALUES ('9', '01', '金杯车', '运输体积', '3立方米', null, '3', '109100');
INSERT INTO `ab_tc_config` VALUES ('10', '01', '金杯车', '长宽高', '270*100*120cm', null, '3', '109100');
INSERT INTO `ab_tc_config` VALUES ('11', '01', '金杯车', '重量限制', '1500KG', null, '3', '109100');
INSERT INTO `ab_tc_config` VALUES ('12', '01', '金杯车', '超公里费', '4', '4元每公里', '3', '109100');
INSERT INTO `ab_tc_config` VALUES ('13', '01', '金杯车', '夜间服务', '35', '35元', '3', '109100');
INSERT INTO `ab_tc_config` VALUES ('14', '03', '货物类型', '1', '液体', null, null, '109100');
INSERT INTO `ab_tc_config` VALUES ('15', '03', '货物类型', '2', '粉末', null, null, '109100');
INSERT INTO `ab_tc_config` VALUES ('16', '03', '货物类型', '3', '玻璃', null, null, '109100');
INSERT INTO `ab_tc_config` VALUES ('17', '03', '货物类型', '4', '易碎品', null, null, '109100');
INSERT INTO `ab_tc_config` VALUES ('18', '05', '电梯费用', '有电梯费用', '和司机面议', '无电梯费用40元.', '1', '109100');
INSERT INTO `ab_tc_config` VALUES ('19', '05', '电梯费用', '无电梯费用', '和司机面议', '无电梯费用70元.', '2', '109100');
INSERT INTO `ab_tc_config` VALUES ('20', '06', '代收货款', '代收货款', '10', '10元每次', null, '109100');
INSERT INTO `ab_tc_config` VALUES ('21', '01', '人工直达', '起步价', '30', '', '1', '109100');
INSERT INTO `ab_tc_config` VALUES ('22', '01', '人工直达', '运输体积', '0.5立方米以内', null, '1', '109100');
INSERT INTO `ab_tc_config` VALUES ('23', '01', '人工直达', '长宽高', '170*110*100cm', null, '1', '109100');
INSERT INTO `ab_tc_config` VALUES ('24', '01', '人工直达', '重量限制', '50KG', null, '1', '109100');
INSERT INTO `ab_tc_config` VALUES ('25', '01', '人工直达', '超公里费', '3', '3元每公里', '1', '109100');
INSERT INTO `ab_tc_config` VALUES ('26', '01', '人工直达', '夜间服务', '25', '25元一次', '1', '109100');
INSERT INTO `ab_tc_config` VALUES ('27', '01', '人工直达', '起步公里', '3', '3公里', '1', '109100');
INSERT INTO `ab_tc_config` VALUES ('28', '01', '小面包', '起步公里', '10', '10公里', '2', '109100');
INSERT INTO `ab_tc_config` VALUES ('29', '01', '金杯车', '起步公里', '10', null, '3', '109100');
INSERT INTO `ab_tc_config` VALUES ('30', '01', '小货车', '起步价', '70', null, '4', '109100');
INSERT INTO `ab_tc_config` VALUES ('31', '01', '小货车', '运输体积', '3立方米', null, '4', '109100');
INSERT INTO `ab_tc_config` VALUES ('32', '01', '小货车', '长宽高', '270*100*120cm', null, '4', '109100');
INSERT INTO `ab_tc_config` VALUES ('33', '01', '小货车', '重量限制', '600KG', null, '4', '109100');
INSERT INTO `ab_tc_config` VALUES ('34', '01', '小货车', '超公里费', '4', null, '4', '109100');
INSERT INTO `ab_tc_config` VALUES ('35', '01', '小货车', '夜间服务', '25', null, '4', '109100');
INSERT INTO `ab_tc_config` VALUES ('36', '01', '小货车', '起步公里', '10', null, '4', '109100');
INSERT INTO `ab_tc_config` VALUES ('37', '01', '小货车', '车长', '10,20,30', null, '4', '109100');
INSERT INTO `ab_tc_config` VALUES ('38', '01', '大货车', '起步价', '200', null, '5', '109100');
INSERT INTO `ab_tc_config` VALUES ('39', '01', '大货车', '运输体积', '500立方米以内', null, '5', '109100');
INSERT INTO `ab_tc_config` VALUES ('40', '01', '大货车', '长宽高', '1700*1100*1000', null, '5', '109100');
INSERT INTO `ab_tc_config` VALUES ('41', '01', '大货车', '重量限制', '5000', null, '5', '109100');
INSERT INTO `ab_tc_config` VALUES ('42', '01', '大货车', '超公里费', '0', null, '5', '109100');
INSERT INTO `ab_tc_config` VALUES ('43', '01', '大货车', '夜间服务', '1000', null, '5', '109100');
INSERT INTO `ab_tc_config` VALUES ('44', '01', '大货车', '车长', '50,80,100', null, '5', '109100');
INSERT INTO `ab_tc_config` VALUES ('45', '01', '大货车', '起步公里', '50', null, '5', '109100');
INSERT INTO `ab_tc_config` VALUES ('47', '01', '高缆车', '高缆车', '高缆车', null, '1', '109100');
INSERT INTO `ab_tc_config` VALUES ('49', '01', '测试车', '起步价', '60', null, '0', '109100');
INSERT INTO `ab_tc_config` VALUES ('50', '01', '测试车', '运输体积', '500，600', null, '0', '109100');
INSERT INTO `ab_tc_config` VALUES ('51', '01', '测试车', '长宽高', '80，60，100', null, '0', '109100');
INSERT INTO `ab_tc_config` VALUES ('52', '01', '测试车', '重量限制', '5-20吨', '', '0', '109100');
INSERT INTO `ab_tc_config` VALUES ('53', '01', '测试车', '超公里费', '20', '', '0', '109100');
INSERT INTO `ab_tc_config` VALUES ('54', '01', '测试车', '夜间服务', '100', '', '0', '109100');
INSERT INTO `ab_tc_config` VALUES ('55', '01', '测试车', '起步公里', '80', '', '0', '109100');
INSERT INTO `ab_tc_config` VALUES ('56', '01', '测试车', '封面', '81afc1b7accd48d9a83260e863c3a284.jpg', null, '0', '109100');
INSERT INTO `ab_tc_config` VALUES ('57', '01', '测试车', '车长', '30,50,90', null, '0', '109100');

-- ----------------------------
-- Table structure for `ab_tc_express_order`
-- ----------------------------
DROP TABLE IF EXISTS `ab_tc_express_order`;
CREATE TABLE `ab_tc_express_order` (
  `id` int(32) NOT NULL auto_increment COMMENT '订单ID',
  `sn` varchar(20) NOT NULL COMMENT '订单编号',
  `style` int(11) default NULL COMMENT '快递订单模式，1：悬赏，2：招标',
  `car` varchar(100) default NULL COMMENT '车辆类型，小面包、金杯车',
  `start_price` decimal(10,2) default '0.00' COMMENT '起步价',
  `over_unit_price` decimal(10,2) default '0.00' COMMENT '超公里单价',
  `night_price` decimal(10,2) default '0.00' COMMENT '夜间服务费',
  `goods_type` varchar(10) default NULL COMMENT '货物类型',
  `send_name` varchar(20) default NULL COMMENT '送货姓名',
  `send_phone` varchar(20) default NULL COMMENT '送货电话',
  `send_addr` varchar(256) default NULL COMMENT '送货地址',
  `rcv_name1` varchar(20) default NULL COMMENT '收货姓名',
  `rcv_phone1` varchar(20) default NULL COMMENT '收货电话',
  `rcv_addr1` varchar(256) default NULL COMMENT '收货地址',
  `rcv_name2` varchar(20) default NULL COMMENT '收货姓名',
  `rcv_phone2` varchar(20) default NULL COMMENT '收货电话',
  `rcv_addr2` varchar(256) default NULL COMMENT '收货地址',
  `rcv_name3` varchar(20) default NULL COMMENT '收货姓名',
  `rcv_phone3` varchar(20) default NULL COMMENT '收货电话',
  `rcv_addr3` varchar(256) default NULL COMMENT '收货地址',
  `rcv_name4` varchar(20) default NULL COMMENT '收货姓名',
  `rcv_phone4` varchar(20) default NULL COMMENT '收货电话',
  `rcv_addr4` varchar(256) default NULL COMMENT '收货地址',
  `rcv_name5` varchar(20) default NULL COMMENT '收货姓名',
  `rcv_phone5` varchar(20) default NULL COMMENT '收货电话',
  `rcv_addr5` varchar(256) default NULL COMMENT '收货地址',
  `kilo` int(11) default NULL COMMENT '行驶里程',
  `send_time` varchar(50) default NULL COMMENT '送货时间',
  `pay_type` int(11) default NULL COMMENT '支付方式',
  `huidan_price` decimal(10,2) default NULL COMMENT '回单价格',
  `service_price` decimal(10,2) default NULL COMMENT '服务费用',
  `total_price` decimal(10,2) default NULL COMMENT '最终费用',
  `user_id` varchar(100) default NULL,
  `lift` varchar(10) default NULL COMMENT '有无电梯，1有，0无',
  `more_money` decimal(10,2) default NULL COMMENT '悬赏金额',
  `weight` int(11) default NULL COMMENT '货物重量（公斤）',
  `people` int(11) default NULL COMMENT '跟车人数',
  `cart` varchar(10) default NULL COMMENT '需要小推车（1需要，其他不需要）',
  `shenhe` char(1) default NULL,
  `min_price` varchar(10) default NULL,
  `max_price` varchar(10) default NULL,
  `remark` varchar(500) default NULL,
  `pic_send` varchar(500) default NULL,
  `pic_arrival` varchar(500) default NULL,
  `goods_mount` varchar(500) default NULL,
  `goods_volumn` varchar(500) default NULL,
  `cc` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_tc_order_id` (`id`),
  KEY `FK_USER_ID` (`user_id`),
  KEY `TC_ORDER_INDEX` (`sn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_tc_express_order[同城快递订单详情表]';

-- ----------------------------
-- Records of ab_tc_express_order
-- ----------------------------
INSERT INTO `ab_tc_express_order` VALUES ('1', 'TC20150125000001', '2', '金杯车', '70.00', '4.00', '0.00', '粉末', '123', '1233333', '天府广场-地铁站', 'dfgdfg', '5566666', '成都成华sm城市广场', null, null, null, null, null, null, null, null, null, null, null, null, '6', '2015-01-25 05:03:52', '2', '3.00', '10.00', '173.00', null, '1', '50.00', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `ab_tc_express_order` VALUES ('3', 'TC20150130000001', '2', '金杯车', '70.00', '4.00', '35.00', '液体', 'aaa', '11111', 'df(河南郑州永辉超市店)', 'bbbbb', '222222', '郑州航空工业管理学院-东南门', null, null, null, null, null, null, null, null, null, null, null, null, '18', '2015-01-30 02:00:00', '2', '3.00', '10.00', '240.00', null, '1', '50.00', '20', '1', '1', null, null, null, null, null, null, null, null, null);
INSERT INTO `ab_tc_express_order` VALUES ('4', 'TC20150211000001', '1', '人工直达', '30.00', '3.00', '25.00', '粉末', '235434', '234523452345', '平顶山银行郑州分行', '2345', '23452345', '郑州汽车客运总站', null, null, null, null, null, null, null, null, null, null, null, null, '14', '2015-02-12 00:00:00', '2', '0.00', '0.00', '137.00', null, '0', '0.00', '33', '0', '1', null, null, null, null, null, null, null, null, null);
INSERT INTO `ab_tc_express_order` VALUES ('5', 'TC20150410000001', '1', '小面包', '50.00', '4.00', '25.00', '液体', '请问', '13265440120', '郑州汽车客运总站', '分为', '13265440120', '河南省郑州市金水区地方税务局', '', '', '', '', '', '', '', '', '', '', '', '', '10', '2015-04-09 00:00:00', '1', '3.00', '0.00', '148.00', null, '0', '0.00', '22', '1', '1', null, '', '', null, null, null, null, null, null);
INSERT INTO `ab_tc_express_order` VALUES ('6', 'TC20150413000001', '1', '小面包', '50.00', '4.00', '25.00', '液体', '舞蹈服', '13265440120', '郑州大学(新校区)', '23の', '13265440120', '郑州长途汽车南站', '钱4', '13265440120', '郑州市二七区郑州大学第一附属医院', '', '', '', '', '', '', '', '', '', '31', '2015-04-14 02:00:00', '2', '3.00', '0.00', '232.00', null, '0', '0.00', '3000', '1', '1', '1', '', '', null, null, null, '3', '25', null);
INSERT INTO `ab_tc_express_order` VALUES ('7', 'TC20150413000002', '2', '小货车', '70.00', '4.00', '0.00', '粉末', '问问', '13265440120', '郑州汽车客运总站', '问5', '13265440120', '郑州汽车客运总站', '', '', '', '', '', '', '', '', '', '', '', '', '1', '2015-04-13 22:33:40', '1', '3.00', '0.00', '70.00', null, '0', '0.00', '500', '0', '', null, '100', '200', null, null, null, '444', '444', null);
INSERT INTO `ab_tc_express_order` VALUES ('8', 'TC826947531', '2', '金杯车', '70.00', '4.00', '0.00', '液体', 'ht', '55555555555', '河南省郑州市中原区西十里铺南路12号', 'we', '11111111111', '中华路南段258', '', '', '', '', '', '', '', '', '', '', '', '', '52', '2015-04-15 00:00:00', '1', '0.00', '0.00', '248.00', null, '1', '0.00', '100', '1', '1', '1', '100', '200', '其他无', null, null, '2', '20', '0');
INSERT INTO `ab_tc_express_order` VALUES ('9', 'TC748361952', '1', '测试车', '60.00', '20.00', '0.00', '液体', 'zsan', '18081949861', '管城区紫荆山路华林新时代广场1731室(商城路交叉口向北100米路西)', '文三', '13612345678', '金水东路88号（金水东路与心怡路交汇处东北）', '', '', '', '', '', '', '', '', '', '', '', '', '11', '2015-04-17 15:08:13', '1', '3.00', '0.00', '1043.00', null, '1', '0.00', '500', '0', '1', '0', '', '', '如：我要跟车，货物是5箱德国啤酒，比较珍贵。要送到地下，限制高度，所以金杯一定要平顶的', null, null, '3', '1000', '50');
INSERT INTO `ab_tc_express_order` VALUES ('10', 'TC325149678', '1', '人工直达', '30.00', '3.00', '0.00', '粉末', 'test', '13012345678', '郑州市巩义市新华路87号佰盛购物广场(新华路)B1层', 'hj', '18081946521', '河南省郑州市通泰路66号正弘山10号楼2单元401室,ip互联(备)第1510号', '', '', '', '', '', '', '', '', '', '', '', '', '86', '2015-04-22 08:00:00', '3', '0.00', '0.00', '338.00', null, '0', '0.00', '11', '1', '1', '1', '', '', '无', null, null, '1', '11', '0');
INSERT INTO `ab_tc_express_order` VALUES ('11', 'TC485293167', '1', '人工直达', '30.00', '3.00', '0.00', '粉末', 'test003', '15612345678', '二七区大学南路8号万达百货1层', 'test005', '15212345678', '建设西路187号泰隆大厦1层', '', '', '', '', '', '', '', '', '', '', '', '', '7', '2015-04-18 15:34:56', '1', '0.00', '0.00', '90.00', null, '1', '0.00', '7', '0', '1', null, '', '', '如：我要跟车，货物是5箱德国啤酒，比较珍贵。要送到地下，限制高度，所以金杯一定要平顶的', null, null, '1', '100', '0');
INSERT INTO `ab_tc_express_order` VALUES ('12', 'TC215834697', '2', '测试车', '60.00', '20.00', '0.00', '易碎品', '测试', '18081948751', '农业路建材市场4区17号', '无名', '18702851456', '广州市大学城外环西路100号', '', '', '', '', '', '', '', '', '', '', '', '', '1477', '2015-04-21 11:00:00', '1', '3.00', '10.00', '0.00', null, '0', '0.00', '100', '0', '1', null, '1000', '1500', '如：我要跟车，货物是5箱德国啤酒，比较珍贵。要送到地下，限制高度，所以金杯一定要平顶的', null, null, '50', '1000', '90');

-- ----------------------------
-- Table structure for `ab_user_address`
-- ----------------------------
DROP TABLE IF EXISTS `ab_user_address`;
CREATE TABLE `ab_user_address` (
  `id` varchar(32) NOT NULL COMMENT '编码',
  `name` varchar(50) default NULL COMMENT '名称',
  `addr` varchar(500) default NULL COMMENT '详细地址描述',
  `order_num` int(11) default NULL COMMENT '排序数值',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `jd` varchar(50) default NULL,
  `wd` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_cityarea[用户的常用地址表]';

-- ----------------------------
-- Records of ab_user_address
-- ----------------------------
INSERT INTO `ab_user_address` VALUES ('70aab42aeb4846c3bf42479d84ec72c1', '王学文', '深圳市', '2', '3587e3ef3971402db97584b6ade9d999', null, null);

-- ----------------------------
-- Table structure for `ab_user_cityarea`
-- ----------------------------
DROP TABLE IF EXISTS `ab_user_cityarea`;
CREATE TABLE `ab_user_cityarea` (
  `id` varchar(32) NOT NULL COMMENT '编码',
  `user_id` varchar(32) default NULL COMMENT '用户ID',
  `city_id` varchar(32) default NULL COMMENT '区域ID',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_user_cityarea_02` (`city_id`),
  KEY `FK_ab_user_cityarea_01` (`user_id`),
  CONSTRAINT `FK_ab_user_cityarea_01` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_ab_user_cityarea_02` FOREIGN KEY (`city_id`) REFERENCES `ab_cityarea` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_user_cityarea[区域人员信息表]';

-- ----------------------------
-- Records of ab_user_cityarea
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_user_vipcard`
-- ----------------------------
DROP TABLE IF EXISTS `ab_user_vipcard`;
CREATE TABLE `ab_user_vipcard` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `cardid` varchar(32) default NULL COMMENT '卡ID',
  `cardname` varchar(50) default NULL COMMENT '卡名称',
  `kme` double(8,2) default NULL COMMENT '卡面额',
  `srvnum` int(11) default NULL COMMENT '服务次数',
  `user_id` varchar(32) default NULL COMMENT '用户ID',
  `user_name` varchar(50) default NULL COMMENT '用户名称',
  `loginid` varchar(20) default NULL COMMENT '用户登录号',
  `gmsj` varchar(20) default NULL COMMENT '购买时间',
  `zffs` char(1) default NULL COMMENT '支付方式[0-充值直扣、1-支付宝]',
  `zfzt` char(1) default NULL COMMENT '支付状态[0-未支付、1-已支付]',
  `remark` varchar(500) default NULL COMMENT '备注',
  PRIMARY KEY  (`id`),
  KEY `FK_ab_user_vipcard_02` (`cardid`),
  KEY `FK_ab_user_vipcard_01` (`user_id`),
  CONSTRAINT `FK_ab_user_vipcard_01` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_ab_user_vipcard_02` FOREIGN KEY (`cardid`) REFERENCES `ab_vipcard` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_user_vipcard[用户购买VIP会员卡记录]';

-- ----------------------------
-- Records of ab_user_vipcard
-- ----------------------------

-- ----------------------------
-- Table structure for `ab_vipcard`
-- ----------------------------
DROP TABLE IF EXISTS `ab_vipcard`;
CREATE TABLE `ab_vipcard` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `cardname` varchar(50) default NULL COMMENT '卡名称',
  `kme` double(8,2) default NULL COMMENT '卡面额',
  `srvnum` int(11) default NULL COMMENT '服务次数',
  `sxh` int(11) default NULL COMMENT '顺序号',
  `remark` varchar(500) default NULL COMMENT '备注',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='ab_vipcard[VIP会员卡信息表]';

-- ----------------------------
-- Records of ab_vipcard
-- ----------------------------
INSERT INTO `ab_vipcard` VALUES ('64880f321de541b48bd30d36e485f97b', '金卡', '100.00', '100', '1', null);
INSERT INTO `ab_vipcard` VALUES ('c86ac3641e5b49e89501a39f37b9da50', '普卡', '50.00', '50', '2', null);

-- ----------------------------
-- Table structure for `cms_content`
-- ----------------------------
DROP TABLE IF EXISTS `cms_content`;
CREATE TABLE `cms_content` (
  `id` int(11) NOT NULL auto_increment COMMENT '内容ID',
  `lbid` int(11) default NULL COMMENT '类别ID',
  `title` varchar(255) default NULL COMMENT '标题',
  `short_title` varchar(255) default NULL COMMENT '简短标题',
  `seo_keyword` varchar(255) default NULL COMMENT '关键字',
  `seo_desc` varchar(255) default NULL COMMENT 'seo描述',
  `author` varchar(100) default NULL COMMENT '作者',
  `origin` varchar(255) default NULL COMMENT '来源',
  `origin_url` varchar(255) default NULL COMMENT '来源链接',
  `link` varchar(255) default NULL COMMENT '外部链接',
  `summary` varchar(255) default NULL COMMENT '摘要',
  `txt` longtext COMMENT '内容',
  `is_publication` char(1) default NULL COMMENT '是否发布',
  `release_date` varchar(20) default NULL COMMENT '发布日期',
  `title_img` varchar(255) default NULL COMMENT '标题图片',
  `title_medium` varchar(255) default NULL COMMENT '中图',
  `title_thumbnail` varchar(255) default NULL COMMENT '缩略图',
  `is_recommend` char(1) default NULL COMMENT '是否推荐[0-否、1-是]',
  `views_day` int(11) default NULL COMMENT '日访问数',
  `comments_day` int(11) default NULL COMMENT '日评论数',
  `downloads_day` int(11) default NULL COMMENT '日下载数',
  `ups_day` int(11) default NULL COMMENT '日顶数',
  `clicksratio` int(11) default '0' COMMENT '点击量',
  PRIMARY KEY  (`id`),
  KEY `FK_cms_content_01` (`lbid`),
  CONSTRAINT `FK_cms_content_01` FOREIGN KEY (`lbid`) REFERENCES `cms_nrfl` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='cms-内容表';

-- ----------------------------
-- Records of cms_content
-- ----------------------------
INSERT INTO `cms_content` VALUES ('1', '1', '关于本地生活', '关于我们', '关于我们', '关于我们', '本地生活网', '本地生活网', '本地生活网', null, '关于我们', '<p><span style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\">&nbsp; &nbsp; 生活网是全国最大的在线服务交易平台，由原《重庆晚报》首席记者朱明跃创办于2006年，服务交易品类涵盖创意设计、网站建设、网络营销、文案策划、生活服务等多种行业。猪八戒网有百万服务商正在出售服务，为企业、公共机构和个人提供定制化的解决方案，将创意、智慧、技能转化为商业价值和社会价值。2011年猪八戒网获得IDG千万级美金投资，并被评选为中国2011年度“最佳商业模式十强”企业。 重庆铁三角网络有限公司（简称“铁三角网络”）系重庆猪八戒网络有限公司下属合资子公司。负责经营猪八戒网工程设计资询频道，是全国最大最专业的工程技术在线交易平台，并享有着工程技术界的“淘宝”美誉之称。</span><br style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\"/><span style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\">作为全国最大的文化创意和在线服务交易平台，平台内的交易对象为非标准、个性化的文化创意产品（方案），包含创意设计、网站建设、营销推广、文案策划、建筑装修、生活和商务服务等400余种现代服务领域。平台拥有1000万的公司级、工作室、个人共同组成的服务商，他们已经为25个国家和地区的100余万家企业、公共机构、个人提供了240万次定制化的解决方案。猪八戒网目前已推出中文网页版（www.zhubajie.com）、英文网页版（www.witmart.com）、手机APP端。 猪八戒网将“让天下人享受诚信服务，让诚信服务商享受成功”作为重点发展目标，致力于打造“诚信”为宗旨的在线服务交易氛围，建立并完善服务商信用体系，坚持齐头并举战略：</span><br style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\"/><span style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\">第一，能力战略。猪八戒网建立了服务商能力评价体系，让雇主更方便地找到有能力的服务商</span><br style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\"/><span style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\">第二，保障战略。猪八戒网在全行业率先推出雇主保障服务，含雇主保障、保证原创、三个月维护等消费者保障体系，让雇主在获得服务时，更有保障，解决他们的后顾之忧 企业文化</span><br style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\"/><span style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\">愿景：成为天下最诚信的服务交易平台、构建一个按需服务的能力社会、成为员工成长的舞台</span><br style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\"/><span style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\">使命：让天下人享受诚信服务</span><br style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\"/><span style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\">价值观：感恩客户、团队合作、行必诚言必信、以万变应不变</span></p>', '1', null, null, null, null, '1', null, null, null, null, '0');
INSERT INTO `cms_content` VALUES ('2', '1', '本地生活招聘', '人才招聘', '人才招聘', '人才招聘', '本地生活网', '本地生活网', null, null, '本地生活网', '<p><span style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\">职位：网络销售</span><br style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\"/><span style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\">要求：无学历要示，有网络销售经验者优先</span><br style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\"/><span style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\">工作地点：广东省东莞市</span><br style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\"/><span style=\"color: rgb(0, 0, 0); font-family: Simsun; font-size: 14px; line-height: 20px; white-space: normal;\">资薪：面议</span></p>', '1', null, null, null, null, '1', null, null, null, null, '0');
INSERT INTO `cms_content` VALUES ('3', '1', '联系我们', '联系我们', '联系我们', null, '本地生活网', '本地生活网', null, null, null, null, '1', null, null, null, null, '0', null, null, null, null, '0');
INSERT INTO `cms_content` VALUES ('4', '1', '免责声明', '免责声明', '免责声明', null, '本地生活网', '本地生活网', null, null, null, null, '1', null, null, null, null, '0', null, null, null, null, '0');
INSERT INTO `cms_content` VALUES ('5', '1', '网站地图', '网站地图', '网站地图', null, '本地生活网', '本地生活网', null, null, null, null, '1', null, null, null, null, '0', null, null, null, null, '0');
INSERT INTO `cms_content` VALUES ('6', '1', '友情链接', '友情链接', '友情链接', null, '本地生活网', '本地生活网', null, null, null, null, '1', null, null, null, null, '0', null, null, null, null, '0');
INSERT INTO `cms_content` VALUES ('7', '1', '帮助中心', '帮助中心', '帮助中心', '帮助中心', '易生活网', '易生活网', '易生活网', null, '易生活网', '<p>易生活网</p>', '1', null, null, null, null, '1', null, null, null, null, '0');
INSERT INTO `cms_content` VALUES ('8', '1', '测试', '测试副标题', '测试', '测试', '3452', '245', '2345', null, '2345', '<p>阿萨德发斯蒂芬</p>', '1', '2015-04-19 23:24:45', 'cebdc1f75e654139bfdbba50af4a56ee.jpg', null, null, '1', null, null, null, null, '0');

-- ----------------------------
-- Table structure for `cms_nrfl`
-- ----------------------------
DROP TABLE IF EXISTS `cms_nrfl`;
CREATE TABLE `cms_nrfl` (
  `id` int(11) NOT NULL auto_increment COMMENT '类别ID',
  `lbmc` varchar(255) default NULL COMMENT '类别名称',
  `seo_keyword` varchar(255) default NULL COMMENT '关键字',
  `seo_desc` varchar(255) default NULL COMMENT 'seo描述',
  `sxh` int(11) default NULL COMMENT '顺序号',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='cms_nrfl[cms_nrfl-内容分类]';

-- ----------------------------
-- Records of cms_nrfl
-- ----------------------------
INSERT INTO `cms_nrfl` VALUES ('1', '网站内容说明', '网站内容说明', '网站内容说明', '1');
INSERT INTO `cms_nrfl` VALUES ('2', '帮助文档', '帮助文档', '3阿萨德发生的房间爱', '2');
INSERT INTO `cms_nrfl` VALUES ('3', '友情链接', '友情链接', '友情链接', '3');
INSERT INTO `cms_nrfl` VALUES ('4', '关于本地生活', '关于本地生活', '关于本地生活', '3');
INSERT INTO `cms_nrfl` VALUES ('5', '本地生活招聘', '本地生活招聘', '本地生活招聘', '4');
INSERT INTO `cms_nrfl` VALUES ('6', '免责声明', '免责声明', '免责声明', '5');
INSERT INTO `cms_nrfl` VALUES ('7', '网站地图', '网站地图', '网站地图', '6');

-- ----------------------------
-- Table structure for `log_user_deposit`
-- ----------------------------
DROP TABLE IF EXISTS `log_user_deposit`;
CREATE TABLE `log_user_deposit` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `userid` varchar(32) default NULL COMMENT '用户ID',
  `loginid` varchar(50) default NULL COMMENT '用户名称',
  `mc` varchar(50) default NULL COMMENT '姓名',
  `createtime` varchar(20) default NULL COMMENT '充值时间',
  `czje` double(8,2) default NULL COMMENT '充值金额',
  `status` char(1) default NULL COMMENT '充值结果[1-成功、0-失败]',
  `remark` varchar(500) default NULL COMMENT '备注',
  PRIMARY KEY  (`id`),
  KEY `FK_log_user_deposit_01` (`userid`),
  CONSTRAINT `FK_log_user_deposit_01` FOREIGN KEY (`userid`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='log_user_deposit[用户充值记录表]';

-- ----------------------------
-- Records of log_user_deposit
-- ----------------------------

-- ----------------------------
-- Table structure for `log_user_sms`
-- ----------------------------
DROP TABLE IF EXISTS `log_user_sms`;
CREATE TABLE `log_user_sms` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `userid` varchar(32) default NULL COMMENT '用户ID',
  `username` varchar(50) default NULL COMMENT '用户名称',
  `phone` varchar(20) default NULL COMMENT '手机号',
  `smscontent` varchar(200) default NULL COMMENT '内容',
  `sendtime` varchar(20) default NULL COMMENT '发送时间',
  `sendstatus` char(1) default NULL COMMENT '发送结果[1-成功、0-失败]',
  `sendtype` varchar(20) default NULL COMMENT '发送类型',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='log_user_sms[短信发送记录]';

-- ----------------------------
-- Records of log_user_sms
-- ----------------------------

-- ----------------------------
-- Table structure for `log_user_vipcard`
-- ----------------------------
DROP TABLE IF EXISTS `log_user_vipcard`;
CREATE TABLE `log_user_vipcard` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `cardtype` char(1) default NULL COMMENT '消费类型',
  `remainsrvnum` int(11) default NULL COMMENT '剩余服务次数',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) default NULL COMMENT '用户名称',
  `order_id` varchar(32) default NULL COMMENT '订单ID',
  `srvmemo` varchar(500) default NULL COMMENT '消费说明',
  `srvtime` varchar(20) default NULL COMMENT '消费时间',
  PRIMARY KEY  (`id`),
  KEY `FK_log_user_vipcard_01` (`user_id`),
  CONSTRAINT `FK_log_user_vipcard_01` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='log_user_vipcard[用户消费VIP卡录]';

-- ----------------------------
-- Records of log_user_vipcard
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_area_subject_ptf`
-- ----------------------------
DROP TABLE IF EXISTS `sys_area_subject_ptf`;
CREATE TABLE `sys_area_subject_ptf` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `subject_id` varchar(32) default NULL COMMENT '分类ID',
  `area_id` varchar(32) NOT NULL COMMENT '商圈ID',
  `srvmoney` decimal(14,2) default NULL COMMENT '跑腿费',
  PRIMARY KEY  (`id`),
  KEY `FK_sys_area_subject_ptf_01` (`subject_id`),
  KEY `FK_sys_area_subject_ptf_02` (`area_id`),
  CONSTRAINT `FK_sys_area_subject_ptf_01` FOREIGN KEY (`subject_id`) REFERENCES `ab_subject` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_sys_area_subject_ptf_02` FOREIGN KEY (`area_id`) REFERENCES `ab_cityarea` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='sys_area_subject_ptf[分类-商圈-跑腿费信息表]';

-- ----------------------------
-- Records of sys_area_subject_ptf
-- ----------------------------
INSERT INTO `sys_area_subject_ptf` VALUES ('0b2ec4a2b3b246149bd7b013394e68ab', '100110021001', '109100101', '5.00');
INSERT INTO `sys_area_subject_ptf` VALUES ('2bc624aa33b1460c9f3e96f9bd0f6b3a', '10011002', '109100', '5.00');
INSERT INTO `sys_area_subject_ptf` VALUES ('333d01a2134f4732b4cfc6aab1b7a4c4', '100110011001', '109100100', '15.10');
INSERT INTO `sys_area_subject_ptf` VALUES ('3759e9382d28462e9c7d48df5aab0156', '100110021001', '109100102', '3.00');
INSERT INTO `sys_area_subject_ptf` VALUES ('4b5cc2833c824b0d9aac2420d6d67c39', '100110011001', '109100', '2.00');
INSERT INTO `sys_area_subject_ptf` VALUES ('531a251ca9ee46e5a3375eee202b7d20', '100110021000', '109100100', '15.20');
INSERT INTO `sys_area_subject_ptf` VALUES ('5e478bd66bee49438cb00843dc3e62e7', '10011001', '109100', '4.00');
INSERT INTO `sys_area_subject_ptf` VALUES ('76379b451d8442778a9ce424e8f2b516', '100110021000', '109100101', '10.00');
INSERT INTO `sys_area_subject_ptf` VALUES ('81d71dbaf54d443d8bf1c7641db51451', '100110021000', '109100102', '4.00');
INSERT INTO `sys_area_subject_ptf` VALUES ('886d87d1f4194c5a8deaf79b4bbaa601', '10011001', '100', '2.00');
INSERT INTO `sys_area_subject_ptf` VALUES ('932a50d437d04a38bb8e944417ebb1b6', '100110011001', '109100100', '15.30');
INSERT INTO `sys_area_subject_ptf` VALUES ('cc223e28c38f49f199b8d49b0306ace7', '100110021001', '109100100', '15.40');
INSERT INTO `sys_area_subject_ptf` VALUES ('d9d112ab0b924a01bd533af59250a344', '100110011002', '109100', '2.00');
INSERT INTO `sys_area_subject_ptf` VALUES ('db56189ad3794c4890edbd299f2e05e7', '100110021001', '109100100', '15.50');
INSERT INTO `sys_area_subject_ptf` VALUES ('dc3904339d5c4b57b32091f8b2ab808d', '100110011000', '109100', '3.00');

-- ----------------------------
-- Table structure for `sys_config`
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` char(2) NOT NULL COMMENT '主键',
  `yt` varchar(20) default NULL COMMENT '用途',
  `c1` varchar(200) default NULL COMMENT '字段A',
  `c2` varchar(200) default NULL COMMENT '字段B',
  `c3` varchar(200) default NULL COMMENT '字段C',
  `c4` varchar(200) default NULL COMMENT '字段D',
  `c5` varchar(200) default NULL COMMENT '字段E',
  `c6` varchar(200) default NULL COMMENT '字段F',
  `c7` varchar(200) default NULL COMMENT '字段G',
  `c8` varchar(200) default NULL COMMENT '字段H',
  `c9` varchar(200) default NULL COMMENT '字段I',
  `c10` varchar(200) default NULL COMMENT '字段J',
  `c11` varchar(200) default NULL COMMENT '字段K',
  `c12` varchar(200) default NULL COMMENT '字段L',
  `c13` varchar(200) default NULL COMMENT '字段M',
  `c14` varchar(200) default NULL COMMENT '字段N',
  `c15` varchar(200) default NULL COMMENT '字段O',
  `c16` varchar(200) default NULL COMMENT '字段P',
  `c17` varchar(200) default NULL COMMENT '字段Q',
  `c18` varchar(200) default NULL COMMENT '字段R',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='sys_config[系统配置]';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('01', '短信服务接口', 'http://web.c123.cn', '50095235', '15989378642', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_config` VALUES ('02', '支付宝即时交易', '2088411613067801', 'bjlcf9wwpr5b04awzusme42cl924tq1y', '支付宝即时交易支付宝即时交易', '23', null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_config` VALUES ('03', '电子邮箱配置', 'smtp.163.com', '25', 'lijinweiatzzhu@163.com', 'li', null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_config` VALUES ('04', '邮箱认证', 'http://www.25wang.net', '80', '/', '【爱帮网】邮件认证', null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_config` VALUES ('05', '服务距离跑腿费', '3', '4.5', '8', '8', '服务距', null, null, null, null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for `sys_cz_tx`
-- ----------------------------
DROP TABLE IF EXISTS `sys_cz_tx`;
CREATE TABLE `sys_cz_tx` (
  `id` varchar(300) default NULL,
  `tradeno` varchar(300) default NULL,
  `type` varchar(30) default NULL,
  `result` varchar(30) default NULL,
  `totalfee` varchar(300) default NULL,
  `userid` varchar(300) default NULL,
  `orderid` mediumtext,
  `num` int(10) default NULL,
  `dipositid` varchar(96) default NULL,
  `yhkid` varchar(600) default NULL,
  `txyhkid` varchar(600) default NULL,
  `txyhknum` varchar(600) default NULL,
  `zfbaddress` varchar(600) default NULL,
  `txtype` varchar(30) default NULL,
  `mc` varchar(600) default NULL,
  `time` text,
  `totime` text,
  `pc` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_cz_tx
-- ----------------------------
INSERT INTO `sys_cz_tx` VALUES ('041fd7a390e54ddb862382603d3b3df1', '1425310151661', '2', '1', '0.01', '3587e3ef3971402db97584b6ade9d999', '6a41cba13ac14737bfa5250be8dab864', '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('0ef93fa00d67437c97181b67d9217cae', '1425483313095', '0', '1', '10.09', '3587e3ef3971402db97584b6ade9d999', null, '0', 'c443f191bc7e4bcd86cfc07fa4a4cfcd', '1', null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('10223df3a3df471bb1f31f062810a521', '1425397864477', '0', '1', '0.01', '614f7c3964cd49d4b8f1eedb1fc76adc', null, '1', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('1bdf3e8339174a19a57d4fff294d7c0c', '1425397366125', '0', '0', 'a', '614f7c3964cd49d4b8f1eedb1fc76adc', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('217924f9061143249d5c7b6a1e93128d', '1425828927298', '0', '0', '0.01', '3587e3ef3971402db97584b6ade9d999', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('22cbfc30ea4a487fb46f3024bea66c4a', '1425475541948', '0', '0', '', '3587e3ef3971402db97584b6ade9d999', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('307cf8dbcad24510a5a766ac1216df7b', '1425480767154', '0', '1', '100', '3587e3ef3971402db97584b6ade9d999', null, '0', '8cbb6f1ef4ad47c8b02f3fe598c5f00e', '0', null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('4af8b4819f564d0f90c121c890353ac0', '1425829101836', '0', '0', '0.01', 'b18147d5101f435a833f2b2b5240ffb3', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('4f88008fcb6440e4a5868403ba0d22e4', '1425479525319', '0', '0', '0.01', '3587e3ef3971402db97584b6ade9d999', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('5de6e222ce3040e498d21e209569a53d', '1425562929856', '1', '1', '0.1', '3587e3ef3971402db97584b6ade9d999', null, '0', null, null, null, null, 'GCCGNN@hotmail.com', '0', '用户01', '2015-03-05 21:42:12', '2015-03-05 23:01:32', null);
INSERT INTO `sys_cz_tx` VALUES ('61da6d656a764d9fa3d4bc755cb34085', '1425479612311', '0', '0', '0.01', '3587e3ef3971402db97584b6ade9d999', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('62052102205c4726ae3710b60dc829ab', '1425483174207', '0', '1', '10.99', '614f7c3964cd49d4b8f1eedb1fc76adc', null, '0', '969d800e134c4c3092531c0dee310c1f', '2', null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('75443a5aadf34cb688856873cf0f0ae0', '1425828345841', '0', '0', '0.01', '3587e3ef3971402db97584b6ade9d999', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('7567b199a5a34dd689cc84dbfe09b27c', '1425397366125', '0', '0', 'a', '3587e3ef3971402db97584b6ade9d999', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('781a449d1e364443b6624e72de35405c', '1425829024752', '0', '0', '0.01', '3587e3ef3971402db97584b6ade9d999', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('7bab604cf9254baf8413ec6b603b2d67', '1425906773549', '0', '0', '0.01', 'admin', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('859aeed2b94742899d2125a7ff174148', '1425564153721', '0', '0', '0.01', 'admin', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('863ade34ee844be98240a3e4031750c4', '1425480960442', '0', '0', '0.01', 'admin', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('933efb99f3d54043a597aa2883c7706e', '1425749469046', '0', '0', '0.01', 'admin', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('97f100e7cf7a417281dc66496a2d3a4f', '1425563122478', '1', '0', '0.01', '3587e3ef3971402db97584b6ade9d999', null, '0', null, null, '4', '666666666666666666666', null, '1', '用户01', '2015-03-05 21:46:49', null, null);
INSERT INTO `sys_cz_tx` VALUES ('a1aef179eca34c779048bf3eaa7b121f', '1425567106643', '1', '0', '0.01', '614f7c3964cd49d4b8f1eedb1fc76adc', null, '0', null, null, null, null, 'GCCGNN@hotmail.com', '0', '耿叁', '2015-03-05 22:52:06', null, '1426049140874');
INSERT INTO `sys_cz_tx` VALUES ('a4e4177dd0854978b6af05cb3ff1d1c5', '1425473619386', '0', '0', '', '3587e3ef3971402db97584b6ade9d999', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('a60cad94c77f49b3bf1fd7678d099fba', '1425569743006', '1', '0', '0.02', '614f7c3964cd49d4b8f1eedb1fc76adc', null, '0', null, null, null, null, '923422203@qq.com', '0', '耿畅畅', '2015-03-05 23:36:01', null, '1426049140874');
INSERT INTO `sys_cz_tx` VALUES ('af7de244d97f450a8396241150d28bc8', '1425565420013', '0', '1', '90', '614f7c3964cd49d4b8f1eedb1fc76adc', null, '0', 'b39b10cb900a4f1896ffef7cdbbd707e', '1', null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('b2dea8ec1beb4bd286de0953fd1c3be9', '1425480980239', '0', '0', '0.01', 'admin', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('b3027a9575bb4dc3b05ecd097edc0bab', '1425397083991', '0', '1', '0.01', '3587e3ef3971402db97584b6ade9d999', null, '1', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('be2cb36962584d32a5d4103d81f5c655', '1425564150588', '0', '0', '0.01', 'admin', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('c32cbd04733049208a38fe02db278dd5', '', '', '0', '', '3587e3ef3971402db97584b6ade9d999', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('c8c4abd17fe348feaf5cbe272874f817', '1425906752672', '0', '0', '0.01', 'admin', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('ded6e8f828ad4486bb01f9c66e20b9e9', '1425481253213', '0', '0', '0.01', 'admin', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('e00550edcf684ad9866889d85510f3fa', '1425664103271', '0', '0', '0.01', 'admin', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('e078c99a8f2142269e98fbd1d4b7b9d7', '1425825904147', '0', '0', '0.01', 'admin', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('f672dee1af104986a45f55c7dbdc38a2', '1425829084038', '0', '0', '0.01', '614f7c3964cd49d4b8f1eedb1fc76adc', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('f8bc33c47ad0474b82a9d8fd113c2f6f', '1425909423861', '0', '0', '0.01', 'admin', null, '0', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_cz_tx` VALUES ('4fbe1c273d714599a02a6e9ad28c70d2', '1426691618608', '0', '0', '4', 'bf8300c4608147a1b468123d10d91a23', null, null, null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for `sys_menu`
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `mc` varchar(20) default NULL COMMENT '功能菜单名称',
  `typeid` char(1) default NULL COMMENT '类型',
  `menu_url` varchar(200) default NULL COMMENT '地址',
  `p_id` varchar(32) default NULL COMMENT '上级id',
  `seq_num` int(11) default NULL COMMENT '顺序号',
  `exta` varchar(50) default NULL COMMENT '扩展A',
  `extb` varchar(50) default NULL COMMENT '扩展B',
  `css` varchar(50) default NULL COMMENT 'css样式',
  `remark` varchar(500) default NULL COMMENT '备注',
  PRIMARY KEY  (`id`),
  KEY `FK_sys_menu_02` (`p_id`),
  CONSTRAINT `FK_sys_menu_02` FOREIGN KEY (`p_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='sys_menu[菜单功能]';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('101', '基础管理', null, null, 'ROOT', '2', null, null, 'user', null);
INSERT INTO `sys_menu` VALUES ('101100', '城市商圈管理', null, '/admin/city/tree', '101', '1', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101101', '城市分类管理', null, '/admin/subject/treeCity', '101', '3', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101102', '分类导航管理', null, '/admin/subject/tree', '101', '2', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101105', '短信服务配置', null, '/admin/config/editsms', '105', '6', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101106', '支付宝配置', null, '/admin/config/editalipay', '105', '7', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101107', '电子邮箱配置', null, '/admin/config/editemail', '105', '8', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101108', '区域员工管理', null, '/admin/user/listcityuser', '101', '10', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101109', '用户信息管理', null, '/admin/user/list', '105', '3', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101110', '区域管理员管理', null, '/admin/user/listcitymng', '101', '12', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101111', '跑腿费设置', null, '/admin/subject/tree_sau', '101', '13', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101112', '服务费设置', null, '/admin/config/editfwf', '105', '9', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101113', '积分兑换商品管理', '', '/admin/integral/listgoods', '101', '14', '', '', '', '');
INSERT INTO `sys_menu` VALUES ('101114', '积分兑换记录管理', '', '/admin/integral/listexchange', '101', '15', '', '', '', '');
INSERT INTO `sys_menu` VALUES ('101115', '留言管理', '', '/admin/leave/listleave', '101', '16', '', '', '', '');
INSERT INTO `sys_menu` VALUES ('101116', '评价管理', '', '/admin/appraise/listappraise', '101', '17', '', '', '', '');
INSERT INTO `sys_menu` VALUES ('101122', '提现管理', null, '/admin/user/listtx', '101', '20', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101123', '充值管理', null, '/admin/user/listcz', '101', '21', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('101124', '提现自动处理配置', null, '/admin/config/edittxconfig', '105', '9', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('102', '企业管理', null, null, 'ROOT', '3', null, null, 'news', null);
INSERT INTO `sys_menu` VALUES ('102100', '企业信息审核', null, '/admin/merchant/listsjsh', '102', '1', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('102101', '企业信息查询', null, '/admin/merchant/listsjcx', '102', '2', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('102102', '企业商品审核', null, '/admin/merchant/listsjspsh', '102', '3', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('102103', '企业商品查询', null, '/admin/merchant/listsjspcx', '102', '4', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('102105', '订单信息查询', null, '/admin/dd/listddcx', '102', '6', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('102108', '订单仲裁管理', '', '/admin/chargeback/listallcb', '102', '8', '', '', '', '');
INSERT INTO `sys_menu` VALUES ('102109', '企业认证审核', null, '/admin/rz/listsj_rz', '102', '5', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('102110', '司机认证审核', null, '/admin/rz/listywy_rz', '104', '2', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('102111', '企业销售统计', null, '/admin/merchant/list_sj', '102', '7', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('102112', '商家销售排名', null, '/admin/merchant/list_xspm', '102', '12', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('103', '会员管理', null, null, 'ROOT', '4', null, null, 'config', null);
INSERT INTO `sys_menu` VALUES ('103100', '会员信息查询', null, '/admin/hy/listhycx', '103', '1', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('103101', '会员充值查询', null, '/admin/hy/listhyczcx', '103', '2', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('103102', '会员卡类型管理', '', '/admin/card/listcardtype', '103', '3', '', '', '', '');
INSERT INTO `sys_menu` VALUES ('103103', '会员卡管理', '', '/admin/card/listcard', '103', '4', '', '', '', '');
INSERT INTO `sys_menu` VALUES ('103104', '会员卡查询', '', '/admin/card/listallusercard', '103', '5', '', '', '', '');
INSERT INTO `sys_menu` VALUES ('103105', '会员卡消费查询', '', '/admin/card/listcardexpense', '103', '6', '', '', '', '');
INSERT INTO `sys_menu` VALUES ('103106', '会员卡补助记录', '', '/admin/card/listaddexpense', '103', '7', '', '', '', '');
INSERT INTO `sys_menu` VALUES ('103107', '会员认证审核', null, '/admin/rz/listhy_rz', '103', '7', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('104', '司机管理', null, null, 'ROOT', '5', null, null, 'user', null);
INSERT INTO `sys_menu` VALUES ('104101', '司机信息查询', null, '/admin/ywy/listywycx', '104', '2', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('104103', '司机接单统计', null, '/admin/ywy/listywy_tj', '104', '3', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('105', '系统设置', null, null, 'ROOT', '6', null, null, 'config', null);
INSERT INTO `sys_menu` VALUES ('105100', '角色管理', null, '/admin/role/list', '105', '1', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('105101', '角色授权管理', null, '/admin/role/listrole', '105', '2', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('105102', '用户密码重置', null, '/admin/user/listusermmcz', '105', '3', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('105104', '区域管理员跑腿费设置', null, '/admin/subject/list_citysubject', '105', '5', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('105105', '文章分类管理', null, '/admin/cms/listfl', '105', '9', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('105107', '文章管理', null, '/admin/cms/listcontent', '105', '10', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('106', '同城快递', null, null, 'ROOT', '5', null, null, 'config', null);
INSERT INTO `sys_menu` VALUES ('106100', '车辆种类管理', null, '/admin/tc/cars', '106', '2', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('106101', '货物类型管理', null, '/admin/tc/goods', '106', '3', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('106102', '其他费用管理', null, '/admin/tc/others', '106', '4', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('106103', '订单管理', null, '/admin/tc/orders', '106', '5', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('107', '订单管理', null, null, 'ROOT', '7', null, null, 'config', null);
INSERT INTO `sys_menu` VALUES ('10700', '添加订单', null, '/admin/order/add', '107', '1', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('10701', '未指派订单', null, '/admin/order/weiZhiPai', '107', '2', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('10702', '正在执行订单', null, '/admin/order/doing', '107', '3', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('10703', '已完成订单', null, '/admin/order/wanCheng', '107', '4', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('10704', '已取消订单', null, '/admin/order/quXiao', '107', '5', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('10705', '订单汇总', null, '/admin/order/all', '107', '6', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('10708', '控制中心百度', null, '/admin/order/controllerBaiDu', '107', '7', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('10709', '支付宝充值后台测试', null, '/admin/order/zfbcz', '107', '8', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('10710', '订单时间分布', null, '/admin/order/ordertime', '107', '9', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('10711L', '订单来源分布', null, '/admin/order/ordersource', '107', '10', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('ROOT', '系统功能', null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for `sys_mobile_blank`
-- ----------------------------
DROP TABLE IF EXISTS `sys_mobile_blank`;
CREATE TABLE `sys_mobile_blank` (
  `id` varchar(32) NOT NULL,
  `userid` varchar(32) NOT NULL COMMENT '用户信息',
  `mobile` varchar(32) NOT NULL COMMENT '黑名单号码',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_mobile_blank
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` varchar(32) NOT NULL COMMENT '角色id',
  `jsmc` varchar(20) default NULL COMMENT '角色名称',
  `sxh` tinyint(4) default NULL COMMENT '顺序号',
  `ddesc` varchar(200) default NULL COMMENT '角色说明',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='sys_role[角色]';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('101', '管理员', '1', '343434');
INSERT INTO `sys_role` VALUES ('103', '区域管理员', '3', 'asdfasdfaf');
INSERT INTO `sys_role` VALUES ('104', '一般员工', '4', null);
INSERT INTO `sys_role` VALUES ('105', '店铺商家', '5', null);
INSERT INTO `sys_role` VALUES ('106', '司机', '6', null);
INSERT INTO `sys_role` VALUES ('107', '网站用户', '7', null);

-- ----------------------------
-- Table structure for `sys_role_menu`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `role_id` varchar(32) default NULL COMMENT '角色id',
  `menu_id` varchar(32) NOT NULL COMMENT '功能id',
  PRIMARY KEY  (`id`),
  KEY `FK_sys_role_menu_02` (`menu_id`),
  KEY `FK_sys_user_role_01` (`role_id`),
  CONSTRAINT `FK_sys_role_menu_02` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_sys_user_role_01` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='sys_role_menu[角色功能]';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('06d51dee61e54a9d9448adc302972d54', '103', '103101');
INSERT INTO `sys_role_menu` VALUES ('09754f9a97db44178973caced3b71c39', '103', '103105');
INSERT INTO `sys_role_menu` VALUES ('0fab2445711b42b1b874b1845314edbb', '103', '102102');
INSERT INTO `sys_role_menu` VALUES ('113d6cda820c4ae1baa6843cff9ab0c3', '101', '101102');
INSERT INTO `sys_role_menu` VALUES ('13b09bc13e754b159113a840ae0f4769', '101', '101100');
INSERT INTO `sys_role_menu` VALUES ('1bf57f2fb27140279de35876d3a26f59', '104', '103102');
INSERT INTO `sys_role_menu` VALUES ('1ed4cf70f84241769f2aef03c6d9db41', '103', '102100');
INSERT INTO `sys_role_menu` VALUES ('270229a657b04fdd9a0aafe865032e9e', '101', '105107');
INSERT INTO `sys_role_menu` VALUES ('27d0a93abd914b6e84ba20543a6ffd30', '101', '101112');
INSERT INTO `sys_role_menu` VALUES ('2e687c6dc34d4ef1ae6e20df82c6a16a', '104', '102111');
INSERT INTO `sys_role_menu` VALUES ('2ee25e1914e64d58ac235f66e90f750a', '104', '104103');
INSERT INTO `sys_role_menu` VALUES ('327b33c0940a4f1082d14bb68fadc70f', '101', '105102');
INSERT INTO `sys_role_menu` VALUES ('37807098f9b94ea4b71bee86578acee0', '101', '101105');
INSERT INTO `sys_role_menu` VALUES ('408a2d0ab3df4fbbb7459cadaa1e2e3f', '101', '101109');
INSERT INTO `sys_role_menu` VALUES ('438561598609466a96ce69eb336acff7', '101', '102101');
INSERT INTO `sys_role_menu` VALUES ('4970bc13f89c4bfa9666e03b1409c099', '104', '102100');
INSERT INTO `sys_role_menu` VALUES ('4d277e9c4298430aa967577a9a43fd9e', '103', '104101');
INSERT INTO `sys_role_menu` VALUES ('4f3d2613f3ca48579d1753e0c81083cc', '103', '103102');
INSERT INTO `sys_role_menu` VALUES ('537d44819b7c434d8b60123173c8dcf3', '101', '102103');
INSERT INTO `sys_role_menu` VALUES ('561908587aa34951b6d8248d027836f9', '103', '103104');
INSERT INTO `sys_role_menu` VALUES ('5761c8ae3077425e8afeb31d0bcb60bf', '104', '104');
INSERT INTO `sys_role_menu` VALUES ('577600f5877c471a87789e0e6e676a68', '104', '104101');
INSERT INTO `sys_role_menu` VALUES ('586c16496d8045079cc88736ff2d2342', '103', '103106');
INSERT INTO `sys_role_menu` VALUES ('59f66ad3af1e4a9a9cc7b2fef260593d', '103', '102105');
INSERT INTO `sys_role_menu` VALUES ('5b5df42acb044ce8ad74202b63b47416', '101', '106101');
INSERT INTO `sys_role_menu` VALUES ('5be58bb6a5bd4ff1a305a8176b66ff1b', '104', '102');
INSERT INTO `sys_role_menu` VALUES ('612ce2ed35404dc09813827a53f38409', '104', '102110');
INSERT INTO `sys_role_menu` VALUES ('627e0708aebd4f71bdddd97b77bc11ae', '104', '102102');
INSERT INTO `sys_role_menu` VALUES ('634210a5a87f42c38921e588615e8552', '103', '103');
INSERT INTO `sys_role_menu` VALUES ('6b52985f07a8436f8d144e01b310135e', '101', '101101');
INSERT INTO `sys_role_menu` VALUES ('6ed3ce805f884f769374d2a2610243d4', '104', '103100');
INSERT INTO `sys_role_menu` VALUES ('76a78ddd54b0453e856e99d28691aeeb', '101', '105100');
INSERT INTO `sys_role_menu` VALUES ('8453fc1a4f1849cfbc7ce3dd73f34e16', '101', '102105');
INSERT INTO `sys_role_menu` VALUES ('8ca107442c6f4679aea36e0c8ea518d2', '104', '102103');
INSERT INTO `sys_role_menu` VALUES ('8e8f832a02174506a2667f3e8105ac17', '104', '103103');
INSERT INTO `sys_role_menu` VALUES ('8eda3b10d59645c19e161a5360f33010', '104', '103104');
INSERT INTO `sys_role_menu` VALUES ('92f1a33947ec49c2a96a48f0905864d6', '101', '102112');
INSERT INTO `sys_role_menu` VALUES ('9cee18270b264140a6878fc6a44d66e4', '103', '103103');
INSERT INTO `sys_role_menu` VALUES ('9d2cb5086b2e4f4abbf34c02fd35f663', '101', '105104');
INSERT INTO `sys_role_menu` VALUES ('9d6ccfa46b14435a96e6a5b33c80913d', '104', '103');
INSERT INTO `sys_role_menu` VALUES ('a0dd12db11894996aaa8f5dabe52df4b', '101', '105101');
INSERT INTO `sys_role_menu` VALUES ('a16e73a8794d4fd58d3b87ab566ab6cb', '101', '105');
INSERT INTO `sys_role_menu` VALUES ('aa82ff70618c41cc84dacba6cbb685c2', '103', '103100');
INSERT INTO `sys_role_menu` VALUES ('b3e97ff115594851988ae021619b52d9', '101', '106100');
INSERT INTO `sys_role_menu` VALUES ('b7106548526c45e3897aa93f94c32798', '103', '102110');
INSERT INTO `sys_role_menu` VALUES ('b756fb1f53e34af48b4235944887acdf', '104', '102105');
INSERT INTO `sys_role_menu` VALUES ('be65de20cd3b4fb5b099bab1976265f7', '101', '106103');
INSERT INTO `sys_role_menu` VALUES ('c3b8bc76bfa64c67946b56d7d481c78d', '103', '102');
INSERT INTO `sys_role_menu` VALUES ('cb3ce1db126e475c83db4c52db5fb13b', '103', '102103');
INSERT INTO `sys_role_menu` VALUES ('cd7dde56e1e14a6fb9d0f957c6a9595c', '101', '106102');
INSERT INTO `sys_role_menu` VALUES ('ce0ef351a31c40a7b450768fa970f2b5', '101', '101106');
INSERT INTO `sys_role_menu` VALUES ('d12425de598948588eb6fc00632b48d2', '103', '102112');
INSERT INTO `sys_role_menu` VALUES ('d36cfb1ea5a54e84a91f4b6a38ce6ab1', '104', '102101');
INSERT INTO `sys_role_menu` VALUES ('d39c323b9b3842c6b4e5d3fd5a15acfc', '101', '101124');
INSERT INTO `sys_role_menu` VALUES ('d3e6c6545c094e5aa54ff83a80717cce', '103', '104');
INSERT INTO `sys_role_menu` VALUES ('d67049721260430d9c7d4e0a1ff496da', '103', '102101');
INSERT INTO `sys_role_menu` VALUES ('da7410b60b904733a05f48689da9aeb7', '104', '103101');
INSERT INTO `sys_role_menu` VALUES ('dab9ab362dec408682a112bf2b315e6e', '103', '102109');
INSERT INTO `sys_role_menu` VALUES ('dce31e0b71f645648acec501b2665bb5', '103', '102108');
INSERT INTO `sys_role_menu` VALUES ('de6930bed815466da57964f93dc9202a', '101', '102');
INSERT INTO `sys_role_menu` VALUES ('de6d645860a746b693f779be4af8391c', '101', '101');
INSERT INTO `sys_role_menu` VALUES ('dfb0c2d7a5654e43bcdec09451c739a7', '101', '106');
INSERT INTO `sys_role_menu` VALUES ('e0d3bc6046d74ab88892521ff5be9d51', '104', '103105');
INSERT INTO `sys_role_menu` VALUES ('e63ebc621abd46e89345e1b8bc619c86', '104', '102109');
INSERT INTO `sys_role_menu` VALUES ('e6973432fc004fbf866dec7f629c95dd', '101', '101111');
INSERT INTO `sys_role_menu` VALUES ('eaece89e41e74d5b96b17eb44c931399', '101', '102111');
INSERT INTO `sys_role_menu` VALUES ('eddd7080930d42c2a514a0ee4841b8ec', '101', '101107');
INSERT INTO `sys_role_menu` VALUES ('ee3f6d286fdd41c7b896965b4885e5f7', '103', '102111');
INSERT INTO `sys_role_menu` VALUES ('ee7d3bc3229149478270c046930ddb06', '104', '103106');
INSERT INTO `sys_role_menu` VALUES ('f027ded245414a30ab183259b8c41d86', '103', '101');
INSERT INTO `sys_role_menu` VALUES ('f2f4d4d04bf549ad8cd6be8f1dc409d7', '104', '102108');
INSERT INTO `sys_role_menu` VALUES ('f63a4eebf0e14f74a4ca4e6d0a7c4e56', '101', '105105');
INSERT INTO `sys_role_menu` VALUES ('f873e60634794dca97ca2a5c452d4b9b', '103', '103107');
INSERT INTO `sys_role_menu` VALUES ('faf1856acbc9449aa5b947611e8a859d', '104', '103107');
INSERT INTO `sys_role_menu` VALUES ('fc4a4cbac4a544ba91d723258c92ec3a', '103', '101108');
INSERT INTO `sys_role_menu` VALUES ('fcf7ec2f9144472ba328f8cd3b6af9f8', '103', '104103');
INSERT INTO `sys_role_menu` VALUES ('fe5022e998e74407bc01239deae90d67', '101', '101110');

-- ----------------------------
-- Table structure for `sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `loginid` varchar(50) default NULL COMMENT '用户名',
  `loginpwd` varchar(50) default NULL COMMENT '密码',
  `zhzt` char(1) default '1' COMMENT '帐号状态[0-永久禁用、1-正常、2-分时禁用]',
  `jykssj` varchar(20) default NULL COMMENT '禁用开始时间',
  `jyjzsj` varchar(20) default NULL COMMENT '禁用截止时间',
  `mc` varchar(50) default NULL COMMENT '姓名',
  `sex` char(1) default NULL COMMENT '性别[0-男、1-女]',
  `birth` varchar(20) default NULL COMMENT '出生日期',
  `idcard` varchar(20) default NULL COMMENT '身份证号',
  `sfzzpzm` varchar(100) default NULL COMMENT '身份证照片正面',
  `sfzzpbm` varchar(100) default NULL COMMENT '身份证照片背面',
  `gzzp` varchar(100) default NULL COMMENT '半身免冠工作照',
  `yzbm` varchar(6) default NULL COMMENT '邮政编码',
  `xxdz` varchar(100) default NULL COMMENT '详细地址',
  `email` varchar(50) default NULL COMMENT '邮箱',
  `mobile` varchar(20) default NULL COMMENT '手机号',
  `tel` varchar(50) default NULL COMMENT '电话',
  `qq` varchar(50) default NULL COMMENT 'QQ',
  `khh` varchar(50) default NULL COMMENT '银行卡开户行',
  `khhkh` varchar(50) default NULL COMMENT '银行卡号',
  `jjllr` varchar(20) default NULL COMMENT '紧急联络人',
  `jjllrdh` varchar(50) default NULL COMMENT '联络人电话',
  `lng` varchar(20) default NULL COMMENT '经度',
  `lat` varchar(20) default NULL COMMENT '维度',
  `mapbusiness` varchar(100) default NULL COMMENT '地图位置',
  `certificateemail` char(1) default '0' COMMENT '邮箱认证状态[0-未认证、1-已认证、2-认证中]',
  `certificatemobile` char(1) default '0' COMMENT '手机认证状态[0-未认证、1-已认证、2-认证中]',
  `certificatecard` char(1) default '0' COMMENT '银行卡认证状态[0-未认证、1-已认证、2-认证中]',
  `certificatemoney` decimal(4,2) default '0.00' COMMENT '认证金额',
  `status` char(1) default '0' COMMENT '是否审核[1-已审核、0-未审核]',
  `is_account_enabled` char(1) default '1' COMMENT '是否可用[0-否、1-是]',
  `is_account_locked` char(1) default '1' COMMENT '是否锁定[0-否、1-是]',
  `locked_date` varchar(20) default NULL COMMENT '锁定时间',
  `login_date` varchar(20) default NULL COMMENT '最后登录日期',
  `login_ip` varchar(20) default NULL COMMENT '最后登录IP',
  `logo` varchar(100) default NULL COMMENT 'logo头像',
  `jifen` int(8) default '0' COMMENT '积分',
  `create_date` varchar(20) default NULL COMMENT '创建时间',
  `modify_date` varchar(20) default NULL COMMENT '修改时间',
  `role_id` varchar(32) default NULL COMMENT '角色ID',
  `role_name` varchar(50) default NULL COMMENT '角色名称',
  `p_id` varchar(32) default NULL COMMENT '上级管理员',
  `zhye` decimal(12,2) default '0.00' COMMENT '账户余额',
  `per` decimal(8,4) default '0.0000' COMMENT '佣金比例',
  `remark` varchar(500) default NULL COMMENT '备注',
  `mid` varchar(32) default NULL COMMENT '商户ID',
  `invite` varchar(32) default NULL COMMENT '邀请码',
  `zhlx` char(1) default NULL COMMENT '帐号类型',
  `jz` varchar(100) default NULL COMMENT '驾照',
  `ct` varchar(100) default NULL COMMENT '车头',
  `cs` varchar(100) default NULL COMMENT '车身',
  `xsz` varchar(100) default NULL COMMENT '行驶证',
  `sfz` varchar(100) default NULL COMMENT '身份证',
  `dtz` varchar(100) default NULL COMMENT '大头照',
  `szqy` varchar(100) default NULL COMMENT '所在地区',
  `zsxm` varchar(100) default NULL COMMENT '真实姓名',
  `kjdsjks` varchar(10) default NULL COMMENT '可接单时间开始',
  `kjdsjjz` varchar(10) default NULL COMMENT '可接单时间截止',
  `kjdqy` varchar(100) default NULL COMMENT '可接单区域',
  `xl` varchar(100) default NULL COMMENT '学历',
  `zy` varchar(100) default NULL COMMENT '职业',
  `jtgj` varchar(100) default NULL COMMENT '交通工具',
  `qymc` varchar(100) default NULL COMMENT '企业名称',
  `qyyx` varchar(50) default NULL COMMENT '企业邮箱',
  `qydz` varchar(100) default NULL COMMENT '企业地址',
  `yyzzzch` varchar(100) default NULL COMMENT '营业执照注册号',
  `yyzzsmj` varchar(100) default NULL COMMENT '营业执照扫描件',
  `zzjgdm` varchar(100) default NULL COMMENT '组织机构代码',
  `yyzsfzm` varchar(100) default NULL COMMENT '运营者身份证明',
  `yyzsfzh` varchar(100) default NULL COMMENT '运营者身份证号码',
  `yyzscsfzzp` varchar(100) default NULL COMMENT '运营者手持身份证照片',
  `sfrzzt` char(1) default '0' COMMENT '认证状态[0-未认证、1-已提交、2-审核通过、3-认证不通过]',
  `tjsj` varchar(20) default NULL COMMENT '提交时间',
  `shsj` varchar(20) default NULL COMMENT '审核时间',
  `shrid` varchar(32) default NULL COMMENT '审核人ID',
  `shrmc` varchar(50) default NULL COMMENT '审核人名称',
  `city_id` varchar(32) default NULL COMMENT '城市ID',
  `city_name` varchar(50) default NULL COMMENT '城市名称',
  `area_id` varchar(32) default NULL COMMENT '商圈ID',
  `area_name` varchar(50) default NULL COMMENT '商圈名称',
  `rate` decimal(14,4) default '0.0000' COMMENT '评价分数',
  `credittype` int(11) default '0' COMMENT '信用等级',
  `nc` varchar(255) default NULL COMMENT '昵称',
  `nl` varchar(255) default NULL COMMENT '年龄',
  `xz` varchar(255) default NULL COMMENT '星座',
  `sg` varchar(255) default NULL COMMENT '身高',
  `qm` varchar(255) default NULL COMMENT '签名',
  `yx` varchar(255) default NULL COMMENT '月薪',
  `dq` varchar(255) default NULL COMMENT '地区',
  `zhiye` varchar(255) default NULL COMMENT '职业',
  `qg` varchar(255) default NULL COMMENT '情感',
  `cy` varchar(255) default NULL COMMENT '抽烟',
  `hj` varchar(255) default NULL COMMENT '喝酒',
  PRIMARY KEY  (`id`),
  KEY `FK_Reference_36` (`role_id`),
  CONSTRAINT `FK_Reference_36` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('0efb7d509cbb40fb869bc72385c97f0c', '13673386255', 'cfcd208495d565ef66e7dff9f98764da', '1', null, null, '司机王梅', null, null, '34342134123412', null, null, null, null, '郑州市二七区郑州卫校人体生命科学馆', null, '13677338625', null, null, '工商银行', '2123412341234', null, null, '113.650113', '34.728176', '郑州卫校人体生命科学馆', '0', '1', '1', '0.37', '0', '1', '1', null, '2015-04-19 23:14:55', '127.0.0.1', '9d4fa5d0fddc46059295a42ae452adec.jpg', '0', '2015-04-17 23:27:23', null, '106', '业务员', null, '0.00', '0.0000', null, null, null, '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '0', null, null, null, null, '109100', '郑州', '109100100', '二七万达商圈', '0.0000', '0', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_user` VALUES ('15eb5bd8ec544237a1dd63ae567332b8', '123', 'cfcd208495d565ef66e7dff9f98764da', '1', null, null, '张明', null, null, '41236252352355', null, null, null, null, null, '123@163.com', '13123723462', null, '123412341234', null, null, null, null, null, null, null, '0', '0', '0', '0.00', '0', '1', '1', null, '2015-04-20 00:26:33', '127.0.0.1', null, '0', '2015-04-16 18:06:47', null, '103', '区域管理员', 'admin', '0.00', '0.0000', '123412341234', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '0', null, null, null, null, null, null, null, null, '0.0000', '0', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_user` VALUES ('25c7cb3315d449bfba173df2a70f972a', '13673386253', 'cfcd208495d565ef66e7dff9f98764da', '1', null, null, null, null, null, null, null, null, null, null, '郑州市二七区郑州汽车客运总站', null, '13673386255', null, null, null, null, null, null, '113.661272', '34.726396', '郑州汽车客运总站', '0', '1', '0', '0.31', '0', '1', '1', null, '2015-04-16 18:21:18', '127.0.0.1', null, '0', '2015-04-16 14:53:04', null, '105', '店铺商家', null, '0.00', '0.0000', null, '25c7cb3315d449bfba173df2a70f972a', null, '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '0', null, null, null, null, '109100', '郑州', '109100100', '二七万达商圈', '0.0000', '0', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_user` VALUES ('63bd3c719f1d4be98a8a396855408c73', '13673386252', 'cfcd208495d565ef66e7dff9f98764da', '1', null, null, '秦淮人家', null, null, '42341234123412341234', null, null, null, null, '郑州市管城回族区紫荆山路/东大街(路口)', '406091127@qq.com', '13673386255', null, null, '工商银行', '1236123641623461234', null, null, '113.688532', '34.754895', '紫荆山路/东大街(路口)', '1', '1', '1', '0.34', '0', '1', '1', null, '2015-04-20 00:28:56', '127.0.0.1', '9efd6c379bec47bb8609f2bc44281a25.jpg', '0', '2015-04-16 18:26:54', null, '105', '店铺商家', null, '0.00', '0.0000', null, '63bd3c719f1d4be98a8a396855408c73', null, '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, '秦淮人家', '406091127@qq.com', null, '123412342134', 'cc1ed5f8896742a2b4193f97cfc92605.jpg', '1123441234123', '张立海', '41152351236424236423', '633cb2c11bc44d2d892e4c7b036a721b.jpg', '1', '2015-04-17 20:59:28', null, null, null, '109100', '郑州', '109100100', '二七万达商圈', '0.0000', '0', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_user` VALUES ('9526be72c83b41c9895b1fd6fd7cf562', '13673386257', 'cfcd208495d565ef66e7dff9f98764da', '1', null, null, '九九鸭脖', null, null, null, null, null, null, '236426', '郑州市金水区金水路/未来路(路口)', '406091127@qq.com', '13673386255', null, null, null, null, null, null, '113.711341', '34.769248', '金水路/未来路(路口)', '1', '0', '0', '0.00', '0', '1', '1', null, '2015-04-17 23:47:02', '127.0.0.1', null, '0', '2015-04-17 22:14:26', null, '105', '店铺商家', null, '0.00', '0.0000', null, '9526be72c83b41c9895b1fd6fd7cf562', null, '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '2344123412341234', 'f5c5df0104bf4651aec1b08df5651edc.jpg', '123412342134', '1234213', '123412341234', '9d6e17de417947c5a85da6203e332f25.jpg', '2', '2015-04-17 22:19:13', null, '15eb5bd8ec544237a1dd63ae567332b8', '张明', '109100', '郑州', '109100100', '二七万达商圈', '0.0000', '0', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_user` VALUES ('981f1d141c5a4f2f93ab3dd58e8b7874', '13673386251', 'cfcd208495d565ef66e7dff9f98764da', '1', null, null, '用户亮亮', null, null, null, null, null, null, null, null, null, '13673386255', null, null, null, null, null, null, null, null, null, '0', '1', '0', '0.00', '0', '1', '1', null, '2015-04-20 01:16:23', '127.0.0.1', '01c317b9250c4804b0cb1d535fb5803c.jpg', '627', '2015-04-16 14:48:39', null, '107', '会员', null, '0.00', '0.0000', null, null, null, '1', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '0', null, null, null, null, '109100', '郑州', '109100101', '紫荆山商圈', '0.0000', '0', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_user` VALUES ('9c0c80812db048158e318add96a92333', 'kfyg', 'cfcd208495d565ef66e7dff9f98764da', '1', null, null, '开封员工', null, null, '2734275723475', null, null, null, null, null, 'kf@163.com', '23463461632', null, '2342345263456425', null, null, null, null, null, null, null, '0', '0', '0', '0.00', '0', '1', '1', null, null, null, null, '0', '2015-04-16 18:14:39', null, '104', '一般员工', '9ffd1868f8f04151b547e5b10592b298', '0.00', '0.0000', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '0', null, null, null, null, null, null, null, null, '0.0000', '0', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_user` VALUES ('9ffd1868f8f04151b547e5b10592b298', '111', 'cfcd208495d565ef66e7dff9f98764da', '1', null, null, '王亮', null, null, '123712341623', null, null, null, null, null, '0', '26346345666', null, '23623462354', null, null, null, null, null, null, null, '0', '0', '0', '0.00', '0', '1', '1', null, '2015-04-17 21:02:21', '127.0.0.1', null, '0', '2015-04-16 18:07:24', null, '103', '区域管理员', 'admin', '0.00', '0.0000', '263462354', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '0', null, null, null, null, null, null, null, null, '0.0000', '0', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_user` VALUES ('a7b25e9592514cc5bd78511af3dfdbe1', '13673386256', 'cfcd208495d565ef66e7dff9f98764da', '1', null, null, '华豫食府', null, null, null, null, null, null, null, '郑州市二七区长江路/碧云路(路口)', null, '13673386255', null, null, null, null, null, null, '113.671612', '34.71616', '长江路/碧云路(路口)', '0', '0', '0', '0.00', '0', '1', '1', null, '2015-04-19 23:59:35', '127.0.0.1', null, '0', '2015-04-17 22:06:22', null, '105', '店铺商家', null, '0.00', '0.0000', null, 'a7b25e9592514cc5bd78511af3dfdbe1', null, '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '0', null, null, null, null, '109100', '郑州', '109100100', '二七万达商圈', '0.0000', '0', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_user` VALUES ('admin', 'admin', 'cfcd208495d565ef66e7dff9f98764da', '1', '2015-05-03 22:41:15', '2015-07-03 22:41:19', '系统管理员', '0', null, '411527198411211123', null, null, null, null, null, 'admin@163.com', '13672372636', null, '234745723', null, null, null, null, null, null, null, '0', '0', '0', '0.00', '0', '1', '1', null, '2015-04-20 01:18:28', '127.0.0.1', null, '0', null, null, '101', '管理员', null, '63.00', '0.0000', null, null, null, '1', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '1', null, null, null, null, '109100', '郑州', '109100100', '二七万达商圈', '0.0000', '0', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_user` VALUES ('bb9e145d6d32452b9e1ceeea92abe023', '13673386250', 'cfcd208495d565ef66e7dff9f98764da', '1', null, null, null, null, null, null, null, null, null, null, null, null, '13673386255', null, null, null, null, null, null, null, null, null, '0', '1', '0', '0.00', '0', '1', '1', null, '2015-04-19 23:13:18', '127.0.0.1', '59ed9058381e467bb8834d82d9e953a2.jpg', '32', '2015-04-16 14:44:11', null, '107', '会员', null, '0.00', '0.0000', null, null, null, '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '0', null, null, null, null, '109100', '郑州', '109100100', '二七万达商圈', '0.0000', '0', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_user` VALUES ('c47d057779234df987fbd8dbe8a6c794', 'zzyg', 'cfcd208495d565ef66e7dff9f98764da', '1', null, null, '郑州员工A', null, null, '12361264361234', null, null, null, null, null, '0', '12346123641', null, '2364162346', null, null, null, null, null, null, null, '0', '0', '0', '0.00', '0', '1', '1', null, null, null, null, '0', '2015-04-16 18:09:14', null, '104', '一般员工', '15eb5bd8ec544237a1dd63ae567332b8', '0.00', '0.0000', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, '0', null, null, null, null, null, null, null, null, '0.0000', '0', null, null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Table structure for `sys_user_invite`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_invite`;
CREATE TABLE `sys_user_invite` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `loginid` varchar(50) default NULL COMMENT '邀请用户名',
  `invite_loginid` varchar(50) default NULL COMMENT '被邀请用户名',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user_invite
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_user_store`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_store`;
CREATE TABLE `sys_user_store` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `userid` varchar(32) default NULL COMMENT '用户ID',
  `lx` char(1) default NULL COMMENT '[0-店铺、1-商品、2-订单]',
  `fkid` varchar(32) default NULL COMMENT '收藏内容主键ID',
  `fkname` varchar(200) default NULL COMMENT '内容名称',
  `c1` varchar(100) default NULL COMMENT '扩展1',
  `c2` varchar(100) default NULL COMMENT '扩展2',
  PRIMARY KEY  (`id`),
  KEY `FK_sys_user_store_01` (`userid`),
  CONSTRAINT `FK_sys_user_store_01` FOREIGN KEY (`userid`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='sys_user_store[人员收藏]';

-- ----------------------------
-- Records of sys_user_store
-- ----------------------------
INSERT INTO `sys_user_store` VALUES ('01226f2239c7413f8d30285f14ad673c', '981f1d141c5a4f2f93ab3dd58e8b7874', '1', 'ca91afb739b44fcda7b117dd45d40d8f', '剁椒鱼头', null, null);
INSERT INTO `sys_user_store` VALUES ('4b9f3c5889cd402d8052187183d9d07a', '981f1d141c5a4f2f93ab3dd58e8b7874', '0', '63bd3c719f1d4be98a8a396855408c73', null, null, null);

-- ----------------------------
-- Table structure for `sys_user_subject`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_subject`;
CREATE TABLE `sys_user_subject` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) default NULL COMMENT '业务员ID',
  `subject_id` varchar(32) default NULL COMMENT '分类ID',
  `area_id` varchar(32) NOT NULL COMMENT '商圈ID',
  `srvmoney` double(8,2) default NULL COMMENT '跑腿费',
  PRIMARY KEY  (`id`),
  KEY `FK_sys_user_subject_01` (`subject_id`),
  KEY `FK_sys_user_subject_02` (`user_id`),
  KEY `FK_sys_user_subject_03` (`area_id`),
  CONSTRAINT `FK_sys_user_subject_01` FOREIGN KEY (`subject_id`) REFERENCES `ab_subject` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_sys_user_subject_02` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_sys_user_subject_03` FOREIGN KEY (`area_id`) REFERENCES `ab_cityarea` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='sys_user_subject[业务员服务分类信息表]';

-- ----------------------------
-- Records of sys_user_subject
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_user_zfpwd`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_zfpwd`;
CREATE TABLE `sys_user_zfpwd` (
  `id` varchar(32) NOT NULL,
  `userid` varchar(32) NOT NULL COMMENT '用户id',
  `zfpwd` varchar(32) default NULL COMMENT '支付密码',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user_zfpwd
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_yhka_item`
-- ----------------------------
DROP TABLE IF EXISTS `sys_yhka_item`;
CREATE TABLE `sys_yhka_item` (
  `id` varchar(600) default NULL,
  `yhkname` varchar(900) default NULL,
  `yhkcode` varchar(600) default NULL,
  `yhkuser` varchar(600) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_yhka_item
-- ----------------------------
INSERT INTO `sys_yhka_item` VALUES ('0', '中国工商银行', '6222111111111111102', '张安');
INSERT INTO `sys_yhka_item` VALUES ('1', '中国建设银行', '6217111111111111111', '张安');
INSERT INTO `sys_yhka_item` VALUES ('2', '中国农业银行', '6228888888888888888', '张安');

-- ----------------------------
-- Function structure for `uf_getmerworktime`
-- ----------------------------
DROP FUNCTION IF EXISTS `uf_getmerworktime`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `uf_getmerworktime`(weekdate INT,timeinfo VARCHAR(10),m_id VARCHAR(32)) RETURNS varchar(10) CHARSET utf8
begin
	DECLARE str_rtn VARCHAR(10);
	DECLARE cnt INT;
	DECLARE str_yyzt VARCHAR(1) DEFAULT '0';
	DECLARE str_fgzrxd VARCHAR(1) DEFAULT '0';
	
	SELECT business_status INTO @str_yyzt from ab_merchant WHERE id = m_id;
	IF @str_yyzt='1' THEN
		SELECT chk_fgzrxd INTO @str_fgzrxd FROM ab_merchant WHERE id = m_id;
		SELECT COUNT(id) INTO @cnt FROM ab_merchant_yysj WHERE MID=m_id AND xq=weekdate AND sbsj<=timeinfo AND xbsj>=timeinfo;
		IF @cnt>0 THEN
			SET str_rtn='gzz';
		elseif @str_fgzrxd='1' and @cnt<1 then
			SET str_rtn='kyd';
		else
			SET str_rtn='ydy';
		end if;
	else
		SET str_rtn='ztyy';
	END IF;
	RETURN str_rtn;
    END
;;
DELIMITER ;
