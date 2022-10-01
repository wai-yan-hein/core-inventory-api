ALTER TABLE `sale_his` 
CHANGE COLUMN `vou_date` `vou_date` DATETIME NOT NULL ;
ALTER TABLE `pur_his` 
CHANGE COLUMN `vou_date` `vou_date` DATETIME NOT NULL ;
ALTER TABLE `ret_in_his` 
CHANGE COLUMN `vou_date` `vou_date` DATETIME NOT NULL ;
ALTER TABLE `ret_out_his` 
CHANGE COLUMN `vou_date` `vou_date` DATETIME NOT NULL ;
ALTER TABLE `stock_in_out` 
CHANGE COLUMN `vou_date` `vou_date` DATETIME NOT NULL ;
CREATE TABLE `transfer_his` (
  `vou_no` varchar(15) NOT NULL,
  `created_by` varchar(15) NOT NULL,
  `created_date` timestamp NULL DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `vou_date` datetime NOT NULL,
  `ref_no` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `updated_by` varchar(15) DEFAULT NULL,
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `loc_code_from` varchar(15) NOT NULL,
  `loc_code_to` varchar(15) NOT NULL,
  `mac_id` int(11) NOT NULL,
  `comp_code` varchar(15) NOT NULL,
  PRIMARY KEY (`vou_no`,`comp_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `transfer_his_detail` (
  `td_code` varchar(20) NOT NULL,
  `vou_no` varchar(20) NOT NULL,
  `stock_code` varchar(10) DEFAULT NULL,
  `qty` float(20,3) NOT NULL,
  `wt` float(20,3) NOT NULL,
  `unit` varchar(10) NOT NULL,
  `unique_id` int(11) NOT NULL,
  `comp_code` varchar(15) NOT NULL DEFAULT '0010010',
  PRIMARY KEY (`td_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

#2022-07-09
ALTER TABLE stock_in_out_detail
ADD COLUMN `comp_code` VARCHAR(15) NOT NULL DEFAULT '0010010' AFTER `cur_code`;

ALTER TABLE sale_his_detail
ADD COLUMN `comp_code` VARCHAR(15) NOT NULL DEFAULT '0010010';

ALTER TABLE pur_his_detail
ADD COLUMN `comp_code` VARCHAR(15) NOT NULL DEFAULT '0010010';

ALTER TABLE ret_in_his_detail
ADD COLUMN `comp_code` VARCHAR(15) NOT NULL DEFAULT '0010010';

ALTER TABLE ret_out_his_detail
ADD COLUMN `comp_code` VARCHAR(15) NOT NULL DEFAULT '0010010';

ALTER TABLE op_his_detail
ADD COLUMN `comp_code` VARCHAR(15) NOT NULL DEFAULT '0010010';

ALTER TABLE transfer_his_detail
ADD COLUMN `comp_code` VARCHAR(15) NOT NULL DEFAULT '0010010';

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_transfer` AS select `th`.`vou_no` AS `vou_no`,`th`.`created_by` AS `created_by`,`th`.`created_date` AS `created_date`,`th`.`deleted` AS `deleted`,`th`.`vou_date` AS `vou_date`,`th`.`ref_no` AS `ref_no`,`th`.`remark` AS `remark`,`th`.`updated_by` AS `updated_by`,`th`.`updated_date` AS `updated_date`,`th`.`loc_code_from` AS `loc_code_from`,`th`.`loc_code_to` AS `loc_code_to`,`th`.`mac_id` AS `mac_id`,`th`.`comp_code` AS `comp_code`,`td`.`td_code` AS `td_code`,`td`.`stock_code` AS `stock_code`,`td`.`qty` AS `qty`,`td`.`wt` AS `wt`,`td`.`unit` AS `unit`,`td`.`unique_id` AS `unique_id`,`s`.`user_code` AS `user_code`,`s`.`stock_name` AS `stock_name`,`s`.`stock_type_code` AS `stock_type_code`,`s`.`category_code` AS `category_code`,`s`.`brand_code` AS `brand_code`,`s`.`rel_code` AS `rel_code`,`s`.`calculate` AS `calculate` from ((`transfer_his` `th` join `transfer_his_detail` `td` on(`th`.`vou_no` = `td`.`vou_no`)) join `stock` `s` on(`td`.`stock_code` = `s`.`stock_code`));

CREATE TABLE `trader_group` (
  `group_code` VARCHAR(15) NOT NULL,
  `comp_code` VARCHAR(15) NOT NULL,
  `user_code` VARCHAR(15) NULL,
  `group_name` VARCHAR(255) NULL,
  PRIMARY KEY (`group_code`, `comp_code`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

ALTER TABLE `trader`
ADD COLUMN `group_code` VARCHAR(15);

CREATE TABLE `bk_sale_his_detail` (
  `tran_id` bigint(25) NOT NULL AUTO_INCREMENT,
  `sd_code` varchar(20) NOT NULL,
  `vou_no` varchar(20) NOT NULL,
  `stock_code` varchar(10) DEFAULT NULL,
  `expire_date` date DEFAULT NULL,
  `qty` float(20,3) NOT NULL,
  `sale_wt` float(20,3) NOT NULL,
  `sale_unit` varchar(10) NOT NULL,
  `sale_price` float(20,3) NOT NULL,
  `sale_amt` float(20,3) NOT NULL,
  `loc_code` varchar(15) NOT NULL,
  `unique_id` int(11) NOT NULL,
  `comp_code` varchar(15) NOT NULL DEFAULT '0010010',
  `log_id` varchar(15) NOT NULL,
  PRIMARY KEY (`tran_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `bk_sale_his` (
  `log_id` bigint(25) NOT NULL AUTO_INCREMENT,
  `vou_no` varchar(25) DEFAULT NULL,
  `trader_code` varchar(15) NOT NULL,
  `saleman_code` varchar(45) DEFAULT NULL,
  `vou_date` datetime NOT NULL,
  `credit_term` date DEFAULT NULL,
  `cur_code` varchar(15) NOT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `vou_total` float(20,3) NOT NULL,
  `grand_total` float(20,3) NOT NULL,
  `discount` float(20,3) DEFAULT NULL,
  `disc_p` float(20,3) DEFAULT NULL,
  `tax_amt` float(20,3) DEFAULT NULL,
  `tax_p` float(20,3) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `created_by` varchar(15) NOT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `paid` float(20,3) DEFAULT NULL,
  `vou_balance` float(20,3) DEFAULT NULL,
  `updated_by` varchar(15) DEFAULT NULL,
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `comp_code` varchar(15) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `order_code` varchar(15) DEFAULT NULL,
  `reg_code` varchar(15) DEFAULT NULL,
  `loc_code` varchar(15) NOT NULL,
  `mac_id` varchar(15) NOT NULL,
  `session_id` int(11) DEFAULT NULL,
  `intg_upd_status` varchar(5) DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;
