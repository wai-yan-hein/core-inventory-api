
#sql for change float to double every table
set @SchemaName = 'cv_inv_kps';
set @OldDataType = 'FLOAT';
set @NewDataType = 'DOUBLE(20,3)';
set @SqlQuery = '';

select @SqlQuery := concat(@SqlQuery, 'ALTER TABLE ', TABLE_NAME, ' MODIFY COLUMN ', COLUMN_NAME, ' ', @NewDataType, ';')
from INFORMATION_SCHEMA.COLUMNS
where TABLE_SCHEMA = @SchemaName and DATA_TYPE = @OldDataType;

-- Print the generated SQL statements
select @SqlQuery;