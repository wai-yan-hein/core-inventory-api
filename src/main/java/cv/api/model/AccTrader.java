package cv.api.model;

import lombok.Data;

@Data
public class AccTrader implements java.io.Serializable {
    private TraderKey key;
    private String userCode;
    private String traderName;
    private String appName;
    private Boolean active;
    private String traderType;
    private Integer macId;
    private String accCode;

    public AccTrader() {
    }
}
