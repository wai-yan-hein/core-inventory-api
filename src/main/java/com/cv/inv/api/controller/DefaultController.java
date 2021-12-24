/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.common.RoleDefault;
import com.cv.inv.api.service.RolePropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lenovo
 */
@RestController
@RequestMapping("/default")
@Slf4j
public class DefaultController {
    @Autowired
    private RolePropertyService propertyService;
    @GetMapping("/default")
    public ResponseEntity<RoleDefault> getRoleDefault(@RequestParam String roleCode) {
        RoleDefault roleDefault = propertyService.getRoleDefault(roleCode);
        return ResponseEntity.ok(roleDefault);
    }
}
