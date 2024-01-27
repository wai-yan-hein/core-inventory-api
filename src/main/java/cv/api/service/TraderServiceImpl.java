/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.common.Util1;
import cv.api.entity.Trader;
import cv.api.entity.TraderKey;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author WSwe
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TraderServiceImpl implements TraderService {

    private final SeqTableService seqService;
    private final ReportService reportService;
    private final DatabaseClient client;


    @Override
    public Mono<Trader> findById(TraderKey key) {
        String sql = """
                select *
                from trader
                where code=:code
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("code", key.getCode())
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapToTrader(row)).one();
    }

    private Trader mapToTrader(Row row) {
        return Trader.builder()
                .key(TraderKey.builder()
                        .code(row.get("code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .macId(row.get("mac_id", Integer.class))
                .type(row.get("type", String.class))
                .active(row.get("active", Boolean.class))
                .address(row.get("address", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .email(row.get("email", String.class))
                .phone(row.get("phone", String.class))
                .townShip(row.get("township", String.class))
                .traderName(row.get("trader_name", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .creditDays(row.get("credit_days", Integer.class))
                .creditLimit(row.get("credit_limit", Integer.class))
                .remark(row.get("remark", String.class))
                .regCode(row.get("reg_code", String.class))
                .contactPerson(row.get("contact_person", String.class))
                .migCode(row.get("mig_code", String.class))
                .createdBy(row.get("created_by", String.class))
                .updatedBy(row.get("updated_by", String.class))
                .userCode(row.get("user_code", String.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .cashDown(row.get("cash_down", Boolean.class))
                .multi(row.get("multi", Boolean.class))
                .priceType(row.get("price_type", String.class))
                .groupCode(row.get("group_code", String.class))
                .account(row.get("account", String.class))
                .rfId(row.get("rfid", String.class))
                .nrc(row.get("nrc", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .creditAmt(row.get("credit_amt", Double.class))
                .countryCode(row.get("country_code", String.class))
                .build();
    }

    @Override
    public Mono<Trader> findByRFID(String rfId, String compCode, Integer deptId) {
        String sql = """
                select code,user_code,trader_name,price_type,type
                from trader
                where comp_code=:compCode
                and (dept_id =:deptId or 0 =:deptId)
                and rfid=:rfId
                limit 1
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("reId", rfId)
                .map((row, rowMetadata) -> Trader.builder()
                        .key(TraderKey.builder()
                                .code(row.get("code", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .deptId(deptId)
                        .userCode(row.get("user_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .priceType(row.get("price_type", String.class))
                        .type(row.get("type", String.class))
                        .build()).one();
    }

    @Override
    public Flux<Trader> searchTrader(String text, String type, String compCode, Integer deptId) {
        text = Util1.cleanStr(text);
        text = text + "%";
        String sql = """
                select code,user_code,trader_name,price_type,type,address,credit_amt,credit_days
                from trader
                where active = true
                and deleted = false
                and comp_code =:compCode
                and (dept_id =:deptId or 0 =deptId)
                and (LOWER(REPLACE(user_code, ' ', '')) like :text or LOWER(REPLACE(trader_name, ' ', '')) like :text)
                and (multi =1 or type =:type)
                order by user_code,trader_name
                limit 100
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("text", text)
                .bind("type", type)
                .map((row) -> Trader.builder()
                        .key(TraderKey.builder()
                                .code(row.get("code", String.class))
                                .compCode(compCode)
                                .build())
                        .userCode(row.get("user_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .priceType(row.get("price_type", String.class))
                        .type(row.get("type", String.class))
                        .address(row.get("address", String.class))
                        .creditAmt(row.get("credit_amt", Double.class))
                        .creditDays(row.get("credit_days", Integer.class))
                        .build()).all();
    }

    @Override
    public Mono<Trader> saveTrader(Trader td) {
        TraderKey key = td.getKey();
        if (Util1.isNull(key.getCode())) {
            String compCode = td.getKey().getCompCode();
            String type = td.getType();
            String code = getTraderCode(type, compCode);
            td.getKey().setCode(code);
            return insert(td);
        } else {
            td.setUpdatedDate(LocalDateTime.now());
            return findById(key)
                    .flatMap(trader -> update(td))
                    .switchIfEmpty(insert(td));
        }
    }

    private String getTraderCode(String option, String compCode) {
        int seqNo = seqService.getSequence(0, option, "-", compCode);
        return option.toUpperCase() + String.format("%0" + 6 + "d", seqNo);
    }

    @Override
    public Flux<Trader> findAll(String compCode) {
        String sql = """
                select *
                from trader
                where comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapToTrader(row)).all();

    }


    @Override
    public Flux<General> delete(TraderKey key) {
        List<General> list = reportService.isTraderExist(key.getCode(), key.getCompCode());
        if (list.isEmpty()) {
            deleteTrader(key);
        }
        return Flux.fromIterable(list);
    }

    private void deleteTrader(TraderKey key) {
        String sql = """
                update trader
                set deleted = true
                where code =:code
                and comp_code=:compCode
                """;
        client.sql(sql)
                .bind("code", key.getCode())
                .bind("compCode", key.getCompCode())
                .fetch()
                .rowsUpdated().then();
    }

    @Override
    public Flux<Trader> unUploadTrader() {
        String sql = """
                select *
                from trader
                where intg_upd_status is null
                """;
        return client.sql(sql)
                .map((row, rowMetadata) -> mapToTrader(row)).all();
    }

    @Override
    public Mono<String> getMaxDate() {
        String sql = """
                select max(updated_date) updated_date
                from trader
                """;
        return client.sql(sql)
                .map((row, rowMetadata) -> {
                    LocalDateTime date = row.get("updated_date", LocalDateTime.class);
                    return date == null ? Util1.getOldDate() : Util1.toDateTimeStrMYSQL(date);
                }).one();
    }

    @Override
    public Flux<Trader> getUpdateTrader(LocalDateTime updatedDate) {
        String sql = """
                select *
                from trader
                where updated_date >=:updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapToTrader(row)).all();
    }
    @Override
    public Flux<Trader> getUpdateCustomer(LocalDateTime updatedDate) {
        String sql = """
                select *
                from trader
                where updated_date >=:updatedDate
                and type ='CUS'
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapToTrader(row)).all();
    }

    @Override
    public Flux<Trader> getCustomer(String compCode, Integer deptId) {
        String sql = """
                select *
                from trader
                where comp_code =:compCode
                and type ='CUS'
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapToTrader(row)).all();
    }

    @Override
    public Flux<Trader> getSupplier(String compCode, Integer deptId) {
        String sql = """
                select *
                from trader
                where comp_code =:compCode
                and type ='SUP'
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapToTrader(row)).all();
    }


    @Override
    public Flux<Trader> getEmployee(String compCode, Integer deptId) {
        String sql = """
                select *
                from trader
                where comp_code =:compCode
                and type ='EMP'
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapToTrader(row)).all();
    }

    public Mono<Trader> insert(Trader trader) {
        String sql = """
                INSERT INTO trader (
                    code, comp_code, dept_id, mac_id, type, active, address, created_date,
                    email, phone, township, trader_name, updated_date, credit_days, credit_limit,
                    remark, reg_code, contact_person, mig_code, created_by, updated_by,
                    user_code, intg_upd_status, cash_down, multi, price_type, group_code,
                    account, rfid, nrc, deleted, credit_amt, country_code
                ) VALUES (
                    :code, :compCode, :deptId, :macId, :type, :active, :address, :createdDate,
                    :email, :phone, :township, :traderName, :updatedDate, :creditDays, :creditLimit,
                    :remark, :regCode, :contactPerson, :migCode, :createdBy, :updatedBy,
                    :userCode, :intgUpdStatus, :cashDown, :multi, :priceType, :groupCode,
                    :account, :rfid, :nrc, :deleted, :creditAmt, :countryCode
                )
                """;

        return executeUpdate(sql, trader);
    }

    public Mono<Trader> update(Trader trader) {
        String sql = """
                UPDATE trader
                SET
                    comp_code = :compCode,
                    dept_id = :deptId,
                    mac_id = :macId,
                    type = :type,
                    active = :active,
                    address = :address,
                    created_date = :createdDate,
                    email = :email,
                    phone = :phone,
                    township = :township,
                    trader_name = :traderName,
                    updated_date = :updatedDate,
                    credit_days = :creditDays,
                    credit_limit = :creditLimit,
                    remark = :remark,
                    reg_code = :regCode,
                    contact_person = :contactPerson,
                    mig_code = :migCode,
                    created_by = :createdBy,
                    updated_by = :updatedBy,
                    user_code = :userCode,
                    intg_upd_status = :intgUpdStatus,
                    cash_down = :cashDown,
                    multi = :multi,
                    price_type = :priceType,
                    group_code = :groupCode,
                    account = :account,
                    rfid = :rfid,
                    nrc = :nrc,
                    deleted = :deleted,
                    credit_amt = :creditAmt,
                    country_code = :countryCode
                WHERE
                    code = :code AND comp_code = :compCode;
                """;

        return executeUpdate(sql, trader);
    }

    private Mono<Trader> executeUpdate(String sql, Trader t) {
        return client.sql(sql)
                .bind("code", t.getKey().getCode())
                .bind("compCode", t.getKey().getCompCode())
                .bind("deptId", t.getDeptId())
                .bind("macId", t.getMacId())
                .bind("type", t.getType())
                .bind("active", t.getActive())
                .bind("address", Parameters.in(R2dbcType.VARCHAR, t.getAddress()))
                .bind("createdDate", t.getCreatedDate())
                .bind("email", Parameters.in(R2dbcType.VARCHAR, t.getEmail()))
                .bind("phone", Parameters.in(R2dbcType.VARCHAR, t.getPhone()))
                .bind("township", Parameters.in(R2dbcType.VARCHAR, t.getTownShip()))
                .bind("traderName", Parameters.in(R2dbcType.VARCHAR, t.getTraderName()))
                .bind("updatedDate", Parameters.in(R2dbcType.TIMESTAMP, t.getUpdatedDate()))
                .bind("creditDays", Parameters.in(R2dbcType.INTEGER, t.getCreditDays()))
                .bind("creditLimit", Parameters.in(R2dbcType.INTEGER, t.getCreditLimit()))
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, t.getRemark()))
                .bind("regCode", Parameters.in(R2dbcType.VARCHAR, t.getRegCode()))
                .bind("contactPerson", Parameters.in(R2dbcType.VARCHAR, t.getContactPerson()))
                .bind("migCode", Parameters.in(R2dbcType.VARCHAR, t.getMigCode()))
                .bind("createdBy", Parameters.in(R2dbcType.VARCHAR, t.getCreatedBy()))
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, t.getUpdatedBy()))
                .bind("userCode", Parameters.in(R2dbcType.VARCHAR, t.getUserCode()))
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, t.getIntgUpdStatus()))
                .bind("cashDown", Parameters.in(R2dbcType.BOOLEAN, t.getCashDown()))
                .bind("multi", Parameters.in(R2dbcType.BOOLEAN, t.getMulti()))
                .bind("priceType", Parameters.in(R2dbcType.VARCHAR, t.getPriceType()))
                .bind("groupCode", Parameters.in(R2dbcType.VARCHAR, t.getGroupCode()))
                .bind("account", Parameters.in(R2dbcType.VARCHAR, t.getAccount()))
                .bind("rfid", Parameters.in(R2dbcType.VARCHAR, t.getRfId()))
                .bind("nrc", Parameters.in(R2dbcType.VARCHAR, t.getNrc()))
                .bind("deleted", t.getDeleted())
                .bind("creditAmt", Parameters.in(R2dbcType.DOUBLE, t.getCreditAmt()))
                .bind("countryCode", Parameters.in(R2dbcType.VARCHAR, t.getCountryCode()))
                .fetch()
                .rowsUpdated()
                .thenReturn(t);
    }

}
