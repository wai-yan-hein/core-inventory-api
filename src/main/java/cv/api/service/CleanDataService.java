package cv.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CleanDataService {
    private final DatabaseClient client;

    public Mono<Boolean> cleanData() {
        return getDatabase().flatMap(schemaName -> getTableName(schemaName)
                .flatMap(this::truncateTable).then(Mono.just(true)));
    }

    public Flux<String> getTableName(String schemaName) {
        String sql = """
                SELECT
                    table_name
                FROM
                    information_schema.TABLES
                WHERE
                    table_schema = :schemaName
                    AND table_type = 'BASE TABLE';
                """;
        return client.sql(sql)
                .bind("schemaName", schemaName)
                .map((row) -> row.get("table_name", String.class)).all();
    }

    private Mono<Boolean> truncateTable(String tableName) {
        if (!neglectTable().contains(tableName)) {
            String sql = "delete from " + tableName;
            return client.sql(sql)
                    .fetch().rowsUpdated().thenReturn(true);
        }
        return Mono.just(false);
    }

    public Mono<String> getDatabase() {
        return client.sql("SELECT DATABASE() schema_name")
                .map((row, rowMetadata) -> row.get("schema_name", String.class)).one();
    }

    private List<String> neglectTable() {
        List<String> list = new ArrayList<>();
        return list;
    }
}
