package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandingHis {

    private LandingHisKey key;
    private Integer deptId;
    private LocalDateTime vouDate;
    private String traderCode;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private Integer macId;
    private String remark;
    private String locCode;
    private String stockCode;
    private Double amount;
    private Double purAmt;
    private Double purPrice;
    private Double price;
    private Double criteriaAmt;
    private String cargo;
    private String curCode;
    private Double grossQty;
    private Integer printCount;
    private Boolean post;
    private Boolean deleted;
    private List<LandingHisPrice> listPrice;
    private List<LandingHisPriceKey> listDelPrice;
    private List<LandingHisQty> listQty;
    private List<LandingHisGrade> listGrade;
    private List<LandingHisQtyKey> listDelQty;
    private String traderName;
    private String locName;
    private String stockName;
    private String traderUserCode;
    private ZonedDateTime vouDateTime;

}