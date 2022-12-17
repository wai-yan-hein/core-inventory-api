alter table vou_status 
change column updated_date updated_date timestamp not null default current_timestamp() on update current_timestamp();

ALTER TABLE `unit_relation` 
ADD COLUMN `updated_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP();

ALTER TABLE `stock_type` 
CHANGE COLUMN `updated_date` `updated_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP() ;

ALTER TABLE `location` 
ADD COLUMN `map_dept_id` INT NULL;

ALTER TABLE `stock` 
CHANGE COLUMN `updated_date` `updated_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP() ;

ALTER TABLE `sale_his_detail` 
DROP PRIMARY KEY,
ADD PRIMARY KEY (`sd_code`, `vou_no`, `dept_id`, `unique_id`);



set sql_safe_updates =0;
ALTER TABLE `transfer_his` 
CHANGE COLUMN `vou_no` `vou_no` VARCHAR(20) NOT NULL ;
ALTER TABLE `transfer_his_detail` 
CHANGE COLUMN `td_code` `td_code` VARCHAR(25) NOT NULL ;


update sale_his
set vou_no =concat('02-',vou_no);
update sale_his_detail
set vou_no =concat('02-',vou_no);
update ret_in_his
set vou_no =concat('02-',vou_no);
update ret_in_his_detail
set vou_no =concat('02-',vou_no);
update transfer_his
set vou_no =concat('02-',vou_no);
update transfer_his_detail
set vou_no =concat('02-',vou_no);