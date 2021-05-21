package eu.arrowhead.core.qos.service.event.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import eu.arrowhead.common.dto.shared.StartedMonitoringMeasurementEventDTO;

public class StartedMonitoringMeasurementEventQueue {

	//=================================================================================================
	// members

	private static final BlockingQueue<StartedMonitoringMeasurementEventDTO> finishedMonitoringMeasurementEventQueue = new LinkedBlockingQueue<>();

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------	
	public void put(final StartedMonitoringMeasurementEventDTO toPut) throws InterruptedException {
		finishedMonitoringMeasurementEventQueue.put(toPut);
	}

	//-------------------------------------------------------------------------------------------------
	public StartedMonitoringMeasurementEventDTO take() throws InterruptedException {
		return finishedMonitoringMeasurementEventQueue.take();
	}

	//-------------------------------------------------------------------------------------------------
	public StartedMonitoringMeasurementEventDTO poll() throws InterruptedException {
		return finishedMonitoringMeasurementEventQueue.poll();
	}
}