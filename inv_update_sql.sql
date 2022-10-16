drop table if exists stock_op_value;
drop table if exists stock_op_value_log;
drop table if exists stock_report;
drop table if exists sys_prop;

create table department (
  dept_id int(11) not null,
  user_code varchar(15) not null,
  dept_name varchar(255) not null,
  queue_name varchar(50) default null,
  primary key (dept_id)
) engine=innodb default charset=utf8mb3;

insert into department (dept_id, user_code, dept_name) values ('1', 'H', 'Head Office');

alter table pattern 
add column dept_id int not null default 1;

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
drop column avg_wt,
drop column avg_price,
drop column std_wt,
add column avg_qty float(20,3) not null default 0 after comp_code;

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

drop view if exists v_opening;
create  view v_opening as select op.op_date as op_date,op.remark as remark,op.created_by as created_by,op.created_date as created_date,op.updated_date as updated_date,op.updated_by as updated_by,op.mac_id as mac_id,op.comp_code as comp_code,op.deleted as deleted,op.op_amt as op_amt,op.dept_id as dept_id,opd.op_code as op_code,opd.stock_code as stock_code,opd.qty as qty,opd.price as price,opd.amount as amount,opd.loc_code as loc_code,opd.unit as unit,opd.vou_no as vou_no,opd.unique_id as unique_id,s.user_code as stock_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((op_his op join op_his_detail opd on(op.vou_no = opd.vou_no)) join stock s on(opd.stock_code = s.stock_code));
drop view if exists v_purchase;
create  view v_purchase as select ph.vou_date as vou_date,ph.balance as balance,ph.deleted as deleted,ph.discount as discount,ph.due_date as due_date,ph.paid as paid,ph.remark as remark,ph.ref_no as ref_no,ph.updated_by as updated_by,ph.updated_date as updated_date,ph.created_by as created_by,ph.created_date as created_date,ph.vou_total as vou_total,ph.cur_code as cur_code,ph.trader_code as trader_code,ph.disc_p as disc_p,ph.tax_p as tax_p,ph.tax_amt as tax_amt,ph.dept_id as dept_id,ph.intg_upd_status as intg_upd_status,ph.comp_code as comp_code,ph.reference as reference,pd.pd_code as pd_code,pd.vou_no as vou_no,pd.stock_code as stock_code,pd.exp_date as exp_date,pd.avg_qty as avg_qty,if(pd.avg_qty = 0,pd.qty,pd.avg_qty) as qty,pd.pur_unit as pur_unit,pd.pur_price as pur_price,pd.pur_amt as pur_amt,pd.loc_code as loc_code,pd.unique_id as unique_id,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((pur_his ph join pur_his_detail pd on(ph.vou_no = pd.vou_no)) join stock s on(pd.stock_code = s.stock_code));
drop view if exists  v_return_in;
create view v_return_in as select rh.balance as balance,rh.created_by as created_by,rh.created_date as created_date,rh.deleted as deleted,rh.discount as discount,rh.paid as paid,rh.vou_date as vou_date,rh.ref_no as ref_no,rh.remark as remark,rh.session_id as session_id,rh.updated_by as updated_by,rh.updated_date as updated_date,rh.vou_total as vou_total,rh.cur_code as cur_code,rh.trader_code as trader_code,rh.disc_p as disc_p,rh.intg_upd_status as intg_upd_status,rh.mac_id as mac_id,rh.comp_code as comp_code,rh.dept_id as dept_id,rd.rd_code as rd_code,rd.vou_no as vou_no,rd.stock_code as stock_code,
if(rd.avg_qty=0,rd.qty,rd.avg_qty) as qty,rd.unit as unit,rd.price as price,rd.amt as amt,rd.loc_code as loc_code,rd.unique_id as unique_id,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((ret_in_his rh join ret_in_his_detail rd on(rh.vou_no = rd.vou_no)) join stock s on(rd.stock_code = s.stock_code));
drop view if exists v_return_in;
create  view v_return_in as select rh.balance as balance,rh.created_by as created_by,rh.created_date as created_date,rh.deleted as deleted,rh.discount as discount,rh.paid as paid,rh.vou_date as vou_date,rh.ref_no as ref_no,rh.remark as remark,rh.session_id as session_id,rh.updated_by as updated_by,rh.updated_date as updated_date,rh.vou_total as vou_total,rh.cur_code as cur_code,rh.trader_code as trader_code,rh.disc_p as disc_p,rh.intg_upd_status as intg_upd_status,rh.mac_id as mac_id,rh.comp_code as comp_code,rh.dept_id as dept_id,rd.rd_code as rd_code,rd.vou_no as vou_no,rd.stock_code as stock_code,if(rd.avg_qty = 0,rd.qty,rd.avg_qty) as qty,rd.avg_qty as avg_qty,rd.unit as unit,rd.price as price,rd.amt as amt,rd.loc_code as loc_code,rd.unique_id as unique_id,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.brand_code as brand_code,s.category_code as category_code,s.rel_code as rel_code,s.calculate as calculate from ((ret_in_his rh join ret_in_his_detail rd on(rh.vou_no = rd.vou_no)) join stock s on(rd.stock_code = s.stock_code));
drop view if exists v_sale;
create  view v_sale as select sh.vou_no as vou_no,sh.trader_code as trader_code,sh.saleman_code as saleman_code,sh.vou_date as vou_date,sh.credit_term as credit_term,sh.cur_code as cur_code,sh.remark as remark,sh.vou_total as vou_total,sh.grand_total as grand_total,sh.discount as discount,sh.disc_p as disc_p,sh.tax_amt as tax_amt,sh.tax_p as tax_p,sh.created_date as created_date,sh.created_by as created_by,sh.deleted as deleted,sh.paid as paid,sh.vou_balance as vou_balance,sh.updated_by as updated_by,sh.updated_date as updated_date,sh.comp_code as comp_code,sh.address as address,sh.order_code as order_code,sh.mac_id as mac_id,sh.session_id as session_id,sh.reference as reference,sh.dept_id as dept_id,sd.sd_code as sd_code,sd.stock_code as stock_code,sd.expire_date as expire_date,sd.qty as qty,sd.sale_unit as sale_unit,sd.sale_price as sale_price,sd.sale_amt as sale_amt,sd.loc_code as loc_code,sd.unique_id as unique_id,s.user_code as s_user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as cat_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((sale_his sh join sale_his_detail sd on(sh.vou_no = sd.vou_no)) join stock s on(sd.stock_code = s.stock_code));
drop view if exists v_stock_io;
create  if exists view v_stock_io as select i.vou_date as vou_date,i.remark as remark,i.description as description,i.comp_code as comp_code,i.mac_id as mac_id,i.created_date as created_date,i.created_by as created_by,i.vou_status as vou_status,i.deleted as deleted,i.dept_id as dept_id,iod.sd_code as sd_code,iod.vou_no as vou_no,iod.unique_id as unique_id,iod.stock_code as stock_code,iod.loc_code as loc_code,iod.in_qty as in_qty,iod.in_unit as in_unit,iod.out_qty as out_qty,iod.out_unit as out_unit,iod.cur_code as cur_code,iod.cost_price as cost_price,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.user_code as s_user_code,s.calculate as calculate from ((stock_in_out i join stock_in_out_detail iod on(i.vou_no = iod.vou_no)) join stock s on(iod.stock_code = s.stock_code));
drop view if exists v_transfer;
create  view v_transfer as select th.vou_no as vou_no,th.created_by as created_by,th.created_date as created_date,th.deleted as deleted,th.vou_date as vou_date,th.ref_no as ref_no,th.remark as remark,th.updated_by as updated_by,th.updated_date as updated_date,th.loc_code_from as loc_code_from,th.loc_code_to as loc_code_to,th.mac_id as mac_id,th.dept_id as dept_id,th.comp_code as comp_code,td.td_code as td_code,td.stock_code as stock_code,td.qty as qty,td.unit as unit,td.unique_id as unique_id,s.user_code as user_code,s.stock_name as stock_name,s.stock_type_code as stock_type_code,s.category_code as category_code,s.brand_code as brand_code,s.rel_code as rel_code,s.calculate as calculate from ((transfer_his th join transfer_his_detail td on(th.vou_no = td.vou_no)) join stock s on(td.stock_code = s.stock_code));

#intg_upd_status
alter table stock 
add column intg_upd_status varchar(15);

alter table pattern 
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
###
create table department (
  dept_id int(11) not null,
  user_code varchar(15) not null,
  dept_name varchar(255) not null,
  queue_name varchar(50) default null,
  primary key (dept_id)
) engine=innodb default charset=utf8mb3;



