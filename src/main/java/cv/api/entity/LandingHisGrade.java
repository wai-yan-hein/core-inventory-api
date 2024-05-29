/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cv.api.entity;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Builder
public class LandingHisGrade {

    private LandingHisGradeKey key;
    private String stockCode;
    private Double matchCount;
    private Double matchPercent;
    private Boolean choose;
    private String stockName;
    private String userCode;

}
