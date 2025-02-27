package org.scoula.three_people.kis.api.restApi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.scoula.three_people.kis.api.DistributionRequest;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Tag(name = "KIS API")
public interface KisApi {

    @Operation(summary = "분봉 차트 API", description = "한국 투자증권 API에서 분봉 그래프 데이터를 호출한다.")
    @GetMapping
    String kis(DistributionRequest req) throws IOException;
}
