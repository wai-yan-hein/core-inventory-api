package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class ProcessHisDetail {
    private ProcessHisDetailKey key;
    private LocalDateTime vouDate;
    private Double qty;
    private String unit;
    private Double price;
    private String locName;
    private String locCode;
    private String stockName;
    private String stockUsrCode;
}
