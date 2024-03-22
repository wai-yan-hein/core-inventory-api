/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.LocationDao;
import cv.api.entity.Location;
import cv.api.entity.LocationKey;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationDao dao;
    private final SeqTableService seqService;
    private final DatabaseClient client;

    @Override
    public Location save(Location loc) {
        if (Util1.isNullOrEmpty(loc.getKey().getLocCode())) {
            Integer macId = loc.getMacId();
            String compCode = loc.getKey().getCompCode();
            String locCode = getLocationCode(macId, compCode);
            loc.getKey().setLocCode(locCode);
        }
        return dao.save(loc);
    }

    @Override
    public List<Location> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public List<Location> findAll() {
        return dao.findAll();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public List<Location> search(String parent) {
        return dao.search(parent);
    }

    @Override
    public List<Location> unUpload() {
        return dao.unUpload();
    }


    @Override
    public List<Location> getLocation(LocalDateTime updatedDate) {
        return dao.getLocation(updatedDate);
    }

    @Override
    public Mono<Boolean> insertTmp(List<String> listLocation, String compCode, Integer macId, String warehouse) {
        if (listLocation == null || listLocation.isEmpty() || !warehouse.equals("-")) {
            String sql = """
                    insert into f_location(f_code,mac_id)
                    select loc_code,:macId
                    from location
                    where comp_code =:compCode
                    and (warehouse_code =:whCode or '-' =:whCode)
                    """;
            return deleteTmp(macId).then(client.sql(sql)
                    .bind("compCode", compCode)
                    .bind("whCode", warehouse)
                    .bind("macId", macId)
                    .fetch().rowsUpdated().thenReturn(true));
        } else {
            return deleteTmp(macId)
                    .flatMap(aBoolean -> Flux.fromIterable(listLocation)
                            .flatMap(locCode -> {
                                String sql = """
                            insert into f_location (f_code,mac_id)
                            values (:locCode,:macId);
                            """;
                                return client.sql(sql)
                                        .bind("locCode", locCode)
                                        .bind("macId", macId)
                                        .fetch()
                                        .rowsUpdated()
                                        .thenReturn(true);
                            }).then(Mono.just(true)));

        }
    }

    private Mono<Boolean> deleteTmp(int macId) {
        String sql = """
                delete from f_location where mac_id =:macId
                """;
        return client.sql(sql)
                .bind("macId", macId)
                .fetch().rowsUpdated().thenReturn(true);
    }

    private String getLocationCode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "Location", "-", compCode);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
    }

    @Override
    public Location findByCode(LocationKey code) {
        return dao.findByCode(code);
    }
}
