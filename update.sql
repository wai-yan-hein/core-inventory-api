alter table vou_status
change column updated_date updated_date timestamp not null default current_timestamp() on update current_timestamp();

alter table unit_relation
add column updated_date timestamp not null default current_timestamp() on update current_timestamp();

alter table stock_type
change column updated_date updated_date timestamp not null default current_timestamp() ;

alter table location
add column map_dept_id int null;

alter table stock
change column updated_date updated_date timestamp not null default current_timestamp() ;

alter table sale_his_detail
drop primary key,
add primary key (sd_code, vou_no, dept_id, unique_id);

set sql_safe_updates =0;
alter table transfer_his
change column vou_no vou_no varchar(20) not null ;
alter table transfer_his_detail
change column td_code td_code varchar(25) not null ;

alter table sale_his
add column vou_lock bit(1) not null default 0;

alter table pur_his
add column vou_lock bit(1) not null default 0;

alter table ret_in_his
add column vou_lock bit(1) not null default 0;

alter table ret_out_his
add column vou_lock bit(1) not null default 0;

alter table transfer_his
add column vou_lock bit(1) not null default 0;

alter table sale_his
change column vou_date vou_date datetime not null ;
alter table pur_his
change column vou_date vou_date datetime not null ;
alter table ret_in_his
change column vou_date vou_date datetime not null ;
alter table ret_out_his
change column vou_date vou_date datetime not null ;
alter table stock_in_out
change column vou_date vou_date datetime not null ;
create table transfer_his (
  vou_no varchar(15) not null,
  created_by varchar(15) not null,
  created_date timestamp null default null,
  deleted bit(1) default null,
  vou_date datetime not null,
  ref_no varchar(255) default null,
  remark varchar(255) default null,
  updated_by varchar(15) default null,
  updated_date timestamp not null default current_timestamp(),
  loc_code_from varchar(15) not null,
  loc_code_to varchar(15) not null,
  mac_id int(11) not null,
  comp_code varchar(15) not null,
  primary key (vou_no,comp_code)
) engine=innodb default charset=utf8mb3;

create table transfer_his_detail (
  td_code varchar(20) not null,
  vou_no varchar(20) not null,
  stock_code varchar(10) default null,
  qty float(20,3) not null,
  wt float(20,3) not null,
  unit varchar(10) not null,
  unique_id int(11) not null,
  comp_code varchar(15) not null default '0010010',
  primary key (td_code)
) engine=innodb default charset=utf8mb3;

#2022-07-09
alter table stock_in_out_detail
add column comp_code varchar(15) not null default '0010010' after cur_code;

alter table sale_his_detail
add column comp_code varchar(15) not null default '0010010';

alter table pur_his_detail
add column comp_code varchar(15) not null default '0010010';

alter table ret_in_his_detail
add column comp_code varchar(15) not null default '0010010';

alter table ret_out_his_detail
add column comp_code varchar(15) not null default '0010010';

alter table op_his_detail
add column comp_code varchar(15) not null default '0010010';

alter table transfer_his_detail
add column comp_code varchar(15) not null default '0010010';


create table trader_group (
  group_code varchar(15) not null,
  comp_code varchar(15) not null,
  user_code varchar(15) null,
  group_name varchar(255) null,
  primary key (group_code, comp_code))
engine = innodb
default character set = utf8;

alter table trader
add column group_code varchar(15);

create table bk_sale_his_detail (
  tran_id bigint(25) not null auto_increment,
  sd_code varchar(20) not null,
  vou_no varchar(20) not null,
  stock_code varchar(10) default null,
  expire_date date default null,
  qty float(20,3) not null,
  sale_wt float(20,3) not null,
  sale_unit varchar(10) not null,
  sale_price float(20,3) not null,
  sale_amt float(20,3) not null,
  loc_code varchar(15) not null,
  unique_id int(11) not null,
  comp_code varchar(15) not null default '0010010',
  log_id varchar(15) not null,
  primary key (tran_id)
) engine=innodb auto_increment=5 default charset=utf8mb3;

create table bk_sale_his (
  log_id bigint(25) not null auto_increment,
  vou_no varchar(25) default null,
  trader_code varchar(15) not null,
  saleman_code varchar(45) default null,
  vou_date datetime not null,
  credit_term date default null,
  cur_code varchar(15) not null,
  remark varchar(500) default null,
  vou_total float(20,3) not null,
  grand_total float(20,3) not null,
  discount float(20,3) default null,
  disc_p float(20,3) default null,
  tax_amt float(20,3) default null,
  tax_p float(20,3) default null,
  created_date datetime not null,
  created_by varchar(15) not null,
  deleted bit(1) default null,
  paid float(20,3) default null,
  vou_balance float(20,3) default null,
  updated_by varchar(15) default null,
  updated_date timestamp not null default current_timestamp(),
  comp_code varchar(15) not null,
  address varchar(255) default null,
  order_code varchar(15) default null,
  reg_code varchar(15) default null,
  loc_code varchar(15) not null,
  mac_id varchar(15) not null,
  session_id int(11) default null,
  intg_upd_status varchar(5) default null,
  reference varchar(255) default null,
  primary key (log_id)
) engine=innodb auto_increment=7 default charset=utf8mb3;

alter table trader_group
add column account varchar(15) null after intg_upd_status;

drop table if exists stock_op_value;
drop table if exists stock_op_value_log;
drop table if exists stock_report;
drop table if exists sys_prop;

alter table reorder_level
change column bal_unit bal_unit varchar(15) null ;

alter table region
add column dept_id int not null default 1 ;

alter table vou_status
add column dept_id int not null default 1,
drop primary key,
add primary key (code, dept_id,comp_code);

alter table unit_relation
add column comp_code varchar(15) not null default '0010010',
add column dept_id int not null default 1,
drop primary key,
add primary key (rel_code,comp_code);

alter table trader_group
add column dept_id int not null default 1,
drop primary key,
add primary key (group_code,comp_code);

alter table trader
add column dept_id int not null default 1,
drop primary key,
add primary key (code,comp_code);

alter table stock_unit
add column dept_id int not null default 1,
drop primary key,
add primary key (unit_code,comp_code);

alter table stock_unit
drop index item_unit_name_UNIQUE ,
drop index item_unit_code ;

alter table stock_type
add column dept_id int not null default 1,
drop primary key,
add primary key (stock_type_code,comp_code);

alter table stock_brand
add column dept_id int not null default 1,
drop primary key,
add primary key (brand_code,comp_code);

alter table stock
add column dept_id int not null default 1,
drop primary key,
add primary key (stock_code,comp_code);

alter table sale_man
add column dept_id int not null default 1,
drop primary key,
add primary key (saleman_code,comp_code);

alter table category
add column dept_id int not null default 1 after comp_code,
drop primary key,
add primary key (cat_code,comp_code);

alter table location
add column dept_id int not null default 1,
drop primary key,
add primary key (loc_code,comp_code);

alter table op_his
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no,comp_code);

alter table op_his_detail
add column dept_id int not null default 1;

alter table pur_his
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no,comp_code);

alter table ret_in_his
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no,comp_code);

alter table ret_in_his_detail
add column dept_id int not null default 1;

alter table ret_in_his_detail
add column avg_qty float(20,3) not null default 0 after dept_id;

alter table ret_in_his_detail
drop column cost_price;

alter table ret_out_his
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no,comp_code);

alter table ret_out_his_detail
add column dept_id int not null default 1;


alter table sale_his
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no,comp_code);

alter table sale_his_detail
add column dept_id int not null default 1;

alter table bk_sale_his
add column dept_id int not null default 1;

alter table bk_sale_his_detail
add column dept_id int not null default 1;

alter table stock_in_out
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no,comp_code);

alter table transfer_his
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no,comp_code);

alter table pur_his_detail
add column dept_id int not null default 1,
drop column avg_wt,
drop column avg_price,
drop column std_wt,
add column avg_qty float(20,3) not null default 0;

alter table sale_his_detail
drop column sale_wt;

alter table op_his_detail
drop column std_wt;

alter table ret_in_his_detail
drop column wt;

alter table ret_out_his_detail
drop column wt;

alter table stock_in_out_detail
drop column out_wt,
drop column in_wt;

alter table stock_in_out_detail
add column dept_id int not null default 1;

alter table transfer_his_detail
drop column wt;
#
alter table unit_relation_detail
add column comp_code varchar(15) not null default '0010010' after unique_id,
add column dept_id int not null default 1 after comp_code;


#intg_upd_status
alter table stock
add column explode bit(1) not null default 0;

alter table stock
add column intg_upd_status varchar(15);


alter table vou_status
add column intg_upd_status varchar(15);

alter table unit_relation
add column intg_upd_status varchar(15);

alter table trader_group
add column intg_upd_status varchar(15);

alter table stock_unit
add column intg_upd_status varchar(15);

alter table stock_type
add column intg_upd_status varchar(15);

alter table stock_brand
add column intg_upd_status varchar(15);

alter table category
add column intg_upd_status varchar(15);

alter table sale_man
add column intg_upd_status varchar(15);

alter table location
add column intg_upd_status varchar(15);

alter table transfer_his
add column intg_upd_status varchar(15);

alter table stock_in_out
add column intg_upd_status varchar(15);

alter table op_his
add column intg_upd_status varchar(15);

alter table trader
add column account varchar(15);

alter table reorder_level
add column dept_id int not null default 1 after comp_code,
drop primary key,
add primary key (stock_code, comp_code, dept_id);

alter table transfer_his_detail
add column dept_id int not null default 1 after comp_code,
drop primary key,
add primary key (td_code, dept_id);


#tmp
drop table if exists tmp_stock_opening;
create table tmp_stock_opening (
  tran_date date not null,
  stock_code varchar(15) not null,
  ttl_qty float(20,3) default null,
  loc_code varchar(15) not null,
  unit varchar(15) not null,
  mac_id int(11) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  primary key (tran_date,stock_code,loc_code,unit,mac_id,comp_code,dept_id)
) engine=innodb default charset=utf8mb3 comment='	';

drop table if exists tmp_stock_io_column;
create table tmp_stock_io_column (
  tran_option varchar(15) not null,
  tran_date date not null,
  stock_code varchar(15) not null,
  loc_code varchar(15) not null,
  op_qty float(20,3) not null default 0.000,
  pur_qty float(20,3) not null default 0.000,
  in_qty float(20,3) not null default 0.000,
  sale_qty float(20,3) not null default 0.000,
  out_qty float(20,3) not null default 0.000,
  mac_id int(11) not null,
  remark varchar(255) default null,
  vou_no varchar(15) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  primary key (tran_option,tran_date,stock_code,loc_code,mac_id,vou_no,dept_id,comp_code)
) engine=innodb default charset=utf8mb3;

drop table if exists tmp_stock_opening;
create table tmp_stock_opening (
  tran_date date not null,
  stock_code varchar(15) not null,
  ttl_qty float(20,3) default null,
  loc_code varchar(15) not null,
  unit varchar(15) not null,
  mac_id int(11) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  primary key (tran_date,stock_code,loc_code,unit,mac_id,comp_code,dept_id)
) engine=innodb default charset=utf8mb3 comment='	';


create table process_his (
  vou_no varchar(15) not null,
  stock_code varchar(15) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  loc_code varchar(15) not null,
  vou_date datetime not null,
  end_date datetime not null,
  unit varchar(45) not null,
  qty float(20,3) not null,
  avg_qty float(20,3) not null,
  price float(20,3) not null,
  avg_price float(20,3) not null default 0.000,
  remark varchar(255) default null,
  process_no varchar(255) default null,
  pt_code varchar(15) not null,
  finished bit(1) default b'0',
  deleted bit(1) not null default b'0',
  created_by varchar(15) not null,
  updated_by varchar(15) default null,
  mac_id int(11) not null,
  primary key (vou_no,stock_code,comp_code,dept_id,loc_code)
) engine=innodb default charset=utf8mb3;

create table process_his_detail (
  vou_no varchar(15) not null,
  stock_code varchar(15) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  unique_id int(11) not null,
  vou_date timestamp not null default current_timestamp() on update current_timestamp(),
  qty float(20,3) not null,
  unit varchar(15) not null,
  price float(20,3) not null,
  loc_code varchar(15) not null,
  primary key (vou_no,stock_code,comp_code,dept_id,unique_id)
) engine=innodb default charset=utf8mb3;

alter table price_option
add column dept_id int not null default 1 after unique_id,
add column tran_option varchar(15) null after dept_id,
drop primary key,
add primary key (type, comp_code, dept_id);

drop table if exists tmp_stock_balance;
create table tmp_stock_balance (
  stock_code varchar(15) not null,
  qty float(20,3) default null,
  unit varchar(15) not null,
  loc_code varchar(15) not null,
  mac_id int(11) not null,
  smallest_qty float(20,3) default null,
  comp_code varchar(15) not null,
  dept_id varchar(15) not null,
  primary key (stock_code,mac_id,loc_code,unit,dept_id,comp_code)
) engine=innodb default charset=utf8mb3;

alter table reorder_level
add column loc_code varchar(15) not null after dept_id,
change column min_qty min_qty float null default 0 ,
change column min_unit min_unit varchar(15) null ,
change column max_qty max_qty varchar(15) null default '0' ,
change column max_unit max_unit varchar(15) null ,
change column bal_qty bal_qty varchar(15) null default '0' ,
drop primary key,
add primary key (stock_code, dept_id, comp_code, loc_code);
alter table pattern
add column price_type varchar(15) null after intg_upd_status;

#2023-01-12
alter table trader
drop column app_trader_code,
drop column app_short_name,
drop column parent,
add column rfid varchar(50) null after account,
add column nrc varchar(255) null after rfid;


alter table sale_his_detail
add column batch_no varchar(15) null after dept_id;

create table grn (
  vou_no varchar(15) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  vou_date datetime not null,
  trader_code varchar(15) default null,
  closed bit(1) not null default b'0',
  created_date timestamp not null default current_timestamp() on update current_timestamp(),
  created_by varchar(15) not null,
  updated_date timestamp not null default current_timestamp() on update current_timestamp(),
  updated_by varchar(15) default null,
  deleted bit(1) not null default b'0',
  batch_no varchar(15) default null,
  remark varchar(255) default null,
  mac_id int(11) not null,
  primary key (vou_no,comp_code,dept_id)
) engine=innodb default charset=utf8mb3;

create table grn_detail (
  vou_no varchar(15) not null,
  unique_id int(11) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  stock_code varchar(15) default null,
  loc_code varchar(15) not null,
  qty float(20,3) not null,
  unit varchar(15) not null,
  primary key (vou_no,unique_id,comp_code,dept_id)
) engine=innodb default charset=utf8mb3;

alter table pur_his
add column batch_no varchar(15) null after vou_lock;
alter table pur_his
add column comm_p float(20,3) null after vou_lock,
add column comm_amt float(20,3) null after comm_p;
alter table acc_setting
add column comm_acc varchar(15) null after bal_acc;
alter table pur_his_detail
add column org_price float(20,3) null after dept_id;

alter table grn_detail
add column weight float(20,3) null after unit,
add column weight_unit varchar(15) null after weight;

alter table stock
add column weight_unit varchar(15) null default null ;
alter table stock
add column weight float null;

alter table pur_his_detail
add column weight float null,
add column std_weight float null,
add column weight_unit float null;


create table expense (
  expense_code varchar(15) not null,
  comp_code varchar(15) not null,
  expense_name varchar(255) not null,
  account_code varchar(15) not null,
  deleted bit(1) not null default b'0',
  primary key (expense_code,comp_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

create table pur_expense (
  expense_code varchar(15) not null,
  vou_no varchar(15) not null,
  comp_code varchar(15) not null,
  unique_id int(11) not null,
  amount float not null,
  primary key (expense_code,comp_code,vou_no,unique_id)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

alter table pur_his
add column expense float(20,3) not null after batch_no;


alter table sale_his_detail
add column weight float(20,3) null after batch_no,
add column weight_unit varchar(15) null after weight,
add column std_weight float(20,3);


alter table grn
add column loc_code varchar(15) null after mac_id;

create table acc_setting (
  type varchar(15) not null,
  comp_code varchar(15) not null,
  dis_acc varchar(15) not null,
  pay_acc varchar(15) not null,
  tax_acc varchar(15) not null,
  dep_code varchar(15) not null,
  source_acc varchar(15) not null,
  bal_acc varchar(15) not null,
  comm_acc varchar(15) default null,
  primary key (type,comp_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;


alter table acc_setting
add column comp_code varchar(15) not null after type,
drop primary key,
add primary key (type, comp_code);

set sql_safe_updates =0;
update acc_setting
set comp_code ='0010010';

alter table pur_his_detail
change column avg_qty avg_qty float(20,3) null default 0.000 ;

ALTER TABLE trader
ADD COLUMN `deleted` BIT(1) NOT NULL DEFAULT 0 AFTER `nrc`;


drop table if exists order_his;
create table order_his (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null default 1,
  trader_code varchar(15) not null,
  saleman_code varchar(45) default null,
  vou_date datetime not null,
  credit_term date default null,
  cur_code varchar(15) not null,
  remark varchar(500) default null,
  vou_total float(20,3) not null,
  grand_total float(20,3) not null,
  discount float(20,3) default null,
  disc_p float(20,3) default null,
  tax_amt float(20,3) default null,
  tax_p float(20,3) default null,
  created_date datetime not null,
  created_by varchar(15) not null,
  deleted bit(1) default null,
  paid float(20,3) default null,
  vou_balance float(20,3) default null,
  updated_by varchar(15) default null,
  updated_date timestamp not null default current_timestamp(),
  address varchar(255) default null,
  loc_code varchar(15) not null,
  mac_id varchar(15) not null,
  intg_upd_status varchar(5) default null,
  reference varchar(255) default null,
  vou_lock bit(1) not null default b'0',
  primary key (vou_no,comp_code,dept_id)
) engine=innodb default charset=utf8mb3;

drop table if exists order_his_detail;
create table order_his_detail (
  vou_no varchar(20) not null,
  comp_code varchar(15) not null default '0010010',
  dept_id int(11) not null default 1,
  unique_id int(11) not null,
  stock_code varchar(10) default null,
  qty float(20,3) not null,
  unit varchar(10) not null,
  price float(20,3) not null,
  amt float(20,3) not null,
  loc_code varchar(15) not null,
  weight float(20,3) default null,
  weight_unit varchar(15) default null,
  std_weight float(20,3) default null,
  primary key (vou_no,dept_id,unique_id,comp_code),
  key fk_item_unt_idx (unit),
  key fk__idx (stock_code)
) engine=innodb default charset=utf8mb3;

alter table pur_his_detail
drop column pd_code,
drop primary key,
add primary key (vou_no, unique_id, comp_code, dept_id);

alter table sale_his_detail
drop column sd_code,
drop primary key,
add primary key (vou_no, dept_id, unique_id, comp_code);

alter table ret_in_his_detail
drop column rd_code,
drop primary key,
add primary key (vou_no, unique_id, comp_code, dept_id);

alter table ret_out_his_detail
drop column rd_code,
drop primary key,
add primary key (vou_no, unique_id, comp_code, dept_id);

alter table op_his_detail
drop column op_code,
drop primary key,
add primary key (dept_id, comp_code, unique_id, vou_no);

alter table stock_in_out_detail
drop column sd_code,
drop primary key,
add primary key (vou_no, unique_id, dept_id, comp_code);

alter table transfer_his_detail
drop column td_code,
drop primary key,
add primary key (vou_no, unique_id, dept_id, comp_code);

alter table sale_his
add column order_no varchar(25) null;

alter table pur_his
add column project_no varchar(15);

alter table order_his
add column project_no varchar(15);

alter table sale_his
add column project_no varchar(15);

alter table ret_in_his
add column project_no varchar(15);

alter table ret_out_his
add column project_no varchar(15);

alter table stock
add column favorite bit(1) not null default 0 after weight;

alter table price_option
add column updated_date timestamp not null after tran_option;

create table payment_his (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  vou_date timestamp not null default current_timestamp() on update current_timestamp(),
  trader_code varchar(15) not null,
  remark varchar(255) default null,
  amount float(20,3) default null,
  deleted bit(1) not null default b'0',
  created_date timestamp not null default '0000-00-00 00:00:00',
  created_by varchar(15) not null,
  updated_date timestamp not null default '0000-00-00 00:00:00',
  updated_by varchar(15) default null,
  mac_id int(11) not null,
  account varchar(15) default null,
  project_no varchar(15) default null,
  intg_upd_status varchar(15) default null,
   cur_code varchar(15) default null,
  primary key (vou_no,comp_code,dept_id)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

create table payment_his_detail (
  vou_no varchar(15) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  unique_id int(11) not null,
  sale_vou_date date not null,
  sale_vou_no varchar(25) not null,
  pay_amt float(20,3) not null,
  dis_percent float(20,3) default null,
  dis_amt float(20,3) default null,
  cur_code varchar(15) default null,
  remark varchar(255) default null,
  reference varchar(255)  default null,
  full_paid bit(1) not null default b'0',
  vou_total float(20,3) not null,
  vou_balance float(20,3) not null,
  primary key (vou_no,comp_code,dept_id,unique_id)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

alter table expense
add column percent float(20,3) not null after deleted;

alter table pur_expense
add column percent float(20,3) not null;

alter table payment_his_detail
add column reference varchar(255) null after remark;

alter table trader
add column credit_amt float(20,3) null after deleted;

create table weight_loss_his (
  vou_no varchar(15) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  vou_date datetime not null,
  ref_no varchar(255) default null,
  remark varchar(255) default null,
  created_by varchar(15) not null,
  updated_by varchar(15) default null,
  updated_date timestamp not null default current_timestamp(),
  mac_id int(11) not null,
  deleted bit(1) not null default b'0',
  primary key (vou_no,comp_code,dept_id)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

create table weight_loss_his_detail (
  vou_no varchar(15) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  unique_id int(11) not null,
  stock_code varchar(15) not null,
  qty float(20,3) not null,
  unit varchar(15) not null,
  price float(20,3) not null,
  loss_qty float(20,3) not null,
  loss_unit varchar(15) not null,
  loss_price float(20,3) not null,
  loc_code varchar(15) not null,
  primary key (vou_no,comp_code,dept_id,unique_id)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

ALTER TABLE expense
ADD COLUMN percent FLOAT(20,3) NOT NULL

alter table tmp_stock_price
add column io_recent_price float(20,3) null after lifo_price;

alter table order_his
add column order_status varchar(15)  default null;

alter table process_his
change column avg_qty avg_qty float(20,3) not null default 0 ;

alter table stock
add column deleted bit(1) null default 0;

alter table price_option
change column tran_type tran_option varchar(15) null default null ;

alter table location
add column dept_code varchar(15) null after map_dept_id;

alter table location
add column cash_acc varchar(15) null after dept_code;

alter table stock
add column sale_closed bit(1) not null after favorite;

alter table stock
change column user_code user_code varchar(50) null default null ;

alter table stock
add column sale_qty float(20,3) null after deleted;

alter table pur_his_detail
change column weight_unit weight_unit varchar(10) null default null ;

alter table pur_his
add column car_no varchar(255) null after project_no;

alter table pur_his_detail
add column length float(20,3) null after weight,
add column width float(20,3) null after length,
add column total_weight float(20,3) null after width,
add column m_percent varchar(255) null after total_weight;

alter table sale_his
add column car_no varchar(255) null;
alter table sale_his
add column grn_vou_no varchar(20) null;
alter table expense
add column user_code varchar(15) null after percent;

drop table if exists milling_his;
create table milling_his (
  vou_no varchar(25) not null,
  trader_code varchar(15) not null,
  vou_date datetime not null,
  cur_code varchar(15) not null,
  remark varchar(500) default null,
  created_date datetime not null,
  created_by varchar(15) not null,
  deleted bit(1) default null,
  updated_by varchar(15) default null,
  updated_date timestamp not null default current_timestamp(),
  comp_code varchar(15) not null,
  mac_id varchar(15) not null,
  intg_upd_status varchar(5) default null,
  reference varchar(255) default null,
  dept_id int(11) not null default 1,
  vou_lock bit(1) not null default b'0',
  project_no varchar(15) default null,
  car_no varchar(255) default null,
  vou_status_id varchar(255) default null,
  load_qty float(20,3) default null,
  load_weight float(20,3) default null,
  load_amount float(20,3) default null,
  load_expense float(20,3) default null,
  load_cost float(20,3) default null,
  output_qty float(20,3) default null,
  output_weight float(20,3) default null,
  output_amount float(20,3) default null,
  diff_weight float(20,3) default null,
  primary key (vou_no,comp_code)
) engine=innodb default charset=utf8mb3;

drop table if exists milling_output;
create table milling_output (
  vou_no varchar(20) not null,
  stock_code varchar(10) default null,
  qty float(20,3) not null,
  unit varchar(10) not null,
  price float(20,3) not null,
  amt float(20,3) not null,
  loc_code varchar(15) not null,
  unique_id int(11) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null default 1,
  weight float(20,3) default null,
  weight_unit varchar(15) default null,
  percent float(20,3) not null,
  tot_weight float(20,3) not null,
  primary key (vou_no,unique_id,comp_code)
) engine=innodb default charset=utf8mb3;

drop table if exists milling_expense;
create table milling_expense (
  expense_code varchar(15) not null,
  vou_no varchar(15) not null,
  comp_code varchar(15) not null,
  unique_id int(11) not null,
  expense_name varchar(15) not null,
  qty float(20,3) not null,
  amount float not null,
  price float(20,3) not null,
  primary key (expense_code,comp_code,vou_no,unique_id)
) engine=innodb default charset=utf8mb3;

drop table if exists milling_raw;
create table milling_raw (
  vou_no varchar(20) not null,
  stock_code varchar(10) default null,
  qty float(20,3) not null,
  unit varchar(10) not null,
  price float(20,3) not null,
  amt float(20,3) not null,
  loc_code varchar(15) not null,
  unique_id int(11) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null default 1,
  weight float(20,3) default null,
  weight_unit varchar(15) default null,
  tot_weight float(20,3) not null,
  primary key (vou_no,unique_id,comp_code)
) engine=innodb default charset=utf8mb3;

alter table payment_his
add column tran_option varchar(1) not null default 'C' after intg_upd_status,
change column dept_id dept_id int(11) null ,
drop primary key,
add primary key (vou_no, comp_code);

alter table payment_his_detail
change column unique_id unique_id int(11) not null after comp_code,
change column dept_id dept_id int(11) null ,
drop primary key,
add primary key (vou_no, comp_code, unique_id);

alter table payment_his_detail
change column vou_no vou_no varchar(25) not null ;

set sql_safe_updates =0;
update payment_his
set vou_no=concat('C-',vou_no);

update payment_his_detail
set vou_no=concat('C-',vou_no);

create table sale_expense (
  expense_code varchar(15) not null,
  vou_no varchar(15) not null,
  comp_code varchar(15) not null,
  unique_id int(11) not null,
  amount float not null,
  primary key (expense_code,comp_code,vou_no,unique_id)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

alter table milling_expense
add column deleted bit(1) null after price;

alter table sale_his
add column expense float(20,3)  null after grn_vou_no;

alter table op_his_detail
add column weight float(20,3) null after dept_id,
add column weight_unit varchar(10) null after weight,
add column total_weight float(20,3) null after weight_unit;

alter table ret_in_his_detail
add column weight float(20,3) null,
add column weight_unit varchar(10) null after weight,
add column total_weight float(20,3) null after weight_unit;

alter table ret_out_his_detail
add column weight float(20,3) null,
add column weight_unit varchar(10) null after weight,
add column total_weight float(20,3) null after weight_unit;

alter table transfer_his_detail
add column weight float(20,3) null after dept_id,
add column weight_unit varchar(10) null after weight,
add column total_weight float(20,3) null after weight_unit;

alter table stock_in_out_detail
add column weight float(20,3) null,
add column weight_unit varchar(10) null after weight,
add column total_weight float(20,3) null after weight_unit;

alter table ret_in_his_detail
drop column avg_qty;

alter table grn_detail
add column total_weight float(20,3) null after weight_unit;

alter table sale_his
add column account varchar(15) null after expense;

alter table sale_his_detail
add column total_weight float(20,3) null after std_weight;

alter table stock_type
add column deleted bit(1) not null default 0 after intg_upd_status,
add column active bit(1) not null default 1 after deleted;

alter table location
add column deleted bit(1) not null default 0 after cash_acc,
add column active bit(1) not null default 1 after deleted;

alter table category
add column deleted bit(1) not null default 0,
add column active bit(1) not null default 1;

alter table stock_brand
add column deleted bit(1) not null default 0,
add column active bit(1) not null default 1;

alter table region
add column deleted bit(1) not null default 0,
add column active bit(1) not null default 1;

alter table vou_status
add column deleted bit(1) not null default 0,
add column active bit(1) not null default 1;

alter table sale_man
add column deleted bit(1) not null default 0;

alter table pur_his
change column vou_total vou_total double(20,3) null default null ;

alter table pur_his
change column vou_total vou_total double(20,3) null default 0,
change column balance balance double(20,3) null default 0,
change column discount discount double(20,3) null default 0,
change column paid paid double(20,3) null default 0,
change column tax_p tax_p double(20,3) null default 0,
change column disc_p disc_p double(20,3) null default 0,
change column tax_amt tax_amt double(20,3) null default 0,
change column comm_p comm_p double(20,3) null default 0,
change column comm_amt comm_amt double(20,3) null default 0;
alter table pur_his_detail
change column qty qty double(20,3) not null ,
change column pur_price pur_price double(20,3) not null ,
change column pur_amt pur_amt double(20,3) not null ,
change column avg_qty avg_qty double(20,3) null default 0.000 ,
change column avg_price avg_price double(20,3) null default null ,
change column org_price org_price double(20,3) null default null ,
change column weight weight double(20,3) null default null ,
change column length length double(20,3) null default null ,
change column width width double(20,3) null default null ,
change column total_weight total_weight double(20,3) null default null ,
change column std_weight std_weight double(20,3) null default null ;
alter table pur_expense
change column amount amount double(20,3) not null default 0 ,
change column percent percent double(20,3) not null default 0 ;

alter table sale_his
change column vou_total vou_total double(20,3) not null ,
change column grand_total grand_total double(20,3) not null ,
change column discount discount double(20,3) null default null ,
change column disc_p disc_p double(20,3) null default null ,
change column tax_amt tax_amt double(20,3) null default null ,
change column tax_p tax_p double(20,3) null default null ,
change column paid paid double(20,3) null default null ,
change column vou_balance vou_balance double(20,3) null default null ,
change column expense expense double(20,3) null default null ;
alter table sale_his_detail
change column qty qty double(20,3) not null ,
change column sale_price sale_price double(20,3) not null ,
change column sale_amt sale_amt double(20,3) not null ,
change column weight weight double(20,3) null default null ,
change column std_weight std_weight double(20,3) null default null ,
change column total_weight total_weight double(20,3) null default null ;


alter table trader
change column user_code user_code varchar(255) null default null;

alter table milling_expense
change column expense_name expense_name varchar(255) not null ;


drop table if exists order_his;
create table order_his (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null default 1,
  trader_code varchar(15) not null,
  saleman_code varchar(45) default null,
  vou_date datetime not null,
  credit_term date default null,
  cur_code varchar(15) not null,
  remark varchar(500) default null,
  vou_total double(20,3) not null,
  created_date datetime not null,
  created_by varchar(15) not null,
  deleted bit(1) default null,
  vou_balance double(20,3) default null,
  updated_by varchar(15) default null,
  updated_date timestamp not null default current_timestamp(),
  loc_code varchar(15) not null,
  mac_id varchar(15) not null,
  intg_upd_status varchar(5) default null,
  reference varchar(255) default null,
  vou_lock bit(1) not null default b'0',
  project_no varchar(15) default null,
  order_status varchar(15) default null,
  primary key (vou_no,comp_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

drop table if exists order_his_detail;
create table order_his_detail (
  vou_no varchar(20) not null,
  comp_code varchar(15) not null default '0010010',
  unique_id int(11) not null,
  dept_id int(11) not null default 1,
  stock_code varchar(10) default null,
  qty double(20,3) not null,
  unit varchar(10) not null,
  price double(20,3) not null,
  amt double(20,3) not null,
  loc_code varchar(15) not null,
  weight double(20,3) default null,
  weight_unit varchar(15) default null,
  primary key (vou_no,comp_code,unique_id),
  key fk_item_unt_idx (unit),
  key fk__idx (stock_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;


create table order_status (
  code varchar(15) not null,
  description varchar(255) default null,
  created_by varchar(15) default null,
  created_date date default null,
  updated_by varchar(15) default null,
  updated_date timestamp not null default current_timestamp() on update current_timestamp(),
  mac_id int(11) default null,
  comp_code varchar(15) not null,
  user_code varchar(15) default null,
  dept_id int(11) not null default 1,
  intg_upd_status varchar(15) default null,
  deleted bit(1) not null default b'0',
  active bit(1) not null default b'1',
  order_by int(5) default null,
  primary key (code,dept_id,comp_code)
) engine=innodb default charset=utf8mb3;

alter table transfer_his
add column trader_code varchar(45) null after vou_lock;

alter table order_his_detail
add column order_qty double not null after stock_code;

alter table op_his change column comp_code comp_code varchar(15) not null after vou_no,change column dept_id dept_id int(11) not null default 1 after comp_code,drop primary key,add primary key (vou_no, comp_code);
alter table op_his_detail change column vou_no vou_no varchar(15) not null first,change column unique_id unique_id int(11) not null after vou_no,change column comp_code comp_code varchar(15) not null default '0010010' after unique_id,drop primary key,add primary key (vou_no, unique_id, comp_code);

alter table trader
add column country_code varchar(15) null after credit_amt;

create table stock_formula (
  code varchar(15) not null,
  comp_code varchar(15) not null,
  user_code varchar(15) null,
  formula_name varchar(255) not null,
  created_by varchar(15) not null,
  created_date timestamp not null,
  updated_by varchar(15) null,
  updated_date timestamp not null,
  active bit(1) not null default 0,
  deleted bit(1) not null default 0,
  primary key (code, comp_code));

create table stock_formula_detail (
  code varchar(15) not null,
  comp_code varchar(15) not null,
  unique_id int not null,
  description varchar(255) not null,
  percent double(10,3) not null,
  price double(20,3) not null,
  primary key (code, comp_code, unique_id));

#view
drop view if exists v_milling_output;
create  view v_milling_output as select sh.project_no as project_no,sh.vou_no as vou_no,sh.trader_code as trader_code,sh.vou_date as vou_date,sh.cur_code as cur_code,sh.remark as remark,sh.created_date as created_date,sh.created_by as created_by,sh.deleted as deleted,sh.updated_by as updated_by,sh.updated_date as updated_date,sh.comp_code as comp_code,sh.mac_id as mac_id,sh.reference as reference,sh.dept_id as dept_id,sd.stock_code as stock_code,sd.weight as weight,sd.weight_unit as weight_unit,sd.qty as qty,sd.unit as unit,sd.price as price,sd.amt as amt,sd.loc_code as loc_code,sd.tot_weight as tot_weight,sd.unique_id as unique_id,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as cat_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((milling_his sh join milling_output sd) join stock s) where sh.vou_no = sd.vou_no and sh.comp_code = sd.comp_code and sd.stock_code = s.stock_code and sd.comp_code = s.comp_code;
drop view if exists v_milling_raw;
create  view v_milling_raw as select sh.project_no as project_no,sh.vou_no as vou_no,sh.trader_code as trader_code,sh.vou_date as vou_date,sh.cur_code as cur_code,sh.remark as remark,sh.created_date as created_date,sh.created_by as created_by,sh.deleted as deleted,sh.updated_by as updated_by,sh.updated_date as updated_date,sh.comp_code as comp_code,sh.mac_id as mac_id,sh.reference as reference,sh.dept_id as dept_id,sd.stock_code as stock_code,sd.weight as weight,sd.weight_unit as weight_unit,sd.qty as qty,sd.unit as unit,sd.price as price,sd.amt as amt,sd.loc_code as loc_code,sd.tot_weight as tot_weight,sd.unique_id as unique_id,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as cat_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((milling_his sh join milling_raw sd) join stock s) where sh.vou_no = sd.vou_no and sh.comp_code = sd.comp_code and sd.stock_code = s.stock_code and sd.comp_code = s.comp_code;
drop view if exists v_opening;
create  view v_opening as select op.op_date as op_date,op.remark as remark,op.created_by as created_by,op.created_date as created_date,op.updated_date as updated_date,op.updated_by as updated_by,op.mac_id as mac_id,op.comp_code as comp_code,op.deleted as deleted,op.op_amt as op_amt,op.dept_id as dept_id,op.cur_code as cur_code,opd.stock_code as stock_code,opd.qty as qty,opd.price as price,opd.amount as amount,opd.loc_code as loc_code,opd.unit as unit,opd.vou_no as vou_no,opd.unique_id as unique_id,opd.weight as weight,opd.weight_unit as weight_unit,opd.total_weight as total_weight,s.user_code as stock_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((op_his op join op_his_detail opd on(op.vou_no = opd.vou_no and op.comp_code = opd.comp_code)) join stock s on(opd.stock_code = s.stock_code and opd.comp_code = s.comp_code));
drop view if exists v_purchase;
create  view v_purchase as select ph.project_no as project_no,ph.vou_date as vou_date,ph.balance as balance,ph.deleted as deleted,ph.discount as discount,ph.due_date as due_date,ph.paid as paid,ph.remark as remark,ph.ref_no as ref_no,ph.updated_by as updated_by,ph.updated_date as updated_date,ph.created_by as created_by,ph.created_date as created_date,ph.vou_total as vou_total,ph.cur_code as cur_code,ph.trader_code as trader_code,ph.disc_p as disc_p,ph.tax_p as tax_p,ph.tax_amt as tax_amt,ph.dept_id as dept_id,ph.intg_upd_status as intg_upd_status,ph.comp_code as comp_code,ph.reference as reference,ph.batch_no as batch_no,pd.vou_no as vou_no,pd.stock_code as stock_code,pd.exp_date as exp_date,pd.avg_qty as avg_qty,pd.qty as qty,pd.weight as weight,pd.weight_unit as weight_unit,pd.std_weight as std_weight,pd.pur_unit as pur_unit,pd.pur_price as pur_price,pd.pur_amt as pur_amt,pd.loc_code as loc_code,pd.unique_id as unique_id,pd.total_weight as total_weight,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((pur_his ph join pur_his_detail pd on(ph.vou_no = pd.vou_no and ph.comp_code = pd.comp_code)) join stock s on(pd.stock_code = s.stock_code and pd.comp_code = s.comp_code));
drop view if exists v_return_in;
create  view v_return_in as select rh.project_no as project_no,rh.balance as balance,rh.created_by as created_by,rh.created_date as created_date,rh.deleted as deleted,rh.discount as discount,rh.paid as paid,rh.vou_date as vou_date,rh.ref_no as ref_no,rh.remark as remark,rh.session_id as session_id,rh.updated_by as updated_by,rh.updated_date as updated_date,rh.vou_total as vou_total,rh.cur_code as cur_code,rh.trader_code as trader_code,rh.disc_p as disc_p,rh.intg_upd_status as intg_upd_status,rh.mac_id as mac_id,rh.comp_code as comp_code,rh.dept_id as dept_id,rd.vou_no as vou_no,rd.stock_code as stock_code,rd.qty as qty,rd.unit as unit,rd.price as price,rd.amt as amt,rd.loc_code as loc_code,rd.unique_id as unique_id,rd.weight as weight,rd.weight_unit as weight_unit,rd.total_weight as total_weight,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((ret_in_his rh join ret_in_his_detail rd on(rh.vou_no = rd.vou_no and rh.comp_code = rd.comp_code)) join stock s on(rd.stock_code = s.stock_code and rd.comp_code = s.comp_code));
drop view if exists v_return_out;
create  view v_return_out as select rh.project_no as project_no,rh.balance as balance,rh.created_by as created_by,rh.created_date as created_date,rh.deleted as deleted,rh.discount as discount,rh.paid as paid,rh.vou_date as vou_date,rh.ref_no as ref_no,rh.remark as remark,rh.session_id as session_id,rh.updated_by as updated_by,rh.updated_date as updated_date,rh.vou_total as vou_total,rh.cur_code as cur_code,rh.trader_code as trader_code,rh.disc_p as disc_p,rh.intg_upd_status as intg_upd_status,rh.mac_id as mac_id,rh.comp_code as comp_code,rh.dept_id as dept_id,rd.vou_no as vou_no,rd.stock_code as stock_code,rd.qty as qty,rd.unit as unit,rd.price as price,rd.amt as amt,rd.loc_code as loc_code,rd.unique_id as unique_id,rd.weight as weight,rd.weight_unit as weight_unit,rd.total_weight as total_weight,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((ret_out_his rh join ret_out_his_detail rd on(rh.vou_no = rd.vou_no and rh.comp_code = rd.comp_code)) join stock s on(rd.stock_code = s.stock_code and rd.comp_code = s.comp_code));
drop view if exists v_sale;
create  view v_sale as select sh.order_no as order_no,sh.project_no as project_no,sh.vou_no as vou_no,sh.trader_code as trader_code,sh.saleman_code as saleman_code,sh.vou_date as vou_date,sh.credit_term as credit_term,sh.cur_code as cur_code,sh.remark as remark,sh.vou_total as vou_total,sh.grand_total as grand_total,sh.discount as discount,sh.disc_p as disc_p,sh.tax_amt as tax_amt,sh.tax_p as tax_p,sh.created_date as created_date,sh.created_by as created_by,sh.deleted as deleted,sh.paid as paid,sh.vou_balance as vou_balance,sh.updated_by as updated_by,sh.updated_date as updated_date,sh.comp_code as comp_code,sh.address as address,sh.order_code as order_code,sh.mac_id as mac_id,sh.session_id as session_id,sh.reference as reference,sh.dept_id as dept_id,sd.stock_code as stock_code,sd.expire_date as expire_date,sd.weight as weight,sd.weight_unit as weight_unit,sd.qty as qty,sd.sale_unit as sale_unit,sd.sale_price as sale_price,sd.sale_amt as sale_amt,sd.loc_code as loc_code,sd.batch_no as batch_no,sd.unique_id as unique_id,sd.total_weight as total_weight,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as cat_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((sale_his sh join sale_his_detail sd on(sh.vou_no = sd.vou_no and sh.comp_code = sd.comp_code)) join stock s on(sd.stock_code = s.stock_code and sd.comp_code = s.comp_code));
drop view if exists v_transfer;
create view v_transfer as select th.vou_no as vou_no,th.created_by as created_by,th.created_date as created_date,th.deleted as deleted,th.vou_date as vou_date,th.ref_no as ref_no,th.remark as remark,th.updated_by as updated_by,th.updated_date as updated_date,th.loc_code_from as loc_code_from,th.loc_code_to as loc_code_to,th.mac_id as mac_id,th.dept_id as dept_id,th.comp_code as comp_code,th.trader_code as trader_code,td.stock_code as stock_code,td.qty as qty,td.unit as unit,td.unique_id as unique_id,td.weight as weight,td.weight_unit as weight_unit,td.total_weight as total_weight,s.user_code as user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((transfer_his th join transfer_his_detail td on(th.vou_no = td.vou_no and th.comp_code = td.comp_code)) join stock s on(td.stock_code = s.stock_code and td.comp_code = s.comp_code));
drop view if exists v_order;
create view v_order as  select oh.order_status,oh.project_no as project_no,oh.vou_no as vou_no,oh.comp_code as comp_code,oh.dept_id as dept_id,oh.trader_code as trader_code,oh.saleman_code as saleman_code,oh.vou_date as vou_date,oh.credit_term as credit_term,oh.cur_code as cur_code,oh.remark as remark,oh.vou_total as vou_total,oh.created_date as created_date,oh.created_by as created_by,oh.deleted as deleted,oh.updated_by as updated_by,oh.updated_date as updated_date,oh.mac_id as mac_id,oh.intg_upd_status as intg_upd_status,oh.reference as reference,oh.vou_lock as vou_lock,ohd.unique_id as unique_id,ohd.stock_code as stock_code,ohd.order_qty as order_qty,ohd.qty as qty,ohd.unit as unit,ohd.price as price,ohd.amt as amt,ohd.loc_code as loc_code,ohd.weight as weight,ohd.weight_unit as weight_unit,s.user_code as user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((order_his oh join order_his_detail ohd on(oh.vou_no = ohd.vou_no and oh.comp_code = ohd.comp_code)) join stock s on(ohd.stock_code = s.stock_code and ohd.comp_code = s.comp_code));
drop view if exists v_weight_loss;
create  view v_weight_loss as select wlh.vou_no as vou_no,wlh.comp_code as comp_code,wlh.dept_id as dept_id,wlh.vou_date as vou_date,wlh.ref_no as ref_no,wlh.remark as remark,wlh.created_by as created_by,wlh.updated_by as updated_by,wlh.updated_date as updated_date,wlh.mac_id as mac_id,wlh.deleted as deleted,wlhd.unique_id as unique_id,wlhd.stock_code as stock_code,wlhd.qty as qty,wlhd.unit as unit,wlhd.price as price,wlhd.loss_qty as loss_qty,wlhd.loss_unit as loss_unit,wlhd.loss_price as loss_price,wlhd.loc_code as loc_code,s.user_code as user_code,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.calculate as calculate,s.rel_code as rel_code from ((weight_loss_his wlh join weight_loss_his_detail wlhd on(wlh.vou_no = wlhd.vou_no and wlh.comp_code = wlhd.comp_code)) join stock s on(wlhd.stock_code = s.stock_code and wlhd.comp_code = s.comp_code));
