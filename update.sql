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
  primary key (vou_no,stock_code,comp_code,loc_code)
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
  primary key (vou_no,stock_code,comp_code,unique_id)
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



alter table pur_his_detail
change column avg_qty avg_qty float(20,3) null default 0.000 ;

ALTER TABLE trader
ADD COLUMN deleted BIT(1) NOT NULL DEFAULT 0 AFTER nrc;


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
  primary key (vou_no,comp_code)
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
  primary key (vou_no,comp_code,unique_id)
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

#drop table if exists milling_his;
create table milling_his (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  trader_code varchar(15) not null,
  vou_date datetime not null,
  cur_code varchar(15) not null,
  remark varchar(500) default null,
  created_date datetime not null,
  created_by varchar(15) not null,
  deleted bit(1) default null,
  updated_by varchar(15) default null,
  updated_date timestamp not null default current_timestamp(),
  mac_id varchar(15) not null,
  intg_upd_status varchar(5) default null,
  reference varchar(255) default null,
  dept_id int(11) not null default 1,
  vou_lock bit(1) not null default b'0',
  project_no varchar(15) default null,
  car_no varchar(255) default null,
  vou_status_id varchar(255) not null,
  load_qty double(20,3) not null,
  load_weight double(20,3) not null,
  load_amount double(20,3) not null,
  load_expense double(20,3) not null,
  load_cost double(20,3) not null,
  output_qty double(20,3) not null,
  output_weight double(20,3) not null,
  output_amount double(20,3) not null,
  diff_weight double(20,3) not null,
  loc_code varchar(15) default null,
  diff_qty double(20,3) not null,
  percent_weight double(20,3) not null,
  percent_qty double(20,3) not null,
  job_no varchar(15) default null,
  print_count int(11) default null,
  primary key (vou_no,comp_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

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

create table stock_criteria (
  criteria_code varchar(15) not null,
  comp_code varchar(15) not null,
  user_code varchar(15) not null,
  criteria_name varchar(255) not null,
  created_by varchar(15) not null,
  created_date timestamp not null,
  updated_by varchar(15) default null,
  updated_date timestamp not null,
  active bit(1) not null,
  deleted bit(1) not null,
  primary key (criteria_code,comp_code)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

create table landing_his (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  mac_id int(11) not null,
  vou_date timestamp not null,
  trader_code varchar(15) not null,
  loc_code varchar(15) not null,
  remark varchar(15) default null,
  created_by varchar(15) not null,
  created_date timestamp not null,
  updated_by varchar(15) default null,
  updated_date timestamp not null,
  deleted bit(1) not null,
  stock_code varchar(15) not null,
  qty double(20,3) not null,
  unit varchar(15) not null,
  weight double(20,3) not null,
  weight_unit varchar(15) not null,
  total_weight double(20,3) not null,
  price double(20,3) not null,
  amount double(20,3) not null,
  cargo varchar(255) default null,
  criteria_amt double(20,3) not null default 0.000,
  pur_amt double(20,3) not null default 0.000,
  pur_price double(20,3) not null default 0.000,
  vou_paid double(20,3) not null default 0.000,
  vou_balance double(20,3) not null default 0.000,
  grand_total double(20,3) not null default 0.000,
  purchase bit(1) default null,
  intg_upd_status varchar(15) not null,
  primary key (vou_no,comp_code)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci comment='				';

create table landing_his_criteria (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  unique_id int(11) not null,
  criteria_code varchar(15) not null,
  percent double(5,3) not null,
  percent_allow double(5,3) not null,
  price double(20,3) not null,
  amount double(20,3) not null,
  primary key (vou_no,unique_id,comp_code)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

alter table stock
add column formula_code varchar(15) null after sale_qty,
add column sale_amt double(20,3) not null default 0 after formula_code,
add column pur_amt double(20,3) not null default 0 after sale_amt,
add column pur_qty double(20,3) null after pur_amt;


drop table if exists tmp_stock_balance;
create table tmp_stock_balance (
  stock_code varchar(15) not null,
  comp_code varchar(15) not null,
  loc_code varchar(15) not null,
  mac_id int(11) not null,
  dept_id varchar(15) default null,
  unit varchar(15) null,
  qty double(20,3) default null,
  smallest_qty double(20,3) default null,
  weight double(20,3) default null,
  primary key (stock_code,comp_code,loc_code,mac_id)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

create table milling_usage (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  unique_id int(11) not null,
  stock_code varchar(15) not null,
  qty double(20,3) not null,
  unit varchar(15) not null,
  loc_code varchar(15) not null,
  primary key (vou_no,comp_code,unique_id)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;


create table labour_group (
  code varchar(15) not null,
  labour_name varchar(255) default null,
  created_by varchar(15) default null,
  created_date date default null,
  updated_by varchar(15) default null,
  updated_date timestamp not null default current_timestamp() on update current_timestamp(),
  comp_code varchar(15) not null,
  user_code varchar(15) default null,
  active bit(1) not null default b'1',
  member_count int(5) default null,
  deleted bit(1) not null default b'0',
  primary key (code,comp_code)
) engine=innodb default charset=utf8mb3;

create table job (
  job_no varchar(15)  not null,
  comp_code varchar(15)  not null,
  job_name varchar(255)  not null,
  start_date date not null,
  end_date date default null,
  updated_date timestamp not null default current_timestamp(),
  created_date timestamp not null,
  created_by varchar(15)  not null,
  updated_by varchar(15)  default null,
  deleted bit(1) not null,
  finished bit(1) not null,
  primary key (job_no,comp_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;


alter table stock_in_out
add column labour_group_code varchar(45) null after intg_upd_status,
add column job_code varchar(45) null after labour_group_code;

alter table transfer_his
add column labour_group_code varchar(45) null after intg_upd_status,
add column job_code varchar(45) null after labour_group_code;

alter table sale_his
add column labour_group_code varchar(15) null after account;

alter table pur_his
add column labour_group_code varchar(15) null after car_no;
alter table pur_his
add column land_vou_no varchar(25) null after labour_group_code;

create table vou_discount (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  unique_id int not null,
  description varchar(255) null,
  unit varchar(15) null,
  qty double(20,3) not null,
  price double(20,3) not null,
  amount double(20,3) not null,
  primary key (vou_no, comp_code, unique_id));

alter table milling_his
add column percent_qty double(20,3) not null,
add column job_no varchar(15) null after percent_qty;

alter table stock
add column pur_qty double(20,3) null after pur_amt;

create table stock_formula (
  formula_code varchar(15) not null,
  comp_code varchar(15) not null,
  user_code varchar(15) default null,
  formula_name varchar(255) not null,
  created_by varchar(15) not null,
  created_date timestamp not null,
  updated_by varchar(15) default null,
  updated_date timestamp not null,
  active bit(1) not null default b'0',
  deleted bit(1) not null default b'0',
  qty double(20,3) not null default 0.000,
  primary key (formula_code,comp_code)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

create table stock_formula_price (
  formula_code varchar(15) not null,
  comp_code varchar(15) not null,
  unique_id int(11) not null,
  criteria_code varchar(255) not null,
  percent double(10,3) not null,
  price double(20,3) not null,
  percent_allow double(20,3) not null default 0.000,
  primary key (formula_code,comp_code,unique_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

create table stock_formula_qty (
  formula_code varchar(15) not null,
  comp_code varchar(15) not null,
  unique_id int(11) not null,
  criteria_code varchar(255) not null,
  percent double(10,3) not null,
  qty double(20,3) not null,
  unit varchar(15) not null,
  percent_allow double(20,3) not null default 0.000,
  primary key (formula_code,comp_code,unique_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

create table grade_detail (
  comp_code varchar(15) not null,
  formula_code varchar(15) not null,
  criteria_code varchar(15) not null,
  unique_id int(11) not null,
  min_percent double(6,3) not null,
  max_percent double(6,3) not null,
  grade_stock_code varchar(15) default null,
  primary key (comp_code,formula_code,criteria_code,unique_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

alter table stock_in_out
add column received_name varchar(255) null after job_code,
add column received_phone varchar(255) null after received_name,
add column car_no varchar(255) null after received_phone,
add column trader_code varchar(15) null after car_no;

alter table stock_formula_qty
add column updated_date timestamp default current_timestamp;

alter table stock_formula_price
add column updated_date timestamp not null default current_timestamp;

alter table grade_detail
add column updated_date timestamp not null default current_timestamp;

alter table vou_status
add column report_name varchar(255);

alter table pattern
add column updated_date timestamp not null default current_timestamp;
//

alter table stock_type
add column finished_group bit(1) not null default 0 after active;

alter table op_his
add column trader_code varchar(15) null after intg_upd_status,
add column tran_source int not null default 1 after trader_code,
change column op_amt op_amt double(10,3) not null default 0.000 ;

alter table milling_his
add column print_count int null;
alter table landing_his
add column print_count int null;
alter table sale_his
add column print_count int null;
alter table pur_his
add column print_count int null,
add column payable_acc varchar(15);
alter table ret_in_his
add column print_count int null;
alter table ret_out_his
add column print_count int null;
alter table stock_in_out
add column print_count int null;
alter table transfer_his
add column print_count int null;
alter table job
add column dept_id int not null default 1;

alter table sale_his_detail
add column org_price double(20,3) null after total_weight,
add column weight_loss double(20,3) null after org_price;

alter table pur_his
add column weight_vou_no varchar(25) null after print_count,
add column cash_acc varchar(15) null after weight_vou_no,
add column purchase_acc varchar(15) null after cash_acc,
add column dept_code varchar(15) null after purchase_acc,
add column grand_total double(20,3) null after payable_acc;

create table warehouse (
  code varchar(15) not null,
  description varchar(255) default null,
  created_by varchar(15) default null,
  created_date date default null,
  updated_by varchar(15) default null,
  updated_date timestamp not null default current_timestamp() on update current_timestamp(),
  comp_code varchar(15) not null,
  user_code varchar(15) default null,
  active bit(1) not null default b'1',
  deleted bit(1) not null default b'0',
  primary key (code,comp_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

create table weight_his (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  vou_date datetime not null,
  trader_code varchar(15) not null,
  stock_code varchar(415) not null,
  weight double(20,3) not null default 0.000,
  total_weight double(20,3) not null default 0.000,
  total_qty double(20,3) not null default 0.000,
  total_bag double(20,3) not null default 0.000,
  created_by varchar(15) not null,
  created_date timestamp not null default current_timestamp() on update current_timestamp(),
  updated_by varchar(15) default null,
  updated_date timestamp not null default current_timestamp() on update current_timestamp(),
  deleted bit(1) not null default b'0',
  mac_id int(11) not null default 0,
  tran_source varchar(15) not null,
  remark varchar(255) default null,
  description varchar(255) default null,
  draft bit(1) not null default b'0',
  post bit(1) not null default b'0',
  primary key (vou_no,comp_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

create table weight_his_detail (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  unique_id int(11) not null default 0,
  weight double(20,3) not null default 0.000,
  primary key (vou_no,comp_code,unique_id)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

alter table location
add column warehouse_code varchar(15) null after active;

alter table landing_his
add column post bit(1) not null default b'0';


alter table acc_setting
add column updated_date timestamp null after comm_acc;

create table consign_his (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null default 1,
  loc_code varchar(20) not null,
  description varchar(255) default null,
  vou_date datetime not null,
  remark varchar(255) default null,
  mac_id int(11) not null,
  created_date datetime not null,
  created_by varchar(15) default null,
  updated_date timestamp not null default current_timestamp(),
  updated_by varchar(15) default null,
  deleted bit(1) default null,
  intg_upd_status varchar(15) default null,
  labour_group_code varchar(45) default null,
  received_name varchar(255) default null,
  received_phone varchar(255) default null,
  car_no varchar(255) default null,
  trader_code varchar(15) not null,
  print_count int(11) default null,
  tran_source int(11) not null default 0,
  primary key (vou_no,comp_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

create table consign_his_detail (
  vou_no varchar(25) not null,
  unique_id int(11) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null default 1,
  stock_code varchar(15) not null,
  loc_code varchar(15) not null,
  wet double(20,3) default 0.000,
  bag double(20,3) default 0.000,
  qty double(20,3) not null default 0.000,
  weight double(20,3) default 0.000,
  rice double(20,3) default 0.000,
  price double(20,3) not null default 0.000,
  amount double(20,3) not null default 0.000,
  primary key (vou_no,unique_id,comp_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

alter table acc_setting
add column updated_date timestamp not null default current_timestamp;

#change
alter table pur_his_detail
add column rice double(20,3) default 0.0 after m_percent,
add column wet double(20,3) default 0.0 after rice,
add column bag double(20,3) default 0.0 after wet;

alter table transfer_his_detail
add column wet double(20,3) default 0.0 after total_weight,
add column rice double(20,3) default 0.0 after wet,
add column bag double(20,3) default 0.0 after rice,
add column price double(20,3) default 0.0 after bag,
add column amount double(20,3) default 0.0 after price;

alter table op_his_detail
add column wet double(20,3) default 0.0 after total_weight,
add column rice double(20,3) default 0.0 after wet,
add column bag double(20,3) default 0.0 after rice;

alter table stock_in_out_detail
add column rice double(20,3) default 0.0 after total_weight,
add column wet double(20,3) default 0.0 after rice,
add column bag double(20,3) default 0.0 after wet;

alter table vou_status
add column mill_report_name varchar(255) null after report_name;
alter table milling_output
add column sort_id int null after percent_qty;

alter table sale_his
add column dept_code varchar(15) null after print_count,
add column cash_acc varchar(15) null after dept_code,
add column debtor_acc varchar(15) null after cash_acc;

#tmp
drop table if exists tmp_stock_io_column;
create table tmp_stock_io_column (
  tran_option varchar(15) not null,
  tran_date date not null,
  stock_code varchar(15) not null,
  loc_code varchar(15) not null,
  mac_id int(11) not null,
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  op_qty double(20,3) not null default 0.000,
  pur_qty double(20,3) not null default 0.000,
  in_qty double(20,3) not null default 0.000,
  sale_qty double(20,3) not null default 0.000,
  out_qty double(20,3) not null default 0.000,
  remark varchar(255) default null,
  trader_code varchar(15) not null default '-',
  op_weight double(20,3) not null default 0.000,
  pur_weight double(20,3) not null default 0.000,
  in_weight double(20,3) not null default 0.000,
  sale_weight double(20,3) not null default 0.000,
  out_weight double(20,3) not null default 0.000,
  op_wet double(20,3) default null,
  pur_wet double(20,3) default null,
  in_wet double(20,3) default null,
  sale_wet double(20,3) default null,
  out_wet double(20,3) default null,
  op_rice double(20,3) default null,
  pur_rice double(20,3) default null,
  in_rice double(20,3) default null,
  sale_rice double(20,3) default null,
  out_rice double(20,3) default null,
  op_bag double(20,3) default null,
  pur_bag double(20,3) default null,
  in_bag double(20,3) default null,
  sale_bag double(20,3) default null,
  out_bag double(20,3) default null,
  op_ttl_amt double(20,3) default null,
  pur_ttl_amt double(20,3) default null,
  in_ttl_amt double(20,3) default null,
  out_ttl_amt double(20,3) default null,
  sale_ttl_amt double(20,3) default null,
  primary key (tran_option,tran_date,stock_code,loc_code,mac_id,vou_no,comp_code,dept_id,trader_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;



drop table if exists tmp_stock_opening;
create table tmp_stock_opening (
  tran_date date not null,
  stock_code varchar(15) not null,
  loc_code varchar(15) not null,
  mac_id int(11) not null,
  comp_code varchar(15) not null,
  trader_code varchar(15) not null default '-',
  dept_id int(11) default null,
  unit varchar(15) default null,
  ttl_weight double(20,3) default null,
  ttl_qty double(20,3) default null,
  ttl_wet double(20,3) default null,
  ttl_rice double(20,3) default null,
  ttl_bag double(20,3) default null,
  ttl_amt double(20,3) default null,
  primary key (tran_date,stock_code,loc_code,mac_id,comp_code,trader_code,unit)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci comment='	';



alter table labour_group
add column qty double(20,3) null after deleted,
add column price double(20,3) null after qty;

create table labour_payment (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  vou_date datetime not null,
  labour_group_code varchar(15) not null,
  cur_code varchar(15) not null,
  remark text default null,
  created_date timestamp not null,
  created_by varchar(15) not null,
  updated_date timestamp not null,
  updated_by varchar(15) default null,
  deleted bit(1) not null default b'0',
  mac_id int(11) not null,
  member_count int(11) default null,
  pay_total double(20,3) default null,
  source_acc varchar(15) default null,
  expense_acc varchar(15) default null,
  from_date date default null,
  to_date date default null,
  dept_code varchar(15) default null,
  intg_upd_status varchar(15) default null,
  primary key (vou_no,comp_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

create table labour_payment_detail (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  unique_id int(11) not null,
  description text default null,
  qty double(20,3) not null,
  price double(20,3) not null,
  amount double(20,3) not null,
  account varchar(15) default null,
  primary key (vou_no,comp_code,unique_id)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

alter table sale_his_detail
add column wet double(20,3) null after weight_loss,
add column rice double(20,3) null after wet,
add column bag double(20,3) null after rice;

alter table sale_his
add column weight_vou_no varchar(25) null after debtor_acc;

create table sale_order_join (
  sale_vou_no varchar(25) not null,
  order_vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  primary key (sale_vou_no,order_vou_no,comp_code)
) engine=innodb default  charset=utf8mb3 collate=utf8mb3_general_ci;

alter table stock_in_out_detail
add column out_bag double(20,3) null default null after in_bag,
change column bag in_bag double(20,3) null default null ;

alter table order_his
add column post bit(1) not null default 0 after order_status;

alter table stock_type
add column group_type int not null default 0 after finished_group;

create table stock_payment (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  dept_id int(11) not null,
  vou_date datetime not null,
  trader_code varchar(15) not null,
  remark text default null,
  reference text default null,
  deleted bit(1) not null default b'0',
  created_date timestamp not null,
  updated_date timestamp not null,
  created_by varchar(15) not null,
  updated_by varchar(15) default null,
  mac_id int(11) not null,
  tran_option varchar(15) not null,
  calculate bit(1) not null default b'0',
  loc_code varchar(15) default null,
  primary key (vou_no,comp_code)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

create table stock_payment_detail (
  vou_no varchar(25) not null,
  comp_code varchar(15) not null,
  unique_id int(11) not null,
  ref_date date not null,
  stock_code varchar(15) default null,
  ref_no varchar(25) default null,
  qty double(20,3) default null,
  pay_qty double(20,3) default null,
  bal_qty double(20,3) default null,
  bag double(20,3) default null,
  pay_bag double(20,3) default null,
  bal_bag double(20,3) default null,
  remark text default null,
  reference text default null,
  full_paid bit(1) not null default b'0',
  project_no varchar(15) default null,
  primary key (vou_no,comp_code,unique_id)
) engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

alter table sale_his
add column post bit(1) not null default 0;

alter table iss_rec_his
rename to  consign_his;
alter table iss_rec_his_detail
rename to  consign_his_detail;

alter table stock_in_out_detail
add column amount double(20,3) null after out_bag;

CREATE TABLE order_note (
  vou_no varchar(25) NOT NULL,
  comp_code varchar(25) NOT NULL,
  dept_id int(11) DEFAULT NULL,
  mac_id int(11) DEFAULT NULL,
  trader_code varchar(25) DEFAULT NULL,
  stock_code varchar(45) DEFAULT NULL,
  order_code varchar(45) DEFAULT NULL,
  order_name text DEFAULT NULL,
  vou_date datetime DEFAULT NULL,
  created_date timestamp NULL DEFAULT current_timestamp(),
  created_by varchar(45) DEFAULT NULL,
  updated_date timestamp NULL DEFAULT current_timestamp(),
  updated_by varchar(45) DEFAULT NULL,
  deleted bit(1) DEFAULT NULL,
  PRIMARY KEY (vou_no,comp_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE order_file_join (
  vou_no varchar(45) NOT NULL,
  comp_code varchar(45) NOT NULL,
  file_id varchar(255) NOT NULL,
  PRIMARY KEY (vou_no,comp_code,file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

alter table pur_his
add column s_rec bit(1) not null default 0;

alter table sale_his
add column s_pay bit(1) not null default 0;

alter table pur_his
add column tran_source int not null default 0;

alter table sale_his
add column tran_source int not null default 0;

update stock_in_out_detail
set amount =ifnull(in_qty,0)+ifnull(out_qty,0)*cost_price;

alter table pur_his
add column outstanding double(20,3);
alter table sale_his
add column outstanding double(20,3);


DELIMITER $$
create definer=root@localhost function iszero(input double, output double) returns double
begin
    if input is null or input = 0 then
        return output;
    else
        return input;
    end if;
end$$
DELIMITER ;

alter table order_his_detail
add column design text null after weight_unit,
add column size text null after design;

alter table ret_in_his_detail
add column wet double(20,3) null after total_weight,
add column rice double(20,3) null after wet,
add column bag double(20,3) null after rice;

alter table ret_out_his_detail
add column wet double(20,3) null after total_weight,
add column rice double(20,3) null after wet,
add column bag double(20,3) null after rice;

alter table sale_his
add column total_payment double(20,3) null after tran_source,
add column opening double(20,3) null after total_payment,
add column total_balance double(20,3) null after opening;

alter table ret_in_his
add column tax_amt double(20,3) null,
add column tax_p double(20,3) null;

alter table ret_out_his
add column tax_amt double(20,3) null,
add column tax_p double(20,3) null;

alter table pur_his
add column grn_vou_no varchar(25) null;

alter table stock
drop index stock_code;

alter table stock_brand
drop index brand_id;

alter table unit_relation_detail
change column comp_code comp_code varchar(15) not null default '0010010' after rel_code,
change column unique_id unique_id int(11) not null after comp_code,
drop primary key,
add primary key (rel_code, comp_code, unique_id);

alter table ret_in_his
add column grand_total double(20,3) null;

alter table ret_out_his
add column grand_total double(20,3) null;

alter table ret_in_his
add column reference varchar(255) null,
add column dept_code varchar(15) null,
add column cash_acc varchar(15) null after dept_code,
add column debtor_acc varchar(15) null after cash_acc
add column src_acc varchar(15) null after debtor_acc;


alter table ret_out_his
add column reference varchar(255) null,
add column dept_code varchar(15) null,
add column cash_acc varchar(15) null after dept_code,
add column payable_acc varchar(15) null after cash_acc
add column src_acc varchar(15) after payable_acc;

set sql_safe_updates =0;
update ret_out_his
set grand_total =(vou_total+tax_amt-discount);
update ret_in_his
set grand_total =(vou_total+tax_amt-discount);

alter table pattern
add column amount double(20,3) null after price,
change column f_stock_code f_stock_code varchar(15) not null after stock_code,
change column unique_id unique_id int(11) not null after f_stock_code,
change column comp_code comp_code varchar(15) not null after unique_id,
change column dept_id dept_id int(11) not null default 1 after comp_code,
drop primary key,
add primary key (stock_code, f_stock_code, unique_id, comp_code);

set sql_safe_updates=0;
update pattern
set amount = qty*price;

alter table ret_in_his
add column s_rec bit(1) not null default 0;

alter table ret_out_his
add column s_pay bit(1) not null default 0;

alter table order_his
add column ref_no varchar(255) null after post;
alter table order_his_detail
add column heat_press_qty double(20,3) null after size;

alter table job
add column output_qty double(20,3) null after dept_id,
add column output_cost double(20,3) null after output_qty;

alter table sale_his_detail
add column design text null after bag,
add column size text null after design;

alter table sale_his_detail
add column length double(20,3) null after bag,
add column height double(20,3) null after length,
add column divider double(20,3) null after height,
add column total_sqft double(20,3) null after divider;

#view
drop view if exists v_milling_output;
create  view v_milling_output as select sh.project_no as project_no,sh.vou_no as vou_no,sh.trader_code as trader_code,sh.vou_date as vou_date,sh.cur_code as cur_code,sh.remark as remark,sh.created_date as created_date,sh.created_by as created_by,sh.deleted as deleted,sh.updated_by as updated_by,sh.updated_date as updated_date,sh.comp_code as comp_code,sh.mac_id as mac_id,sh.reference as reference,sh.dept_id as dept_id,sd.stock_code as stock_code,sd.weight as weight,sd.weight_unit as weight_unit,sd.qty as qty,sd.unit as unit,sd.price as price,sd.amt as amt,sd.loc_code as loc_code,sd.tot_weight as tot_weight,sd.unique_id as unique_id,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as cat_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((milling_his sh join milling_output sd) join stock s) where sh.vou_no = sd.vou_no and sh.comp_code = sd.comp_code and sd.stock_code = s.stock_code and sd.comp_code = s.comp_code;
drop view if exists v_milling_raw;
create  view v_milling_raw as select sh.project_no as project_no,sh.vou_no as vou_no,sh.trader_code as trader_code,sh.vou_date as vou_date,sh.cur_code as cur_code,sh.remark as remark,sh.created_date as created_date,sh.created_by as created_by,sh.deleted as deleted,sh.updated_by as updated_by,sh.updated_date as updated_date,sh.comp_code as comp_code,sh.mac_id as mac_id,sh.reference as reference,sh.dept_id as dept_id,sd.stock_code as stock_code,sd.weight as weight,sd.weight_unit as weight_unit,sd.qty as qty,sd.unit as unit,sd.price as price,sd.amt as amt,sd.loc_code as loc_code,sd.tot_weight as tot_weight,sd.unique_id as unique_id,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as cat_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((milling_his sh join milling_raw sd) join stock s) where sh.vou_no = sd.vou_no and sh.comp_code = sd.comp_code and sd.stock_code = s.stock_code and sd.comp_code = s.comp_code;
drop view if exists v_opening;
create  view v_opening as select op.op_date as op_date,op.remark as remark,op.created_by as created_by,op.created_date as created_date,op.updated_date as updated_date,op.updated_by as updated_by,op.mac_id as mac_id,op.comp_code as comp_code,op.deleted as deleted,op.op_amt as op_amt,op.dept_id as dept_id,op.cur_code as cur_code,op.tran_source as tran_source,op.trader_code as trader_code,opd.stock_code as stock_code,opd.qty as qty,opd.price as price,opd.amount as amount,opd.loc_code as loc_code,opd.unit as unit,opd.vou_no as vou_no,opd.unique_id as unique_id,opd.weight as weight,opd.weight_unit as weight_unit,opd.total_weight as total_weight,opd.wet as wet,opd.rice as rice,opd.bag as bag,round(iszero(opd.qty,opd.bag) * opd.wet,2) as ttl_wet,round(iszero(opd.qty,opd.bag) * opd.rice,2) as ttl_rice,s.user_code as stock_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((op_his op join op_his_detail opd on(op.vou_no = opd.vou_no and op.comp_code = opd.comp_code)) join stock s on(opd.stock_code = s.stock_code and opd.comp_code = s.comp_code));
drop view if exists v_return_in;
create  view v_return_in as select rh.project_no as project_no,rh.balance as balance,rh.created_by as created_by,rh.created_date as created_date,rh.deleted as deleted,rh.discount as discount,rh.paid as paid,rh.vou_date as vou_date,rh.ref_no as ref_no,rh.remark as remark,rh.session_id as session_id,rh.updated_by as updated_by,rh.s_rec as s_rec,rh.vou_total as vou_total,rh.cur_code as cur_code,rh.trader_code as trader_code,rh.disc_p as disc_p,rh.intg_upd_status as intg_upd_status,rh.mac_id as mac_id,rh.comp_code as comp_code,rh.dept_id as dept_id,rd.vou_no as vou_no,rd.stock_code as stock_code,rd.qty as qty,rd.unit as unit,rd.price as price,rd.amt as amt,rd.loc_code as loc_code,rd.unique_id as unique_id,rd.weight as weight,rd.weight_unit as weight_unit,rd.total_weight as total_weight,rd.wet as wet,rd.rice as rice,rd.bag as bag,iszero(rd.qty,rd.bag) * rd.wet as ttl_wet,iszero(rd.qty,rd.bag) * rd.rice as ttl_rice,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((ret_in_his rh join ret_in_his_detail rd on(rh.vou_no = rd.vou_no and rh.comp_code = rd.comp_code)) join stock s on(rd.stock_code = s.stock_code and rd.comp_code = s.comp_code));
drop view if exists v_return_out;
create  view v_return_out as select rh.project_no as project_no,rh.balance as balance,rh.created_by as created_by,rh.created_date as created_date,rh.deleted as deleted,rh.discount as discount,rh.paid as paid,rh.vou_date as vou_date,rh.ref_no as ref_no,rh.remark as remark,rh.session_id as session_id,rh.updated_by as updated_by,rh.s_pay as s_pay,rh.vou_total as vou_total,rh.cur_code as cur_code,rh.trader_code as trader_code,rh.disc_p as disc_p,rh.intg_upd_status as intg_upd_status,rh.mac_id as mac_id,rh.comp_code as comp_code,rh.dept_id as dept_id,rd.vou_no as vou_no,rd.stock_code as stock_code,rd.qty as qty,rd.unit as unit,rd.price as price,rd.amt as amt,rd.loc_code as loc_code,rd.unique_id as unique_id,rd.weight as weight,rd.weight_unit as weight_unit,rd.total_weight as total_weight,rd.wet as wet,rd.rice as rice,rd.bag as bag,iszero(rd.qty,rd.bag) * rd.wet as ttl_wet,iszero(rd.qty,rd.bag) * rd.rice as ttl_rice,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((ret_out_his rh join ret_out_his_detail rd on(rh.vou_no = rd.vou_no and rh.comp_code = rd.comp_code)) join stock s on(rd.stock_code = s.stock_code and rd.comp_code = s.comp_code));
drop view if exists v_order;
create view v_order as select oh.order_status as order_status,oh.project_no as project_no,oh.vou_no as vou_no,oh.comp_code as comp_code,oh.dept_id as dept_id,oh.trader_code as trader_code,oh.saleman_code as saleman_code,oh.vou_date as vou_date,oh.credit_term as credit_term,oh.cur_code as cur_code,oh.remark as remark,oh.vou_total as vou_total,oh.created_date as created_date,oh.created_by as created_by,oh.deleted as deleted,oh.updated_by as updated_by,oh.updated_date as updated_date,oh.mac_id as mac_id,oh.intg_upd_status as intg_upd_status,oh.reference as reference,oh.vou_lock as vou_lock,oh.post as post,ohd.unique_id as unique_id,ohd.stock_code as stock_code,ohd.order_qty as order_qty,ohd.qty as qty,ohd.unit as unit,ohd.price as price,ohd.amt as amt,ohd.loc_code as loc_code,ohd.weight as weight,ohd.weight_unit as weight_unit,ohd.design as design,ohd.size as size,s.user_code as user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((order_his oh join order_his_detail ohd on(oh.vou_no = ohd.vou_no and oh.comp_code = ohd.comp_code)) join stock s on(ohd.stock_code = s.stock_code and ohd.comp_code = s.comp_code));
drop view if exists v_weight_loss;
create  view v_weight_loss as select wlh.vou_no as vou_no,wlh.comp_code as comp_code,wlh.dept_id as dept_id,wlh.vou_date as vou_date,wlh.ref_no as ref_no,wlh.remark as remark,wlh.created_by as created_by,wlh.updated_by as updated_by,wlh.updated_date as updated_date,wlh.mac_id as mac_id,wlh.deleted as deleted,wlhd.unique_id as unique_id,wlhd.stock_code as stock_code,wlhd.qty as qty,wlhd.unit as unit,wlhd.price as price,wlhd.loss_qty as loss_qty,wlhd.loss_unit as loss_unit,wlhd.loss_price as loss_price,wlhd.loc_code as loc_code,s.user_code as user_code,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.calculate as calculate,s.rel_code as rel_code from ((weight_loss_his wlh join weight_loss_his_detail wlhd on(wlh.vou_no = wlhd.vou_no and wlh.comp_code = wlhd.comp_code)) join stock s on(wlhd.stock_code = s.stock_code and wlhd.comp_code = s.comp_code));
drop view if exists v_purchase;
create  view v_purchase as select ph.project_no as project_no,ph.vou_date as vou_date,ph.balance as balance,ph.deleted as deleted,ph.discount as discount,ph.due_date as due_date,ph.paid as paid,ph.remark as remark,ph.ref_no as ref_no,ph.updated_by as updated_by,ph.updated_date as updated_date,ph.created_by as created_by,ph.created_date as created_date,ph.vou_total as vou_total,ph.cur_code as cur_code,ph.trader_code as trader_code,ph.disc_p as disc_p,ph.tax_p as tax_p,ph.tax_amt as tax_amt,ph.dept_id as dept_id,ph.intg_upd_status as intg_upd_status,ph.comp_code as comp_code,ph.reference as reference,ph.batch_no as batch_no,ph.labour_group_code as labour_group_code,ph.land_vou_no as land_vou_no,ph.grand_total as grand_total,ph.s_rec as s_rec,pd.vou_no as vou_no,pd.stock_code as stock_code,pd.exp_date as exp_date,pd.avg_qty as avg_qty,pd.qty as qty,pd.weight as weight,pd.weight_unit as weight_unit,pd.std_weight as std_weight,pd.pur_unit as pur_unit,pd.pur_price as pur_price,pd.pur_amt as pur_amt,pd.loc_code as loc_code,pd.unique_id as unique_id,pd.total_weight as total_weight,pd.wet as wet,pd.rice as rice,pd.bag as bag,iszero(pd.qty,pd.bag) * pd.wet as ttl_wet,iszero(pd.qty,pd.bag) * pd.rice as ttl_rice,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate,st.group_type as group_type from (((pur_his ph join pur_his_detail pd on(ph.vou_no = pd.vou_no and ph.comp_code = pd.comp_code)) join stock s on(pd.stock_code = s.stock_code and pd.comp_code = s.comp_code)) join stock_type st on(s.stock_type_code = st.stock_type_code and s.comp_code = st.comp_code));
drop view if exists v_transfer;
create  view v_transfer as select th.vou_no as vou_no,th.created_by as created_by,th.created_date as created_date,th.deleted as deleted,th.vou_date as vou_date,th.ref_no as ref_no,th.remark as remark,th.updated_by as updated_by,th.updated_date as updated_date,th.loc_code_from as loc_code_from,th.loc_code_to as loc_code_to,th.mac_id as mac_id,th.dept_id as dept_id,th.comp_code as comp_code,th.trader_code as trader_code,th.labour_group_code as labour_group_code,td.stock_code as stock_code,td.qty as qty,td.unit as unit,td.unique_id as unique_id,td.weight as weight,td.weight_unit as weight_unit,td.total_weight as total_weight,td.wet as wet,td.rice as rice,td.bag as bag,td.price as price,td.amount as amount,iszero(td.qty,td.bag) * td.wet as ttl_wet,iszero(td.qty,td.bag) * td.rice as ttl_rice,s.user_code as user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate,s.sale_price_n as sale_price_n from ((transfer_his th join transfer_his_detail td on(th.vou_no = td.vou_no and th.comp_code = td.comp_code)) join stock s on(td.stock_code = s.stock_code and td.comp_code = s.comp_code));
drop view if exists v_milling_usage;
create  view v_milling_usage as select mh.vou_no as vou_no,mh.comp_code as comp_code,mh.trader_code as trader_code,mh.vou_date as vou_date,mh.remark as remark,mh.deleted as deleted,mh.job_no as job_no,mu.stock_code as stock_code,mu.qty as qty,mu.unit as unit,mu.loc_code as loc_code,s.user_code as user_code,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.calculate as calculate,s.rel_code as rel_code from ((milling_his mh join milling_usage mu on(mh.vou_no = mu.vou_no and mh.comp_code = mu.comp_code)) join stock s on(mu.stock_code = s.stock_code and mu.comp_code = s.comp_code));
drop view if exists v_process_his_detail;
create  view v_process_his_detail as select pd.vou_no as vou_no,pd.stock_code as stock_code,pd.comp_code as comp_code,pd.dept_id as dept_id,pd.unique_id as unique_id,pd.vou_date as vou_date,pd.qty as qty,pd.unit as unit,pd.price as price,pd.loc_code as loc_code,p.deleted as deleted,p.pt_code as pt_code,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.calculate as calculate,s.rel_code as rel_code from ((process_his_detail pd join stock s on(pd.stock_code = s.stock_code and pd.comp_code = s.comp_code)) join process_his p on(pd.vou_no = p.vou_no and pd.comp_code = p.comp_code));
drop view if exists v_process_his;
create  view v_process_his as select p.vou_no as vou_no,p.stock_code as stock_code,p.comp_code as comp_code,p.dept_id as dept_id,p.loc_code as loc_code,p.vou_date as vou_date,p.end_date as end_date,p.qty as qty,p.unit as unit,p.avg_qty as avg_qty,p.price as price,p.remark as remark,p.process_no as process_no,p.pt_code as pt_code,p.finished as finished,p.deleted as deleted,p.created_by as created_by,p.updated_by as updated_by,p.mac_id as mac_id,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.calculate as calculate,s.rel_code as rel_code from (process_his p join stock s on(p.stock_code = s.stock_code and p.comp_code = s.comp_code));
drop view if exists v_sale;
create view v_sale as select sh.order_no as order_no,sh.project_no as project_no,sh.vou_no as vou_no,sh.trader_code as trader_code,sh.saleman_code as saleman_code,sh.vou_date as vou_date,sh.credit_term as credit_term,sh.cur_code as cur_code,sh.remark as remark,sh.vou_total as vou_total,sh.grand_total as grand_total,sh.discount as discount,sh.disc_p as disc_p,sh.tax_amt as tax_amt,sh.tax_p as tax_p,sh.created_date as created_date,sh.created_by as created_by,sh.deleted as deleted,sh.paid as paid,sh.vou_balance as vou_balance,sh.updated_by as updated_by,sh.updated_date as updated_date,sh.comp_code as comp_code,sh.address as address,sh.order_code as order_code,sh.mac_id as mac_id,sh.session_id as session_id,sh.reference as reference,sh.dept_id as dept_id,sh.post as post,sh.s_pay as s_pay,sh.opening as opening,sh.outstanding as outstanding,sh.total_balance as total_balance,sh.total_payment as total_payment,sd.stock_code as stock_code,sd.expire_date as expire_date,sd.weight as weight,sd.weight_unit as weight_unit,sd.qty as qty,sd.sale_unit as sale_unit,sd.sale_price as sale_price,sd.sale_amt as sale_amt,sd.loc_code as loc_code,sd.batch_no as batch_no,sd.unique_id as unique_id,sd.wet as wet,sd.rice as rice,sd.bag as bag,sd.design as design,sd.size as size,if(coalesce(sd.total_weight,0) = 0,sd.qty * s.weight,sd.total_weight) as total_weight,iszero(sd.qty,sd.bag) * sd.wet as ttl_wet,iszero(sd.qty,sd.bag) * sd.rice as ttl_rice,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as cat_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate,st.group_type as group_type from (((sale_his sh join sale_his_detail sd on(sh.vou_no = sd.vou_no and sh.comp_code = sd.comp_code)) left join stock s on(sd.stock_code = s.stock_code and sd.comp_code = s.comp_code)) left join stock_type st on(s.stock_type_code = st.stock_type_code and s.comp_code = st.comp_code));
drop view if exists v_stock_io;
create  view v_stock_io as select i.vou_date as vou_date,i.remark as remark,i.description as description,i.comp_code as comp_code,i.mac_id as mac_id,i.created_date as created_date,i.created_by as created_by,i.vou_status as vou_status,i.deleted as deleted,i.dept_id as dept_id,i.labour_group_code as labour_group_code,i.job_code as job_code,i.received_name as received_name,i.received_phone as received_phone,i.car_no as car_no,i.trader_code as trader_code,iod.vou_no as vou_no,iod.unique_id as unique_id,iod.stock_code as stock_code,iod.loc_code as loc_code,iod.in_qty as in_qty,iod.in_unit as in_unit,iod.out_qty as out_qty,iod.out_unit as out_unit,iod.cur_code as cur_code,iod.cost_price as cost_price,iod.weight as weight,if(ifnull(iod.total_weight,0) = 0,if(coalesce(iod.in_qty,0) = 0,coalesce(iod.out_qty,0),coalesce(iod.in_qty,0)) * s.weight,0) as total_weight,coalesce(iod.weight_unit,s.weight_unit) as weight_unit,iod.wet as wet,iod.rice as rice,iod.in_bag as in_bag,iod.out_bag as out_bag,iod.amount as amount,iszero(iszero(iod.in_qty,iod.out_qty),iszero(iod.in_bag,iod.out_bag)) * iod.wet as ttl_wet,iszero(iszero(iod.in_qty,iod.out_qty),iszero(iod.in_bag,iod.out_bag)) * iod.rice as ttl_rice,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.user_code as s_user_code,s.calculate as calculate from ((stock_in_out i join stock_in_out_detail iod on(i.vou_no = iod.vou_no and i.comp_code = iod.comp_code)) join stock s on(iod.stock_code = s.stock_code and iod.comp_code = s.comp_code));
drop view if exists v_consign;
create view v_consign as select th.vou_no as vou_no,th.created_by as created_by,th.created_date as created_date,th.deleted as deleted,th.vou_date as vou_date,th.description as description,th.remark as remark,th.updated_by as updated_by,th.updated_date as updated_date,th.loc_code as loc_code,th.mac_id as mac_id,th.dept_id as dept_id,th.comp_code as comp_code,th.trader_code as trader_code,th.tran_source as tran_source,th.labour_group_code as labour_group_code,td.stock_code as stock_code,td.unique_id as unique_id,td.wet as wet,td.bag as bag,td.qty as qty,td.weight as weight,td.rice as rice,td.price as price,td.amount as amount,s.user_code as stock_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((consign_his th join consign_his_detail td on(th.vou_no = td.vou_no and th.comp_code = td.comp_code)) join stock s on(td.stock_code = s.stock_code and td.comp_code = s.comp_code));
drop view if exists v_stock_payment;
create  view v_stock_payment as select sp.vou_no as vou_no,sp.comp_code as comp_code,sp.dept_id as dept_id,sp.vou_date as vou_date,sp.trader_code as trader_code,sp.deleted as deleted,sp.tran_option as tran_option,sp.calculate as calculate,sp.loc_code as loc_code,spd.stock_code as stock_code,spd.ref_no as ref_no,spd.qty as qty,spd.pay_qty as pay_qty,spd.bal_qty as bal_qty,spd.pay_bag as pay_bag,spd.bal_bag as bal_bag,spd.remark as remark,spd.reference as reference,spd.full_paid as full_paid,spd.project_no as project_no,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code from ((stock_payment sp join stock_payment_detail spd) join stock s) where sp.vou_no = spd.vou_no and sp.comp_code = spd.comp_code and spd.stock_code = s.stock_code and spd.comp_code = s.comp_code;
drop view if exists v_relation;
create view v_relation as select r.rel_code as rel_code,r.rel_name as rel_name,r.comp_code as comp_code,r.dept_id as dept_id,rd.unit as unit,rd.qty as qty,rd.smallest_qty as smallest_qty,rd.unique_id as unique_id from (unit_relation r join unit_relation_detail rd on(r.rel_code = rd.rel_code and r.comp_code = rd.comp_code));
