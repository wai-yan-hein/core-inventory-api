package cv.api.controller;

import cv.api.common.General;
import cv.api.common.ReportFilter;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
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
    private final PatternService patternService;
    private final ReorderLevelService reorderService;
    private final PriceOptionService optionService;
    private final UnitRelationService unitRelationService;
    private final ReportService reportService;
    private final TraderGroupService traderGroupService;
    private final ConverterService converterService;
    private final AccountRepo accountRepo;
    private final AccSettingService accSettingService;
    private final OrderStatusService orderStatusService;
    private final LabourGroupService labourGroupService;
    private final JobService jobService;
    private final StockFormulaService stockFormulaService;
    private final StockFormulaPriceService stockFormulaPriceService;
    private final StockFormulaQtyService stockFormulaQtyService;
    private final GradeDetailService gradeDetailService;
    private final StockColorService stockColorService;
    private final CleanDataService cleanDataService;

    @GetMapping(path = "/hello")
    public Mono<?> hello() {
        return Mono.just("Hello");
    }

    @DeleteMapping(path = "/cleanData")
    public Mono<Boolean> cleanData() {
        return cleanDataService.cleanData();
    }

    @PostMapping(path = "/saveCategory")
    public Mono<Category> saveCategory(@RequestBody Category cat) {
        return categoryService.saveOrUpdate(cat);
    }

    @GetMapping(path = "/getCategory")
    public Flux<?> getCategory(@RequestParam String compCode) {
        return categoryService.findAll(compCode);
    }


    @GetMapping(path = "/getUpdateCategory")
    public Flux<Category> getUpdateCategory(@RequestParam String updatedDate) {
        return categoryService.getCategory(Util1.toLocalDateTime(updatedDate));
    }


    @DeleteMapping(path = "/deleteCategory")
    public Mono<Boolean> deleteCategory(@RequestBody CategoryKey key) {
        return categoryService.delete(key);
    }

    @PostMapping(path = "/findCategory")
    public Mono<Category> findCategory(@RequestBody CategoryKey key) {
        return categoryService.findByCode(key);
    }

    @PostMapping(path = "/saveStockCriteria")
    public Mono<StockCriteria> saveStockCriteria(@RequestBody StockCriteria cat) {
        cat.setUpdatedDate(Util1.getTodayLocalDate());
        return stockCriteriaService.save(cat);
    }

    @GetMapping(path = "/getStockCriteria")
    public Flux<?> getStockCriteria(@RequestParam String compCode, @RequestParam boolean active) {
        return stockCriteriaService.findAll(compCode, active).onErrorResume(throwable -> Flux.empty());
    }


    @GetMapping(path = "/getUpdateStockCriteria")
    public Flux<?> getUpdateStockCriteria(@RequestParam String updatedDate) {
        return stockCriteriaService.getStockCriteria(Util1.toLocalDateTime(updatedDate)).onErrorResume(throwable -> Flux.empty());
    }


    @DeleteMapping(path = "/deleteStockCriteria")
    public Mono<?> deleteStockCriteria(@RequestParam String code) {
        stockCriteriaService.delete(code);
        ro.setMessage("Deleted.");
        return Mono.justOrEmpty(ro);
    }

    @PostMapping(path = "/findStockCriteria")
    public Mono<StockCriteria> findStockCriteria(@RequestBody StockCriteriaKey key) {
        return stockCriteriaService.findById(key);
    }

    @GetMapping(path = "/searchStockCriteria")
    public Mono<?> searchStockCriteria(@RequestParam String text, @RequestParam String compCode) {
        return stockCriteriaService.search(compCode, text);
    }


    @PostMapping(path = "/saveLocation")
    public Mono<Location> saveLocation(@RequestBody Location dto) {
        return locationService.save(dto);
    }

    @GetMapping(path = "/getLocation")
    public Flux<Location> getLocation(@RequestParam String compCode, @RequestParam String whCode) {
        return locationService.findAll(compCode, whCode);
    }

    @GetMapping(path = "/getUpdateLocation")
    public Flux<Location> getUpdateLocation(@RequestParam String updatedDate) {
        return locationService.getLocation(Util1.toLocalDateTime(updatedDate));
    }

    @GetMapping(path = "/getUpdateLabourGroup")
    public Flux<LabourGroup> getUpdateLabourGroup(@RequestParam String updatedDate) {
        return labourGroupService.getLabourGroup(Util1.toLocalDateTime(updatedDate));
    }

    @DeleteMapping(path = "/deleteLocation")
    public Mono<Boolean> deleteLocation(@RequestBody LocationKey key) {
        return locationService.delete(key);
    }

    @PostMapping(path = "/findLocation")
    public Mono<Location> findLocation(@RequestBody LocationKey key) {
        return locationService.findByCode(key);
    }

    @PostMapping(path = "/saveSaleMan")
    public Mono<SaleMan> saveSaleMan(@RequestBody SaleMan dto) {
        return saleManService.save(dto);
    }

    @GetMapping(path = "/getSaleMan")
    public Flux<SaleMan> getSaleMan(@RequestParam String compCode) {
        return saleManService.findAll(compCode);
    }

    @GetMapping(path = "/getUpdateSaleMan")
    public Flux<SaleMan> getUpdateSaleMan(@RequestParam String updatedDate) {
        return saleManService.getSaleMan(Util1.toLocalDateTime(updatedDate));
    }

    @DeleteMapping(path = "/deleteSaleMan")
    public Mono<Boolean> deleteSaleMan(@RequestParam SaleManKey key) {
        return saleManService.delete(key);
    }

    @PostMapping(path = "/findSaleMan")
    public Mono<SaleMan> findSaleMan(@RequestBody SaleManKey key) {
        return saleManService.findById(key);
    }

    @PostMapping(path = "/saveBrand")
    public Mono<StockBrand> saveBrand(@RequestBody StockBrand dto) {
        return brandService.saveOrUpdate(dto);
    }

    @GetMapping(path = "/getBrand")
    public Flux<StockBrand> getBrand(@RequestParam String compCode) {
        return brandService.findAll(compCode);
    }

    @GetMapping(path = "/getUpdateBrand")
    public Flux<StockBrand> getUpdateBrand(@RequestParam String updatedDate) {
        return brandService.getStockBrand(Util1.toLocalDateTime(updatedDate));
    }

    @DeleteMapping(path = "/deleteBrand")
    public Mono<Boolean> deleteBrand(@RequestBody StockBrandKey key) {
        return brandService.delete(key);
    }

    @PostMapping(path = "/findBrand")
    public Mono<StockBrand> findBrand(@RequestBody StockBrandKey key) {
        return brandService.findByCode(key);
    }

    @PostMapping(path = "/findUnitRelation")
    public Mono<UnitRelation> findUnitRelation(@RequestBody RelationKey key) {
        return unitRelationService.findByCode(key);
    }

    @PostMapping(path = "/saveType")
    public Mono<StockType> saveType(@RequestBody StockType type) {
        return typeService.saveOrUpdate(type);
    }

    @GetMapping(path = "/getType")
    public Flux<StockType> getType(@RequestParam String compCode) {
        return typeService.findAll(compCode);
    }

    @GetMapping(path = "/getUpdateStockType")
    public Flux<StockType> getUpdateStockType(@RequestParam String updatedDate) {
        return typeService.getStockType(Util1.toLocalDateTime(updatedDate));
    }


    @DeleteMapping(path = "/deleteType")
    public Mono<Boolean> deleteType(@RequestParam StockTypeKey key) {
        return typeService.delete(key);
    }

    @PostMapping(path = "/findType")
    public Mono<StockType> findType(@RequestBody StockTypeKey key) {
        return typeService.findByCode(key);
    }

    @PostMapping(path = "/saveUnit")
    public Mono<StockUnit> saveUnit(@RequestBody StockUnit unit) {
        return unitService.insert(unit);
    }

    @GetMapping(path = "/getUnit")
    public Flux<StockUnit> getUnit(@RequestParam String compCode) {
        return unitService.findAll(compCode);
    }

    @GetMapping(path = "/getUpdateUnit")
    public Flux<StockUnit> getUpdateUnit(@RequestParam String updatedDate) {
        return unitService.getUnit(Util1.toLocalDateTime(updatedDate));
    }

    @PostMapping(path = "/findUnit")
    public Mono<StockUnit> findUnit(@RequestBody StockUnitKey key) {
        return unitService.findByCode(key);
    }

    @PostMapping(path = "/saveRegion")
    public Mono<Region> saveRegion(@RequestBody Region dto) {
        return regionService.save(dto);
    }

    @GetMapping(path = "/getRegion")
    public Flux<Region> getRegion(@RequestParam String compCode) {
        return regionService.findAll(compCode);
    }

    @PostMapping(path = "/deleteRegion")
    public Mono<Boolean> deleteRegion(@RequestBody RegionKey key) {
        return regionService.delete(key);
    }

    @PostMapping(path = "/findRegion")
    public Mono<Region> findRegion(@RequestBody RegionKey key) {
        return regionService.findById(key);

    }

    @GetMapping(path = "/getUpdateRegion")
    public Flux<Region> getUpdateRegion(@RequestParam String updatedDate) {
        return regionService.getRegion(Util1.toLocalDateTime(updatedDate));
    }

    @PostMapping(path = "/saveCustomer")
    public Mono<Trader> saveCustomer(@RequestBody Trader trader) {
        trader.setUpdatedDate(Util1.getTodayLocalDate());
        trader.setType("CUS");
        trader.setMacId(0);
        return traderService.saveTrader(trader).map(t -> {
            accountRepo.sendTrader(t);
            return t;
        });
    }

    @PostMapping(path = "/saveTrader")
    public Mono<Trader> saveTrader(@RequestBody Trader trader) {
        return traderService.saveTrader(trader).map(t -> {
            accountRepo.sendTrader(t);
            return t;
        });
    }

    @GetMapping(path = "/getTrader")
    public Flux<Trader> getTrader(@RequestParam String compCode) {
        return traderService.findAll(compCode);
    }

    @GetMapping(path = "/getUpdateTrader")
    public Flux<Trader> getUpdateTrader(@RequestParam String updatedDate) {
        return traderService.getUpdateTrader(Util1.toLocalDateTime(updatedDate));
    }

    @GetMapping(path = "/getUpdateCustomer")
    public Flux<Trader> getUpdateCustomer(@RequestParam String updatedDate) {
        return traderService.getUpdateCustomer(Util1.toLocalDateTime(updatedDate));
    }

    @GetMapping(path = "/getCustomer")
    public Flux<Trader> getCustomer(@RequestParam String compCode) {
        return traderService.getCustomer(compCode);
    }

    @GetMapping(path = "/getEmployee")
    public Flux<Trader> getEmployee(@RequestParam String compCode) {
        return traderService.getEmployee(compCode);
    }

    @GetMapping(path = "/getTraderList")
    public Flux<Trader> getTraderList(@RequestParam String text, @RequestParam String type, @RequestParam String compCode) {
        return traderService.searchTrader(Util1.cleanStr(text), type, compCode);
    }

    @GetMapping(path = "/getSupplier")
    public Flux<Trader> getSupplier(@RequestParam String compCode) {
        return traderService.getSupplier(compCode);
    }


    @PostMapping(path = "/deleteTrader")
    public Flux<General> deleteTrader(@RequestBody TraderKey key) {
        return traderService.delete(key)
                .collectList()
                .doOnNext(generals -> {
                    if (generals.isEmpty()) {
                        AccTraderKey accTraderKey = new AccTraderKey();
                        accTraderKey.setCompCode(key.getCompCode());
                        accTraderKey.setCode(key.getCode());
                        accountRepo.deleteTrader(accTraderKey);
                    }
                })
                .flatMapMany(Flux::fromIterable);
    }


    @PostMapping(path = "/findTrader")
    public Mono<Trader> findTrader(@RequestBody TraderKey key) {
        return traderService.findById(key);
    }

    @GetMapping(path = "/findTraderRFID")
    public Mono<?> findTraderRfId(@RequestParam String rfId, @RequestParam String compCode, @RequestParam Integer deptId) {
        return Mono.justOrEmpty(traderService.findByRFID(rfId, compCode, deptId));
    }

    @PostMapping(path = "/saveStock")
    public Mono<Stock> saveStock(@RequestBody Stock stock) {
        return stockService.save(stock);
    }

    @PostMapping(path = "/updateStock")
    public Mono<Boolean> updateSaleClosed(@RequestBody Stock stock) {
        return stockService.updateStock(stock);
    }

    @GetMapping(path = "/getStock")
    public Flux<Stock> getStock(@RequestParam String compCode, @RequestParam Integer deptId, @RequestParam boolean active) {
        return active ? stockService.findActiveStock(compCode) : stockService.findAll(compCode, deptId);
    }

    @GetMapping(path = "/getUpdateStock")
    public Flux<Stock> getUpdateStock(@RequestParam String updatedDate) {
        return stockService.getStock(Util1.toLocalDateTime(updatedDate));
    }

    @GetMapping(path = "/getUpdateStockFormula")
    public Flux<StockFormula> getUpdateStockFormula(@RequestParam String updatedDate) {
        return stockFormulaService.getStockFormula(Util1.toLocalDateTime(updatedDate));
    }

    @GetMapping(path = "/getUpdateStockFormulaPrice")
    public Flux<StockFormulaPrice> getUpdateStockFormulaPrice(@RequestParam String updatedDate) {
        return stockFormulaPriceService.getStockFormulaPrice(Util1.toLocalDateTime(updatedDate));
    }

    @GetMapping(path = "/getUpdateStockFormulaQty")
    public Flux<StockFormulaQty> getUpdateStockFormulaQty(@RequestParam String updatedDate) {
        return stockFormulaQtyService.getStockFormulaQty(Util1.toLocalDateTime(updatedDate));
    }

    @GetMapping(path = "/getUpdateGradeDetail")
    public Flux<GradeDetail> getUpdateGradeDetail(@RequestParam String updatedDate) {
        return gradeDetailService.getGradeDetail(Util1.toLocalDateTime(updatedDate));
    }

    @GetMapping(path = "/getService")
    public Flux<Stock> getService(@RequestParam String compCode, @RequestParam Integer deptId) {
        return stockService.getService(compCode, deptId);
    }

    @PostMapping(path = "/searchStock")
    public Flux<Stock> searchStock(@RequestBody ReportFilter filter) {
        return stockService.search(filter);
    }

    @GetMapping(path = "/getStockList")
    public Flux<Stock> getStockList(@RequestParam String text, @RequestParam String compCode, @RequestParam Integer deptId) {
        return stockService.getStock(text, compCode, deptId);
    }

    @PostMapping(path = "/deleteStock")
    public Flux<General> deleteStock(@RequestBody StockKey key) {
        return stockService.delete(key);
    }

    @PostMapping(path = "/restoreStock")
    public Mono<?> restoreStock(@RequestBody StockKey key) {
        return Mono.just(stockService.restore(key));
    }

    @PostMapping(path = "/findStock")
    public Mono<Stock> findStock(@RequestBody StockKey key) {
        return stockService.findById(key);
    }

    @PostMapping(path = "/findStockByBarcode")
    public Mono<Stock> findStockByBarcode(@RequestBody StockKey key) {
        return stockService.findByBarcode(key);
    }


    @PostMapping(path = "/saveVoucherStatus")
    public Mono<VouStatus> saveVoucherStatus(@RequestBody VouStatus dto) {
        return vouStatusService.save(dto);
    }

    @GetMapping(path = "/getVouStatus")
    public Flux<VouStatus> getVoucherStatus(@RequestParam String compCode) {
        return vouStatusService.findAll(compCode);
    }

    @GetMapping(path = "/getUpdateVouStatus")
    public Flux<VouStatus> getUpdateVouStatus(@RequestParam String updatedDate) {
        return vouStatusService.getVouStatus(Util1.toLocalDateTime(updatedDate));
    }

    @PostMapping(path = "/findVouStatus")
    public Mono<VouStatus> findVouStatus(@RequestBody VouStatusKey key) {
        return vouStatusService.findById(key);
    }

    @PostMapping(path = "/saveOrderStatus")
    public Mono<OrderStatus> saveOrderStatus(@RequestBody OrderStatus orderStatus) {
        return orderStatusService.save(orderStatus);
    }

    @PostMapping(path = "/saveLabourGroup")
    public Mono<LabourGroup> saveLabourGroup(@RequestBody LabourGroup dto) {
        return labourGroupService.save(dto);
    }

    @PostMapping(path = "/saveJob")
    public Mono<Job> saveJob(@RequestBody Job job) {
        return jobService.save(job);
    }

    @GetMapping(path = "/getOrderStatus")
    public Flux<?> getOrderStatus(@RequestParam String compCode) {
        return orderStatusService.findAll(compCode);
    }

    @GetMapping(path = "/getLabourGroup")
    public Flux<LabourGroup> getLabourGroup(@RequestParam String compCode) {
        return labourGroupService.findAll(compCode);
    }

    @PostMapping(path = "/getJob")
    public Flux<Job> getJob(@RequestBody ReportFilter filter) {
        return jobService.findAll(filter);
    }

    @GetMapping(path = "/getActiveJob")
    public Flux<Job> getJob(@RequestParam String compCode) {
        return jobService.getActiveJob(compCode);
    }

    @GetMapping(path = "/getUpdateJob")
    public Flux<Job> getUpdateJob(@RequestParam String updatedDate) {
        return jobService.getJob(Util1.toLocalDateTime(updatedDate));
    }

    @GetMapping(path = "/getUpdateOrderStatus")
    public Flux<OrderStatus> getUpdateOrderStatus(@RequestParam String updatedDate) {
        return orderStatusService.getOrderStatus(Util1.toLocalDateTime(updatedDate));
    }

    @PostMapping(path = "/findOrderStatus")
    public Mono<OrderStatus> findOrderStatus(@RequestBody OrderStatusKey key) {
        return orderStatusService.findById(key);
    }

    @PostMapping(path = "/findLabourGroup")
    public Mono<LabourGroup> findLabourGroup(@RequestBody LabourGroupKey key) {
        return labourGroupService.findById(key);
    }

    @PostMapping(path = "/findJob")
    public Mono<Job> findJob(@RequestBody JobKey key) {
        return jobService.findById(key);
    }

    @PostMapping(path = "/saveOpening")
    public Mono<OPHis> saveOpening(@RequestBody OPHis dto) {
        return opHisService.save(dto);
    }

    @PostMapping(path = "/getOpening")
    public Flux<OPHis> getOpening(@RequestBody ReportFilter filter) {
        return opHisService.getOpeningHistory(filter);
    }

    @PostMapping(path = "/findOpening")
    public Mono<OPHis> findOpening(@RequestBody OPHisKey key) {
        return opHisService.findByCode(key);
    }

    @PostMapping(path = "/deleteOpening")
    public Mono<Boolean> deleteOpening(@RequestBody OPHisKey key) {
        return opHisService.delete(key);
    }

    @PostMapping(path = "/restoreOpening")
    public Mono<Boolean> restoreOpening(@RequestBody OPHisKey key) {
        return opHisService.restore(key);
    }


    @GetMapping(path = "/getOpeningDetail")
    public Flux<OPHisDetail> getOpeningDetail(@RequestParam String vouNo, @RequestParam String compCode) {
        return opHisService.getOpeningDetail(vouNo, compCode);
    }

    @PostMapping(path = "/savePattern")
    public Mono<Pattern> savePattern(@RequestBody Pattern dto) {
        return patternService.save(dto);
    }

    @PostMapping(path = "/deletePattern")
    public Mono<Boolean> deletePattern(@RequestBody Pattern p) {
        return patternService.delete(p.getKey());
    }

    @PostMapping(path = "/findPattern")
    public Mono<Pattern> findPattern(@RequestBody PatternKey p) {
        return patternService.findByCode(p);
    }

    @GetMapping(path = "/getPattern")
    public Flux<Pattern> getPattern(@RequestParam String stockCode, @RequestParam String compCode, @RequestParam String vouDate) {
        return patternService.search(stockCode, compCode).flatMap(pattern -> {
            if (!Util1.isNullOrEmpty(vouDate)) {
                String code = pattern.getKey().getStockCode();
                String type = pattern.getPriceTypeCode();
                if (type != null) {
                    return getPrice(code, vouDate, pattern.getUnitCode(), pattern.getPriceTypeCode(), compCode)
                            .map(g -> {
                                pattern.setPrice(g == null ? 0.0 : Util1.getDouble(g.getAmount()));
                                return pattern;
                            });
                }
            }
            return Mono.just(pattern);
        });
    }

    @GetMapping(path = "/getUpdatePattern")
    public Flux<Pattern> getUpdatePattern(@RequestParam String updatedDate) {
        return patternService.getPattern(Util1.toLocalDateTime(updatedDate));
    }

    public Mono<General> getPrice(String stockCode, String vouDate, String unit, String type, String compCode) {
        return switch (type) {
            case "PUR-R" -> reportService.getPurchaseRecentPrice(stockCode, vouDate, unit, compCode);
            case "PUR-A" -> reportService.getPurchaseAvgPrice(stockCode, vouDate, unit, compCode);
            case "PRO-R" -> reportService.getProductionRecentPrice(stockCode, vouDate, unit, compCode);
            case "WL-R" -> reportService.getWeightLossRecentPrice(stockCode, vouDate, unit, compCode);
            default -> Mono.empty();
        };
    }

    @PostMapping(path = "/saveReorder")
    public Mono<ReorderLevel> saveReorder(@RequestBody ReorderLevel dto) {
        return reorderService.saveOrUpdate(dto);
    }

    @PostMapping(path = "/savePriceOption")
    public Mono<?> savePriceOption(@RequestBody PriceOption po) {
        return Mono.justOrEmpty(optionService.save(po));
    }

    @GetMapping(path = "/getPriceOption")
    public Flux<?> getPriceOption(@RequestParam String option, @RequestParam String compCode, @RequestParam Integer deptId) {
        return optionService.getPriceOptions(Util1.isNull(option, "-"), compCode, deptId).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUpdatePriceOption")
    public Flux<?> getUpdatePriceOption(@RequestParam String updatedDate) {
        return optionService.getPriceOption(Util1.toLocalDateTime(updatedDate)).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getUnitRelation")
    public Flux<UnitRelation> getUnitRelation(@RequestParam String compCode) {
        return unitRelationService.getRelation(compCode);
    }

    @GetMapping(path = "/getUpdateRelation")
    public Flux<UnitRelation> getUpdateRelation(@RequestParam String updatedDate) {
        return unitRelationService.getUnitRelation(Util1.toLocalDateTime(updatedDate));
    }

    @GetMapping(path = "/getUnitRelationDetail")
    public Flux<?> getUnitRelationDetail(@RequestParam String code, @RequestParam String compCode) {
        return unitRelationService.getRelationDetail(code, compCode);
    }

    @PostMapping(path = "/saveUnitRelation")
    public Mono<UnitRelation> saveUnitRelation(@RequestBody UnitRelation dto) {
        return unitRelationService.save(dto);
    }


    @PostMapping(path = "/saveTraderGroup")
    public Mono<?> saveTraderGroup(@RequestBody TraderGroup dto) {
        return traderGroupService.save(dto);
    }

    @PostMapping(path = "/findTraderGroup")
    public Mono<TraderGroup> findTraderGroup(@RequestBody TraderGroupKey key) {
        return traderGroupService.findById(key);
    }

    @GetMapping(path = "/getTraderGroup")
    public Flux<TraderGroup> getTraderGroup(@RequestParam String compCode) {
        return traderGroupService.findAll(compCode);
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

    @GetMapping(path = "/getAccSetting")
    public Flux<AccSetting> getAccSetting(@RequestParam String compCode) {
        return accSettingService.findAll(compCode);
    }

    @PostMapping(path = "/saveAccSetting")
    public Mono<AccSetting> saveAccSetting(@RequestBody AccSetting setting) {
        return accSettingService.save(setting);
    }

    @GetMapping(path = "/getUpdatedAccSetting")
    public Flux<AccSetting> getUpdatedAccSetting(@RequestParam String updatedDate) {
        return accSettingService.getAccSetting(Util1.toLocalDateTime(updatedDate));
    }

    @PostMapping(path = "/saveStockFormula")
    public Mono<?> saveStockFormula(@RequestBody StockFormula f) {
        return stockFormulaService.save(f);
    }

    @PostMapping(path = "/deleteStockFormula")
    public Mono<?> deleteStockFormula(@RequestBody StockFormulaKey key) {
        return Mono.just(stockFormulaService.delete(key));
    }

    @PostMapping(path = "/deleteGradeDetail")
    public Mono<?> deleteGradeDetail(@RequestBody GradeDetailKey key) {
        return Mono.just(gradeDetailService.delete(key));
    }

    @PostMapping(path = "/findStockFormula")
    public Mono<?> findStockFormula(@RequestBody StockFormulaKey key) {
        return Mono.justOrEmpty(stockFormulaService.findById(key));
    }

    @GetMapping(path = "/getStockFormula")
    public Mono<?> getStockFormula(@RequestParam String compCode) {
        return Mono.just(stockFormulaService.findAll(compCode));
    }

    @GetMapping(path = "/getStockFormulaPrice")
    public Flux<?> getStockFormulaPrice(@RequestParam String formulaCode, @RequestParam String compCode) {
        return stockFormulaPriceService.getStockFormulaPrice(formulaCode, compCode).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getStockFormulaQty")
    public Flux<?> getStockFormulaQty(@RequestParam String formulaCode, @RequestParam String compCode) {
        return stockFormulaQtyService.getStockFormulaQty(formulaCode, compCode).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getGradeDetail")
    public Flux<?> getGradeDetail(@RequestParam String formulaCode, @RequestParam String criteriaCode, @RequestParam String compCode) {
        return gradeDetailService.getGradeDetail(formulaCode, criteriaCode, compCode).onErrorResume(throwable -> Flux.empty());
    }

    @GetMapping(path = "/getStockFormulaGrade")
    public Flux<?> getStockFormulaGrade(@RequestParam String formulaCode, @RequestParam String compCode) {
        return gradeDetailService.getStockFormulaGrade(formulaCode, compCode).onErrorResume(throwable -> Flux.empty());
    }

    @PostMapping(path = "/saveStockFormulaPrice")
    public Mono<?> saveStockFormulaPrice(@RequestBody StockFormulaPrice f) {
        return Mono.just(stockFormulaPriceService.save(f));
    }

    @PostMapping(path = "/saveStockFormulaQty")
    public Mono<?> saveStockFormulaQty(@RequestBody StockFormulaQty f) {
        return Mono.just(stockFormulaQtyService.save(f));
    }

    @PostMapping(path = "/saveGradeDetail")
    public Mono<?> saveGradeDetail(@RequestBody GradeDetail f) {
        return Mono.just(gradeDetailService.save(f));
    }

    @PostMapping(path = "/deleteStockFormulaDetail")
    public Mono<?> deleteStockFormulaDetail(@RequestBody StockFormulaPriceKey key) {
        return Mono.justOrEmpty(stockFormulaPriceService.delete(key));
    }

    @PostMapping(path = "/deleteFormula")
    public Mono<?> deleteFormula(@RequestBody StockFormulaKey key) {
        return Mono.just(stockFormulaService.delete(key));
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
