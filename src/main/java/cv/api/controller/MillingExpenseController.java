package cv.api.controller;

import cv.api.entity.MillingExpense;
import cv.api.entity.PurExpense;
import cv.api.service.MillingExpenseService;
import cv.api.service.PurExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/millingExpense")
public class MillingExpenseController {
    @Autowired
    private MillingExpenseService service;

    @GetMapping(path = "/get-pur-expense")
    public Flux<MillingExpense> getExpense(@RequestParam String vouNo,
                                           @RequestParam String compCode) {
        return Flux.fromIterable(service.search(vouNo, compCode)).onErrorResume(throwable -> Flux.empty());
    }

}
