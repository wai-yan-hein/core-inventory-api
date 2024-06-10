package cv.api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class YearEnd {
    private String yeCompCode;
    private String compCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate yearEndDate;
    private boolean batchLock;
    private boolean opening;
    private String createBy;
    private LocalDateTime createdDate;
    private String message;
    private String token;
}
