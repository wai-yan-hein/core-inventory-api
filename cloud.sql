alter table vou_status 
change column updated_date updated_date timestamp not null default current_timestamp() on update current_timestamp();

ALTER TABLE `unit_relation` 
ADD COLUMN `updated_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP();
