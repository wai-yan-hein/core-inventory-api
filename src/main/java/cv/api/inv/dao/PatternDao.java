package cv.api.inv.dao;

import cv.api.inv.entity.Pattern;
import cv.api.inv.entity.PatternKey;

import java.util.List;

public interface PatternDao {
    Pattern findByCode(PatternKey key);

    Pattern save(Pattern pattern);

    void delete(Pattern pattern);

    List<Pattern> search(String stockCode,String compCode,Integer deptId);
    List<Pattern> unUpload();


}
