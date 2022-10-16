/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.MessageSender;
import cv.api.common.FilterObject;
import cv.api.common.ReportFilter;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.inv.entity.*;
import cv.api.inv.service.*;
import cv.api.inv.view.VOpening;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wai yan
 */
@CrossOrigin
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
    private PatternService patternService;
    @Autowired
    private ReorderService reorderService;
    @Autowired
    private PriceOptionService optionService;
    @Autowired
    private UnitRelationService unitRelationService;
    @Autowired
    private ReportService reportService;


    @Autowired
    private FontService fontService;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private TraderGroupService traderGroupService;
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
    public ResponseEntity<List<Category>> getCategory(@RequestParam String compCode, @RequestParam Integer deptId) {
        List<Category> listCat = categoryService.findAll(compCode, deptId);
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
    public ResponseEntity<?> getLocation(@RequestParam String compCode, @RequestParam Integer deptId) {
        return ResponseEntity.ok(locationService.findAll(compCode, deptId));
    }

    @DeleteMapping(path = "/delete-location")
    public ResponseEntity<ReturnObject> deleteLocation(@RequestParam String code) {
        locationService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/find-location")
    public ResponseEntity<Location> findLocation(@RequestBody LocationKey key) {
        return ResponseEntity.ok(locationService.findByCode(key));
    }

    @PostMapping(path = "/save-saleman")
    public ResponseEntity<SaleMan> saveSaleMan(@RequestBody SaleMan saleMan, HttpServletRequest request) throws Exception {
        SaleMan sm = saleManService.save(saleMan);
        return ResponseEntity.ok(sm);
    }

    @GetMapping(path = "/get-saleman")
    public ResponseEntity<List<SaleMan>> getSaleMan(@RequestParam String compCode, @RequestParam Integer deptId) {
        List<SaleMan> listSM = saleManService.findAll(compCode, deptId);
        return ResponseEntity.ok(listSM);
    }

    @DeleteMapping(path = "/delete-saleman")
    public ResponseEntity<ReturnObject> deleteSaleMan(@RequestParam String code) {
        saleManService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/find-saleman")
    public ResponseEntity<SaleMan> findSaleMan(@RequestBody SaleManKey key) {
        SaleMan sm = saleManService.findByCode(key);
        return ResponseEntity.ok(sm);
    }

    @PostMapping(path = "/save-brand")
    public ResponseEntity<StockBrand> saveBrand(@RequestBody StockBrand brand, HttpServletRequest request) throws Exception {
        StockBrand b = brandService.save(brand);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-brand")
    public ResponseEntity<List<StockBrand>> getBrand(@RequestParam String compCode, @RequestParam Integer deptId) {
        List<StockBrand> listB = brandService.findAll(compCode, deptId);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-brand")
    public ResponseEntity<ReturnObject> deleteBrand(@RequestParam String code) {
        brandService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/find-brand")
    public ResponseEntity<StockBrand> findBrand(@RequestBody StockBrandKey key) {
        StockBrand b = brandService.findByCode(key);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/find-unit-relation")
    public ResponseEntity<?> findUnitRelation(@RequestBody RelationKey key) {
        return ResponseEntity.ok(unitRelationService.findByKey(key));
    }

    @PostMapping(path = "/save-type")
    public ResponseEntity<StockType> saveType(@RequestBody StockType type, HttpServletRequest request) throws Exception {
        StockType b = typeService.save(type);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-type")
    public ResponseEntity<List<StockType>> getType(@RequestParam String compCode, @RequestParam Integer deptId) {
        List<StockType> listB = typeService.findAll(compCode, deptId);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-type")
    public ResponseEntity<ReturnObject> deleteType(@RequestParam String code) {
        typeService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/find-type")
    public ResponseEntity<StockType> findType(@RequestBody StockTypeKey key) {
        StockType b = typeService.findByCode(key);
        return ResponseEntity.ok(b);
    }

    @SneakyThrows
    @PostMapping(path = "/save-unit")
    public ResponseEntity<StockUnit> saveUnit(@RequestBody StockUnit unit, HttpServletRequest request) {
        StockUnit b = unitService.save(unit);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-unit")
    public ResponseEntity<List<StockUnit>> getUnit(@RequestParam String compCode, @RequestParam Integer deptId) {
        List<StockUnit> listB = unitService.findAll(compCode, deptId);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-unit")
    public ResponseEntity<ReturnObject> deleteUnit(@RequestParam String code) {
        unitService.delete(code);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/find-unit")
    public ResponseEntity<StockUnit> findUnit(@RequestBody StockUnitKey key) {
        return ResponseEntity.ok(unitService.findByCode(key));
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

    @PostMapping(path = "/find-region")
    public ResponseEntity<Region> findRegion(@RequestBody RegionKey key) {
        Region b = regionService.findByCode(key);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-customer")
    public ResponseEntity<Trader> saveCustomer(@RequestBody Trader trader) throws Exception {
        trader.setType("CUS");
        trader.setMacId(0);
        Trader b = traderService.saveTrader(trader);
        try {
            messageSender.sendMessage("TRADER", b.getKey().getCode());
        } catch (Exception e) {
            Trader t = traderService.findById(b.getKey());
            t.setIntgUpdStatus(null);
            traderService.saveTrader(t);
            log.error(String.format("sendMessage: SALE %s", e.getMessage()));
        }
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-trader")
    public ResponseEntity<Trader> saveTrader(@RequestBody Trader trader) throws Exception {
        Trader b = traderService.saveTrader(trader);
        try {
            messageSender.sendMessage("TRADER", b.getKey().getCode());
        } catch (Exception e) {
            Trader t = traderService.findById(b.getKey());
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
    public ResponseEntity<List<Trader>> getCustomer(@RequestParam String compCode, @RequestParam Integer deptId) {
        List<Trader> listB = traderService.findCustomer(compCode, deptId);
        return ResponseEntity.ok(listB);
    }

    @GetMapping(path = "/get-trader-list")
    public ResponseEntity<List<Trader>> getCustomerList(@RequestParam String text,
                                                        @RequestParam String type,
                                                        @RequestParam String compCode,
                                                        @RequestParam Integer deptId) {
        return ResponseEntity.ok(traderService.searchTrader(text, type, compCode, deptId));
    }

    @GetMapping(path = "/get-supplier")
    public ResponseEntity<List<Trader>> getSupplier(@RequestParam String compCode, @RequestParam Integer deptId) {
        List<Trader> listB = traderService.findSupplier(compCode, deptId);
        return ResponseEntity.ok(listB);
    }


    @PostMapping(path = "/delete-trader")
    public ResponseEntity<?> deleteTrader(@RequestBody TraderKey key) {
        List<String> str = traderService.delete(key);
        return ResponseEntity.ok(str.isEmpty() ? null : str);
    }

    @PostMapping(path = "/find-trader")
    public ResponseEntity<Trader> findTrader(@RequestBody TraderKey key) {
        Trader b = traderService.findById(key);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-stock")
    public ResponseEntity<Stock> saveStock(@RequestBody Stock stock) throws Exception {
        Stock b = stockService.save(stock);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-stock")
    public ResponseEntity<List<Stock>> getStock(@RequestParam String compCode, @RequestParam boolean active) {
        List<Stock> listB = active ? stockService.findActiveStock(compCode) : stockService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @PostMapping(path = "/search-stock")
    public ResponseEntity<?> searchStock(@RequestBody ReportFilter filter) {
        String stockCode = filter.getStockCode();
        String typCode = filter.getStockTypeCode();
        String catCode = filter.getCatCode();
        String brandCode = filter.getBrandCode();
        return ResponseEntity.ok(stockService.search(stockCode, typCode, catCode, brandCode));
    }

    @GetMapping(path = "/get-stock-list")
    public ResponseEntity<?> getStockList(@RequestParam String text, @RequestParam String compCode, @RequestParam Integer deptId) {
        return ResponseEntity.ok(stockService.getStock(text, compCode, deptId));
    }

    @PostMapping(path = "/delete-stock")
    public ResponseEntity<?> deleteStock(@RequestBody StockKey key) {
        List<String> str = stockService.delete(key);
        return ResponseEntity.ok(str.isEmpty() ? null : str);
    }

    @GetMapping(path = "/find-stock")
    public ResponseEntity<Stock> findStock(@RequestParam StockKey key) {
        Stock b = stockService.findById(key);
        return ResponseEntity.ok(b);
    }


    @PostMapping(path = "/save-voucher-status")
    public ResponseEntity<VouStatus> saveVoucherStatus(@RequestBody VouStatus vouStatus, HttpServletRequest request) {
        VouStatus b = vouStatusService.save(vouStatus);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-voucher-status")
    public ResponseEntity<List<VouStatus>> getVoucherStatus(@RequestParam String compCode, @RequestParam Integer deptId) {
        List<VouStatus> listB = vouStatusService.findAll(compCode, deptId);
        return ResponseEntity.ok(listB);
    }

    @PostMapping(path = "/find-voucher-status")
    public ResponseEntity<VouStatus> findVouStatus(@RequestBody VouStatusKey key) {
        VouStatus b = vouStatusService.findById(key);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-opening")
    public ResponseEntity<ReturnObject> saveOpening(@RequestBody OPHis opHis, HttpServletRequest request) {
        if (Util1.isNullOrEmpty(opHis.getVouDate())) {
            ro.setMessage("Invalid Opening Date.");
        } else if (Util1.isNullOrEmpty(opHis.getLocCode())) {
            ro.setMessage("Invalid Location.");
        } else if (opHis.getDetailList().size() <= 1) {
            ro.setMessage("Invalid Opening Record.");
        } else {
            List<OPHisDetail> detailList = opHis.getDetailList();
            detailList.forEach(op -> op.setLocCode(opHis.getLocCode()));
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
    public ResponseEntity<List<VOpening>> getOpening(@RequestBody FilterObject filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        List<VOpening> opHisList = reportService.getOpeningHistory(fromDate, toDate, vouNo, remark, userCode, locCode, stockCode, compCode);
        return ResponseEntity.ok(opHisList);
    }

    @PostMapping(path = "/find-opening")
    public ResponseEntity<OPHis> findOpening(@RequestBody OPHisKey key) {
        return ResponseEntity.ok(opHisService.findByCode(key));
    }

    @PostMapping(path = "/save-opening-detail")
    public ResponseEntity<OPHisDetail> saveOpeningDetail(@RequestBody OPHisDetail opHis, HttpServletRequest request) {
        OPHisDetail op = opHisDetailService.save(opHis);
        return ResponseEntity.ok(op);
    }

    @GetMapping(path = "/get-opening-detail")
    public ResponseEntity<List<OPHisDetail>> getOpeningDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<OPHisDetail> opHis = opHisDetailService.search(vouNo, compCode, deptId);
        return ResponseEntity.ok(opHis);
    }

    @PostMapping(path = "/save-pattern")
    public ResponseEntity<Pattern> savePattern(@RequestBody Pattern pattern) {
        patternService.save(pattern);
        return ResponseEntity.ok(pattern);
    }

    @GetMapping(path = "/delete-pattern")
    public ResponseEntity<?> deletePattern(@RequestParam String stockCode) {
        patternService.delete(stockCode);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/get-pattern")
    public ResponseEntity<?> getPattern(@RequestParam String stockCode, @RequestParam String compCode, @RequestParam Integer deptId) {
        return ResponseEntity.ok(patternService.search(stockCode, compCode, deptId));
    }

    @PostMapping(path = "/save-reorder")
    public ResponseEntity<ReturnObject> saveReorder(@RequestBody ReorderLevel rl, HttpServletRequest request) {
        try {
            if (Util1.isNullOrEmpty(rl.getStockCode())) {
                ro.setMessage("Invalid Stock.");
            } else if (Util1.isNullOrEmpty(rl.getMinUnitCode())) {
                ro.setMessage("Invalid Min Unit.");
            } else if (Util1.isNullOrEmpty(rl.getMaxUnitCode())) {
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
    public ResponseEntity<List<UnitRelationDetail>> getUnitRelation(@RequestParam String code,
                                                                    @RequestParam String compCode,
                                                                    @RequestParam Integer deptId) {
        List<UnitRelationDetail> listB = unitRelationService.getRelationDetail(code, compCode, deptId);
        return ResponseEntity.ok(listB);
    }

    @PostMapping(path = "/save-unit-relation")
    public ResponseEntity<UnitRelation> saveRelation(@RequestBody UnitRelation relation) {
        UnitRelation b = unitRelationService.save(relation);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-font")
    public ResponseEntity<List<CFont>> getFont() {
        List<CFont> type = fontService.getFont();
        return ResponseEntity.ok(type);
    }

    @PostMapping(path = "/save-trader-group")
    public ResponseEntity<?> saveTraderGroup(@RequestBody TraderGroup group) {
        TraderGroup g = traderGroupService.save(group);
        return ResponseEntity.ok(g);
    }

    @GetMapping(path = "/get-trader-group")
    public ResponseEntity<?> getTraderGroup(@RequestParam String compCode, @RequestParam Integer deptId) {
        List<TraderGroup> g = traderGroupService.getTraderGroup(compCode, deptId);
        return ResponseEntity.ok(g);
    }
}