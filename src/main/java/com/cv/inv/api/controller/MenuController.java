/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.entity.VRoleMenu;
import com.cv.inv.api.service.MenuService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Lenovo
 */
@RestController
@Slf4j
public class MenuController {

    @Autowired
    private MenuService menuService;

    @RequestMapping("/get-menu")
    public ResponseEntity<List<VRoleMenu>> getParentChildMenu(@RequestParam String roleCode, @RequestParam String type) {
        log.info("/get-menu");
        List<VRoleMenu> listM = menuService.getParentChildMenu(roleCode, type);
        return ResponseEntity.ok(listM);
    }
}
