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
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccSetting {
    private AccKey key;
    private String sourceAcc;
    private String payAcc;
    private String discountAcc;
    private String balanceAcc;
    private String taxAcc;
    private String commAcc;
    private String deptCode;
    private LocalDateTime updatedDate;
}
