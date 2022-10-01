#setup
truncate category;
truncate location;
truncate machine_info;
truncate stock;
truncate stock_type;
truncate stock_brand;
truncate stock_unit;
truncate vou_status;
truncate unit_relation;
truncate unit_relation_detail;
truncate seq_table;
truncate trader;
#transaction
truncate op_his;
truncate op_his_detail;
truncate order_his;
truncate order_his_detail;
truncate pattern;
truncate pattern_detail;
truncate pur_his;
truncate pur_his_detail;
truncate ret_in_his;
truncate ret_in_his_detail;
truncate ret_out_his;
truncate ret_out_his_detail;
truncate sale_his;
truncate sale_his_detail;

#tmp
truncate tmp_closing_column;
truncate tmp_inv_closing;
truncate tmp_stock_balance;
truncate tmp_stock_io_column;
truncate tmp_stock_opening;
truncate tmp_stock_price;