/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.AccKey;
import cv.api.entity.AccSetting;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * @author wai yan
 */
@Service
@RequiredArgsConstructor
public class AccSettingService {
    private final DatabaseClient client;

    public Flux<AccSetting> findAll(String compCode) {
        String sql = """
                select *
                from acc_setting
                where comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<AccSetting> save(AccSetting dto) {
        return findByCode(dto.getKey())
                .flatMap(t -> update(dto)).switchIfEmpty(Mono.defer(() -> insert(dto)));
    }

    @Transactional
    private Mono<AccSetting> insert(AccSetting dto) {
        String sql = """
                INSERT INTO acc_setting (type, comp_code, dis_acc, pay_acc, tax_acc, dep_code, source_acc, bal_acc, comm_acc)
                VALUES (:type, :compCode, :disAcc, :payAcc, :taxAcc, :depCode, :sourceAcc, :balAcc, :commAcc)
                """;
        return executeUpdate(dto, sql);
    }

    private Mono<AccSetting> update(AccSetting dto) {
        String sql = """
                UPDATE acc_setting
                SET dis_acc = :disAcc, pay_acc = :payAcc, tax_acc = :taxAcc, dep_code = :depCode,
                    source_acc = :sourceAcc, bal_acc = :balAcc, comm_acc = :commAcc
                WHERE type = :type AND comp_code = :compCode
                """;
        return executeUpdate(dto, sql);
    }

    private Mono<AccSetting> executeUpdate(AccSetting dto, String sql) {
        return client.sql(sql)
                .bind("type", dto.getKey().getType())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("disAcc", Parameters.in(R2dbcType.VARCHAR, dto.getDiscountAcc()))
                .bind("payAcc", Parameters.in(R2dbcType.VARCHAR, dto.getPayAcc()))
                .bind("taxAcc", Parameters.in(R2dbcType.VARCHAR, dto.getTaxAcc()))
                .bind("depCode", Parameters.in(R2dbcType.VARCHAR, dto.getDeptCode()))
                .bind("sourceAcc", Parameters.in(R2dbcType.VARCHAR, dto.getSourceAcc()))
                .bind("balAcc", Parameters.in(R2dbcType.VARCHAR, dto.getBalanceAcc()))
                .bind("commAcc", Parameters.in(R2dbcType.VARCHAR, dto.getCommAcc()))
                .fetch().rowsUpdated().thenReturn(dto);
    }

    public AccSetting mapRow(Row row) {
        return AccSetting.builder()
                .key(AccKey.builder()
                        .type(row.get("type", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .discountAcc(row.get("dis_acc", String.class))
                .payAcc(row.get("pay_acc", String.class))
                .taxAcc(row.get("tax_acc", String.class))
                .deptCode(row.get("dep_code", String.class))
                .sourceAcc(row.get("source_acc", String.class))
                .balanceAcc(row.get("bal_acc", String.class))
                .commAcc(row.get("comm_acc", String.class))
                .build();
    }


    public Mono<AccSetting> findByCode(AccKey key) {
        if (Util1.isNullOrEmpty(key.getType())) {
            return Mono.empty();
        }
        String sql = """
                select *
                from acc_setting
                where type=:type
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("type", key.getType())
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Flux<AccSetting> getAccSetting(LocalDateTime updatedDate) {
        String sql = """
                select *
                from acc_setting
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }
}
