/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.common.FilterObject;
import com.cv.inv.api.common.ReturnObject;
import com.cv.inv.api.common.Util1;
import com.cv.inv.api.entity.*;
import com.cv.inv.api.service.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lenovo
 */
@RestController
@RequestMapping("/setup")
@Slf4j
public class SetupController {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private SaleManService saleManService;
    @Autowired
    private StockBrandService brandService;
    @Autowired
    private StockTypeService typeService;
    @Autowired
    private StockUnitService unitService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private StockService stockService;
    @Autowired
    private VouStatusService vouStatusService;
    @Autowired
    private OPHisService opHisService;
    @Autowired
    private OPHisDetailService opHisDetailService;
    @Autowired
    private SysPropertyService propertyService;
    @Autowired
    private PatternService patternService;
    @Autowired
    private ReorderService reorderService;
    private final ReturnObject ro = new ReturnObject();


    @PostMapping(path = "/save-currency")
    public ResponseEntity<Currency> saveCurrency(@RequestBody Currency machine, HttpServletRequest request) {
        Currency currency = currencyService.save(machine);
        return ResponseEntity.ok(currency);
    }

    @PostMapping(path = "/find-by-id")
    public ResponseEntity<Currency> findById(@RequestBody String curCode) {
        Currency cur = currencyService.findById(curCode);
        return ResponseEntity.ok(cur);
    }

    @GetMapping(path = "/get-currency")
    public ResponseEntity<List<Currency>> getCurrency() {
        List<Currency> currency = currencyService.search("-", "-");
        return ResponseEntity.ok(currency);
    }

    @PostMapping(path = "/save-category")
    public ResponseEntity<Category> saveCategory(@RequestBody Category cat, HttpServletRequest request) throws Exception {
        log.info("/save-category");
        Category category = categoryService.save(cat);
        return ResponseEntity.ok(category);
    }

    @GetMapping(path = "/get-category")
    public ResponseEntity<List<Category>> getCategory(@RequestParam String compCode) {
        log.info("/get-category");
        List<Category> listCat = categoryService.findAll(compCode);
        return ResponseEntity.ok(listCat);
    }

    @DeleteMapping(path = "/delete-category")
    public ResponseEntity<ReturnObject> deleteCategory(@RequestParam String code) {
        log.info("/delete-category");
        categoryService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-category")
    public ResponseEntity<Category> findCategory(@RequestParam String code) {
        log.info("/find-category");
        Category cat = categoryService.findByCode(code);
        return ResponseEntity.ok(cat);
    }

    @PostMapping(path = "/save-location")
    public ResponseEntity<Location> saveLocation(@RequestBody Location location, HttpServletRequest request) throws Exception {
        log.info("/save-location");
        Location loc = locationService.save(location);
        return ResponseEntity.ok(loc);
    }

    @GetMapping(path = "/get-location")
    public ResponseEntity<List<Location>> getLocation(@RequestParam String compCode) {
        log.info("/get-location");
        List<Location> listLoc = locationService.findAll(compCode);
        return ResponseEntity.ok(listLoc);
    }

    @DeleteMapping(path = "/delete-location")
    public ResponseEntity<ReturnObject> deleteLocation(@RequestParam String code) {
        log.info("/delete-location");
        locationService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-location")
    public ResponseEntity<Location> findLocation(@RequestParam String locCode) {
        log.info("/find-location");
        Location loc = locationService.findByCode(locCode);
        return ResponseEntity.ok(loc);
    }

    @PostMapping(path = "/save-saleman")
    public ResponseEntity<SaleMan> saveSaleMan(@RequestBody SaleMan saleMan, HttpServletRequest request) throws Exception {
        log.info("/save-saleman");
        SaleMan sm = saleManService.save(saleMan);
        return ResponseEntity.ok(sm);
    }

    @GetMapping(path = "/get-saleman")
    public ResponseEntity<List<SaleMan>> getSaleMan(@RequestParam String compCode) {
        log.info("/get-saleman");
        List<SaleMan> listSM = saleManService.findAll(compCode);
        return ResponseEntity.ok(listSM);
    }

    @DeleteMapping(path = "/delete-saleman")
    public ResponseEntity<ReturnObject> deleteSaleMan(@RequestParam String code) {
        log.info("/delete-saleman");
        saleManService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-saleman")
    public ResponseEntity<SaleMan> findSaleMan(@RequestParam String code) {
        log.info("/find-saleman");
        SaleMan sm = saleManService.findByCode(code);
        return ResponseEntity.ok(sm);
    }

    @PostMapping(path = "/save-brand")
    public ResponseEntity<StockBrand> saveBrand(@RequestBody StockBrand brand, HttpServletRequest request) throws Exception {
        log.info("/save-brand");
        StockBrand b = brandService.save(brand);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-brand")
    public ResponseEntity<List<StockBrand>> getBrand(@RequestParam String compCode) {
        log.info("/get-brand");
        List<StockBrand> listB = brandService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-brand")
    public ResponseEntity<ReturnObject> deleteBrand(@RequestParam String code) {
        log.info("delete-brand");
        brandService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-brand")
    public ResponseEntity<StockBrand> findBrand(@RequestParam String code) {
        log.info("/find-brand");
        StockBrand b = brandService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-type")
    public ResponseEntity<StockType> saveType(@RequestBody StockType type, HttpServletRequest request) throws Exception {
        log.info("/save-type");
        StockType b = typeService.save(type);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-type")
    public ResponseEntity<List<StockType>> getType(@RequestParam String compCode) {
        log.info("/get-type");
        List<StockType> listB = typeService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-type")
    public ResponseEntity<ReturnObject> deleteType(@RequestParam String code) {
        log.info("/delete-type");
        typeService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-type")
    public ResponseEntity<StockType> findType(@RequestParam String code) {
        log.info("/find-type");
        StockType b = typeService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @SneakyThrows
    @PostMapping(path = "/save-unit")
    public ResponseEntity<StockUnit> saveUnit(@RequestBody StockUnit unit, HttpServletRequest request) {
        log.info("/save-unit");
        StockUnit b = unitService.save(unit);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-unit")
    public ResponseEntity<List<StockUnit>> getUnit(@RequestParam String compCode) {
        log.info("/get-unit");
        List<StockUnit> listB = unitService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-unit")
    public ResponseEntity<ReturnObject> deleteUnit(@RequestParam String code) {
        log.info("/delete-unit");
        unitService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-unit")
    public ResponseEntity<StockUnit> findUnit(@RequestParam String code) {
        log.info("/find-type");
        StockUnit b = unitService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-region")
    public ResponseEntity<Region> saveRegion(@RequestBody Region region, HttpServletRequest request) throws Exception {
        log.info("/save-region");
        Region b = regionService.save(region);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-region")
    public ResponseEntity<List<Region>> getRegion(@RequestParam String compCode) {
        log.info("/get-region");
        List<Region> listB = regionService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-region")
    public ResponseEntity<ReturnObject> deleteRegion(@RequestParam String code) {
        log.info("/delete-unit");
        List<Trader> search = traderService.search(code, "-");
        if (search.isEmpty()) {
            regionService.delete(code);
            ro.setMessage("Deleted.");
        } else {
            ro.setMessage("Can't delete.");
        }
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-region")
    public ResponseEntity<Region> findRegion(@RequestParam String code) {
        log.info("/find-region");
        Region b = regionService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-trader")
    public ResponseEntity<Trader> saveTrader(@RequestBody Trader trader, HttpServletRequest request) throws Exception {
        log.info("/save-trader");
        Trader b = traderService.saveTrader(trader);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-trader")
    public ResponseEntity<List<Trader>> getTrader(@RequestParam String compCode) {
        log.info("/get-trader");
        List<Trader> listB = traderService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @GetMapping(path = "/get-customer")
    public ResponseEntity<List<Trader>> getCustomer(@RequestParam String compCode) {
        log.info("/get-customer");
        List<Trader> listB = traderService.findCustomer(compCode);
        return ResponseEntity.ok(listB);
    }

    @GetMapping(path = "/get-supplier")
    public ResponseEntity<List<Trader>> getSupplier(@RequestParam String compCode) {
        log.info("/get-supplier");
        List<Trader> listB = traderService.findSupplier(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-trader")
    public ResponseEntity<ReturnObject> deleteTrader(@RequestParam String code) {
        log.info("/delete-trader");
        traderService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-trader")
    public ResponseEntity<Trader> findTrader(@RequestParam String code) {
        log.info("/find-trader");
        Trader b = traderService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-stock")
    public ResponseEntity<Stock> saveStock(@RequestBody Stock region, HttpServletRequest request) throws Exception {
        log.info("/save-stock");
        Stock b = stockService.save(region);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-stock")
    public ResponseEntity<List<Stock>> getStock(@RequestParam String compCode) {
        log.info("/get-stock");
        List<Stock> listB = stockService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-stock")
    public ResponseEntity<ReturnObject> deleteStock(@RequestParam String code) {
        log.info("/delete-stock");
        stockService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-stock")
    public ResponseEntity<Stock> findStock(@RequestParam String code) {
        log.info("/find-region");
        Stock b = stockService.findById(code);
        return ResponseEntity.ok(b);
    }


    @PostMapping(path = "/save-voucher-status")
    public ResponseEntity<VouStatus> saveVoucherStatus(@RequestBody VouStatus vouStatus, HttpServletRequest request) {
        log.info("/save-voucher-status");
        VouStatus b = vouStatusService.save(vouStatus);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-voucher-status")
    public ResponseEntity<List<VouStatus>> getVoucherStatus(@RequestParam String compCode) {
        log.info("/get-voucher-status");
        List<VouStatus> listB = vouStatusService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @GetMapping(path = "/find-voucher-status")
    public ResponseEntity<VouStatus> findVouStatus(@RequestParam String code) {
        log.info("/find-voucher-status");
        VouStatus b = vouStatusService.findById(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-opening")
    public ResponseEntity<ReturnObject> saveOpening(@RequestBody OPHis opHis,
                                                    HttpServletRequest request) {
        log.info("/save-opening");
        if (Util1.isNullOrEmpty(opHis.getVouNo())) {
            ro.setMessage("Invalid Voucher No.");
        } else if (Util1.isNullOrEmpty(opHis.getVouDate())) {
            ro.setMessage("Invalid Opening Date.");
        } else if (Util1.isNullOrEmpty(opHis.getLocation())) {
            ro.setMessage("Invalid Location.");
        } else if (opHis.getDetailList().size() <= 1) {
            ro.setMessage("Invalid Opening Record.");
        } else {
            List<OPHisDetail> detailList = opHis.getDetailList();
            detailList.forEach(op -> op.setLocation(opHis.getLocation()));
            opHis.setDetailList(detailList);
            try {
                opHisService.save(opHis);
            } catch (Exception e) {
                ro.setMessage(e.getMessage());
            }
            ro.setMessage("Saved Opening.");
        }
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/get-opening")
    public ResponseEntity<ReturnObject> getOpening(@RequestBody FilterObject filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        List<OPHis> opHisList = opHisService.search(fromDate, toDate, vouNo, userCode, compCode);
        ro.setList(Arrays.asList(opHisList.toArray()));
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/save-opening-detail")
    public ResponseEntity<OPHisDetail> saveOpeningDetail(@RequestBody OPHisDetail opHis, HttpServletRequest request) {
        log.info("/save-opening-detail");
        OPHisDetail op = opHisDetailService.save(opHis);
        return ResponseEntity.ok(op);
    }

    @GetMapping(path = "/get-opening-detail")
    public ResponseEntity<List<OPHisDetail>> getOpeningDetail(@RequestParam String vouNo) {
        List<OPHisDetail> opHis = opHisDetailService.search(vouNo);
        return ResponseEntity.ok(opHis);
    }

    @PostMapping(path = "/save-system-property")
    public ResponseEntity<ReturnObject> saveSysProperty(@RequestBody SysProperty property,
                                                        HttpServletRequest request) {
        try {
            if (Util1.isNullOrEmpty(property.getPropKey())) {
                ro.setMessage("Invalid Property Key.");
            } else if (Util1.isNullOrEmpty(property.getPropValue())) {
                ro.setMessage("Invalid Property Value.");
            } else if (Util1.isNullOrEmpty(property.getCompCode())) {
                ro.setMessage("Invalid Company Id.");
            } else {
                property = propertyService.save(property);
                ro.setMessage("Save Property.");
                ro.setObj(property);
            }
        } catch (Exception e) {
            ro.setMessage(e.getMessage());
        }
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/get-system-property")
    public ResponseEntity<ReturnObject> getSystemProperty(@RequestParam String compCode) {
        List<SysProperty> propertyList = propertyService.search(compCode);
        ro.setList(Arrays.asList(propertyList.toArray()));
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/save-pattern")
    public ResponseEntity<ReturnObject> savePattern(@RequestBody Pattern pattern,
                                                    HttpServletRequest request) {
        try {
            if (Util1.isNullOrEmpty(pattern.getPatternName())) {
                ro.setMessage("Invalid Pattern Name.");
            } else if (Util1.isNullOrEmpty(pattern.getCompCode())) {
                ro.setMessage("Invalid Company Id.");
            } else if (Util1.isNullOrEmpty(pattern.getMacId())) {
                ro.setMessage("Invalid Mac Id.");
            } else {
                pattern = patternService.save(pattern);
                ro.setMessage("Save Pattern");
                ro.setObj(pattern);
            }
        } catch (Exception e) {
            ro.setMessage(e.getMessage());
            log.error(String.format("savePattern %s", e.getMessage()));
        }
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/get-pattern")
    public ResponseEntity<ReturnObject> getPattern(@RequestParam String compCode, Boolean active) {
        List<Pattern> patternList = patternService.search(compCode, active);
        ro.setList(Arrays.asList(patternList.toArray()));
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/get-pattern-detail")
    public ResponseEntity<ReturnObject> getPatternDetail(@RequestParam String patternCode) {
        List<PatternDetail> patternList = new ArrayList<>();
        try {
            patternList = patternService.searchDetail(patternCode);
        } catch (Exception e) {
            ro.setErrorMessage(e.getMessage());
        }
        ro.setList(Arrays.asList(patternList.toArray()));
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/save-pattern-detail")
    public ResponseEntity<ReturnObject> savePatternDetail(@RequestBody PatternDetail pattern,
                                                          HttpServletRequest request) {
        try {
            if (Util1.isNullOrEmpty(pattern.getStock())) {
                ro.setErrorMessage("Invalid Stock");
            } else if (Util1.isNullOrEmpty(pattern.getPatternCode())) {
                ro.setErrorMessage("Invalid Invalid Pattern.");
            } else if (Util1.getFloat(pattern.getInQty()) <= 0 && Util1.getFloat(pattern.getOutQty()) <= 0) {
                ro.setErrorMessage("Invalid Qty.");
            } else if (pattern.getOutUnit() == null && pattern.getInUnit() == null) {
                ro.setErrorMessage("Invalid Unit.");
            } else {
                pattern = patternService.save(pattern);
                ro.setMessage("Save Pattern");
                ro.setObj(pattern);
            }
        } catch (Exception e) {
            ro.setMessage(e.getMessage());
            log.error(String.format("savePattern %s", e.getMessage()));
        }
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/save-reorder")
    public ResponseEntity<ReturnObject> saveReorder(@RequestBody ReorderLevel rl,
                                                    HttpServletRequest request) {
        try {
            if (Util1.isNullOrEmpty(rl.getStock())) {
                ro.setMessage("Invalid Stock.");
            } else if (Util1.isNullOrEmpty(rl.getMinUnit())) {
                ro.setMessage("Invalid Min Unit.");
            } else if (Util1.isNullOrEmpty(rl.getMaxUnit())) {
                ro.setMessage("Invalid Max Unit.");
            } else if (Util1.isNullOrEmpty(rl.getBalUnit())) {
                ro.setMessage("Invalid Balance Unit.");
            } else {
                reorderService.save(rl);
                ro.setMessage("Save Reorder.");
                ro.setObj(rl);
            }
        } catch (Exception e) {
            ro.setMessage(e.getMessage());
            log.error(String.format("savePattern %s", e.getMessage()));
        }
        return ResponseEntity.ok(ro);
    }
}
