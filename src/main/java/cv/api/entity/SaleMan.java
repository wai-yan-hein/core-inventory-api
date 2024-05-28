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

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class SaleMan {
    private SaleManKey key;
    private String saleManName;
    private Boolean active;
    private String phone;
    private LocalDateTime updatedDate;
    private String address;
    private Integer macId;
    private String userCode;
    private LocalDateTime createdDate;
    private String createdBy;
    private String updatedBy;
    private String intgUpdStatus;
    private Integer deptId;
    private Boolean deleted;
    private String genderId;
}
