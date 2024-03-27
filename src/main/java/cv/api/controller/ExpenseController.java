package cv.api.controller;

import cv.api.entity.Expense;
import cv.api.entity.ExpenseKey;
import cv.api.entity.PurExpense;
import cv.api.entity.SaleExpense;
import cv.api.service.ExpenseService;
import cv.api.service.PurExpenseService;
import cv.api.service.SaleExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpenseController {
    private final PurExpenseService purExpenseService;
    private final SaleExpenseService saleExpenseService;
    private final ExpenseService expenseService;

    @PostMapping(path = "/saveExpense")
    public Mono<Expense> saveExpense(@RequestBody Expense e) {
        return expenseService.save(e);
    }

    @GetMapping(path = "/getExpense")
    public Flux<Expense> getExpense(@RequestParam String compCode) {
        return expenseService.getExpense(compCode);
    }

    @PostMapping(path = "/deleteExpense")
    public Mono<Boolean> deleteExpense(@RequestBody ExpenseKey key) {
        return expenseService.delete(key);
    }

    @GetMapping(path = "/getPurExpense")
    public Flux<PurExpense> getPurExpense(@RequestParam String vouNo,
                                          @RequestParam String compCode) {
        return purExpenseService.search(vouNo, compCode);
    }

    @GetMapping(path = "/getSaleExpense")
    public Flux<SaleExpense> getSaleExpense(@RequestParam String vouNo,
                                            @RequestParam String compCode) {
        return saleExpenseService.search(vouNo, compCode);
    }
}
