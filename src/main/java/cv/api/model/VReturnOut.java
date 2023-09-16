/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author wai yan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VReturnOut implements Serializable {

    private String vouNo;
    private String traderCode;
    private String vouDate;
    private String curCode;
    private String remark;
    private Float vouTotal;
    private Float discount;
    private Float discountPrice;
    private Date createdDate;
    private String createdBy;
    private boolean deleted;
    private Float paid;
    private Float vouBalance;
    private String updatedBy;
    private Date updatedDate;
    private String compCode;
    private Integer macId;
    private Integer sessionId;
    private String stockCode;
    private Float qty;
    private Float wt;
    private String unit;
    private Float price;
    private Float amount;
    private String locCode;
    private Integer uniqueId;
    private String traderName;
    private String stockName;
    private String locationName;
    private Integer deptId;
    private ZonedDateTime vouDateTime;
}
