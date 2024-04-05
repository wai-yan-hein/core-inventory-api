/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class Location {
    private LocationKey key;
    private String locName;
    private String parentCode;
    private Boolean calcStock;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private LocalDateTime createdDate;
    private String createdBy;
    private Integer macId;
    private String userCode;
    private String intgUpdStatus;
    private Integer mapDeptId;
    private Integer deptId;
    private String deptCode;
    private String cashAcc;
    private Boolean deleted;
    private Boolean active;
    private String wareHouseCode;
    private String wareHouseName;
    private String locationType;
}
