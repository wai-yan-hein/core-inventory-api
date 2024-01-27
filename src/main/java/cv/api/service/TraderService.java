/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.entity.Trader;
import cv.api.entity.TraderKey;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author WSwe
 */
public interface TraderService {

    Mono<Trader> findById(TraderKey key);

    Mono<Trader> findByRFID(String rfId, String compCode, Integer deptId);

    Flux<Trader> searchTrader(String str, String type, String compCode, Integer deptId);


    Mono<Trader> saveTrader(Trader trader);

    Flux<Trader> findAll(String compCode);

    Flux<General> delete(TraderKey key);

    Flux<Trader> unUploadTrader();

    Mono<String> getMaxDate();

    Flux<Trader> getUpdateTrader(LocalDateTime updatedDate);
    Flux<Trader> getUpdateCustomer(LocalDateTime updatedDate);

    Flux<Trader> getCustomer(String compCode,Integer deptId);
    Flux<Trader> getSupplier(String compCode,Integer deptId);
    Flux<Trader> getEmployee(String compCode,Integer deptId);

}
