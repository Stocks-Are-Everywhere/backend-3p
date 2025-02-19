package org.scoula.three_people.order.service.simulator;

import org.scoula.three_people.order.service.OrderService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderSimulationService {

	private final org.scoula.three_people.order.service.simulator.SingleOrderSimulator singleOrderSimulator;

	public OrderSimulationService(final OrderService orderService) {
		this.singleOrderSimulator = new org.scoula.three_people.order.service.simulator.SingleOrderSimulator(orderService);
	}

	public void startSingleSimulation() {
		singleOrderSimulator.startSimulation();
	}

	public void stopSingleSimulation() {
		singleOrderSimulator.stopSimulation();
	}
}