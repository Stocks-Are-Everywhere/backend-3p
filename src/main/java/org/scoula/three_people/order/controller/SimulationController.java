package org.scoula.three_people.order.controller;

import org.scoula.three_people.order.controller.restApi.SimulationApi;
import org.scoula.three_people.order.service.simulator.OrderSimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class SimulationController implements SimulationApi {

	private final OrderSimulationService orderSimulationService;

	@Override
	public ResponseEntity<String> startSingleSimulation() {
		orderSimulationService.startSingleSimulation();
		return ResponseEntity.ok("Single simulation started");
	}

	@Override
	public ResponseEntity<String> stopSingleSimulation() {
		orderSimulationService.stopSingleSimulation();
		return ResponseEntity.ok("Single simulation stopped");
	}
}
