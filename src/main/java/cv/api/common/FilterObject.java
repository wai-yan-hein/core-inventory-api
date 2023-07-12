/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.common;

import lombok.Data;

/**
 * @author wai yan
 */
@Data
public class FilterObject {

    private String fromDate;
    private String toDate;
    private String cusCode;
    private String vouNo;
    private String userCode;
    private String description;
    private String remark;
    private String compCode;
    private String vouStatus;
    private String stockCode;
    private String saleManCode;
    private String reference;
    private String locCode;
    private String locCodeTo;
    private String refNo;
    private boolean deleted;
    private boolean close;
    private Integer deptId;
    private String processNo;
    private boolean finished;
    private String traderCode;
    private boolean nullBatch;
    private String batchNo;
    private boolean orderByBatch;
    private String projectNo;
    private String curCode;
    private String account;
    private String saleVouNo;

}
