package cv.api.controller;

import cv.api.dao.SaleExpenseDao;
import cv.api.entity.Expense;
import cv.api.entity.ExpenseKey;
import cv.api.entity.PurExpense;
import cv.api.entity.SaleExpense;
import cv.api.service.ExpenseService;
import cv.api.service.PurExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
    @Autowired
    private PurExpenseService purExpenseService;
    @Autowired
    private SaleExpenseDao saleExpenseDao;

    @Autowired
    private ExpenseService expenseService;

    @PostMapping(path = "/saveExpense")
    public Mono<?> saveExpense(@RequestBody Expense e) {
        return Mono.justOrEmpty(expenseService.save(e));
    }

    @GetMapping(path = "/getExpense")
    public Flux<?> getExpense(@RequestParam String compCode) {
        return Flux.fromIterable(expenseService.getExpense(compCode));
    }

    @PostMapping(path = "/deleteExpense")
    public Mono<?> deletePur(@RequestBody ExpenseKey key) {
        expenseService.delete(key);
        return Mono.justOrEmpty(true);
    }

    @GetMapping(path = "/getPurExpense")
    public Flux<PurExpense> getPurExpense(@RequestParam String vouNo,
                                          @RequestParam String compCode) {
        return Flux.fromIterable(purExpenseService.search(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getSaleExpense")
    public Flux<SaleExpense> getSaleExpense(@RequestParam String vouNo,
                                            @RequestParam String compCode) {
        return Flux.fromIterable(saleExpenseDao.search(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }
}
