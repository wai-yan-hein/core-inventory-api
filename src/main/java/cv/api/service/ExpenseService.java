package cv.api.service;

import cv.api.common.Util1;

import cv.api.entity.Expense;
import cv.api.entity.ExpenseKey;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<Expense> findById(ExpenseKey key) {
        String sql = """
                select *
                from expense
                where expense_code =:expenseCode
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("expenseCode", key.getExpenseCode())
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Mono<Expense> save(Expense exp) {
        String coaCode = exp.getKey().getExpenseCode();
        String compCode = exp.getKey().getCompCode();
        if (Util1.isNullOrEmpty(coaCode)) {
            return seqService.getNextCode("EXPENSE", compCode, 5)
                    .flatMap(seqNo -> {
                        exp.getKey().setExpenseCode(seqNo);
                        return insert(exp);
                    });
        }
        return update(exp);
    }

    public Flux<Expense> getExpense(String compCode) {
        String sql = """
                select *
                from expense
                where comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> delete(ExpenseKey key) {
        String sql = """
                update expense
                set deleted =true
                where expense_code =:expenseCode
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("expenseCode", key.getExpenseCode())
                .bind("compCode", key.getCompCode())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<Expense> insert(Expense dto) {
        String sql = """
                INSERT INTO expense
                (expense_code, comp_code, expense_name, account_code, deleted, percent, user_code)
                VALUES (:expenseCode, :compCode, :expenseName, :accountCode, :deleted, :percent, :userCode)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<Expense> update(Expense dto) {
        String sql = """
                UPDATE expense
                SET expense_name = :expenseName, account_code = :accountCode,
                deleted = :deleted, percent = :percent, user_code = :userCode
                WHERE expense_code = :expenseCode AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<Expense> executeUpdate(String sql, Expense dto) {
        return client.sql(sql)
                .bind("expenseName", dto.getExpenseName())
                .bind("accountCode", Util1.isNull(dto.getAccountCode(), "-"))
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("percent", Parameters.in(R2dbcType.DOUBLE, dto.getPercent()))
                .bind("userCode", Parameters.in(R2dbcType.VARCHAR, dto.getUserCode()))
                .bind("expenseCode", dto.getKey().getExpenseCode())
                .bind("compCode", dto.getKey().getCompCode())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Expense mapRow(Row row) {
        return Expense.builder()
                .key(ExpenseKey.builder()
                        .expenseCode(row.get("expense_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .expenseName(row.get("expense_name", String.class))
                .accountCode(row.get("account_code", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .percent(row.get("percent", Double.class))
                .userCode(row.get("user_code", String.class))
                .build();
    }
}
