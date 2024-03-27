package cv.api.service;

import cv.api.entity.Expense;
import cv.api.entity.ExpenseKey;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ExpenseService {
    Mono<Expense> findById(ExpenseKey key);
    Mono<Expense> save(Expense exp);

    Flux<Expense> getExpense(String compCode);

    Mono<Boolean> delete(ExpenseKey key);
}
