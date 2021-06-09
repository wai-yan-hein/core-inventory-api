/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.entity.Currency;
import com.cv.inv.api.entity.CurrencyKey;
import com.cv.inv.api.service.CurrencyService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Lenovo
 */
@RestController
@Slf4j
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @PostMapping(path = "/save-currency")
    public ResponseEntity<Currency> saveCurrency(@RequestBody Currency machine, HttpServletRequest request) throws Exception {
        Currency currency = currencyService.save(machine);
        return ResponseEntity.ok(currency);
    }

    @PostMapping(path = "/find-by-id")
    public ResponseEntity<Currency> findById(@RequestBody CurrencyKey key) {
        Currency cur = currencyService.findById(key);
        return ResponseEntity.ok(cur);
    }

    @GetMapping(path = "/get-currency")
    public ResponseEntity<List<Currency>> getCurrency(@RequestParam String compCode) {
        List<Currency> currency = currencyService.search("-", "-", compCode);
        return ResponseEntity.ok(currency);
    }
}
