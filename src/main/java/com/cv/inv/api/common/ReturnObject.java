/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.common;

import lombok.Data;

import java.util.List;

/**
 * @author Lenovo
 */
@Data
public class ReturnObject {
    private String status;
    private String message;
    private String errorMessage;
    private List<Object> list;
    private Object obj;
    private byte[] file;
}
