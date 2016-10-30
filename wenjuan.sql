/*
Navicat MySQL Data Transfer

Source Server         : l
Source Server Version : 50709
Source Host           : 127.0.0.1:3306
Source Database       : wenjuan

Target Server Type    : MYSQL
Target Server Version : 50709
File Encoding         : 65001

Date: 2016-05-31 12:43:00
*/

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for wj_admin_users
-- ----------------------------
DROP TABLE IF EXISTS `wj_admin_users`;
CREATE TABLE `wj_admin_users` (
  `id`       INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(20)      NOT NULL DEFAULT '',
  `password` VARCHAR(50)      NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`) USING BTREE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_admin_users
-- ----------------------------

-- ----------------------------
-- Table structure for wj_article
-- ----------------------------
DROP TABLE IF EXISTS `wj_article`;
CREATE TABLE `wj_article` (
  `id`           INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `author`       INT(11) UNSIGNED          DEFAULT NULL,
  `title`        VARCHAR(127)              DEFAULT NULL,
  `content`      MEDIUMTEXT,
  `time`         TIMESTAMP        NULL     DEFAULT CURRENT_TIMESTAMP,
  `avai`         TINYINT(1) UNSIGNED       DEFAULT '1'
  COMMENT '是否默认全部可见',
  `status`       TINYINT(4) UNSIGNED       DEFAULT '0'
  COMMENT '状态，0为正常，1为已删除',
  `follow_count` INT(10) UNSIGNED          DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `author` (`author`),
  CONSTRAINT `wj_article_ibfk_1` FOREIGN KEY (`author`) REFERENCES `wj_user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_article
-- ----------------------------

-- ----------------------------
-- Table structure for wj_article_enable
-- ----------------------------
DROP TABLE IF EXISTS `wj_article_enable`;
CREATE TABLE `wj_article_enable` (
  `article_id` INT(11) UNSIGNED NOT NULL,
  `name`       VARCHAR(31)      NOT NULL,
  `enable`     TINYINT(1) UNSIGNED DEFAULT '1'
  COMMENT '1为组不允许,2为用户不允许，3为组允许，4为用户允许',
  PRIMARY KEY (`article_id`, `name`),
  CONSTRAINT `wj_article_enable_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `wj_article` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_article_enable
-- ----------------------------

-- ----------------------------
-- Table structure for wj_comment_article
-- ----------------------------
DROP TABLE IF EXISTS `wj_comment_article`;
CREATE TABLE `wj_comment_article` (
  `id`         INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `comment_to` INT(11) UNSIGNED          DEFAULT NULL
  COMMENT '回复的评论id',
  `article`    INT(10) UNSIGNED          DEFAULT NULL
  COMMENT '评论的文章id',
  `commenter`  INT(11) UNSIGNED          DEFAULT NULL
  COMMENT '评论用户',
  `content`    TEXT COMMENT '评论内容(文字)',
  `pics`       TEXT COMMENT '图片路径，以冒号为分隔符',
  `time`       TIMESTAMP        NULL     DEFAULT CURRENT_TIMESTAMP,
  `status`     TINYINT(1) UNSIGNED       DEFAULT '0'
  COMMENT '1表示删除该评论，0表示正常',
  PRIMARY KEY (`id`),
  KEY `comment_to` (`comment_to`),
  KEY `article` (`article`),
  KEY `commenter` (`commenter`),
  CONSTRAINT `wj_comment_article_ibfk_1` FOREIGN KEY (`comment_to`) REFERENCES `wj_comment_article` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `wj_comment_article_ibfk_2` FOREIGN KEY (`article`) REFERENCES `wj_article` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `wj_comment_article_ibfk_3` FOREIGN KEY (`commenter`) REFERENCES `wj_user` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_comment_article
-- ----------------------------

-- ----------------------------
-- Table structure for wj_comment_diary
-- ----------------------------
DROP TABLE IF EXISTS `wj_comment_diary`;
CREATE TABLE `wj_comment_diary` (
  `id`         INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `comment_to` INT(255) UNSIGNED         DEFAULT NULL
  COMMENT '回复评论id',
  `commenter`  INT(11) UNSIGNED          DEFAULT NULL,
  `content`    TEXT COMMENT '评论内容',
  `diary`      INT(10) UNSIGNED          DEFAULT NULL
  COMMENT '评论的文章id',
  `time`       TIMESTAMP        NULL     DEFAULT CURRENT_TIMESTAMP,
  `status`     TINYINT(1) UNSIGNED       DEFAULT '0'
  COMMENT '1表示删除，0表示正常',
  PRIMARY KEY (`id`),
  KEY `diary` (`diary`),
  KEY `wj_comment_diary_ibfk_1` (`comment_to`),
  KEY `wj_comment_diary_ibfk_2` (`commenter`),
  CONSTRAINT `wj_comment_diary_ibfk_1` FOREIGN KEY (`comment_to`) REFERENCES `wj_comment_diary` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `wj_comment_diary_ibfk_2` FOREIGN KEY (`commenter`) REFERENCES `wj_user` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `wj_comment_diary_ibfk_3` FOREIGN KEY (`diary`) REFERENCES `wj_diary` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_comment_diary
-- ----------------------------

-- ----------------------------
-- Table structure for wj_config
-- ----------------------------
DROP TABLE IF EXISTS `wj_config`;
CREATE TABLE `wj_config` (
  `id`    INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`  VARCHAR(15)               DEFAULT NULL,
  `value` VARCHAR(31)               DEFAULT NULL,
  `extra` VARCHAR(31)               DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_config
-- ----------------------------

-- ----------------------------
-- Table structure for wj_diary
-- ----------------------------
DROP TABLE IF EXISTS `wj_diary`;
CREATE TABLE `wj_diary` (
  `id`           INT(10) UNSIGNED NOT NULL AUTO_INCREMENT
  COMMENT '日记表',
  `author`       INT(11) UNSIGNED          DEFAULT NULL,
  `content`      MEDIUMTEXT COMMENT '日记详情',
  `pics`         TEXT COMMENT '评论的图片和视频，以冒号分隔',
  `time`         TIMESTAMP        NULL     DEFAULT NULL,
  `followCount`  INT(11) UNSIGNED          DEFAULT '0'
  COMMENT '赞数',
  `status`       TINYINT(4) UNSIGNED       DEFAULT '0'
  COMMENT '0为正常，1为已删除',
  `follow_count` INT(11) UNSIGNED          DEFAULT '0',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_diary
-- ----------------------------

-- ----------------------------
-- Table structure for wj_feedback
-- ----------------------------
DROP TABLE IF EXISTS `wj_feedback`;
CREATE TABLE `wj_feedback` (
  `id`      INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `content` TEXT,
  `user`    INT(11) UNSIGNED          DEFAULT NULL
  COMMENT '发送反馈的人',
  `time`    TIMESTAMP        NULL     DEFAULT CURRENT_TIMESTAMP,
  `reply`   TEXT COMMENT '反馈回复',
  `status`  TINYINT(3) UNSIGNED       DEFAULT '0'
  COMMENT '0为正常，1为隐藏',
  PRIMARY KEY (`id`),
  KEY `user` (`user`),
  CONSTRAINT `wj_feedback_ibfk_1` FOREIGN KEY (`user`) REFERENCES `wj_user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_feedback
-- ----------------------------

-- ----------------------------
-- Table structure for wj_good
-- ----------------------------
DROP TABLE IF EXISTS `wj_good`;
CREATE TABLE `wj_good` (
  `id`          INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(31)               DEFAULT NULL,
  `pic`         TEXT COMMENT '商品图片',
  `need_score`  INT(11) UNSIGNED          DEFAULT '99999999'
  COMMENT '所需积分数',
  `description` TEXT,
  `time`        TIMESTAMP        NULL     DEFAULT CURRENT_TIMESTAMP
  COMMENT '商品发布时间',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_good
-- ----------------------------

-- ----------------------------
-- Table structure for wj_good_duihuan
-- ----------------------------
DROP TABLE IF EXISTS `wj_good_duihuan`;
CREATE TABLE `wj_good_duihuan` (
  `user_id` INT(10) UNSIGNED NOT NULL,
  `good_id` INT(10) UNSIGNED NOT NULL,
  `time`    TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `handled` TINYINT(1) UNSIGNED       DEFAULT '0'
  COMMENT '0为未处理，1为已处理',
  PRIMARY KEY (`user_id`, `good_id`, `time`),
  KEY `good_id` (`good_id`),
  CONSTRAINT `wj_good_duihuan_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `wj_user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `wj_good_duihuan_ibfk_2` FOREIGN KEY (`good_id`) REFERENCES `wj_good` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_good_duihuan
-- ----------------------------

-- ----------------------------
-- Table structure for wj_help
-- ----------------------------
DROP TABLE IF EXISTS `wj_help`;
CREATE TABLE `wj_help` (
  `id`      INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `title`   VARCHAR(63)               DEFAULT NULL,
  `content` TEXT,
  `time`    TIMESTAMP        NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status`  TINYINT(3) UNSIGNED       DEFAULT '0'
  COMMENT '0为正常，1为删除',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_help
-- ----------------------------

-- ----------------------------
-- Table structure for wj_history
-- ----------------------------
DROP TABLE IF EXISTS `wj_history`;
CREATE TABLE `wj_history` (
  `id`        INT(11)                 NOT NULL AUTO_INCREMENT,
  `chat_type` VARCHAR(100)
              CHARACTER SET utf8      NOT NULL,
  `from`      VARCHAR(100)
              CHARACTER SET utf8      NOT NULL,
  `to`        VARCHAR(100)
              CHARACTER SET utf8      NOT NULL,
  `content`   TEXT CHARACTER SET utf8 NOT NULL,
  `content_y` TEXT CHARACTER SET utf8 NOT NULL,
  `time`      TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `msg_type`  VARCHAR(100)
              CHARACTER SET utf8      NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin;

-- ----------------------------
-- Records of wj_history
-- ----------------------------

-- ----------------------------
-- Table structure for wj_log_user_login
-- ----------------------------
DROP TABLE IF EXISTS `wj_log_user_login`;
CREATE TABLE `wj_log_user_login` (
  `userid`     INT(11) UNSIGNED NOT NULL,
  `login_time` TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `duration`   INT(11)                   DEFAULT NULL
  COMMENT '登录时长',
  `ip`         VARCHAR(127)              DEFAULT NULL,
  PRIMARY KEY (`userid`, `login_time`),
  CONSTRAINT `wj_log_user_login_ibfk_1` FOREIGN KEY (`userid`) REFERENCES `wj_user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_log_user_login
-- ----------------------------

-- ----------------------------
-- Table structure for wj_log_user_view_article
-- ----------------------------
DROP TABLE IF EXISTS `wj_log_user_view_article`;
CREATE TABLE `wj_log_user_view_article` (
  `user_id`    INT(10) UNSIGNED NOT NULL,
  `article_id` INT(10) UNSIGNED NOT NULL,
  `time`       TIMESTAMP        NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '浏览时间，重复不计',
  PRIMARY KEY (`user_id`, `article_id`),
  KEY `article_id` (`article_id`),
  CONSTRAINT `wj_log_user_view_article_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `wj_user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `wj_log_user_view_article_ibfk_2` FOREIGN KEY (`article_id`) REFERENCES `wj_article` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_log_user_view_article
-- ----------------------------

-- ----------------------------
-- Table structure for wj_notifys
-- ----------------------------
DROP TABLE IF EXISTS `wj_notifys`;
CREATE TABLE `wj_notifys` (
  `id`          INT(11)             NOT NULL AUTO_INCREMENT,
  `platform`    VARCHAR(50)
                COLLATE utf8mb4_bin NOT NULL,
  `content`     VARCHAR(400)
                COLLATE utf8mb4_bin NOT NULL,
  `create_time` INT(11)             NOT NULL,
  `send_status` INT(11)             NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin;

-- ----------------------------
-- Records of wj_notifys
-- ----------------------------
INSERT INTO `wj_notifys` VALUES ('2', 'all', '测试通知', '1464230719', '1');

-- ----------------------------
-- Table structure for wj_score_history
-- ----------------------------
DROP TABLE IF EXISTS `wj_score_history`;
CREATE TABLE `wj_score_history` (
  `id`       INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id`  INT(10) UNSIGNED          DEFAULT NULL,
  `event_id` INT(11) UNSIGNED          DEFAULT NULL,
  `time`     TIMESTAMP        NULL     DEFAULT CURRENT_TIMESTAMP,
  `score`    INT(10) UNSIGNED          DEFAULT NULL
  COMMENT '兑换的积分值',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `event_id` (`event_id`),
  CONSTRAINT `wj_score_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `wj_user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `wj_score_history_ibfk_2` FOREIGN KEY (`event_id`) REFERENCES `wj_config` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_score_history
-- ----------------------------

-- ----------------------------
-- Table structure for wj_session
-- ----------------------------
DROP TABLE IF EXISTS `wj_session`;
CREATE TABLE `wj_session` (
  `user_id`  INT(10) UNSIGNED NOT NULL,
  `token_id` INT(10) UNSIGNED      DEFAULT NULL,
  `time`     TIMESTAMP        NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
)
  ENGINE = MyISAM
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_session
-- ----------------------------

-- ----------------------------
-- Table structure for wj_user
-- ----------------------------
DROP TABLE IF EXISTS `wj_user`;
CREATE TABLE `wj_user` (
  `id`               INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`             VARCHAR(31)      NOT NULL
  COMMENT '真实姓名',
  `password`         VARCHAR(15)      NOT NULL,
  `sex`              TINYINT(1) UNSIGNED       DEFAULT '1'
  COMMENT '0为男，1为女',
  `birthday`         DATETIME                  DEFAULT NULL
  COMMENT '出生日期',
  `degree`           TINYINT(1) UNSIGNED       DEFAULT NULL
  COMMENT '学历',
  `marriage`         TINYINT(1) UNSIGNED       DEFAULT NULL
  COMMENT '婚姻状况',
  `nickname`         VARCHAR(31)               DEFAULT NULL
  COMMENT '昵称',
  `graduated_from`   VARCHAR(63)               DEFAULT NULL
  COMMENT '毕业学校',
  `major`            VARCHAR(63)               DEFAULT NULL
  COMMENT '专业',
  `location`         VARCHAR(127)              DEFAULT NULL
  COMMENT '现居地',
  `interest`         VARCHAR(127)              DEFAULT NULL
  COMMENT '兴趣，英文逗号分割',
  `group`            VARCHAR(31)               DEFAULT NULL
  COMMENT '标签',
  `score`            INT(11) UNSIGNED NOT NULL DEFAULT '0'
  COMMENT '积分',
  `realname`         VARCHAR(15)               DEFAULT NULL
  COMMENT '手机号',
  `subscribe`        TINYINT(3) UNSIGNED       DEFAULT '0'
  COMMENT '0为未，1为已订阅新商品提醒',
  `allow_article`    TINYINT(3) UNSIGNED       DEFAULT '1'
  COMMENT '是否允许发布话题，0为不允许，1为允许',
  `view_follow_time` TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '用户最后浏览关注时间',
  `view_reply_time`  TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '用户最后回复评论时间',
  `extra`            TEXT COMMENT '额外字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `wj_user_tel_unique` (`realname`) USING BTREE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_user
-- ----------------------------

-- ----------------------------
-- Table structure for wj_user_follow_article
-- ----------------------------
DROP TABLE IF EXISTS `wj_user_follow_article`;
CREATE TABLE `wj_user_follow_article` (
  `userid`    INT(10) UNSIGNED NOT NULL,
  `articleid` INT(11) UNSIGNED NOT NULL,
  `time`      TIMESTAMP        NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userid`, `articleid`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_user_follow_article
-- ----------------------------

-- ----------------------------
-- Table structure for wj_user_follow_diary
-- ----------------------------
DROP TABLE IF EXISTS `wj_user_follow_diary`;
CREATE TABLE `wj_user_follow_diary` (
  `userid`  INT(10) UNSIGNED NOT NULL,
  `diaryid` INT(10) UNSIGNED NOT NULL,
  `time`    TIMESTAMP        NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userid`, `diaryid`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of wj_user_follow_diary
-- ----------------------------
DROP TRIGGER IF EXISTS `userfollowarticle_afterinsert`;
DELIMITER ;;
CREATE TRIGGER `userfollowarticle_afterinsert` AFTER INSERT ON `wj_user_follow_article` FOR EACH ROW UPDATE wj_article
SET follow_count = follow_count + 1
WHERE articleid = new.articleid
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `userfollowarticle_afterdelete`;
DELIMITER ;;
CREATE TRIGGER `userfollowarticle_afterdelete` AFTER DELETE ON `wj_user_follow_article` FOR EACH ROW UPDATE wj_article
SET follow_count = follow_count - 1
WHERE articleid = old.articleid
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `userfollowdiary_afterinsert`;
DELIMITER ;;
CREATE TRIGGER `userfollowdiary_afterinsert` AFTER INSERT ON `wj_user_follow_diary` FOR EACH ROW UPDATE wj_article
SET follow_count = follow_count + 1
WHERE diaryid = new.diaryid
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `userfollowdiary_afterdelete`;
DELIMITER ;;
CREATE TRIGGER `userfollowdiary_afterdelete` AFTER DELETE ON `wj_user_follow_diary` FOR EACH ROW UPDATE wj_article
SET follow_count = follow_count - 1
WHERE diaryid = old.diaryid
;;
DELIMITER ;
