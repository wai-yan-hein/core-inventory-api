package cv.api.model;

import cv.api.entity.LocationKey;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RequestModel {
    private List<LocationKey> keys;
    private Date updatedDate;

    public RequestModel(List<LocationKey> keys, Date updatedDate) {
        this.keys = keys;
        this.updatedDate = updatedDate;
    }
}
