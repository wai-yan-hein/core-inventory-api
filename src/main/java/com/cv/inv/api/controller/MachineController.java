/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.common.ReturnObject;
import com.cv.inv.api.entity.MachineInfo;
import com.cv.inv.api.service.MachineInfoService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Lenovo
 */
@RestController
public class MachineController {

    @Autowired
    private MachineInfoService macService;
    @Autowired
    private ReturnObject ro;

    @RequestMapping(path = "/get-mac-id", method = RequestMethod.GET)
    public ResponseEntity<ReturnObject> getMacId(@RequestParam String macName) throws Exception {
        ro.setObj(macService.getMax(macName));
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/save-machine")
    public ResponseEntity<MachineInfo> saveMachine(@RequestBody MachineInfo machine, HttpServletRequest request) throws Exception {
        MachineInfo mac = macService.save(machine);
        return ResponseEntity.ok(mac);
    }
}
