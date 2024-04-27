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
 * @author wai yan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class OrderHis {

    private OrderHisKey key;
    private Integer deptId;
    private String traderCode;
    private String saleManCode;
    private LocalDateTime vouDate;
    private LocalDateTime creditTerm;
    private String curCode;
    private String remark;
    private String reference;
    private Double vouTotal;
    private Double vouBalance;
    private Boolean deleted;
    private LocalDateTime createdDate;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private String locCode;
    private Integer macId;
    private String intgUpdStatus;
    private Boolean vouLock;
    private String projectNo;
    private String orderStatus;
    private Boolean post;
    private List<OrderHisDetail> listSH;
    private List<OrderDetailKey> listDel;
    private Boolean backup;
    private List<String> location;
    private ZonedDateTime vouDateTime;
    private String traderName;
    private String userCode;
    private String orderStatusName;
    private Integer vouCount;
}
