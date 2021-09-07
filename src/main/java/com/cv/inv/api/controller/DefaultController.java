/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.common.ReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cv.inv.api.service.RolePropertyService;

/**
 *
 * @author Lenovo
 */
@RestController
@RequestMapping("/default")
@Slf4j
public class DefaultController {

    @Autowired
    private ReturnObject ro;
    @Autowired
    private RolePropertyService defService;
}
