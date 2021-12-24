/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.common;

import lombok.Data;

/**
 *
 * @author Lenovo
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
}
