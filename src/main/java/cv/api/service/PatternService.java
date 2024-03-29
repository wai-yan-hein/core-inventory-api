package cv.api.service;

import cv.api.entity.Job;
import cv.api.entity.Pattern;
import cv.api.entity.PatternKey;

import java.time.LocalDateTime;
import java.util.List;

public interface PatternService {
    Pattern findByCode(PatternKey key);

    Pattern save(Pattern pattern);

    List<Pattern> search(String stockCode, String compCode);

    void delete(Pattern pattern);

    List<Pattern> unUpload();
    List<Pattern> getPattern(LocalDateTime updatedDate);

}
