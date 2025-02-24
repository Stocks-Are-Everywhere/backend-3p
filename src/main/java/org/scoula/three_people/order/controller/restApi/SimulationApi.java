package org.scoula.three_people.order.controller.restApi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// @Tag(name = "Simulation API", description = "시뮬레이션 관련 API")
@Tag(name = "Simulation")
@RequestMapping("/api/v1/simulation")
public interface SimulationApi {

    @PostMapping("/single/start")
    @Operation(summary = "단일 시뮬레이션 시작", description = "단일 시뮬레이션을 시작합니다.")
    ResponseEntity<String> startSingleSimulation();

    @PostMapping("/single/stop")
    @Operation(summary = "단일 시뮬레이션 중지", description = "단일 시뮬레이션을 중지합니다.")
    ResponseEntity<String> stopSingleSimulation();
}
