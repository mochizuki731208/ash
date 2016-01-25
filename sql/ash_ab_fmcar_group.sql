CREATE DATABASE  IF NOT EXISTS `ash` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ash`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win64 (x86_64)
--
-- Host: localhost    Database: ash
-- ------------------------------------------------------
-- Server version	5.6.21-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ab_fmcar_group`
--

DROP TABLE IF EXISTS `ab_fmcar_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ab_fmcar_group` (
  `id` varchar(32) NOT NULL,
  `groupname` varchar(45) DEFAULT NULL COMMENT '分组名称',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`),
  KEY `ab_fmcar_group_fk2_idx` (`user_id`),
  CONSTRAINT `ab_fmcar_group_fk2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='车队分组';


ALTER TABLE `ash`.`ab_fmcar_user` 
ADD COLUMN `group_id` VARCHAR(32) NULL AFTER `user_id`;

/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ab_fmcar_group`
--

LOCK TABLES `ab_fmcar_group` WRITE;
/*!40000 ALTER TABLE `ab_fmcar_group` DISABLE KEYS */;
INSERT INTO `ab_fmcar_group` VALUES ('4287a915d475473f92d04788b9e37bba','多方位','a9f1eb7de66740bfbb8ef36339513a18'),('6114bc36463847809b1e045e081c5325','一个分组wXS','a9f1eb7de66740bfbb8ef36339513a18'),('9c46324548f14a498da20f3e364543d5','一个分组y','a9f1eb7de66740bfbb8ef36339513a18'),('b5ab2365190c4fd997cbfef7452cc3bb','臭豆腐','a9f1eb7de66740bfbb8ef36339513a18'),('eewew','一个分组','a9f1eb7de66740bfbb8ef36339513a18'),('fd4f4342403d4097a5d90eaeea7e5591','因厅','a9f1eb7de66740bfbb8ef36339513a18');
/*!40000 ALTER TABLE `ab_fmcar_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ab_fmcar_group_map`
--

DROP TABLE IF EXISTS `ab_fmcar_group_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ab_fmcar_group_map` (
  `id` varchar(32) NOT NULL,
  `group_id` varchar(32) NOT NULL,
  `fmcar_id` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fkab_fmcar_group_map_idx` (`group_id`),
  KEY `fkab_fmcar_group_map2_idx` (`fmcar_id`),
  CONSTRAINT `fkab_fmcar_group_map` FOREIGN KEY (`group_id`) REFERENCES `ab_fmcar_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fkab_fmcar_group_map2` FOREIGN KEY (`fmcar_id`) REFERENCES `ab_fmcar` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ab_fmcar_group_map`
--

LOCK TABLES `ab_fmcar_group_map` WRITE;
/*!40000 ALTER TABLE `ab_fmcar_group_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `ab_fmcar_group_map` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-10-02 11:18:57
