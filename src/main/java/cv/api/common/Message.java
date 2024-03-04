package cv.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    private String header;
    private String entity;
    private String message;
    private String vouNo;
    private Map<String,Object> params;
    private Integer macId;
    private List<Integer> pageSize;
}
