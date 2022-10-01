package cv.api.controller;

import cv.api.inv.entity.ProcessHis;
import cv.api.inv.entity.ProcessHisKey;
import cv.api.inv.service.ProcessHisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/process")
@Slf4j
public class ProcessController {
    @Autowired
    private ProcessHisService processHisService;

    @PostMapping(path = "/save-process")
    public ResponseEntity<?> saveProcess(@RequestBody ProcessHis ph) {
        ph = processHisService.save(ph);
        return ResponseEntity.ok(ph);
    }
    @PostMapping(path = "/get-process")
    public ResponseEntity<?> getProcess(@RequestBody ProcessHisKey key) {
        ProcessHis ph =processHisService.findById(key);
        return ResponseEntity.ok(ph);
    }
}
