/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.FilterObject;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.dto.OrderNote;
import cv.api.repo.AccountRepo;
import cv.api.service.OrderNoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

    private final ReturnObject ro = ReturnObject.builder().build();
    private final OrderNoteService orderNoteService;
    private final AccountRepo accountRepo;

    @PostMapping(path = "/saveOrderNote")
    public Mono<?> saveSale(@NotNull @RequestBody OrderNote orderNote) {
        orderNote.setUpdatedDate(Util1.getTodayLocalDate());
        return orderNoteService.save(orderNote).thenReturn(orderNote);
    }

    @PostMapping(path = "/getOrderNote")
    public Flux<?> getOrderNote(@NotNull @RequestBody FilterObject filter) {
        return orderNoteService.history(filter);
    }

    @GetMapping(path = "/findOrderNote")
    public Mono<?> findOrderNote(@NotNull @RequestParam String vouNo, @RequestParam String compCode) {
        return orderNoteService.findOrderNote(vouNo, compCode);
    }

    @GetMapping(path = "/getOrderNoteDetail")
    public Flux<?> getOrderNoteDetail(@NotNull @RequestParam String vouNo, @RequestParam String compCode) {
        return orderNoteService.getDetail(vouNo, compCode);
    }

    @GetMapping(path = "/updateOrderNote")
    public Mono<?> updateOrderNote(@NotNull @RequestParam String vouNo, @RequestParam String compCode, @RequestParam Boolean deleted) {
        return orderNoteService.update(vouNo, compCode, deleted);
    }

}
