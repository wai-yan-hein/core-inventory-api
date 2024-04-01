/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wai yan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class StockType {

    private StockTypeKey key;
    private String stockTypeName;
    private String account;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private LocalDateTime createdDate;
    private String createdBy;
    private Integer macId;
    private String userCode;
    private String intgUpdStatus;
    private Integer deptId;
    private Boolean deleted;
    private Boolean active;
    private Boolean finishedGroup;
    private Integer groupType;
}
