package org.scoula.three_people.kis.api;

import lombok.RequiredArgsConstructor;
import org.scoula.three_people.kis.KisService;
import org.scoula.three_people.kis.api.restApi.KisApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/charts")
public class KisController implements KisApi {

    private final KisService kisService;

    @GetMapping
    public String kis(
            DistributionRequest req
    ) throws IOException {
        return kisService.sendChartData(req);
    }
}
