package cv.api.service;

import cv.api.common.ClosingBalance;
import cv.api.common.ReturnObject;
import cv.api.common.StockValue;
import cv.api.common.Util1;
import cv.api.entity.UnitRelationDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockRelationService {
    private final HashMap<String, List<UnitRelationDetail>> hmRelation = new HashMap<>();
    private final HashMap<String, String> hmRelationName = new HashMap<>();
    private final DecimalFormat formatter = new DecimalFormat("###.##");
    private final DatabaseClient client;
    private final UnitRelationService unitRelationService;

    private Mono<Boolean> calculateOpening(String opDate, String fromDate, String typeCode,
                                           String catCode, String brandCode, String stockCode,
                                           String vouStatus, boolean calSale,
                                           boolean calPur, boolean calRI, boolean calRO,
                                           String compCode, Integer deptId, Integer macId) {
        String opSql = """
                INSERT INTO tmp_stock_opening(tran_date, stock_code, ttl_qty, loc_code, unit, comp_code, dept_id, mac_id)
                SELECT :fromDate op_date, stock_code, SUM(qty) ttl_qty, loc_code, unit, :compCode, :deptId, :macId
                FROM (
                    SELECT stock_code, SUM(qty) qty, loc_code, unit
                    FROM v_opening
                    WHERE DATE(op_date) = :opDate
                    AND comp_code = :compCode
                    AND tran_source = 1
                    AND deleted = false
                    AND calculate = true
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code, pur_unit
                    FROM v_purchase
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = false
                    AND (calculate = true AND :calPur = false)
                    AND comp_code = :compCode
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, pur_unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code, unit
                    FROM v_return_in
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = false
                    AND (calculate = true AND :calRI = false)
                    AND comp_code = :compCode
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(in_qty) qty, loc_code, in_unit
                    FROM v_stock_io
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = false
                    AND calculate = true
                    AND in_qty IS NOT NULL AND in_unit IS NOT NULL
                    AND comp_code = :compCode
                    AND (vou_status = :vouStatus OR '-' = :vouStatus)
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, in_unit
                    UNION ALL
                    SELECT stock_code, SUM(out_qty)*-1 qty, loc_code, out_unit
                    FROM v_stock_io
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = false
                    AND calculate = true
                    AND out_qty IS NOT NULL AND out_unit IS NOT NULL
                    AND comp_code = :compCode
                    AND (vou_status = :vouStatus OR '-' = :vouStatus)
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, out_unit
                    UNION ALL
                    SELECT stock_code, SUM(qty)*-1 qty, loc_code, unit
                    FROM v_return_out
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = false
                    AND (calculate = true AND :calRO = false)
                    AND comp_code = :compCode
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(qty)*-1 qty, loc_code, sale_unit
                    FROM v_sale
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = false
                    AND (calculate = true AND :calSale = false)
                    AND comp_code = :compCode
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (cat_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, sale_unit
                    UNION ALL
                    SELECT stock_code, SUM(qty)*-1 qty, loc_code, unit
                    FROM v_order
                    WHERE inv_update = true
                    AND DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = false
                    AND calculate = true
                    AND comp_code = :compCode
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(qty)*-1 qty, loc_code_from, unit
                    FROM v_transfer
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = false
                    AND calculate = true
                    AND comp_code = :compCode
                    AND loc_code_from IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    and skip_inv = false
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code_to, unit
                    FROM v_transfer
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = false
                    AND calculate = true
                    AND comp_code = :compCode
                    AND loc_code_to IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    and skip_inv = false
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(qty)*-1 qty, loc_code, unit
                    FROM v_process_his_detail
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND calculate = true
                    AND comp_code = :compCode
                    AND (pt_code = :vouStatus OR '-' = :vouStatus)
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code, unit
                    FROM v_process_his
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = false
                    AND (pt_code = :vouStatus OR '-' = :vouStatus)
                    AND calculate = true
                    AND comp_code = :compCode
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                ) x
                GROUP BY stock_code, unit""";
        return deleteTmpOpening(macId)
                .then(client.sql(opSql)
                        .bind("opDate", opDate)
                        .bind("fromDate", fromDate)
                        .bind("typeCode", typeCode)
                        .bind("catCode", catCode)
                        .bind("brandCode", brandCode)
                        .bind("stockCode", stockCode)
                        .bind("vouStatus", vouStatus)
                        .bind("calSale", calSale)
                        .bind("calPur", calPur)
                        .bind("calRI", calRI)
                        .bind("calRO", calRO)
                        .bind("compCode", compCode)
                        .bind("deptId", deptId)
                        .bind("macId", macId)
                        .fetch()
                        .rowsUpdated().thenReturn(true));

    }

    private Mono<Boolean> calculateClosing(String fromDate, String toDate, String typeCode, String catCode,
                                           String brandCode, String stockCode, String vouStatus,
                                           boolean calSale, boolean calPur, boolean calRI,
                                           boolean calRO, String compCode, Integer deptId,
                                           Integer macId) {
        String opSql = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,op_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'Opening',a.tran_date,'-','A-Opening',a.stock_code,sum(smallest_qty) smallest_qty,a.loc_code,a.mac_id,:compCode,:deptId
                    from (
                        select tmp.tran_date,tmp.stock_code,tmp.ttl_qty * rel.smallest_qty smallest_qty,tmp.loc_code,tmp.mac_id
                        from tmp_stock_opening tmp
                        join stock s on tmp.stock_code = s.stock_code
                        and tmp.comp_code = s.comp_code
                        join v_relation rel on s.rel_code = rel.rel_code
                        and tmp.comp_code = rel.comp_code
                        and tmp.unit = rel.unit
                        where tmp.mac_id = :macId
                    ) a
                    group by tran_date,stock_code,mac_id
                """;

        String purSql = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,pur_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'Purchase',a.vou_date vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code,:macId,:compCode,:deptId
                    from (
                        select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code, pur_unit,rel_code,comp_code,dept_id
                        from v_purchase
                        where date(vou_date) between :fromDate and :toDate
                        and deleted = false
                        and (calculate = true and :calPur = false)
                        and comp_code = :compCode
                        and loc_code in (select f_code from f_location where mac_id =  :macId )
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (category_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        group by date(vou_date),vou_no,stock_code,pur_unit
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    and a.pur_unit = rel.unit
                    group by a.vou_date ,a.stock_code,a.vou_no
                """;
        String retInSql = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'ReturnIn',a.vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code,:macId,:compCode,:deptId
                    from (
                        select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code,rel_code,unit,comp_code,dept_id
                        from v_return_in
                        where date(vou_date) between :fromDate and :toDate
                        and deleted = false
                        and (calculate = true and :calRI = false)
                        and comp_code = :compCode
                        and loc_code in (select f_code from f_location where mac_id = :macId)
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (category_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        group by date(vou_date),stock_code,vou_no,unit
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    and a.unit = rel.unit
                    group by vou_date,stock_code,vou_no
                """;

        String stockInSql = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'StockIn',date(a.vou_date) vou_date,vou_no,a.description,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code,:macId,:compCode,:deptId
                    from (
                        select date(vou_date) vou_date,vou_no,description,stock_code,sum(in_qty) qty,loc_code,in_unit,rel_code,comp_code,dept_id
                        from v_stock_io
                        where ifnull(in_qty,0)<>0 and in_unit is not null
                        and date(vou_date) between :fromDate and :toDate
                        and deleted = false
                        and calculate = true
                        and comp_code = :compCode
                        and (vou_status = :vouStatus or '-' = :vouStatus)
                        and loc_code in (select f_code from f_location where mac_id = :macId)
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (category_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        group by date(vou_date),stock_code,in_unit,vou_no
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    and a.in_unit = rel.unit
                    group by a.vou_date,a.stock_code,a.vou_no
                """;

        String saleSql = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,sale_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'Sale',a.vou_date ,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code,:macId,:compCode,:deptId
                    from (
                        select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code,sale_unit,rel_code,comp_code,dept_id
                        from v_sale
                        where date(vou_date) between :fromDate and :toDate
                        and deleted = false
                        and (calculate = true and :calSale = false)
                        and comp_code = :compCode
                        and loc_code in (select f_code from f_location where mac_id = :macId)
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (cat_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        group by date(vou_date),stock_code,sale_unit,vou_no
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    and a.sale_unit = rel.unit
                    group by a.vou_date,a.stock_code,a.vou_no
                """;
        String orderSql = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,sale_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'Sale-Order',a.vou_date ,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code,:macId,:compCode,:deptId
                    from (
                        select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code,unit,rel_code,comp_code,dept_id
                        from v_order
                        where inv_update = true
                        and date(vou_date) between :fromDate and :toDate
                        and deleted = false
                        and calculate = true
                        and comp_code = :compCode
                        and loc_code in (select f_code from f_location where mac_id = :macId)
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (category_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        group by date(vou_date),stock_code,unit,vou_no
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    and a.unit = rel.unit
                    group by a.vou_date,a.stock_code,a.vou_no
                """;
        String returnOutSql = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'ReturnOut',a.vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code,:macId,:compCode,:deptId
                    from (
                        select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code,unit,rel_code,comp_code,dept_id
                        from v_return_out
                        where date(vou_date) between :fromDate and :toDate
                        and deleted = false
                        and (calculate = true and :calRO = false)
                        and comp_code = :compCode
                        and loc_code in (select f_code from f_location where mac_id = :macId)
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (category_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        group by date(vou_date),stock_code,unit,vou_no
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    and a.unit = rel.unit
                    group by vou_date,stock_code,vou_no
                """;

        String stockOutSql = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'StockOut',a.vou_date,a.vou_no,a.description,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code,:macId,:compCode,:deptId
                    from (
                        select date(vou_date) vou_date,vou_no,description,stock_code,sum(out_qty) qty,loc_code,out_unit,rel_code,comp_code,dept_id
                        from v_stock_io
                        where ifnull(out_qty,0)<>0 and out_unit is not null
                        and date(vou_date) between :fromDate and :toDate
                        and deleted = false
                        and calculate = true
                        and comp_code = :compCode
                        and (vou_status = :vouStatus or '-' = :vouStatus)
                        and loc_code in (select f_code from f_location where mac_id = :macId)
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (category_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        group by date(vou_date),stock_code,out_unit,vou_no
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    and a.out_unit = rel.unit
                    group by vou_date,a.stock_code,vou_no
                """;

        String fFSql = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'Transfer-F',a.vou_date,a.vou_no,if(ifnull(a.remark,'')='','Transfer',a.remark),a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,
                    loc_code_from,:macId,:compCode,:deptId
                    from (
                        select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code_from,rel_code,unit,comp_code,dept_id
                        from v_transfer
                        where date(vou_date) between :fromDate and :toDate
                        and deleted = false
                        and calculate = true
                        and comp_code = :compCode
                        and loc_code_from in (select f_code from f_location where mac_id = :macId)
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (category_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        and skip_inv = false
                        group by date(vou_date),stock_code,unit,vou_no
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    and a.unit = rel.unit
                    group by vou_date,stock_code,vou_no
                """;

        String tFSql = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'Transfer-T',a.vou_date,a.vou_no,if(ifnull(a.remark,'')='','Transfer',a.remark),a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,
                    loc_code_to,:macId,:compCode,:deptId
                    from (
                        select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code_to,rel_code,unit
                        from v_transfer
                        where date(vou_date) between :fromDate and :toDate
                        and deleted = false
                        and calculate = true
                        and comp_code = :compCode
                        and loc_code_to in (select f_code from f_location where mac_id = :macId)
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (category_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        and skip_inv = false
                        group by date(vou_date),stock_code,unit,vou_no
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.unit = rel.unit
                    group by vou_date,stock_code,vou_no
                """;

        String pIn = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'P-IN',a.end_date ,a.vou_no,v.description,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code,:macId,:compCode,:deptId
                    from (
                        select date(end_date) end_date,vou_no,pt_code,stock_code,sum(qty) qty,loc_code,unit,rel_code,comp_code,dept_id
                        from v_process_his
                        where date(end_date) between :fromDate and :toDate
                        and deleted = false
                        and calculate = true
                        and finished = true
                        and comp_code = :compCode
                        and (pt_code = :vouStatus or '-' = :vouStatus)
                        and loc_code in (select f_code from f_location where mac_id = :macId)
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (category_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        group by date(end_date),stock_code,unit,vou_no
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    and a.unit = rel.unit
                    join vou_status v on a.pt_code = v.code
                    and a.comp_code = v.comp_code
                    group by a.end_date,a.stock_code,a.vou_no
                """;

        String pOut = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'P-OUT',a.vou_date ,a.vou_no,v.description,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code,:macId,:compCode,:deptId
                    from (
                        select date(vou_date) vou_date,vou_no,pt_code,stock_code,sum(qty) qty,loc_code,unit,rel_code,comp_code,dept_id
                        from v_process_his_detail
                        where date(vou_date) between :fromDate and :toDate
                        and deleted = false
                        and calculate = true
                        and comp_code = :compCode
                        and (pt_code = :vouStatus or '-'=:vouStatus)
                        and loc_code in (select f_code from f_location where mac_id = :macId)
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (category_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        group by date(vou_date),stock_code,unit,vou_no
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    and a.unit = rel.unit
                    join vou_status v on a.pt_code = v.code
                    and a.comp_code = v.comp_code
                    group by a.vou_date,a.stock_code,a.vou_no
                """;
        String mRawSql = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'UM-RAW',a.vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code,:macId,:compCode,:deptId
                    from (
                        select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code,unit,rel_code,comp_code,dept_id
                        from v_milling_raw
                        where date(vou_date) between :fromDate and :toDate
                        and deleted = false
                        and (calculate = true and :calRO = false)
                        and comp_code = :compCode
                        and loc_code in (select f_code from f_location where mac_id = :macId)
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (cat_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        group by date(vou_date),stock_code,unit,vou_no
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    and a.unit = rel.unit
                    group by vou_date,stock_code,vou_no
                """;

        String mOutSql = """
                    insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)
                    select 'UM-OUTPUT',a.vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code,:macId,:compCode,:deptId
                    from (
                        select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code,rel_code,unit,comp_code,dept_id
                        from v_milling_output
                        where date(vou_date) between :fromDate and :toDate
                        and deleted = false
                        and (calculate = true and :calRI = false)
                        and comp_code = :compCode
                        and loc_code in (select f_code from f_location where mac_id = :macId)
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (cat_code = :catCode or '-' = :catCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        group by date(vou_date),stock_code,vou_no,unit
                    ) a
                    join v_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    and a.unit = rel.unit
                    group by vou_date,stock_code,vou_no
                """;
        Mono<Long> opMono = client.sql(opSql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("macId", macId)
                .fetch()
                .rowsUpdated();

        Mono<Long> monoPur = client.sql(purSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("calPur", calPur)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();

        Mono<Long> retInMono = client.sql(retInSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("calRI", calRI)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();
        Mono<Long> saleMono = client.sql(saleSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("calSale", calSale)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();
        Mono<Long> orderMono = client.sql(orderSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();

        Mono<Long> returnOutMono = client.sql(returnOutSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("calRO", calRO)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();

        Mono<Long> stockInMono = client.sql(stockInSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("vouStatus", vouStatus)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();

        Mono<Long> stockOutMono = client.sql(stockOutSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("vouStatus", vouStatus)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();
        Mono<Long> fFMono = client.sql(fFSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();
        Mono<Long> tFMono = client.sql(tFSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();
        Mono<Long> pInMono = client.sql(pIn)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("vouStatus", vouStatus)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();

        Mono<Long> pOutMono = client.sql(pOut)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("vouStatus", vouStatus)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();

        Mono<Long> mRawMono = client.sql(mRawSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("calRO", calRO)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();

        Mono<Long> mOutMono = client.sql(mOutSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("calRI", calRI)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();

        return deleteTmpIO(macId)
                .then(opMono)
                .then(monoPur)
                .then(saleMono)
                .then(orderMono)
                .then(retInMono)
                .then(returnOutMono)
                .then(stockInMono)
                .then(stockOutMono)
                .then(fFMono)
                .then(tFMono)
                .then(pInMono)
                .then(pOutMono)
                .then(mRawMono)
                .then(mOutMono)
                .thenReturn(true);

    }

    @Transactional
    private Mono<Long> deleteTmpIO(Integer macId) {
        String sql = "delete from tmp_stock_io_column where mac_id=:macId";
        return client.sql(sql).bind("macId", macId).fetch().rowsUpdated();
    }

    public Mono<ReturnObject> getStockInOutSummary(String opDate, String fromDate, String toDate,
                                                   String typeCode, String catCode, String brandCode,
                                                   String stockCode, String vouStatus,
                                                   boolean calSale, boolean calPur, boolean calRI,
                                                   boolean calRO, String compCode, Integer deptId, Integer macId) {
        Mono<Boolean> op = calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        Mono<Boolean> cl = calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        return op.then(cl).then(initRelation(compCode).then(getStkInOutSummary(macId)));
    }

    public Mono<ReturnObject> getStockInOutDetail(String opDate, String fromDate, String toDate,
                                                  String typeCode, String catCode, String brandCode,
                                                  String stockCode, String vouStatus,
                                                  boolean calSale, boolean calPur, boolean calRI,
                                                  boolean calRO, String compCode, Integer deptId, Integer macId) {
        Mono<Boolean> op = calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        Mono<Boolean> cl = calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        return op.then(cl).then(initRelation(compCode)).then(getStkInOutDetail(macId, compCode));
    }

    public Mono<ReturnObject> getStockValue(String opDate, String fromDate, String toDate,
                                            String typeCode, String catCode, String brandCode,
                                            String stockCode, String vouStatus,
                                            boolean calSale, boolean calPur, boolean calRI,
                                            boolean calRO, String compCode, Integer deptId, Integer macId) {
        Mono<Boolean> op = calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        Mono<Boolean> cl = calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        Mono<Boolean> price = calculatePrice(toDate, opDate, stockCode, typeCode, catCode, brandCode, compCode, macId);
        return op.then(cl).then(price).then(initRelation(compCode).then(getStkValue(macId, compCode)));
    }

    private Mono<ReturnObject> getStkInOutSummary(Integer macId) {
        String getSql = """
                select a.*,sum(a.op_qty+a.pur_qty+a.in_qty+a.out_qty+a.sale_qty) bal_qty,
                s.rel_code,s.user_code s_user_code,s.stock_name,st.user_code st_user_code,st.stock_type_name
                from (select stock_code,loc_code,sum(op_qty) op_qty,sum(pur_qty) pur_qty,
                sum(in_qty) in_qty,sum(out_qty) out_qty,sum(sale_qty) sale_qty,comp_code
                from tmp_stock_io_column
                where mac_id =:macId
                group by stock_code)a
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                join stock_type st on s.stock_type_code = st.stock_type_code
                and a.comp_code = st.comp_code
                group by stock_code
                order by s.user_code""";
        return client.sql(getSql)
                .bind("macId", macId)
                .map((row) -> ClosingBalance.builder()
                        .openQty(Util1.toNull(row.get("op_qty", Double.class)))
                        .openRel(getRelStr(row.get("rel_code", String.class), row.get("op_qty", Double.class)))
                        .purQty(Util1.toNull(row.get("pur_qty", Double.class)))
                        .purRel(getRelStr(row.get("rel_code", String.class), row.get("pur_qty", Double.class)))
                        .inQty(Util1.toNull(row.get("in_qty", Double.class)))
                        .inRel(getRelStr(row.get("rel_code", String.class), row.get("in_qty", Double.class)))
                        .saleQty(Util1.toNull(row.get("sale_qty", Double.class)))
                        .saleRel(getRelStr(row.get("rel_code", String.class), row.get("sale_qty", Double.class)))
                        .outQty(Util1.toNull(row.get("out_qty", Double.class)))
                        .outRel(getRelStr(row.get("rel_code", String.class), row.get("out_qty", Double.class)))
                        .balQty(Util1.toNull(row.get("bal_qty", Double.class)))
                        .balRel(getRelStr(row.get("rel_code", String.class), row.get("bal_qty", Double.class)))
                        .stockUsrCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    private Mono<ReturnObject> getStkInOutDetail(Integer macId, String compCode) {
        String getSql = """
                select a.*,sum(a.op_qty+a.pur_qty+a.in_qty+a.out_qty+a.sale_qty) bal_qty,
                s.rel_code,s.user_code s_user_code,a.stock_code,s.stock_name
                from (
                select tran_option,tran_date,stock_code,loc_code,sum(op_qty) op_qty,sum(pur_qty) pur_qty,
                sum(in_qty) in_qty,sum(out_qty) out_qty,sum(sale_qty) sale_qty,remark,vou_no,comp_code,dept_id
                from tmp_stock_io_column
                where mac_id = :macId
                and comp_code = :compCode
                group by tran_date,stock_code,tran_option,vou_no)a
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                group by tran_date,stock_code,vou_no,tran_option
                order by s.user_code,a.tran_date,a.tran_option,a.vou_no""";
        return client.sql(getSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .map((row) -> ClosingBalance.builder()
                        .openQty(Util1.toNull(row.get("op_qty", Double.class)))
                        .purQty(Util1.toNull(row.get("pur_qty", Double.class)))
                        .inQty(Util1.toNull(row.get("in_qty", Double.class)))
                        .saleQty(Util1.toNull(row.get("sale_qty", Double.class)))
                        .outQty(Util1.toNull(row.get("out_qty", Double.class)))
                        .balQty(Util1.toNull(row.get("bal_qty", Double.class)))
                        .stockUsrCode(row.get("s_user_code", String.class))
                        .relCode(row.get("rel_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .vouDate(Util1.toDateStr(row.get("tran_date", LocalDate.class), "dd/MM/yyyy"))
                        .stockUsrCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .remark(row.get("remark", String.class))
                        .vouNo(row.get("vou_no", String.class))
                        .build())
                .all()
                .collectList()
                .flatMap((balances -> {
                    if (balances.isEmpty()) {
                        ReturnObject ro = ReturnObject.builder().build();
                        ro.setMessage("No Records.");
                        ro.setFile(Util1.convertToJsonBytes(balances));
                        return Mono.just(ro);
                    }
                    for (int i = 0; i < balances.size(); i++) {
                        if (i > 0) {
                            ClosingBalance prv = balances.get(i - 1);
                            double prvCl = Util1.getDouble(prv.getBalQty());
                            double prvWCl = Util1.getDouble(prv.getBalWeight());
                            ClosingBalance c = balances.get(i);
                            c.setOpenQty(prvCl);
                            c.setOpenWeight(prvWCl);
                            String relCode = c.getRelCode();
                            double opQty = Util1.getDouble(c.getOpenQty());
                            double purQty = Util1.getDouble(c.getPurQty());
                            double inQty = Util1.getDouble(c.getInQty());
                            double outQty = Util1.getDouble(c.getOutQty());
                            double saleQty = Util1.getDouble(c.getSaleQty());
                            double clQty = opQty + purQty + inQty + outQty + saleQty;

                            double opWeight = Util1.getDouble(c.getOpenWeight());
                            double purWeight = Util1.getDouble(c.getPurWeight());
                            double inWeight = Util1.getDouble(c.getInWeight());
                            double outWeight = Util1.getDouble(c.getOutWeight());
                            double saleWeight = Util1.getDouble(c.getSaleWeight());
                            double clWeight = opWeight + purWeight + inWeight + outWeight + saleWeight;
                            c.setOpenQty(opQty);
                            c.setOpenRel(getRelStr(relCode, opQty));
                            c.setPurQty(purQty);
                            c.setPurRel(getRelStr(relCode, purQty));
                            c.setInQty(inQty);
                            c.setInRel(getRelStr(relCode, inQty));
                            c.setSaleQty(saleQty);
                            c.setSaleRel(getRelStr(relCode, saleQty));
                            c.setOutQty(outQty);
                            c.setOutRel(getRelStr(relCode, outQty));
                            c.setBalQty(clQty);
                            c.setBalRel(getRelStr(relCode, clQty));
                            //weight
                            c.setOpenWeight(opWeight);
                            c.setPurWeight(purWeight);
                            c.setInWeight(inWeight);
                            c.setSaleWeight(saleWeight);
                            c.setOutWeight(outWeight);
                            c.setBalWeight(clWeight);
                        } else {
                            ClosingBalance c = balances.get(i);
                            String relCode = c.getRelCode();
                            double opQty = Util1.getDouble(c.getOpenQty());
                            double purQty = Util1.getDouble(c.getPurQty());
                            double inQty = Util1.getDouble(c.getInQty());
                            double outQty = Util1.getDouble(c.getOutQty());
                            double saleQty = Util1.getDouble(c.getSaleQty());
                            double clQty = opQty + purQty + inQty + outQty + saleQty;

                            double opWeight = Util1.getDouble(c.getOpenWeight());
                            double purWeight = Util1.getDouble(c.getPurWeight());
                            double inWeight = Util1.getDouble(c.getInWeight());
                            double outWeight = Util1.getDouble(c.getOutWeight());
                            double saleWeight = Util1.getDouble(c.getSaleWeight());
                            double clWeight = opWeight + purWeight + inWeight + outWeight + saleWeight;
                            c.setOpenQty(opQty);
                            c.setOpenRel(getRelStr(relCode, opQty));
                            c.setPurQty(purQty);
                            c.setPurRel(getRelStr(relCode, purQty));
                            c.setInQty(inQty);
                            c.setInRel(getRelStr(relCode, inQty));
                            c.setSaleQty(saleQty);
                            c.setSaleRel(getRelStr(relCode, saleQty));
                            c.setOutQty(outQty);
                            c.setOutRel(getRelStr(relCode, outQty));
                            c.setBalQty(clQty);
                            c.setBalRel(getRelStr(relCode, clQty));
                            c.setOpenWeight(opWeight);
                            c.setPurWeight(purWeight);
                            c.setInWeight(inWeight);
                            c.setSaleWeight(saleWeight);
                            c.setOutWeight(outWeight);
                            c.setBalWeight(clWeight);
                        }
                    }
                    String relCode = balances.getFirst().getRelCode();
                    double purQty = balances.stream().mapToDouble(t -> Util1.getDouble(t.getPurQty())).sum();
                    double saleQty = balances.stream().mapToDouble(t -> Util1.getDouble(t.getSaleQty())).sum();
                    double inQty = balances.stream().mapToDouble(t -> Util1.getDouble(t.getInQty())).sum();
                    double outQty = balances.stream().mapToDouble(t -> Util1.getDouble(t.getOutQty())).sum();
                    ReturnObject ro = ReturnObject.builder().build();
                    ro.setFile(Util1.convertToJsonBytes(balances));
                    ro.setRelName(hmRelationName.get(relCode));
                    ro.setPurRel(getRelStr(relCode, purQty));
                    ro.setSaleRel(getRelStr(relCode, saleQty));
                    ro.setInRel(getRelStr(relCode, inQty));
                    ro.setOutRel(getRelStr(relCode, outQty));
                    return Mono.just(ro);
                }));
    }

    private Mono<ReturnObject> getStkValue(Integer macId, String compCode) {
        String getSql = """
                select a.*,
                sum(ifnull(tmp.pur_avg_price,0)) pur_avg_price,bal_qty*sum(ifnull(tmp.pur_avg_price,0)) pur_avg_amt,
                sum(ifnull(tmp.in_avg_price,0)) in_avg_price,bal_qty*sum(ifnull(tmp.in_avg_price,0)) in_avg_amt,
                sum(ifnull(tmp.std_price,0)) std_price,bal_qty*sum(ifnull(tmp.std_price,0)) std_amt,
                sum(ifnull(tmp.pur_recent_price,0)) pur_recent_price,bal_qty*sum(ifnull(tmp.pur_recent_price,0)) pur_recent_amt,
                sum(ifnull(tmp.fifo_price,0)) fifo_price,bal_qty*sum(ifnull(tmp.fifo_price,0)) fifo_amt,
                sum(ifnull(tmp.lifo_price,0)) lifo_price,bal_qty*sum(ifnull(tmp.lifo_price,0)) lifo_amt,
                sum(ifnull(tmp.io_recent_price,0)) io_recent_price,bal_qty*sum(ifnull(tmp.io_recent_price,0)) io_recent_amt,
                s.rel_code,s.user_code s_user_code,s.stock_name,st.user_code st_user_code,st.stock_type_name,rel.rel_name
                from (
                select stock_code,sum(op_qty)+sum(pur_qty)+sum(in_qty) +sum(out_qty) +sum(sale_qty) bal_qty,mac_id,comp_code
                from tmp_stock_io_column
                where mac_id = :macId
                and comp_code= :compCode
                group by stock_code)a
                left join tmp_stock_price tmp
                on a.stock_code  = tmp.stock_code
                and a.mac_id = tmp.mac_id
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                left join unit_relation rel on s.rel_code = rel.rel_code
                and a.comp_code = rel.comp_code
                join stock_type st on s.stock_type_code = st.stock_type_code
                and a.comp_code = st.comp_code
                group by a.stock_code
                order by s.user_code""";
        return client.sql(getSql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .map((rs) -> StockValue.builder()
                        .stockUserCode(rs.get("s_user_code", String.class))
                        .stockName(rs.get("stock_name", String.class))
                        .balRel(getRelStr(rs.get("rel_code", String.class), rs.get("bal_qty", Double.class)))
                        .qty(rs.get("bal_qty", Double.class))
                        .relation(rs.get("rel_name", String.class))
                        .purAvgPrice(rs.get("pur_avg_price", Double.class))
                        .purAvgAmount(rs.get("pur_avg_amt", Double.class))
                        .inAvgPrice(rs.get("in_avg_price", Double.class))
                        .inAvgAmount(rs.get("in_avg_amt", Double.class))
                        .stdPrice(rs.get("std_price", Double.class))
                        .stdAmount(rs.get("std_amt", Double.class))
                        .recentPrice(rs.get("pur_recent_price", Double.class))
                        .recentAmt(rs.get("pur_recent_amt", Double.class))
                        .fifoPrice(rs.get("fifo_price", Double.class))
                        .fifoAmt(rs.get("fifo_amt", Double.class))
                        .lifoPrice(rs.get("lifo_price", Double.class))
                        .lifoAmt(rs.get("lifo_amt", Double.class))
                        .ioRecentPrice(rs.get("io_recent_price", Double.class))
                        .ioRecentAmt(rs.get("io_recent_amt", Double.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    private Mono<Boolean> initRelation(String compCode) {
        return unitRelationService.getUnitRelationAndDetail(compCode)
                .map(t -> {
                    String relCode = t.getKey().getRelCode();
                    hmRelation.put(relCode, t.getDetailList());
                    hmRelationName.put(relCode, t.getRelName());
                    return true;
                }).then(Mono.just(true));
    }

    private String getRelStr(String relCode, Double smallestQty) {
        //generate unit relation.
        StringBuilder relStr = new StringBuilder();
        if (smallestQty != 0 && !Objects.isNull(relCode)) {
            List<UnitRelationDetail> detailList = hmRelation.get(relCode);
            if (detailList != null) {
                for (UnitRelationDetail detail : detailList) {
                    double smallQty = detail.getSmallestQty();
                    double divider = smallestQty / smallQty;
                    smallestQty = smallestQty % smallQty;
                    String str;
                    if (smallQty == 1) {
                        if (divider != 0) {
                            str = formatter.format(divider);
                            relStr.append(String.format("%s %s%s", str, detail.getUnit(), "*"));
                        }
                    } else {
                        int first = (int) divider;
                        if (first != 0) {
                            str = formatter.format(first);
                            relStr.append(String.format("%s %s%s", str, detail.getUnit(), "*"));
                        }
                    }
                }
            } else {
                log.info(String.format("non relation: %s", relCode));
            }
        }
        String str = relStr.toString();
        if (str.contains("-")) {
            str = str.replaceAll("-", "");
            str = String.format("%s%s", "-", str);
        }
        if (str.isEmpty()) {
            str = "*";
        }
        str = str.substring(0, str.length() - 1);
        if (str.contains("-")) {
            str = str.replaceAll("-", "");
            str = String.format("(%s)", str);
        }
        return str;

    }

    @Transactional
    private Mono<Boolean> deleteTmpOpening(int macId) {
        String sql = """
                delete from tmp_stock_opening where mac_id = :macId
                """;
        return client.sql(sql)
                .bind("macId", macId)
                .fetch().rowsUpdated().thenReturn(true);
    }

    @Transactional
    private Mono<Boolean> calculatePrice(String toDate, String opDate, String stockCode,
                                         String typeCode, String catCode, String brandCode,
                                         String compCode, Integer macId) {
        String delSql = """
                delete from tmp_stock_price where mac_id = :macId
                """;
        String purSql = """
                insert into tmp_stock_price(tran_option,stock_code,pur_avg_price,mac_id)
                select 'PUR-AVG',stock_code,avg(small_price),:macId
                from (
                select 'PUR-AVG',pur.stock_code,(pur.pur_price/rel.smallest_qty) small_price
                from v_purchase pur
                join v_relation rel
                on pur.rel_code = rel.rel_code
                and pur.comp_code = rel.comp_code
                and pur.pur_unit = rel.unit
                where deleted = false
                and date(vou_date) <=:toDate
                and pur.comp_code =:compCode
                and (pur.stock_code = :stockCode or '-' =:stockCode)
                and (pur.stock_type_code = :typeCode or '-' =:typeCode)
                and (pur.brand_code = :brandCode or '-' =:brandCode)
                and (pur.category_code = :catCode or '-' =:catCode)
                group by pur.stock_code,small_price
                    union all
                select 'OP',op.stock_code,(op.price/rel.smallest_qty) small_price
                from v_opening op
                join v_relation rel
                on op.rel_code = rel.rel_code
                and op.comp_code = rel.comp_code
                and op.unit = rel.unit
                where op.price > 0
                and deleted = false
                and date(op_date) <=:toDate
                and op.comp_code =:compCode
                and (op.stock_code = :stockCode or '-' =:stockCode)
                and (op.stock_type_code = :typeCode or '-' =:typeCode)
                and (op.brand_code = :brandCode or '-' =:brandCode)
                and (op.category_code = :catCode or '-' =:catCode)
                group by op.stock_code,small_price)a
                group by stock_code""";
        String sInSql = """
                insert into tmp_stock_price(tran_option,stock_code,in_avg_price,mac_id)
                select 'SIN-AVG',stock_code,avg(small_price),:macId
                from(
                select 'SIN-AVG',a.stock_code,(a.cost_price/rel.smallest_qty) small_price
                from (
                select stock_code,cost_price,ifnull(in_unit,out_unit) unit,comp_code,rel_code
                from v_stock_io
                where cost_price >0
                and deleted = false
                and date(vou_date) <=:toDate
                and comp_code =:compCode
                and (stock_code = :stockCode or '-' =:stockCode)
                and (stock_type_code = :typeCode or '-' =:typeCode)
                and (brand_code = :brandCode or '-' =:brandCode)
                and (category_code = :catCode or '-' =:catCode)
                group by stock_code,cost_price,ifnull(in_unit,out_unit)
                )a
                join v_relation rel on a.rel_code =rel.rel_code
                and a.unit =rel.unit
                and a.comp_code =rel.comp_code
                group by stock_code,small_price
                    union all
                select 'OP',op.stock_code,(op.price/rel.smallest_qty) small_price
                from v_opening op
                join v_relation rel
                on op.rel_code = rel.rel_code
                and op.comp_code = rel.comp_code
                and op.unit = rel.unit
                where op.price > 0
                and op.deleted = false
                and date(op_date) =:opDate
                and op.comp_code =:compCode
                and (op.stock_code = :stockCode or '-' =:stockCode)
                and (op.stock_type_code = :typeCode or '-' =:typeCode)
                and (op.brand_code = :brandCode or '-' =:brandCode)
                and (op.category_code = :catCode or '-' =:catCode)
                group by op.stock_code,small_price
                )a
                group by stock_code""";
        String purRecentSql = """
                insert into tmp_stock_price(stock_code,tran_option,pur_recent_price,mac_id)
                select a.stock_code,'PUR_RECENT',a.pur_price/rel.smallest_qty pur_price,:macId
                from (
                with rows_and_position as
                (
                select stock_code, pur_price,pur_unit,row_number() over (partition by stock_code order by vou_date desc) as position,rel_code,comp_code,dept_id
                from v_purchase
                where date(vou_date) <=:toDate
                and comp_code =:compCode
                and deleted = false
                and (stock_code = :stockCode or '-' =:stockCode)
                and (stock_type_code = :typeCode or '-' =:typeCode)
                and (brand_code = :brandCode or '-' =:brandCode)
                and (category_code = :catCode or '-' =:catCode)
                  )
                select stock_code, pur_price,pur_unit,rel_code,comp_code,dept_id
                from  rows_and_position
                where position =1
                )a
                join v_relation rel
                on a.rel_code = rel.rel_code
                and a.pur_unit = rel.unit
                and a.comp_code = rel.comp_code
                """;
        String ioRecent = """
                insert into tmp_stock_price(stock_code,tran_option,io_recent_price,mac_id)
                select a.stock_code,'IO_RECENT',a.cost_price/rel.smallest_qty price,:macId
                from (
                with rows_and_position as
                  (
                    select stock_code, cost_price,ifnull(in_unit,out_unit) unit,row_number() over (partition by stock_code order by vou_date desc) as position,rel_code,comp_code,dept_id
                    from v_stock_io
                    where date(vou_date) <=:toDate
                    and comp_code =:compCode
                    and deleted = false
                    and cost_price>0
                    and (stock_code = :stockCode or '-' =:stockCode)
                    and (stock_type_code = :typeCode or '-' =:typeCode)
                    and (brand_code = :brandCode or '-' =:brandCode)
                    and (category_code = :catCode or '-' =:catCode)
                  )
                select stock_code, cost_price,unit,rel_code,comp_code,dept_id
                from  rows_and_position
                where position =1
                )a
                join v_relation rel
                on a.rel_code = rel.rel_code
                and a.unit = rel.unit
                and a.comp_code = rel.comp_code""";
        Mono<Boolean> deleteMono = client.sql(delSql)
                .bind("macId", macId)
                .fetch().rowsUpdated().thenReturn(true);
        Mono<Boolean> purMono = client.sql(purSql)
                .bind("macId", macId)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("stockCode", stockCode)
                .fetch().rowsUpdated().thenReturn(true);
        Mono<Boolean> ioMono = client.sql(sInSql)
                .bind("macId", macId)
                .bind("opDate", opDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("stockCode", stockCode)
                .fetch().rowsUpdated().thenReturn(true);
        Mono<Boolean> purRecentMono = client.sql(purRecentSql)
                .bind("macId", macId)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("stockCode", stockCode)
                .fetch().rowsUpdated().thenReturn(true);
        Mono<Boolean> ioRecentMono = client.sql(ioRecent)
                .bind("macId", macId)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("stockCode", stockCode)
                .fetch().rowsUpdated().thenReturn(true);
        return deleteMono.then(purMono).then(ioMono).then(purRecentMono).then(ioRecentMono);
    }
}
