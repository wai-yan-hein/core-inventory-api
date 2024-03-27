package cv.api.service;

import cv.api.entity.SaleExpense;
import cv.api.entity.SaleExpenseKey;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SaleExpenseService {
    private final DatabaseClient client;

    public Mono<SaleExpense> insert(SaleExpense dto) {
        String sql = """
                INSERT INTO sale_expense (expense_code, vou_no, comp_code, unique_id, amount)
                VALUES (:expenseCode, :vouNo, :compCode, :uniqueId, :amount)
                """;
        return client.sql(sql)
                .bind("expenseCode", dto.getKey().getExpenseCode())
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("amount", dto.getAmount())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }
    public Flux<SaleExpense> search(String vouNo, String compCode) {
        String sql = """
            SELECT a.*, e.expense_name
            FROM (
                SELECT *
                FROM sale_expense
                WHERE vou_no = :vouNo
                AND comp_code = :compCode
            ) a
            JOIN expense e
            ON a.expense_code = e.expense_code
            AND a.comp_code = e.comp_code
            ORDER BY a.unique_id
            """;

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> SaleExpense.builder()
                        .key(SaleExpenseKey.builder()
                                .expenseCode(row.get("expense_code", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .vouNo(row.get("vou_no", String.class))
                                .build())
                        .expenseName(row.get("expense_name", String.class))
                        .amount(row.get("amount", Double.class))
                        .build())
                .all();
    }


}
