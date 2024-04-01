/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author pann
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ConsignHis {

    private ConsignHisKey key;
    private Integer deptId;
    private String locCode;
    private String remark;
    private String description;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdDate;
    private Integer macId;
    private LocalDateTime vouDate;
    private Boolean deleted;
    private String intgUpdStatus;
    private String labourGroupCode;
    private String receivedName;
    private String receivedPhoneNo;
    private String carNo;
    private String traderCode;
    private Integer printCount;
    private Integer tranSource;
    private String status;
    private List<ConsignHisDetail> listDetail;
    private List<LocationKey> keys;
    private ZonedDateTime vouDateTime;
}
