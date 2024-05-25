package cv.api.service;

import cv.api.entity.MillingExpense;
import cv.api.entity.MillingExpenseKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class MillingExpenseService {
    private final DatabaseClient client;


    public Mono<MillingExpense> insert(MillingExpense dto) {
        String sql = """
                INSERT INTO milling_expense (expense_code, vou_no, comp_code, unique_id, expense_name, qty, amount, price, deleted, updated_date, created_date, created_by, updated_by)
                VALUES (:expenseCode, :vouNo, :compCode, :uniqueId, :expenseName, :qty, :amount, :price, :deleted, :updatedDate, :createdDate, :createdBy, :updatedBy)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<MillingExpense> update(MillingExpense dto) {
        String sql = """
                UPDATE milling_expense
                SET expense_name = :expenseName, qty = :qty, amount = :amount, price = :price, deleted = :deleted, updated_date = :updatedDate, created_date = :createdDate, created_by = :createdBy, updated_by = :updatedBy
                WHERE expense_code = :expenseCode AND comp_code = :compCode AND vou_no = :vouNo AND unique_id = :uniqueId
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<MillingExpense> executeUpdate(String sql, MillingExpense dto) {
        return client.sql(sql)
                .bind("expenseCode", dto.getKey().getExpenseCode())
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("expenseName", dto.getExpenseName())
                .bind("qty", dto.getQty())
                .bind("amount", dto.getAmount())
                .bind("price", dto.getPrice())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Flux<MillingExpense> search(String vouNo, String compCode) {
        String sql = """
                select a.*
                from (
                select *
                from milling_expense
                where vou_no = :vouNo
                and comp_code = :compCode
                )a
                join expense e
                on a.expense_code = e.expense_code
                and a.comp_code = e.comp_code
                order by a.unique_id
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, metadata) -> MillingExpense.builder()
                        .key(MillingExpenseKey.builder()
                                .expenseCode(row.get("expense_code", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .vouNo(row.get("vou_no", String.class))
                                .build())
                        .expenseName(row.get("expense_name", String.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("price", Double.class))
                        .amount(row.get("amount", Double.class))
                        .build())
                .all();
    }

    public Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from milling_expense where vou_no = :vouNo and compCode = :compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }
}
