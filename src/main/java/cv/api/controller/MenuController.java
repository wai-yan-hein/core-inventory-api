/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReturnObject;
import cv.api.inv.entity.Menu;
import cv.api.inv.entity.VRoleMenu;
import cv.api.inv.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wai yan
 */
@RestController
@Slf4j
public class MenuController {

    @Autowired
    private MenuService menuService;
    private final ReturnObject ro = new ReturnObject();

    @RequestMapping("/get-role-menu")
    public ResponseEntity<List<VRoleMenu>> getParentChildMenu(@RequestParam String roleCode,
                                                              @RequestParam String type,
                                                              @RequestParam String compCode) {
        List<VRoleMenu> listM = menuService.getParentChildMenu(roleCode, type, compCode);
        return ResponseEntity.ok(listM);
    }

    @RequestMapping("/get-menu")
    public ResponseEntity<List<Menu>> getMenuTree(@RequestParam String compCode) {
        List<Menu> menus = menuService.getMenuTree(compCode);
        return ResponseEntity.ok(menus);
    }

    @RequestMapping("/get-menu-parent")
    public ResponseEntity<List<Menu>> getParentMenu(@RequestParam String compCode) {
        List<Menu> menus = menuService.getParentMenu(compCode);
        return ResponseEntity.ok(menus);
    }

    @PostMapping("/save-menu")
    public ResponseEntity<ReturnObject> saveMenu(@RequestBody Menu menu) {
        menu = menuService.saveMenu(menu);
        ro.setMessage("Saved Menu.");
        ro.setData(menu);
        return ResponseEntity.ok(ro);
    }

    @PostMapping("/delete-menu")
    public ResponseEntity<ReturnObject> deleteMenu(@RequestBody Menu menu) {
        menuService.deleteMenu(menu);
        ro.setMessage("Deleted Menu.");
        ro.setData(menu);
        return ResponseEntity.ok(ro);
    }

    @RequestMapping("/get-report")
    public ResponseEntity<List<VRoleMenu>> getReport(@RequestParam String roleCode) {
        log.info("/get-report");
        List<VRoleMenu> list = menuService.getReports(roleCode);
        return ResponseEntity.ok(list);
    }
}
