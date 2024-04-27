package cv.api.report.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class Income {
    private String id;
    private String compCode;
    private String tranGroup;
    private String tranOption;
    private LocalDate tranDate;
    private Double vouTotal;
    private Double vouPaid;
    private Integer vouCount;
    private Integer patientCount;
    private String currency;
    private LocalDateTime updatedDate;
}
