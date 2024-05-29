package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.SaleMan;
import cv.api.entity.SaleManKey;
import cv.api.entity.VouStatus;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SaleManService {
    private final DatabaseClient client;
    private final SeqService seqService;
    public Mono<SaleMan> save(SaleMan dto) {
        String code = dto.getKey().getSaleManCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(code)) {
            return seqService.getNextCode("SaleMan", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setSaleManCode(seqNo);
                        return insert(dto);
                    });
        }
        return update(dto);
    }
    public Mono<SaleMan> insert(SaleMan dto) {
        String sql = """
                INSERT INTO sale_man (saleman_code, mac_id, saleman_name, active, phone, updated_date,
                gender_id, address, comp_code, user_code, created_date, created_by, updated_by,
                dept_id, intg_upd_status, deleted)
                VALUES (:salemanCode, :macId, :salemanName, :active, :phone, :updatedDate, :genderId,
                :address, :compCode, :userCode, :createdDate, :createdBy, :updatedBy, :deptId,
                :intgUpdStatus, :deleted)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<SaleMan> update(SaleMan dto) {
        String sql = """
                UPDATE sale_man
                SET mac_id = :macId, saleman_name = :salemanName, active = :active, phone = :phone,
                updated_date = :updatedDate, gender_id = :genderId, address = :address, user_code = :userCode,
                created_date = :createdDate, created_by = :createdBy, updated_by = :updatedBy,
                dept_id = :deptId, intg_upd_status = :intgUpdStatus, deleted = :deleted
                WHERE saleman_code = :salemanCode AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<SaleMan> executeUpdate(String sql, SaleMan dto) {
        return client.sql(sql)
                .bind("salemanCode", dto.getKey().getSaleManCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("macId", dto.getMacId())
                .bind("salemanName", dto.getSaleManName())
                .bind("active", Util1.getInteger(dto.getActive()))
                .bind("phone", Parameters.in(R2dbcType.VARCHAR,dto.getPhone()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("genderId", Parameters.in(R2dbcType.VARCHAR, dto.getGenderId()))
                .bind("address", Parameters.in(R2dbcType.VARCHAR, dto.getAddress()))
                .bind("userCode", Parameters.in(R2dbcType.VARCHAR,dto.getUserCode()))
                .bind("createdDate", dto.getCreatedDate())
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR,dto.getUpdatedBy()))
                .bind("deptId", dto.getDeptId())
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR,dto.getIntgUpdStatus()))
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    private SaleMan mapRow(Row row) {
        return SaleMan.builder()
                .key(SaleManKey.builder()
                        .saleManCode(row.get("saleman_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .macId(row.get("mac_id", Integer.class))
                .saleManName(row.get("saleman_name", String.class))
                .active(row.get("active", Boolean.class))
                .phone(row.get("phone", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .genderId(row.get("gender_id", String.class))
                .address(row.get("address", String.class))
                .userCode(row.get("user_code", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .updatedBy(row.get("updated_by", String.class))
                .deptId(row.get("dept_id", Integer.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .build();
    }

    public Flux<SaleMan> findAll(String compCode) {
        String sql = """
                SELECT *
                FROM sale_man
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> delete(SaleManKey key) {
        String sql = """
                update sale_man
                set deleted = true,updated_date = :updatedDate
                where comp_code =:compCode
                and code =:code
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("code", key.getSaleManCode())
                .bind("updatedDate", LocalDateTime.now())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<SaleMan> findById(SaleManKey key) {
        String smCode = key.getSaleManCode();
        if(Util1.isNullOrEmpty(smCode)){
            return Mono.empty();
        }
        String sql = """
                SELECT *
                FROM sale_man
                WHERE saleman_code = :salemanCode
                AND comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("salemanCode", smCode)
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }
    public Flux<SaleMan> getSaleMan(LocalDateTime updatedDate) {
        String sql = """
                select *
                from sale_man
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }
}
