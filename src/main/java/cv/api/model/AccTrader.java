package cv.api.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccTrader implements java.io.Serializable {
    private AccTraderKey key;
    private String userCode;
    private String traderName;
    private String appName;
    private Boolean active;
    private String traderType;
    private Integer macId;
    private String account;
    private boolean deleted;
    private LocalDateTime createdDate;
    private String createdBy;
    public AccTrader() {
    }
}
