package cv.api.controller;

import cv.api.common.ReturnObject;
import cv.api.entity.Expense;
import cv.api.entity.ExpenseKey;
import cv.api.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;
    private final ReturnObject ro = new ReturnObject();

    @PostMapping(path = "/save-expense")
    public ResponseEntity<?> saveExpense(@RequestBody Expense e) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.save(e));
    }

    @GetMapping(path = "/get-expense")
    public ResponseEntity<?> getExpense(@RequestParam String compCode) {
        return ResponseEntity.ok(expenseService.getExpense(compCode));
    }

    @PostMapping(path = "/delete-expense")
    public ResponseEntity<?> deletePur(@RequestBody ExpenseKey key) {
        expenseService.delete(key);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ro);
    }
}
