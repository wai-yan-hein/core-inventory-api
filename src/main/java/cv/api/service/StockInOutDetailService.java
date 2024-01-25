/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dto.StockInOutDetailDto;
import cv.api.entity.StockInOutDetail;
import cv.api.entity.StockInOutKey;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author wai yan
 */
public interface StockInOutDetailService {

    StockInOutDetail save(StockInOutDetail stock);

    Flux<StockInOutDetailDto> search(String vouNo, String compCode);

    Flux<StockInOutDetailDto> searchByJob(String jobId, String compCode);

    int delete(StockInOutKey key);
}
