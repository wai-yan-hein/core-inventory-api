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
import cv.api.exception.ResponseUtil;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author WSwe
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TraderService {

    private final SeqService seqService;
    private final DatabaseClient client;


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


    public Mono<Trader> findByRFID(String rfId, String compCode) {
        String sql = """
                select code,user_code,trader_name,price_type,type
                from trader
                where comp_code=:compCode
                and rfid=:rfId
                limit 1
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("reId", rfId)
                .map((row, rowMetadata) -> Trader.builder()
                        .key(TraderKey.builder()
                                .code(row.get("code", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .userCode(row.get("user_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .priceType(row.get("price_type", String.class))
                        .type(row.get("type", String.class))
                        .build()).one();
    }


    public Flux<Trader> searchTrader(String text, String type, String compCode) {
        text = Util1.cleanStr(text);
        text = text + "%";
        String sql = """
                select code,user_code,trader_name,price_type,type,address,credit_amt,credit_days,account
                from trader
                where active = true
                and deleted = false
                and comp_code =:compCode
                and (LOWER(REPLACE(user_code, ' ', '')) like :text or LOWER(REPLACE(trader_name, ' ', '')) like :text)
                and (multi =true or type = :type or '-' = :type)
                order by user_code,trader_name
                limit 100
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
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
                        .account(row.get("account", String.class))
                        .build()).all();
    }


    public Mono<Trader> saveTrader(Trader dto) {
        return isDuplicateName(dto)
                .flatMap(duplicate -> {
                    if (duplicate) {
                        return ResponseUtil.createConflict("Duplicate Name : " + dto.getTraderName());
                    } else {
                        TraderKey key = dto.getKey();
                        if (Util1.isNull(key.getCode())) {
                            String compCode = dto.getKey().getCompCode();
                            String type = dto.getType();
                            return seqService.getNextCode(type, compCode, 5)
                                    .flatMap(seqNo -> {
                                        String code = type + "-" + seqNo;
                                        dto.getKey().setCode(code);
                                        return insert(dto);
                                    });
                        } else {
                            dto.setUpdatedDate(LocalDateTime.now());
                            return findById(key)
                                    .flatMap(trader -> update(dto))
                                    .switchIfEmpty(insert(dto));
                        }
                    }
                });
    }

    private Mono<Boolean> isDuplicateName(Trader dto) {
        String traderName = dto.getTraderName();
        String traderCode = dto.getKey().getCode();
        //not check in update mode
        if (!Util1.isNullOrEmpty(traderCode)) {
            return Mono.just(false);
        }
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(traderName)) {
            return Mono.just(false);
        } else {
            String sql = """
                    select count(*) count from trader where deleted = false and  trader_name = :traderName and comp_code = :compCode
                    """;
            return client.sql(sql)
                    .bind("traderName", traderName)
                    .bind("compCode", compCode)
                    .map((row, rowMetadata) -> row.get("count", Integer.class))
                    .one()
                    .map(integer -> integer > 0);
        }
    }


    public Flux<Trader> findAll(String compCode) {
        String sql = """
                select *
                from trader
                where comp_code =:compCode
                and deleted =false
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapToTrader(row)).all();

    }

    public Flux<Trader> findAllActive(String compCode) {
        String sql = """
                select *
                from trader
                where comp_code =:compCode
                and deleted =false
                and active = true
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapToTrader(row)).all();

    }


    public Flux<General> delete(TraderKey key) {
        return searchVoucher(key.getCode(), key.getCompCode())
                .collectList()
                .flatMapMany(list -> {
                    if (list.isEmpty()) {
                        return deleteTrader(key)
                                .thenMany(Flux.empty());
                    } else {
                        return Flux.fromIterable(list);
                    }
                });
    }


    private Flux<General> searchVoucher(String traderCode, String compCode) {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("sale_his", "Sale");
        hm.put("pur_his", "Purchase");
        hm.put("ret_in_his", "Return In");
        hm.put("ret_out_his", "Return Out");

        return Flux.fromIterable(hm.entrySet())
                .flatMap(entry -> {
                    String tableName = entry.getKey();
                    String transactionType = entry.getValue();

                    String sql = """
                            SELECT COUNT(*) AS count
                            FROM %s
                            WHERE deleted = false
                            AND trader_code = :traderCode
                            AND comp_code = :compCode""";
                    sql = String.format(sql, tableName);
                    return client.sql(sql)
                            .bind("traderCode", traderCode)
                            .bind("compCode", compCode)
                            .map(row -> {
                                Integer count = row.get("count", Integer.class);
                                if (count != null && count > 0) {
                                    return General.builder().message("Transaction exists in " + transactionType).build();
                                }
                                return General.builder().build();
                            }).all();
                })
                .filter(general -> general.getMessage() != null);
    }


    private Mono<Boolean> deleteTrader(TraderKey key) {
        String sql = """
                update trader
                set deleted = true,updated_date = :updatedDate
                where code =:code
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("code", key.getCode())
                .bind("compCode", key.getCompCode())
                .bind("updatedDate", LocalDateTime.now())
                .fetch()
                .rowsUpdated().thenReturn(true);
    }

    public Flux<Trader> unUploadTrader() {
        String sql = """
                select *
                from trader
                where intg_upd_status is null
                """;
        return client.sql(sql)
                .map((row, rowMetadata) -> mapToTrader(row)).all();
    }


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


    public Flux<Trader> getCustomer(String compCode) {
        String sql = """
                select *
                from trader
                where comp_code =:compCode
                and type ='CUS'
                and deleted = false
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapToTrader(row)).all();
    }


    public Flux<Trader> getSupplier(String compCode) {
        String sql = """
                select *
                from trader
                where comp_code =:compCode
                and type ='SUP'
                and deleted = false
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapToTrader(row)).all();
    }


    public Flux<Trader> getEmployee(String compCode) {
        String sql = """
                select *
                from trader
                where comp_code =:compCode
                and type ='EMP'
                and deleted = false
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
                .bind("macId", Parameters.in(R2dbcType.INTEGER, t.getMacId()))
                .bind("type", t.getType())
                .bind("active", Util1.getBoolean(t.getActive()))
                .bind("address", Parameters.in(R2dbcType.VARCHAR, t.getAddress()))
                .bind("createdDate", t.getCreatedDate())
                .bind("email", Parameters.in(R2dbcType.VARCHAR, t.getEmail()))
                .bind("phone", Parameters.in(R2dbcType.VARCHAR, t.getPhone()))
                .bind("township", Parameters.in(R2dbcType.VARCHAR, t.getTownShip()))
                .bind("traderName", Parameters.in(R2dbcType.VARCHAR, t.getTraderName()))
                .bind("updatedDate", LocalDateTime.now())
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
                .bind("deleted", Util1.getBoolean(t.getDeleted()))
                .bind("creditAmt", Parameters.in(R2dbcType.DOUBLE, t.getCreditAmt()))
                .bind("countryCode", Parameters.in(R2dbcType.VARCHAR, t.getCountryCode()))
                .fetch()
                .rowsUpdated()
                .thenReturn(t);
    }

    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT count(*) count
                FROM trader
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row) -> row.get("count", Integer.class))
                .one()
                .map(count -> count > 0);
    }

    public Mono<Boolean> updateACK(String traderCode, String account, String compCode) {
        String sql = """
                update trader set intg_upd_status = 'ACK',account =:account where  code =:traderCode and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("account", Parameters.in(R2dbcType.VARCHAR, account))
                .bind("traderCode", traderCode)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }
}
