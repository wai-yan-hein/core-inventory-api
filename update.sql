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

create  view v_transfer as select th.vou_no as vou_no,th.created_by as created_by,th.created_date as created_date,th.deleted as deleted,th.vou_date as vou_date,th.ref_no as ref_no,th.remark as remark,th.updated_by as updated_by,th.updated_date as updated_date,th.loc_code_from as loc_code_from,th.loc_code_to as loc_code_to,th.mac_id as mac_id,th.comp_code as comp_code,td.td_code as td_code,td.stock_code as stock_code,td.qty as qty,td.wt as wt,td.unit as unit,td.unique_id as unique_id,s.user_code as user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((transfer_his th join transfer_his_detail td on(th.vou_no = td.vou_no)) join stock s on(td.stock_code = s.stock_code));

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
add primary key (rel_code, dept_id,comp_code);

alter table trader_group
add column dept_id int not null default 1,
drop primary key,
add primary key (group_code, dept_id,comp_code);

alter table trader
add column dept_id int not null default 1,
drop primary key,
add primary key (code, dept_id,comp_code);

alter table stock_unit
add column dept_id int not null default 1,
drop primary key,
add primary key (unit_code, dept_id,comp_code);

alter table stock_unit
drop index item_unit_name_UNIQUE ,
drop index item_unit_code ;

alter table stock_type
add column dept_id int not null default 1,
drop primary key,
add primary key (stock_type_code, dept_id,comp_code);

alter table stock_brand
add column dept_id int not null default 1,
drop primary key,
add primary key (brand_code, dept_id,comp_code);

alter table stock
add column dept_id int not null default 1,
drop primary key,
add primary key (stock_code, dept_id,comp_code);

alter table sale_man
add column dept_id int not null default 1,
drop primary key,
add primary key (saleman_code, dept_id,comp_code);

alter table category
add column dept_id int not null default 1 after comp_code,
drop primary key,
add primary key (cat_code, dept_id,comp_code);

alter table location
add column dept_id int not null default 1,
drop primary key,
add primary key (loc_code, dept_id,comp_code);

alter table op_his
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no, dept_id,comp_code);

alter table op_his_detail
add column dept_id int not null default 1;

alter table pur_his
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no, dept_id,comp_code);

alter table ret_in_his
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no, dept_id,comp_code);

alter table ret_in_his_detail
add column dept_id int not null default 1;

alter table ret_in_his_detail
add column avg_qty float(20,3) not null default 0 after dept_id;

alter table ret_in_his_detail
drop column cost_price;

alter table ret_out_his
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no, dept_id,comp_code);

alter table ret_out_his_detail
add column dept_id int not null default 1;


alter table sale_his
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no, dept_id,comp_code);

alter table sale_his_detail
add column dept_id int not null default 1;

alter table bk_sale_his
add column dept_id int not null default 1;

alter table bk_sale_his_detail
add column dept_id int not null default 1;

alter table stock_in_out
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no, dept_id,comp_code);

alter table transfer_his
add column dept_id int not null default 1,
drop primary key,
add primary key (vou_no, dept_id,comp_code);

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


#view
drop view if exists v_opening;
create  view v_opening as select op.op_date as op_date,op.remark as remark,op.created_by as created_by,op.created_date as created_date,op.updated_date as updated_date,op.updated_by as updated_by,op.mac_id as mac_id,op.comp_code as comp_code,op.deleted as deleted,op.op_amt as op_amt,op.dept_id as dept_id,opd.op_code as op_code,opd.stock_code as stock_code,opd.qty as qty,opd.price as price,opd.amount as amount,opd.loc_code as loc_code,opd.unit as unit,opd.vou_no as vou_no,opd.unique_id as unique_id,s.user_code as stock_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((op_his op join op_his_detail opd on(op.vou_no = opd.vou_no)) join stock s on(opd.stock_code = s.stock_code));

drop view if exists v_relation;
create  view v_relation as select r.rel_code as rel_code,r.rel_name as rel_name,r.comp_code as comp_code,r.dept_id as dept_id,rd.unit as unit,rd.qty as qty,rd.smallest_qty as smallest_qty,rd.unique_id as unique_id from (unit_relation r join unit_relation_detail rd on(r.rel_code = rd.rel_code));

drop view if exists v_reorder_level;
create  view v_reorder_level as select rl.stock_code as stock_code,rl.min_qty as min_qty,rl.min_unit as min_unit,rl.max_qty as max_qty,rl.max_unit as max_unit,rl.bal_qty as bal_qty,rl.bal_unit as bal_unit,rl.comp_code as comp_code,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code from (reorder_level rl join stock s on(rl.stock_code = s.stock_code));


drop view if exists v_stock_io;
create  view v_stock_io as select i.vou_date as vou_date,i.remark as remark,i.description as description,i.comp_code as comp_code,i.mac_id as mac_id,i.created_date as created_date,i.created_by as created_by,i.vou_status as vou_status,i.deleted as deleted,i.dept_id as dept_id,iod.sd_code as sd_code,iod.vou_no as vou_no,iod.unique_id as unique_id,iod.stock_code as stock_code,iod.loc_code as loc_code,iod.in_qty as in_qty,iod.in_unit as in_unit,iod.out_qty as out_qty,iod.out_unit as out_unit,iod.cur_code as cur_code,iod.cost_price as cost_price,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.user_code as s_user_code,s.calculate as calculate from ((stock_in_out i join stock_in_out_detail iod on(i.vou_no = iod.vou_no)) join stock s on(iod.stock_code = s.stock_code));

drop view if exists v_transfer;
create  view v_transfer as select th.vou_no as vou_no,th.created_by as created_by,th.created_date as created_date,th.deleted as deleted,th.vou_date as vou_date,th.ref_no as ref_no,th.remark as remark,th.updated_by as updated_by,th.updated_date as updated_date,th.loc_code_from as loc_code_from,th.loc_code_to as loc_code_to,th.mac_id as mac_id,th.dept_id as dept_id,th.comp_code as comp_code,td.td_code as td_code,td.stock_code as stock_code,td.qty as qty,td.unit as unit,td.unique_id as unique_id,s.user_code as user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((transfer_his th join transfer_his_detail td on(th.vou_no = td.vou_no)) join stock s on(td.stock_code = s.stock_code));

drop view if exists v_process_his;
create view v_process_his as select p.vou_no as vou_no,p.stock_code as stock_code,p.comp_code as comp_code,p.dept_id as dept_id,p.loc_code as loc_code,p.vou_date as vou_date,p.end_date as end_date,p.qty as qty,p.unit as unit,p.price as price,p.remark as remark,p.process_no as process_no,p.pt_code as pt_code,p.finished as finished,p.deleted as deleted,p.created_by as created_by,p.updated_by as updated_by,p.mac_id as mac_id,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.calculate as calculate,s.rel_code as rel_code from (process_his p join stock s on(p.stock_code = s.stock_code and p.comp_code = s.comp_code and p.dept_id = s.dept_id));

drop view if exists v_process_his_detail;
create  view v_process_his_detail as select pd.vou_no as vou_no,pd.stock_code as stock_code,pd.comp_code as comp_code,pd.dept_id as dept_id,pd.unique_id as unique_id,pd.vou_date as vou_date,pd.qty as qty,pd.unit as unit,pd.price as price,pd.loc_code as loc_code,p.deleted as deleted,p.pt_code as pt_code,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.calculate as calculate,s.rel_code as rel_code from ((process_his_detail pd join stock s on(pd.stock_code = s.stock_code and pd.comp_code = s.comp_code and pd.dept_id = s.dept_id)) join process_his p on(pd.vou_no = p.vou_no and pd.comp_code = p.comp_code and pd.dept_id = p.dept_id));

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

drop view if exists v_sale;
create view v_sale as select sh.order_no as order_no,sh.project_no as project_no,sh.vou_no as vou_no,sh.trader_code as trader_code,sh.saleman_code as saleman_code,sh.vou_date as vou_date,sh.credit_term as credit_term,sh.cur_code as cur_code,sh.remark as remark,sh.vou_total as vou_total,sh.grand_total as grand_total,sh.discount as discount,sh.disc_p as disc_p,sh.tax_amt as tax_amt,sh.tax_p as tax_p,sh.created_date as created_date,sh.created_by as created_by,sh.deleted as deleted,sh.paid as paid,sh.vou_balance as vou_balance,sh.updated_by as updated_by,sh.updated_date as updated_date,sh.comp_code as comp_code,sh.address as address,sh.order_code as order_code,sh.mac_id as mac_id,sh.session_id as session_id,sh.reference as reference,sh.dept_id as dept_id,sd.stock_code as stock_code,sd.expire_date as expire_date,sd.weight as weight,sd.weight_unit as weight_unit,sd.qty as qty,sd.sale_unit as sale_unit,sd.sale_price as sale_price,sd.sale_amt as sale_amt,sd.loc_code as loc_code,sd.batch_no as batch_no,sd.unique_id as unique_id,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as cat_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((sale_his sh join sale_his_detail sd on(sh.vou_no = sd.vou_no)) join stock s on(sd.stock_code = s.stock_code));

drop view if exists v_purchase;
create  view v_purchase as select ph.project_no as project_no,ph.vou_date as vou_date,ph.balance as balance,ph.deleted as deleted,ph.discount as discount,ph.due_date as due_date,ph.paid as paid,ph.remark as remark,ph.ref_no as ref_no,ph.updated_by as updated_by,ph.updated_date as updated_date,ph.created_by as created_by,ph.created_date as created_date,ph.vou_total as vou_total,ph.cur_code as cur_code,ph.trader_code as trader_code,ph.disc_p as disc_p,ph.tax_p as tax_p,ph.tax_amt as tax_amt,ph.dept_id as dept_id,ph.intg_upd_status as intg_upd_status,ph.comp_code as comp_code,ph.reference as reference,ph.batch_no as batch_no,pd.vou_no as vou_no,pd.stock_code as stock_code,pd.exp_date as exp_date,pd.avg_qty as avg_qty,pd.qty as qty,pd.weight as weight,pd.weight_unit as weight_unit,pd.std_weight as std_weight,pd.pur_unit as pur_unit,pd.pur_price as pur_price,pd.pur_amt as pur_amt,pd.loc_code as loc_code,pd.unique_id as unique_id,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((pur_his ph join pur_his_detail pd on(ph.vou_no = pd.vou_no)) join stock s on(pd.stock_code = s.stock_code));

drop view if exists v_return_in;
create  view v_return_in as select rh.project_no as project_no,rh.balance as balance,rh.created_by as created_by,rh.created_date as created_date,rh.deleted as deleted,rh.discount as discount,rh.paid as paid,rh.vou_date as vou_date,rh.ref_no as ref_no,rh.remark as remark,rh.session_id as session_id,rh.updated_by as updated_by,rh.updated_date as updated_date,rh.vou_total as vou_total,rh.cur_code as cur_code,rh.trader_code as trader_code,rh.disc_p as disc_p,rh.intg_upd_status as intg_upd_status,rh.mac_id as mac_id,rh.comp_code as comp_code,rh.dept_id as dept_id,rd.vou_no as vou_no,rd.stock_code as stock_code,if(rd.avg_qty = 0,rd.qty,rd.avg_qty) as qty,rd.unit as unit,rd.price as price,rd.amt as amt,rd.loc_code as loc_code,rd.unique_id as unique_id,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((ret_in_his rh join ret_in_his_detail rd on(rh.vou_no = rd.vou_no)) join stock s on(rd.stock_code = s.stock_code));

drop view if exists v_return_out;
create  view v_return_out as select rh.project_no as project_no,rh.balance as balance,rh.created_by as created_by,rh.created_date as created_date,rh.deleted as deleted,rh.discount as discount,rh.paid as paid,rh.vou_date as vou_date,rh.ref_no as ref_no,rh.remark as remark,rh.session_id as session_id,rh.updated_by as updated_by,rh.updated_date as updated_date,rh.vou_total as vou_total,rh.cur_code as cur_code,rh.trader_code as trader_code,rh.disc_p as disc_p,rh.intg_upd_status as intg_upd_status,rh.mac_id as mac_id,rh.comp_code as comp_code,rh.dept_id as dept_id,rd.vou_no as vou_no,rd.stock_code as stock_code,rd.qty as qty,rd.unit as unit,rd.price as price,rd.amt as amt,rd.loc_code as loc_code,rd.unique_id as unique_id,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((ret_out_his rh join ret_out_his_detail rd on(rh.vou_no = rd.vou_no)) join stock s on(rd.stock_code = s.stock_code));

drop view if exists v_order;
create view v_order as select oh.project_no as project_no,oh.vou_no as vou_no,oh.comp_code as comp_code,oh.dept_id as dept_id,oh.trader_code as trader_code,oh.saleman_code as saleman_code,oh.vou_date as vou_date,oh.credit_term as credit_term,oh.cur_code as cur_code,oh.remark as remark,oh.vou_total as vou_total,oh.grand_total as grand_total,oh.discount as discount,oh.disc_p as disc_p,oh.tax_amt as tax_amt,oh.tax_p as tax_p,oh.created_date as created_date,oh.created_by as created_by,oh.deleted as deleted,oh.paid as paid,oh.vou_balance as vou_balance,oh.updated_by as updated_by,oh.updated_date as updated_date,oh.address as address,oh.mac_id as mac_id,oh.intg_upd_status as intg_upd_status,oh.reference as reference,oh.vou_lock as vou_lock,ohd.unique_id as unique_id,ohd.stock_code as stock_code,ohd.qty as qty,ohd.unit as unit,ohd.price as price,ohd.amt as amt,ohd.loc_code as loc_code,ohd.weight as weight,ohd.weight_unit as weight_unit,ohd.std_weight as std_weight,s.user_code as user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((order_his oh join order_his_detail ohd on(oh.vou_no = ohd.vou_no and oh.comp_code = ohd.comp_code and oh.dept_id = ohd.dept_id))join stock s on(ohd.stock_code = s.stock_code and ohd.comp_code = s.comp_code and ohd.dept_id = s.dept_id));

drop view if exists v_transfer;
create view v_transfer as select th.vou_no as vou_no,th.created_by as created_by,th.created_date as created_date,th.deleted as deleted,th.vou_date as vou_date,th.ref_no as ref_no,th.remark as remark,th.updated_by as updated_by,th.updated_date as updated_date,th.loc_code_from as loc_code_from,th.loc_code_to as loc_code_to,th.mac_id as mac_id,th.dept_id as dept_id,th.comp_code as comp_code,td.stock_code as stock_code,td.qty as qty,td.unit as unit,td.unique_id as unique_id,s.user_code as user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((transfer_his th join transfer_his_detail td on(th.vou_no = td.vou_no)) join stock s on(td.stock_code = s.stock_code));

drop view if exists v_stock_io;
create  view v_stock_io as select i.vou_date as vou_date,i.remark as remark,i.description as description,i.comp_code as comp_code,i.mac_id as mac_id,i.created_date as created_date,i.created_by as created_by,i.vou_status as vou_status,i.deleted as deleted,i.dept_id as dept_id,iod.vou_no as vou_no,iod.unique_id as unique_id,iod.stock_code as stock_code,iod.loc_code as loc_code,iod.in_qty as in_qty,iod.in_unit as in_unit,iod.out_qty as out_qty,iod.out_unit as out_unit,iod.cur_code as cur_code,iod.cost_price as cost_price,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.user_code as s_user_code,s.calculate as calculate from ((stock_in_out i join stock_in_out_detail iod on(i.vou_no = iod.vou_no)) join stock s on(iod.stock_code = s.stock_code));

drop view if exists v_opening;
create  view v_opening as select op.cur_code,op.op_date as op_date,op.remark as remark,op.created_by as created_by,op.created_date as created_date,op.updated_date as updated_date,op.updated_by as updated_by,op.mac_id as mac_id,op.comp_code as comp_code,op.deleted as deleted,op.op_amt as op_amt,op.dept_id as dept_id,opd.stock_code as stock_code,opd.qty as qty,opd.price as price,opd.amount as amount,opd.loc_code as loc_code,opd.unit as unit,opd.vou_no as vou_no,opd.unique_id as unique_id,s.user_code as stock_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((op_his op join op_his_detail opd on(op.vou_no = opd.vou_no)) join stock s on(opd.stock_code = s.stock_code));

drop view if exists v_grn;
create  view v_grn as select g.vou_no as vou_no,g.comp_code as comp_code,g.dept_id as dept_id,g.vou_date as vou_date,g.trader_code as trader_code,g.closed as closed,g.created_date as created_date,g.created_by as created_by,g.updated_date as updated_date,g.updated_by as updated_by,g.deleted as deleted,g.batch_no as batch_no,g.remark as remark,g.mac_id as mac_id,gd.stock_code as stock_code,gd.loc_code as loc_code,gd.qty as qty,gd.unit as unit,gd.weight as weight,gd.weight_unit as weight_unit,s.user_code as user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((grn g join grn_detail gd on(g.vou_no = gd.vou_no and g.comp_code = gd.comp_code)) join stock s on(gd.stock_code = s.stock_code and gd.comp_code = s.comp_code));

drop view if exists v_opening;
CREATE VIEW v_opening AS select op.cur_code AS cur_code,op.op_date AS op_date,op.remark AS remark,op.created_by AS created_by,op.created_date AS created_date,op.updated_date AS updated_date,op.updated_by AS updated_by,op.mac_id AS mac_id,op.comp_code AS comp_code,op.deleted AS deleted,op.op_amt AS op_amt,op.dept_id AS dept_id,opd.stock_code AS stock_code,opd.qty AS qty,opd.price AS price,opd.amount AS amount,opd.loc_code AS loc_code,opd.unit AS unit,opd.vou_no AS vou_no,opd.unique_id AS unique_id,s.user_code AS stock_user_code,s.stock_name AS stock_name,s.stock_type_code AS stock_type_code,s.brand_code AS brand_code,s.category_code AS category_code,s.rel_code AS rel_code,s.calculate AS calculate from ((op_his op join op_his_detail opd on(op.vou_no = opd.vou_no)) join stock s on(opd.stock_code = s.stock_code));

drop view if exists v_process_his;
create  view v_process_his as select p.vou_no as vou_no,p.stock_code as stock_code,p.comp_code as comp_code,p.dept_id as dept_id,p.loc_code as loc_code,p.vou_date as vou_date,p.end_date as end_date,p.qty as qty,p.unit as unit,p.price as price,p.remark as remark,p.process_no as process_no,p.pt_code as pt_code,p.finished as finished,p.deleted as deleted,p.created_by as created_by,p.updated_by as updated_by,p.mac_id as mac_id,s.user_code as user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.calculate as calculate,s.rel_code as rel_code from (process_his p join stock s on(p.stock_code = s.stock_code and p.comp_code = s.comp_code and p.dept_id = s.dept_id));

drop view if exists v_process_his_detail;
create  view v_process_his_detail as select pd.vou_no as vou_no,pd.stock_code as stock_code,pd.comp_code as comp_code,pd.dept_id as dept_id,pd.unique_id as unique_id,pd.vou_date as vou_date,pd.qty as qty,pd.unit as unit,pd.price as price,pd.loc_code as loc_code,p.deleted as deleted,p.pt_code as pt_code,s.user_code as user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.calculate as calculate,s.rel_code as rel_code from ((process_his_detail pd join stock s on(pd.stock_code = s.stock_code and pd.comp_code = s.comp_code and pd.dept_id = s.dept_id)) join process_his p on(pd.vou_no = p.vou_no and pd.comp_code = p.comp_code and pd.dept_id = p.dept_id));

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
