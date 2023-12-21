package cv.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Gl {

    private GlKey key;
    private LocalDateTime glDate;
    private String description;
    private String srcAccCode;
    private String accCode;
    private String curCode;
    private Double drAmt;
    private Double crAmt;
    private String reference;
    private String deptCode;
    private String vouNo;
    private String traderCode;
    private LocalDateTime createdDate;
    private String createdBy;
    private String tranSource;
    private String remark;
    private Integer macId;
    private String refNo;
    private boolean deleted;
    private boolean cash = false;
    private String batchNo;
    private String projectNo;

    public Gl() {
    }
}
