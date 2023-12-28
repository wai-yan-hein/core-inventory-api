package cv.api.service;


import cv.api.model.VTransfer;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final DatabaseClient client;

    public Flux<VTransfer> getTransferVoucher(String vouNo, String compCode) {
        String sql = """
                SELECT stock_name, unit, t.qty,sale_price_n,t.qty*sale_price_n sale_amt,ft.loc_name AS fLocName, tt.loc_name AS tLocName,
                t.vou_no, t.vou_date, t.user_code, t.remark, t.ref_no, t.weight, t.weight_unit,
                u1.unit_name, u2.unit_name AS weight_unit_name, g.labour_name
                FROM v_transfer t
                JOIN location ft ON t.loc_code_from = ft.loc_code AND t.comp_code = ft.comp_code
                JOIN location tt ON t.loc_code_to = tt.loc_code AND t.comp_code = tt.comp_code
                LEFT JOIN stock_unit u1 ON t.unit = u1.unit_code AND t.comp_code = u1.comp_code
                LEFT JOIN stock_unit u2 ON t.weight_unit = u2.unit_code AND t.comp_code = u2.comp_code
                LEFT JOIN labour_group g ON t.labour_group_code = g.code AND t.comp_code = g.comp_code
                WHERE t.comp_code = :compCode AND t.vou_no = :vouNo
                ORDER BY unique_id
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map((row) -> VTransfer.builder()
                        .stockName(row.get("stock_name", String.class))
                        .unit(row.get("unit", String.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("sale_price_n", Double.class))
                        .saleAmt(row.get("sale_amt", Double.class))
                        .vouNo(row.get("vou_no", String.class))
                        .vouDate(row.get("vou_date", String.class))
                        .fromLocationName(row.get("fLocName", String.class))
                        .toLocationName(row.get("tLocName", String.class))
                        .stockCode(row.get("user_code", String.class))
                        .remark(row.get("remark", String.class))
                        .refNo(row.get("ref_no", String.class))
                        .unitName(row.get("unit_name", String.class))
                        .labourGroupName(row.get("labour_name", String.class))
                        .weight(row.get("weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .weightUnitName(row.get("weight_unit_name", String.class))
                        .build())
                .all();
    }
}
