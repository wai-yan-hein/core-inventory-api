/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReportFilter;
import cv.api.dto.OrderFileJoin;
import cv.api.dto.OrderNote;
import cv.api.service.OrderNoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/orderNote")
@Slf4j
@RequiredArgsConstructor
public class OrderNoteController {

    private final OrderNoteService orderNoteService;

    @PostMapping(path = "/saveOrderNote")
    public Mono<OrderNote> saveSale(@RequestBody OrderNote note) {
        return orderNoteService.save(note);
    }

    @PostMapping(path = "/getOrderNote")
    public Flux<OrderNote> getOrderNote(@RequestBody ReportFilter filter) {
        return orderNoteService.history(filter);
    }

    @GetMapping(path = "/findOrderNote")
    public Mono<OrderNote> findOrderNote(@RequestParam String vouNo, @RequestParam String compCode) {
        return orderNoteService.findOrderNote(vouNo, compCode);
    }

    @GetMapping(path = "/getOrderNoteDetail")
    public Flux<OrderFileJoin> getOrderNoteDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return orderNoteService.getDetail(vouNo, compCode);
    }

    @GetMapping(path = "/updateOrderNote")
    public Mono<Boolean> updateOrderNote(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Boolean deleted) {
        return orderNoteService.update(vouNo, compCode, deleted);
    }

}
