/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50016
Source Host           : localhost:3306
Source Database       : ash

Target Server Type    : MYSQL
Target Server Version : 50016
File Encoding         : 65001

Date: 2015-04-02 22:23:22
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ab_order_chargeback
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
