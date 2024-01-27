/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class Trader {
    private TraderKey key;
    private String traderName;
    private String address;
    private String regCode;
    private String phone;
    private String email;
    private Boolean active;
    private String remark;
    private String rfId;
    private String nrc;
    private String migCode;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private LocalDateTime createdDate;
    private String createdBy;
    private Integer macId;
    private String userCode;
    private Integer creditLimit;
    private Integer creditDays;
    private String contactPerson;
    private String type;
    private Boolean cashDown;
    private Boolean multi;
    private String intgUpdStatus;
    private String priceType;
    private String groupCode;
    private String account;
    private Boolean deleted;
    private Double creditAmt;
    private Integer deptId;
    private String countryCode;
    private String townShip;
}
