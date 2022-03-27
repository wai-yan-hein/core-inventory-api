/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.MessageSender;
import cv.api.common.FilterObject;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.inv.entity.*;
import cv.api.inv.service.*;
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
 * @author wai yan
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
    @Autowired
    private PriceOptionService optionService;
    @Autowired
    private UnitRelationService unitRelationService;
    @Autowired
    private MessageSender messageSender;
    private final ReturnObject ro = new ReturnObject();


    @PostMapping(path = "/save-currency")
    public ResponseEntity<Currency> saveCurrency(@RequestBody Currency machine, HttpServletRequest request) {
        Currency currency = currencyService.save(machine);
        return ResponseEntity.ok(currency);
    }

    @GetMapping(path = "/find-currency")
    public ResponseEntity<Currency> findById(@RequestParam String curCode) {
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
        Category category = categoryService.save(cat);
        return ResponseEntity.ok(category);
    }

    @GetMapping(path = "/get-category")
    public ResponseEntity<List<Category>> getCategory(@RequestParam String compCode) {
        List<Category> listCat = categoryService.findAll(compCode);
        return ResponseEntity.ok(listCat);
    }

    @DeleteMapping(path = "/delete-category")
    public ResponseEntity<ReturnObject> deleteCategory(@RequestParam String code) {
        categoryService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-category")
    public ResponseEntity<Category> findCategory(@RequestParam String code) {
        Category cat = categoryService.findByCode(code);
        return ResponseEntity.ok(cat);
    }

    @PostMapping(path = "/save-location")
    public ResponseEntity<Location> saveLocation(@RequestBody Location location) throws Exception {
        Location loc = locationService.save(location);
        return ResponseEntity.ok(loc);
    }

    @GetMapping(path = "/get-location")
    public ResponseEntity<List<Location>> getLocation(@RequestParam String compCode) {
        List<Location> listLoc = locationService.findAll(compCode);
        return ResponseEntity.ok(listLoc);
    }

    @DeleteMapping(path = "/delete-location")
    public ResponseEntity<ReturnObject> deleteLocation(@RequestParam String code) {
        locationService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-location")
    public ResponseEntity<Location> findLocation(@RequestParam String locCode) {
        Location loc = locationService.findByCode(locCode);
        return ResponseEntity.ok(loc);
    }

    @PostMapping(path = "/save-saleman")
    public ResponseEntity<SaleMan> saveSaleMan(@RequestBody SaleMan saleMan, HttpServletRequest request) throws Exception {
        SaleMan sm = saleManService.save(saleMan);
        return ResponseEntity.ok(sm);
    }

    @GetMapping(path = "/get-saleman")
    public ResponseEntity<List<SaleMan>> getSaleMan(@RequestParam String compCode) {
        List<SaleMan> listSM = saleManService.findAll(compCode);
        return ResponseEntity.ok(listSM);
    }

    @DeleteMapping(path = "/delete-saleman")
    public ResponseEntity<ReturnObject> deleteSaleMan(@RequestParam String code) {
        saleManService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-saleman")
    public ResponseEntity<SaleMan> findSaleMan(@RequestParam String smCode) {
        SaleMan sm = saleManService.findByCode(smCode);
        return ResponseEntity.ok(sm);
    }

    @PostMapping(path = "/save-brand")
    public ResponseEntity<StockBrand> saveBrand(@RequestBody StockBrand brand, HttpServletRequest request) throws Exception {
        StockBrand b = brandService.save(brand);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-brand")
    public ResponseEntity<List<StockBrand>> getBrand(@RequestParam String compCode) {
        List<StockBrand> listB = brandService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-brand")
    public ResponseEntity<ReturnObject> deleteBrand(@RequestParam String code) {
        brandService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-brand")
    public ResponseEntity<StockBrand> findBrand(@RequestParam String code) {
        StockBrand b = brandService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-type")
    public ResponseEntity<StockType> saveType(@RequestBody StockType type, HttpServletRequest request) throws Exception {
        StockType b = typeService.save(type);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-type")
    public ResponseEntity<List<StockType>> getType(@RequestParam String compCode) {
        List<StockType> listB = typeService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-type")
    public ResponseEntity<ReturnObject> deleteType(@RequestParam String code) {
        typeService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-type")
    public ResponseEntity<StockType> findType(@RequestParam String code) {
        StockType b = typeService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @SneakyThrows
    @PostMapping(path = "/save-unit")
    public ResponseEntity<StockUnit> saveUnit(@RequestBody StockUnit unit, HttpServletRequest request) {
        StockUnit b = unitService.save(unit);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-unit")
    public ResponseEntity<List<StockUnit>> getUnit(@RequestParam String compCode) {
        List<StockUnit> listB = unitService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-unit")
    public ResponseEntity<ReturnObject> deleteUnit(@RequestParam String code) {
        unitService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-unit")
    public ResponseEntity<StockUnit> findUnit(@RequestParam String code) {
        StockUnit b = unitService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-region")
    public ResponseEntity<Region> saveRegion(@RequestBody Region region, HttpServletRequest request) throws Exception {
        Region b = regionService.save(region);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-region")
    public ResponseEntity<List<Region>> getRegion(@RequestParam String compCode) {
        List<Region> listB = regionService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-region")
    public ResponseEntity<ReturnObject> deleteRegion(@RequestParam String code) {
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
        Region b = regionService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-trader")
    public ResponseEntity<Trader> saveTrader(@RequestBody Trader trader, HttpServletRequest request) throws Exception {
        Trader b = traderService.saveTrader(trader);
        try {
            messageSender.sendMessage("TRADER", b.getCode());
        } catch (Exception e) {
            Trader t = traderService.findByCode(b.getCode());
            t.setIntgUpdStatus(null);
            traderService.saveTrader(t);
            log.error(String.format("sendMessage: SALE %s", e.getMessage()));
        }
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-trader")
    public ResponseEntity<List<Trader>> getTrader(@RequestParam String compCode) {
        List<Trader> listB = traderService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @GetMapping(path = "/get-customer")
    public ResponseEntity<List<Trader>> getCustomer(@RequestParam String compCode) {
        List<Trader> listB = traderService.findCustomer(compCode);
        return ResponseEntity.ok(listB);
    }

    @GetMapping(path = "/get-supplier")
    public ResponseEntity<List<Trader>> getSupplier(@RequestParam String compCode) {
        List<Trader> listB = traderService.findSupplier(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-trader")
    public ResponseEntity<ReturnObject> deleteTrader(@RequestParam String code) {
        traderService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-trader")
    public ResponseEntity<Trader> findTrader(@RequestParam String traderCode) {
        Trader b = traderService.findByCode(traderCode);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-stock")
    public ResponseEntity<Stock> saveStock(@RequestBody Stock region, HttpServletRequest request) throws Exception {
        Stock b = stockService.save(region);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-stock")
    public ResponseEntity<List<Stock>> getStock(@RequestParam String compCode, @RequestParam boolean active) {
        List<Stock> listB = active ? stockService.findActiveStock(compCode) : stockService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-stock")
    public ResponseEntity<ReturnObject> deleteStock(@RequestParam String code) {
        stockService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-stock")
    public ResponseEntity<Stock> findStock(@RequestParam String code) {
        Stock b = stockService.findById(code);
        return ResponseEntity.ok(b);
    }


    @PostMapping(path = "/save-voucher-status")
    public ResponseEntity<VouStatus> saveVoucherStatus(@RequestBody VouStatus vouStatus, HttpServletRequest request) {
        VouStatus b = vouStatusService.save(vouStatus);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-voucher-status")
    public ResponseEntity<List<VouStatus>> getVoucherStatus(@RequestParam String compCode) {
        List<VouStatus> listB = vouStatusService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @GetMapping(path = "/find-voucher-status")
    public ResponseEntity<VouStatus> findVouStatus(@RequestParam String code) {
        VouStatus b = vouStatusService.findById(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-opening")
    public ResponseEntity<ReturnObject> saveOpening(@RequestBody OPHis opHis,
                                                    HttpServletRequest request) {
        if (Util1.isNullOrEmpty(opHis.getVouDate())) {
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
                ro.setMessage("Saved Opening.");
            } catch (Exception e) {
                log.error(String.format("saveOpening : %s", e.getMessage()));
            }
        }
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/get-opening")
    public ResponseEntity<List<OPHis>> getOpening(@RequestBody FilterObject filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        List<OPHis> opHisList = opHisService.search(fromDate, toDate, vouNo, userCode, compCode);
        return ResponseEntity.ok(opHisList);
    }

    @PostMapping(path = "/save-opening-detail")
    public ResponseEntity<OPHisDetail> saveOpeningDetail(@RequestBody OPHisDetail opHis, HttpServletRequest request) {
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
                ro.setData(property);
            }
        } catch (Exception e) {
            ro.setMessage(e.getMessage());
        }
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/get-system-property")
    public ResponseEntity<ReturnObject> getSystemProperty(@RequestParam String compCode) {
        List<SysProperty> propertyList = propertyService.search(compCode);
        ro.setData(propertyList);
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/save-pattern")
    public ResponseEntity<Pattern> savePattern(@RequestBody Pattern pattern,
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
                ro.setData(pattern);
            }
        } catch (Exception e) {
            ro.setMessage(e.getMessage());
            log.error(String.format("savePattern %s", e.getMessage()));
        }
        return ResponseEntity.ok(pattern);
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
                ro.setData(pattern);
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
                ro.setData(rl);
            }
        } catch (Exception e) {
            ro.setMessage(e.getMessage());
            log.error(String.format("savePattern %s", e.getMessage()));
        }
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/save-price-option")
    public ResponseEntity<ReturnObject> savePriceOption(@RequestBody PriceOption po) {
        try {
            if (Util1.isNullOrEmpty(po.getPriceType())) {
                ro.setMessage("Invalid type.");
            } else if (Util1.isNullOrEmpty(po.getCompCode())) {
                ro.setMessage("Invalid Company Id.");
            } else {
                optionService.save(po);
                ro.setMessage("Save Price Option.");
                ro.setData(po);
            }
        } catch (Exception e) {
            ro.setMessage(e.getMessage());
            log.error(String.format("savePriceOption %s", e.getMessage()));
        }
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/get-price-option")
    public ResponseEntity<List<PriceOption>> getPriceOption(@RequestParam String compCode) {
        return ResponseEntity.ok(optionService.getPriceOption(compCode));
    }

    @GetMapping(path = "/get-unit-relation")
    public ResponseEntity<List<UnitRelation>> getUnitRelation() {
        List<UnitRelation> listB = unitRelationService.findRelation();
        return ResponseEntity.ok(listB);
    }

    @GetMapping(path = "/get-unit-relation-detail")
    public ResponseEntity<List<UnitRelationDetail>> getUnitRelation(@RequestParam String code) {
        List<UnitRelationDetail> listB = unitRelationService.getRelationDetail(code);
        return ResponseEntity.ok(listB);
    }

    @PostMapping(path = "/save-unit-relation")
    public ResponseEntity<UnitRelation> saveRelation(@RequestBody UnitRelation relation) {
        UnitRelation b = unitRelationService.save(relation);
        return ResponseEntity.ok(b);
    }
}
