package cv.api.repo;

import cv.api.inv.entity.Location;
import cv.api.inv.entity.LocationKey;
import cv.api.inv.service.LocationService;
import cv.api.model.Department;
import cv.api.model.PropertyKey;
import cv.api.model.SystemProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserRepo {
    @Autowired
    private WebClient userApi;
    private List<Department> listDept;
    private List<String> location;
    private final HashMap<String, String> hmKey = new HashMap<>();
    @Autowired
    private LocationService locationService;
    int min = 1;

    public SystemProperty findProperty(String key, String compCode) {
        PropertyKey p = new PropertyKey();
        p.setPropKey(key);
        p.setCompCode(compCode);
        Mono<SystemProperty> result = userApi.post()
                .uri("/user/find-system-property")
                .body(Mono.just(p), PropertyKey.class)
                .retrieve()
                .bodyToMono(SystemProperty.class);
        return result.block(Duration.ofMinutes(min));
    }

    public String getProperty(String key) {
        if (hmKey.isEmpty()) {
            Mono<ResponseEntity<List<SystemProperty>>> result = userApi.get()
                    .uri(builder -> builder.path("/user/get-system-property")
                            .queryParam("compCode", "-")
                            .build())
                    .retrieve().toEntityList(SystemProperty.class);
            ResponseEntity<List<SystemProperty>> block = result.block();
            if (block != null) {
                List<SystemProperty> list = block.getBody();
                if (list != null) {
                    for (SystemProperty s : list) {
                        hmKey.put(s.getKey().getPropKey(), s.getPropValue());
                    }
                }
            }
        }
        return hmKey.get(key);
    }

    public List<Department> getDepartment() {
        if (listDept == null) {
            Mono<ResponseEntity<List<Department>>> result = userApi.get()
                    .uri(builder -> builder.path("/user/get-department")
                            .build())
                    .retrieve().toEntityList(Department.class);
            listDept = Objects.requireNonNull(result.block(Duration.ofMinutes(min))).getBody();
        }
        return listDept;
    }

    public List<String> getLocation() {
        if (location == null) {
            List<Department> list = getDepartment();
            if (list != null) {
                Integer deptId = list.get(0).getDeptId();
                return locationService.getLocation(deptId);

            }
        }
        return location;
    }

    public Integer getDeptId() {
        List<Department> list = getDepartment();
        return list.isEmpty() ? 0 : list.get(0).getDeptId();
    }
}
