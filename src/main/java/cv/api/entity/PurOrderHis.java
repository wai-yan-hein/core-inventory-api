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
public class PurOrderHis {

    private PurOrderHisKey key;
    private Integer deptId;
    private String reference;
    private String remark;
    private String description;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdDate;
    private Integer macId;
    private LocalDateTime vouDate;
    private LocalDateTime dueDate;
    private Boolean deleted;
    private String intgUpdStatus;
    private String receivedName;
    private String receivedPhoneNo;
    private String carNo;
    private String traderCode;
    private Integer printCount;
    private List<PurOrderHisDetail> listPurOrderDetail;
    private List<LocationKey> keys;
    private ZonedDateTime vouDateTime;
}
