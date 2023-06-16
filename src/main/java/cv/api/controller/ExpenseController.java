package cv.api.controller;

import cv.api.common.ReturnObject;
import cv.api.entity.Expense;
import cv.api.entity.ExpenseKey;
import cv.api.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Flushable;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;
    private final ReturnObject ro = new ReturnObject();

    @PostMapping(path = "/save-expense")
    public Mono<?> saveExpense(@RequestBody Expense e) {
        return Mono.justOrEmpty(expenseService.save(e));
    }

    @GetMapping(path = "/get-expense")
    public Flux<?> getExpense(@RequestParam String compCode) {
        return Flux.fromIterable(expenseService.getExpense(compCode));
    }

    @PostMapping(path = "/delete-expense")
    public Mono<?> deletePur(@RequestBody ExpenseKey key) {
        expenseService.delete(key);
        return Mono.justOrEmpty(true);
    }
}
