package cv.api.controller;

import cv.api.inv.entity.GRN;
import cv.api.inv.service.GRNService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/process")
@Slf4j
public class GRNController {
    @Autowired
    private GRNService grnService;

    @PostMapping
    public ResponseEntity<?> saveProcess(@RequestBody GRN g) {
        return ResponseEntity.ok(grnService.save(g));
    }
}
