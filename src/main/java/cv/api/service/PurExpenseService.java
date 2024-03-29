package cv.api.service;

import cv.api.entity.PurExpense;
import cv.api.entity.PurExpenseKey;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
public class PurExpenseService {
    private final DatabaseClient client;

    public Mono<PurExpense> save(PurExpense dto) {
        String sql = """
                 INSERT INTO pur_expense (expense_code, vou_no, comp_code, unique_id, amount, percent)
                 VALUES (:expenseCode, :vouNo, :compCode, :uniqueId, :amount, :percent)
                 """;
        return client
                .sql(sql)
                .bind("expenseCode", dto.getKey().getExpenseCode())
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("amount", dto.getAmount())
                .bind("percent", Parameters.in(R2dbcType.DOUBLE,dto.getPercent()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }


    public Flux<PurExpense> search(String vouNo, String compCode) {
        String sql = """
             SELECT a.*, e.expense_name
             FROM pur_expense a
             JOIN expense e ON a.expense_code = e.expense_code AND a.comp_code = e.comp_code
             WHERE a.vou_no = :vouNo AND a.comp_code = :compCode
             ORDER BY a.unique_id
             """;

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map(row -> PurExpense.builder()
                        .key(PurExpenseKey.builder()
                                .expenseCode(row.get("expense_code", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .vouNo(row.get("vou_no", String.class))
                                .build())
                        .expenseName(row.get("expense_name", String.class))
                        .amount(row.get("amount", Double.class))
                        .percent(row.get("percent", Double.class))
                        .build())
                .all();
    }

}
