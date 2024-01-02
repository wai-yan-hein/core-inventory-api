package cv.api.controller;

import cv.api.common.*;
import cv.api.entity.*;
import cv.api.model.AccTraderKey;
import cv.api.r2dbc.StockColor;
import cv.api.repo.AccountRepo;
import cv.api.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@CrossOrigin
@RestController
@RequestMapping("/setup")
@Slf4j
@RequiredArgsConstructor
public class SetupController {

    private final ReturnObject ro = ReturnObject.builder().build();
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final SaleManService saleManService;
    private final StockBrandService brandService;
    private final StockTypeService typeService;
    private final StockUnitService unitService;
    private final TraderService traderService;
    private final RegionService regionService;
    private final StockService stockService;
    private final StockCriteriaService stockCriteriaService;
    private final VouStatusService vouStatusService;
    private final OPHisService opHisService;
    private final OPHisDetailService opHisDetailService;
    private final PatternService patternService;
    private final ReorderService reorderService;
    private final PriceOptionService optionService;
    private final UnitRelationService unitRelationService;
    private final ReportService reportService;
    private final TraderGroupService traderGroupService;
    private final GRNService batchService;
    private final ConverterService converterService;
    private final AccountRepo accountRepo;
    private final AccSettingService accSettingService;
    private final OrderStatusService orderStatusService;
    private final LabourGroupService labourGroupService;
    private final JobService jobService;
    private final StockFormulaService stockFormulaService;
    private final OutputCostService outputCostService;
    private final StockColorService stockColorService;

    @GetMapping(path = "/hello")
    public Mono<?> hello() {
        return Mono.just("Hello");
    }

    @PostMapping(path = "/saveCategory")
    public Mono<Category> saveCategory(@RequestBody Category cat) {
        cat.setUpdatedDate(Util1.getTodayLocalDate());
        Category category = categoryService.save(cat);
        return Mono.justOrEmpty(category);
    }

    @GetMapping(path = "/getCategory")
    public Flux<?> getCategory(@RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(categoryService.findAll(compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }


    @GetMapping(path = "/getUpdateCategory")
    public Flux<?> getUpdateCategory(@RequestParam String updatedDate) {
        return Flux.fromIterable(categoryService.getCategory(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }


    @DeleteMapping(path = "/deleteCategory")
    public Mono<?> deleteCategory(@RequestParam String code) {
        categoryService.delete(code);
        ro.setMessage("Deleted.");
        return Mono.justOrEmpty(ro);
    }

    @PostMapping(path = "/findCategory")
    public Mono<Category> findCategory(@RequestBody CategoryKey key) {
        Category cat = categoryService.findByCode(key);
        return Mono.justOrEmpty(cat);
    }

    @PostMapping(path = "/saveStockCriteria")
    public Mono<StockCriteria> saveStockCriteria(@RequestBody StockCriteria cat) {
        cat.setUpdatedDate(Util1.getTodayLocalDate());
        StockCriteria category = stockCriteriaService.save(cat);
        return Mono.justOrEmpty(category);
    }

    @GetMapping(path = "/getStockCriteria")
    public Flux<?> getStockCriteria(@RequestParam String compCode, @RequestParam boolean active) {
        return Flux.fromIterable(stockCriteriaService.findAll(compCode, active)).onErrorResume(throwable -> Flux.empty());
    }


    @GetMapping(path = "/getUpdateStockCriteria")
    public Flux<?> getUpdateStockCriteria(@RequestParam String updatedDate) {
        return Flux.fromIterable(stockCriteriaService.getCriteria(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }


    @DeleteMapping(path = "/deleteStockCriteria")
    public Mono<?> deleteStockCriteria(@RequestParam String code) {
        stockCriteriaService.delete(code);
        ro.setMessage("Deleted.");
        return Mono.justOrEmpty(ro);
    }

    @PostMapping(path = "/findStockCriteria")
    public Mono<StockCriteria> findStockCriteria(@RequestBody StockCriteriaKey key) {
        StockCriteria cat = stockCriteriaService.findByCode(key);
        return Mono.justOrEmpty(cat);
    }

    @GetMapping(path = "/searchStockCriteria")
    public Mono<?> searchStockCriteria(@RequestParam String text, @RequestParam String compCode) {
        return Mono.just(stockCriteriaService.search(compCode, text));
    }


    @PostMapping(path = "/saveLocation")
    public Mono<Location> saveLocation(@RequestBody Location location) {
        location.setUpdatedDate(Util1.getTodayLocalDate());
        Location loc = locationService.save(location);
        return Mono.justOrEmpty(loc);
    }

    @GetMapping(path = "/getLocation")
    public Flux<?> getLocation(@RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(locationService.findAll(compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateLocation")
    public Flux<?> getUpdateLocation(@RequestParam String updatedDate) {
        return Flux.fromIterable(locationService.getLocation(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateLabourGroup")
    public Flux<?> getUpdateLabourGroup(@RequestParam String updatedDate) {
        return Flux.fromIterable(labourGroupService.getLabourGroup(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @DeleteMapping(path = "/deleteLocation")
    public Mono<ReturnObject> deleteLocation(@RequestParam String code) {
        locationService.delete(code);
        ro.setMessage("Deleted.");
        return Mono.justOrEmpty(ro);
    }

    @PostMapping(path = "/findLocation")
    public Mono<Location> findLocation(@RequestBody LocationKey key) {
        return Mono.justOrEmpty(locationService.findByCode(key));
    }

    @PostMapping(path = "/saveSaleMan")
    public Mono<SaleMan> saveSaleMan(@RequestBody SaleMan saleMan) {
        saleMan.setUpdatedDate(Util1.getTodayLocalDate());
        SaleMan sm = saleManService.save(saleMan);
        return Mono.justOrEmpty(sm);
    }

    @GetMapping(path = "/getSaleMan")
    public Flux<?> getSaleMan(@RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(saleManService.findAll(compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateSaleMan")
    public Flux<?> getSaleMan(@RequestParam String updatedDate) {
        return Flux.fromIterable(saleManService.getSaleMan(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @DeleteMapping(path = "/deleteSaleMan")
    public Mono<ReturnObject> deleteSaleMan(@RequestParam String code) {
        saleManService.delete(code);
        ro.setMessage("Deleted.");
        return Mono.justOrEmpty(ro);
    }

    @PostMapping(path = "/findSaleMan")
    public Mono<SaleMan> findSaleMan(@RequestBody SaleManKey key) {
        SaleMan sm = saleManService.findByCode(key);
        return Mono.justOrEmpty(sm);
    }

    @PostMapping(path = "/saveBrand")
    public Mono<StockBrand> saveBrand(@RequestBody StockBrand brand) {
        brand.setUpdatedDate(Util1.getTodayLocalDate());
        StockBrand b = brandService.save(brand);
        return Mono.justOrEmpty(b);
    }

    @GetMapping(path = "/getBrand")
    public Flux<?> getBrand(@RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(brandService.findAll(compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateBrand")
    public Flux<?> getUpdateBrand(@RequestParam String updatedDate) {
        return Flux.fromIterable(brandService.getBrand(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @DeleteMapping(path = "/deleteBrand")
    public Mono<?> deleteBrand(@RequestParam String code) {
        brandService.delete(code);
        ro.setMessage("Deleted.");
        return Mono.justOrEmpty(ro);
    }

    @PostMapping(path = "/findBrand")
    public Mono<StockBrand> findBrand(@RequestBody StockBrandKey key) {
        StockBrand b = brandService.findByCode(key);
        return Mono.justOrEmpty(b);
    }

    @PostMapping(path = "/findUnitRelation")
    public Mono<?> findUnitRelation(@RequestBody RelationKey key) {
        return Mono.justOrEmpty(unitRelationService.findByKey(key));
    }

    @PostMapping(path = "/saveType")
    public Mono<StockType> saveType(@RequestBody StockType type) {
        type.setUpdatedDate(Util1.getTodayLocalDate());
        StockType b = typeService.save(type);
        return Mono.justOrEmpty(b);
    }

    @GetMapping(path = "/getType")
    public Flux<?> getType(@RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(typeService.findAll(compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateStockType")
    public Flux<?> getUpdateStockType(@RequestParam String updatedDate) {
        return Flux.fromIterable(typeService.getStockType(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }


    @DeleteMapping(path = "/deleteType")
    public Mono<ReturnObject> deleteType(@RequestParam String code) {
        typeService.delete(code);
        ro.setMessage("Deleted.");
        return Mono.justOrEmpty(ro);
    }

    @PostMapping(path = "/findType")
    public Mono<StockType> findType(@RequestBody StockTypeKey key) {
        StockType b = typeService.findByCode(key);
        return Mono.justOrEmpty(b);
    }

    @PostMapping(path = "/saveUnit")
    public Mono<StockUnit> saveUnit(@RequestBody StockUnit unit) {
        unit.setUpdatedDate(Util1.getTodayLocalDate());
        StockUnit b = unitService.save(unit);
        return Mono.justOrEmpty(b);
    }

    @GetMapping(path = "/getUnit")
    public Flux<?> getUnit(@RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(unitService.findAll(compCode, deptId));
    }

    @GetMapping(path = "/getUpdateUnit")
    public Flux<?> getUpdateUnit(@RequestParam String updatedDate) {
        return Flux.fromIterable(unitService.getUnit(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @DeleteMapping(path = "/deleteUnit")
    public Mono<?> deleteUnit(@RequestParam String code) {
        unitService.delete(code);
        ro.setMessage("Deleted.");
        return Mono.justOrEmpty(ro);
    }

    @PostMapping(path = "/findUnit")
    public Mono<StockUnit> findUnit(@RequestBody StockUnitKey key) {
        return Mono.justOrEmpty(unitService.findByCode(key));
    }

    @PostMapping(path = "/saveRegion")
    public Mono<Region> saveRegion(@RequestBody Region region) throws Exception {
        region.setUpdatedDate(Util1.getTodayLocalDate());
        Region b = regionService.save(region);
        return Mono.justOrEmpty(b);
    }

    @GetMapping(path = "/getRegion")
    public Flux<?> getRegion(@RequestParam String compCode) {
        return Flux.fromIterable(regionService.findAll(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/deleteRegion")
    public Mono<?> deleteRegion(@RequestBody RegionKey key) {
        return Mono.just(regionService.delete(key));
    }

    @PostMapping(path = "/findRegion")
    public Mono<Region> findRegion(@RequestBody RegionKey key) {
        Region b = regionService.findByCode(key);
        return Mono.justOrEmpty(b);
    }

    @GetMapping(path = "/getUpdateRegion")
    public Flux<?> getUpdateRegion(@RequestParam String updatedDate) {
        return Flux.fromIterable(regionService.getRegion(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/saveCustomer")
    public Mono<Trader> saveCustomer(@RequestBody Trader trader) {
        trader.setUpdatedDate(Util1.getTodayLocalDate());
        trader.setType("CUS");
        trader.setMacId(0);
        Trader b = traderService.saveTrader(trader);
        accountRepo.sendTrader(b);
        return Mono.justOrEmpty(b);
    }

    @PostMapping(path = "/saveTrader")
    public Mono<Trader> saveTrader(@RequestBody Trader trader) {
        trader.setUpdatedDate(Util1.getTodayLocalDate());
        trader = traderService.saveTrader(trader);
        accountRepo.sendTrader(trader);
        return Mono.justOrEmpty(trader);
    }

    @GetMapping(path = "/getTrader")
    public Flux<?> getTrader(@RequestParam String compCode) {
        return Flux.fromIterable(traderService.findAll(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateTrader")
    public Flux<?> getUpdateTrader(@RequestParam String updatedDate) {
        return Flux.fromIterable(traderService.getTrader(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }


    @GetMapping(path = "/getCustomer")
    public Flux<Trader> getCustomer(@RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(traderService.findCustomer(compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getEmployee")
    public Flux<Trader> getEmployee(@RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(traderService.findEmployee(compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getTraderList")
    public Flux<?> getTraderList(@RequestParam String text, @RequestParam String type, @RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(traderService.searchTrader(Util1.cleanStr(text), type, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getSupplier")
    public Flux<?> getSupplier(@RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(traderService.findSupplier(compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }


    @PostMapping(path = "/deleteTrader")
    public Flux<?> deleteTrader(@RequestBody TraderKey key) {
        List<General> list = traderService.delete(key);
        if (list.isEmpty()) {
            AccTraderKey k = new AccTraderKey();
            k.setCompCode(key.getCompCode());
            k.setCode(key.getCode());
            accountRepo.deleteTrader(k);
        }
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/findTrader")
    public Mono<Trader> findTrader(@RequestBody TraderKey key) {
        Trader b = traderService.findById(key);
        return Mono.justOrEmpty(b);
    }

    @GetMapping(path = "/findTraderRFID")
    public Mono<?> findTraderRfId(@RequestParam String rfId, @RequestParam String compCode, @RequestParam Integer deptId) {
        return Mono.justOrEmpty(traderService.findByRFID(rfId, compCode, deptId));
    }

    @PostMapping(path = "/saveStock")
    public Mono<Stock> saveStock(@RequestBody Stock stock) {
        stock.setUpdatedDate(LocalDateTime.now());
        Stock b = stockService.save(stock);
        return Mono.justOrEmpty(b);
    }

    @PostMapping(path = "/updateStock")
    public Mono<?> updateSaleClosed(@RequestBody Stock stock) {
        return Mono.justOrEmpty(stockService.updateStock(stock));
    }

    @GetMapping(path = "/getStock")
    public Flux<Stock> getStock(@RequestParam String compCode, @RequestParam Integer deptId, @RequestParam boolean active) {
        List<Stock> listB = active ? stockService.findActiveStock(compCode, deptId) : stockService.findAll(compCode, deptId);
        return Flux.fromIterable(listB).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateStock")
    public Flux<Stock> getUpdateStock(@RequestParam String updatedDate) {
        return Flux.fromIterable(stockService.getStock(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateStockFormula")
    public Flux<StockFormula> getUpdateStockFormula(@RequestParam String updatedDate) {
        List<StockFormula> list = stockFormulaService.getStockFormula(Util1.toLocalDateTime(updatedDate));
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateStockFormulaPrice")
    public Flux<StockFormulaPrice> getUpdateStockFormulaPrice(@RequestParam String updatedDate) {
        List<StockFormulaPrice> list = stockFormulaService.getStockFormulaPrice(Util1.toLocalDateTime(updatedDate));
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateStockFormulaQty")
    public Flux<StockFormulaQty> getUpdateStockFormulaQty(@RequestParam String updatedDate) {
        List<StockFormulaQty> list = stockFormulaService.getStockFormulaQty(Util1.toLocalDateTime(updatedDate));
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateGradeDetail")
    public Flux<GradeDetail> getUpdateGradeDetail(@RequestParam String updatedDate) {
        List<GradeDetail> list = stockFormulaService.getGradeDetail(Util1.toLocalDateTime(updatedDate));
        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getService")
    public Flux<?> getService(@RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(stockService.getService(compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/searchStock")
    public Flux<?> searchStock(@RequestBody ReportFilter filter) {
        String stockCode = Util1.isAll(filter.getStockCode());
        String typCode = Util1.isAll(filter.getStockTypeCode());
        String catCode = Util1.isAll(filter.getCatCode());
        String brandCode = Util1.isAll(filter.getBrandCode());
        Integer deptId = filter.getDeptId();
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        boolean active = filter.isActive();
        return Flux.fromIterable(stockService.search(stockCode, typCode, catCode, brandCode, compCode, deptId, active, deleted)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getStockList")
    public Flux<?> getStockList(@RequestParam String text, @RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(stockService.getStock(Util1.cleanStr(text), compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/deleteStock")
    public Flux<?> deleteStock(@RequestBody StockKey key) {
        List<General> str = stockService.delete(key);
        return Flux.fromIterable(str);
    }

    @PostMapping(path = "/restoreStock")
    public Mono<?> restoreStock(@RequestBody StockKey key) {
        return Mono.just(stockService.restore(key));
    }

    @PostMapping(path = "/findStock")
    public Mono<Stock> findStock(@RequestBody StockKey key) {
        Stock b = stockService.findById(key);
        return Mono.justOrEmpty(b);
    }


    @PostMapping(path = "/saveVoucherStatus")
    public Mono<VouStatus> saveVoucherStatus(@RequestBody VouStatus vouStatus) {
        vouStatus.setUpdatedDate(Util1.getTodayLocalDate());
        VouStatus b = vouStatusService.save(vouStatus);
        return Mono.justOrEmpty(b);
    }

    @GetMapping(path = "/getVouStatus")
    public Flux<?> getVoucherStatus(@RequestParam String compCode) {
        return Flux.fromIterable(vouStatusService.findAll(compCode));
    }

    @GetMapping(path = "/getUpdateVouStatus")
    public Flux<?> getUpdateVouStatus(@RequestParam String updatedDate) {
        return Flux.fromIterable(vouStatusService.getVouStatus(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/findVouStatus")
    public Mono<VouStatus> findVouStatus(@RequestBody VouStatusKey key) {
        VouStatus b = vouStatusService.findById(key);
        return Mono.justOrEmpty(b);
    }

    @PostMapping(path = "/saveOrderStatus")
    public Mono<OrderStatus> saveOrderStatus(@RequestBody OrderStatus orderStatus) {
        orderStatus.setUpdatedDate(Util1.getTodayLocalDate());
        OrderStatus b = orderStatusService.save(orderStatus);
        return Mono.justOrEmpty(b);
    }

    @PostMapping(path = "/saveLabourGroup")
    public Mono<LabourGroup> saveLabourGroup(@RequestBody LabourGroup labourGroup) {
        labourGroup.setUpdatedDate(Util1.getTodayLocalDate());
        LabourGroup b = labourGroupService.save(labourGroup);
        return Mono.justOrEmpty(b);
    }

    @PostMapping(path = "/saveJob")
    public Mono<Job> saveJob(@RequestBody Job job) {
        job.setUpdatedDate(Util1.getTodayLocalDate());
        Job b = jobService.save(job);
        return Mono.justOrEmpty(b);
    }

    @GetMapping(path = "/getOrderStatus")
    public Flux<?> getOrderStatus(@RequestParam String compCode) {
        return Flux.fromIterable(orderStatusService.findAll(compCode));
    }

    @GetMapping(path = "/getLabourGroup")
    public Flux<?> getLabourGroup(@RequestParam String compCode) {
        return Flux.fromIterable(labourGroupService.findAll(compCode));
    }

    @PostMapping(path = "/getJob")
    public Flux<?> getJob(@RequestBody FilterObject filterObject) {
        return Flux.fromIterable(jobService.findAll(filterObject));
    }

    @GetMapping(path = "/getUpdateJob")
    public Flux<?> getUpdateJob(@RequestParam String updatedDate) {
        return Flux.fromIterable(jobService.getJob(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateOrderStatus")
    public Flux<?> getUpdateOrderStatus(@RequestParam String updatedDate) {
        return Flux.fromIterable(orderStatusService.getOrderStatus(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/findOrderStatus")
    public Mono<OrderStatus> findOrderStatus(@RequestBody OrderStatusKey key) {
        OrderStatus b = orderStatusService.findById(key);
        return Mono.justOrEmpty(b);
    }

    @PostMapping(path = "/findLabourGroup")
    public Mono<LabourGroup> findLabourGroup(@RequestBody LabourGroupKey key) {
        LabourGroup b = labourGroupService.findById(key);
        return Mono.justOrEmpty(b);
    }

    @PostMapping(path = "/findJob")
    public Mono<Job> findOrderStatus(@RequestBody JobKey key) {
        Job b = jobService.findById(key);
        return Mono.justOrEmpty(b);
    }

    @PostMapping(path = "/saveOpening")
    public Mono<?> saveOpening(@RequestBody OPHis opHis) {
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
                opHis.setUpdatedDate(Util1.getTodayLocalDate());
                return Mono.just(opHisService.save(opHis));
            } catch (Exception e) {
                log.error(String.format("saveOpening : %s", e.getMessage()));
            }
        }
        return Mono.just(ro);
    }

    @PostMapping(path = "/getOpening")
    public Flux<?> getOpening(@RequestBody FilterObject filter) throws Exception {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String compCode = Util1.isNull(filter.getCompCode(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        Integer deptId = filter.getDeptId();
        String curCode = Util1.isAll(filter.getCurCode());
        String deleted = String.valueOf(filter.isDeleted());
        int type = Integer.parseInt(filter.getTranSource());
        String traderCode = String.valueOf(filter.getTraderCode());
        List<OPHis> opHisList = reportService.getOpeningHistory(fromDate, toDate, vouNo, remark, userCode,
                stockCode, locCode, compCode, deptId, curCode, deleted, type, traderCode);
        return Flux.fromIterable(opHisList).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/findOpening")
    public Mono<OPHis> findOpening(@RequestBody OPHisKey key) {
        return Mono.justOrEmpty(opHisService.findByCode(key));
    }

    @PostMapping(path = "/deleteOpening")
    public Mono<?> deleteOpening(@RequestBody OPHisKey key) {
        return Mono.just(opHisService.delete(key));
    }

    @PostMapping(path = "/restoreOpening")
    public Mono<?> restoreOpening(@RequestBody OPHisKey key) {
        return Mono.just(opHisService.delete(key));
    }


    @GetMapping(path = "/getOpeningDetail")
    public Flux<OPHisDetail> getOpeningDetail(@RequestParam String vouNo, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<OPHisDetail> opHis = opHisDetailService.search(vouNo, compCode, deptId);
        return Flux.fromIterable(opHis).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/savePattern")
    public Mono<?> savePattern(@RequestBody Pattern pattern) {
        return Mono.justOrEmpty(patternService.save(pattern));
    }

    @PostMapping(path = "/deletePattern")
    public Mono<?> deletePattern(@RequestBody Pattern p) {
        patternService.delete(p);
        return Mono.just(true);
    }

    @PostMapping(path = "/findPattern")
    public Mono<?> findPattern(@RequestBody PatternKey p) {
        return Mono.justOrEmpty(patternService.findByCode(p));
    }

    @GetMapping(path = "/getPattern")
    public Flux<?> getPattern(@RequestParam String stockCode, @RequestParam String compCode, @RequestParam String vouDate) {
        List<Pattern> list = patternService.search(stockCode, compCode);
        list.forEach(p -> {
            if (!Util1.isNullOrEmpty(vouDate)) {
                String code = p.getKey().getStockCode();
                String type = p.getPriceTypeCode();
                if (type != null) {
                    General g = getPrice(code, vouDate, p.getUnitCode(), p.getPriceTypeCode(), compCode);
                    p.setPrice(g == null ? 0.0 : Util1.getDouble(g.getAmount()));
                }
            }
            p.setAmount(Util1.getDouble(p.getQty()) * Util1.getDouble(p.getPrice()));
        });

        return Flux.fromIterable(list).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdatePattern")
    public Flux<?> getUpdatePattern(@RequestParam String updatedDate) {
        return Flux.fromIterable(patternService.getPattern(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    public General getPrice(String stockCode, String vouDate, String unit, String type, String compCode) {
        return switch (type) {
            case "PUR-R" -> reportService.getPurchaseRecentPrice(stockCode, vouDate, unit, compCode);
            case "PUR-A" -> reportService.getPurchaseAvgPrice(stockCode, vouDate, unit, compCode);
            case "PRO-R" -> reportService.getProductionRecentPrice(stockCode, vouDate, unit, compCode);
            case "WL-R" -> reportService.getWeightLossRecentPrice(stockCode, vouDate, unit, compCode);
            default -> null;
        };
    }

    @PostMapping(path = "/saveReorder")
    public Mono<?> saveReorder(@RequestBody ReorderLevel rl) {
        return Mono.justOrEmpty(reorderService.save(rl));
    }

    @PostMapping(path = "/savePriceOption")
    public Mono<?> savePriceOption(@RequestBody PriceOption po) {
        return Mono.justOrEmpty(optionService.save(po));
    }

    @GetMapping(path = "/getPriceOption")
    public Flux<?> getPriceOption(@RequestParam String option, @RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(optionService.getPriceOption(Util1.isNull(option, "-"), compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdatePriceOption")
    public Flux<?> getUpdatePriceOption(@RequestParam String updatedDate) {
        return Flux.fromIterable(optionService.getPriceOption(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUnitRelation")
    public Flux<?> getUnitRelation(@RequestParam String compCode, @RequestParam Integer deptId) {
        List<UnitRelation> listB = unitRelationService.findRelation(compCode, deptId);
        return Flux.fromIterable(listB).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getRelation")
    public Flux<?> getRelation(@RequestParam String relCode, @RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(unitRelationService.getRelation(relCode, compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdateRelation")
    public Flux<?> getUpdateRelation(@RequestParam String updatedDate) {
        return Flux.fromIterable(unitRelationService.getRelation(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUnitRelationDetail")
    public Flux<?> getUnitRelationDetail(@RequestParam String code, @RequestParam String compCode, @RequestParam Integer deptId) {
        List<UnitRelationDetail> listB = unitRelationService.getRelationDetail(code, compCode);
        return Flux.fromIterable(listB).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/saveUnitRelation")
    public Mono<UnitRelation> saveUnitRelation(@RequestBody UnitRelation relation) {
        UnitRelation b = unitRelationService.save(relation);
        return Mono.justOrEmpty(b);
    }


    @PostMapping(path = "/saveTraderGroup")
    public Mono<?> saveTraderGroup(@RequestBody TraderGroup group) {
        TraderGroup g = traderGroupService.save(group);
        return Mono.justOrEmpty(g);
    }

    @PostMapping(path = "/findTraderGroup")
    public Mono<?> findTraderGroup(@RequestBody TraderGroupKey key) {
        return Mono.justOrEmpty(traderGroupService.findById(key));
    }

    @GetMapping(path = "/getTraderGroup")
    public Flux<?> getTraderGroup(@RequestParam String compCode, @RequestParam Integer deptId) {
        List<TraderGroup> g = traderGroupService.getTraderGroup(compCode, deptId);
        return Flux.fromIterable(g).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/convert-to-unicode")
    public Mono<?> convertToUniCode() {
        converterService.convertToUnicode();
        return Mono.justOrEmpty("converted.");
    }

    @GetMapping(path = "/convert-trader")
    public Mono<?> convertTrader() {
        converterService.trader();
        return Mono.justOrEmpty("converted.");
    }

    @GetMapping(path = "/getBatch")
    public Flux<?> getBatch(@RequestParam String compCode, @RequestParam Integer deptId) {
        return Flux.fromIterable(batchService.findAll(compCode, deptId)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getAccSetting")
    public Flux<?> getAccSetting(@RequestParam String compCode) {
        return Flux.fromIterable(accSettingService.findAll(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/saveAccSetting")
    public Mono<?> saveAccSetting(@RequestBody AccSetting setting) {
        return Mono.just(accSettingService.save(setting));
    }

    @GetMapping(path = "/getUpdatedAccSetting")
    public Flux<?> getUpdatedAccSetting(@RequestParam String updatedDate) {
        return Flux.fromIterable(accSettingService.getAccSetting(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/saveStockFormula")
    public Mono<?> saveStockFormula(@RequestBody StockFormula f) {
        return Mono.just(stockFormulaService.save(f));
    }

    @PostMapping(path = "/deleteStockFormula")
    public Mono<?> deleteStockFormula(@RequestBody StockFormulaKey key) {
        return Mono.just(stockFormulaService.delete(key));
    }

    @PostMapping(path = "/deleteGradeDetail")
    public Mono<?> deleteGradeDetail(@RequestBody GradeDetailKey key) {
        return Mono.just(stockFormulaService.delete(key));
    }

    @PostMapping(path = "/findStockFormula")
    public Mono<?> findStockFormula(@RequestBody StockFormulaKey key) {
        return Mono.justOrEmpty(stockFormulaService.find(key));
    }

    @GetMapping(path = "/getStockFormula")
    public Mono<?> getStockFormula(@RequestParam String compCode) {
        return Mono.just(stockFormulaService.getFormula(compCode));
    }

    @GetMapping(path = "/getStockFormulaPrice")
    public Flux<?> getStockFormulaPrice(@RequestParam String formulaCode, @RequestParam String compCode) {
        return Flux.fromIterable(stockFormulaService.getStockFormulaPrice(formulaCode, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getStockFormulaQty")
    public Flux<?> getStockFormulaQty(@RequestParam String formulaCode, @RequestParam String compCode) {
        return Flux.fromIterable(stockFormulaService.getStockFormulaQty(formulaCode, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getGradeDetail")
    public Flux<?> getGradeDetail(@RequestParam String formulaCode, @RequestParam String criteriaCode, @RequestParam String compCode) {
        return Flux.fromIterable(stockFormulaService.getGradeDetail(formulaCode, criteriaCode, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getStockFormulaGrade")
    public Flux<?> getStockFormulaGrade(@RequestParam String formulaCode, @RequestParam String compCode) {
        return Flux.fromIterable(stockFormulaService.getStockFormulaGrade(formulaCode, compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/saveStockFormulaPrice")
    public Mono<?> saveStockFormulaPrice(@RequestBody StockFormulaPrice f) {
        return Mono.just(stockFormulaService.save(f));
    }

    @PostMapping(path = "/saveStockFormulaQty")
    public Mono<?> saveStockFormulaQty(@RequestBody StockFormulaQty f) {
        return Mono.just(stockFormulaService.save(f));
    }

    @PostMapping(path = "/saveGradeDetail")
    public Mono<?> saveGradeDetail(@RequestBody GradeDetail f) {
        return Mono.just(stockFormulaService.save(f));
    }

    @PostMapping(path = "/deleteStockFormulaDetail")
    public Mono<?> deleteStockFormulaDetail(@RequestBody StockFormulaPriceKey key) {
        return Mono.justOrEmpty(stockFormulaService.delete(key));
    }

    @PostMapping(path = "/deleteFormula")
    public Mono<?> deleteFormula(@RequestBody StockFormulaKey key) {
        return Mono.just(stockFormulaService.delete(key));
    }

    @PostMapping(path = "/saveOutputCost")
    public Mono<OutputCost> saveOutputCost(@RequestBody OutputCost outputCost) {
        outputCost.setUpdatedDate(Util1.getTodayLocalDate());
        OutputCost b = outputCostService.save(outputCost);
        return Mono.justOrEmpty(b);
    }

    @GetMapping(path = "/getOutputCost")
    public Flux<?> getOutputCost(@RequestParam String compCode) {
        return Flux.fromIterable(outputCostService.findAll(compCode)).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/deleteOutputCost")
    public Mono<?> deleteOutputCost(@RequestBody OutputCostKey key) {
        return Mono.just(outputCostService.delete(key));
    }

    @PostMapping(path = "/findOutputCost")
    public Mono<OutputCost> findOutputCost(@RequestBody OutputCostKey key) {
        OutputCost b = outputCostService.findByCode(key);
        return Mono.justOrEmpty(b);
    }

    @GetMapping(path = "/getUpdateOutputCost")
    public Flux<?> getUpdateOutputCost(@RequestParam String updatedDate) {
        return Flux.fromIterable(outputCostService.getOutputCost(Util1.toLocalDateTime(updatedDate))).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/saveStockColor")
    public Mono<StockColor> saveStockColor(@RequestBody StockColor color) {
        return stockColorService.saveOrUpdate(color);
    }

    @GetMapping(path = "/getStockColor")
    public Flux<StockColor> getStockColor(@RequestParam String compCode) {
        return stockColorService.getStockColor(compCode);
    }

}
