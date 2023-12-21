package cv.api.dao;

import cv.api.entity.Pattern;
import cv.api.entity.PatternKey;

import java.time.LocalDateTime;
import java.util.List;

public interface PatternDao {
    Pattern findByCode(PatternKey key);

    Pattern save(Pattern pattern);

    void delete(Pattern pattern);

    List<Pattern> search(String stockCode, String compCode);

    List<Pattern> unUpload();
    List<Pattern> getPattern(LocalDateTime updatedDate);

}
