package eu.arrowhead.core.qos.dto.event.monitoringevents;

import java.io.Serializable;

import eu.arrowhead.common.dto.shared.QosMonitorEventType;

public class StartedMonitoringMeasurementEventDTO extends MeasurementMonitoringEvent implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -5448189680057980316L;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public StartedMonitoringMeasurementEventDTO() {
		this.eventType = QosMonitorEventType.STARTED_MONITORING_MEASUREMENT;
	}

}
