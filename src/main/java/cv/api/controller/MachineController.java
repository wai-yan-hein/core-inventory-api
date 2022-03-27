/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReturnObject;
import cv.api.inv.entity.MachineInfo;
import cv.api.inv.service.MachineInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wai yan
 */
@RestController
public class MachineController {

    @Autowired
    private MachineInfoService macService;
    @Autowired
    private ReturnObject ro;

    @RequestMapping(path = "/get-mac-id", method = RequestMethod.GET)
    public ResponseEntity<ReturnObject> getMacId(@RequestParam String macName) throws Exception {
        ro.setData(macService.getMax(macName));
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/save-machine")
    public ResponseEntity<MachineInfo> saveMachine(@RequestBody MachineInfo machine, HttpServletRequest request) throws Exception {
        MachineInfo mac = macService.save(machine);
        return ResponseEntity.ok(mac);
    }
}
