alter table vou_status 
change column updated_date updated_date timestamp not null default current_timestamp() on update current_timestamp();
