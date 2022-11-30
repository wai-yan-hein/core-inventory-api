package cv.api.inv.service;

import cv.api.inv.entity.Pattern;
import cv.api.inv.entity.PatternKey;

import java.util.List;

public interface PatternService {
    Pattern findByCode(PatternKey key);

    Pattern save(Pattern pattern);
    List<Pattern> search(String stockCode,String compCode,Integer deptId);
    void delete(Pattern pattern);
    List<Pattern> unUpload();

}
