package eu.arrowhead.core.qos.service.ping.monitor.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import eu.arrowhead.common.dto.shared.IcmpPingRequestACK;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.core.qos.dto.IcmpPingResponse;
import eu.arrowhead.core.qos.measurement.properties.PingMeasurementProperties;
import eu.arrowhead.core.qos.service.QoSMonitorDriver;
import eu.arrowhead.core.qos.service.ping.monitor.PingEventProcessor;

@RunWith(SpringRunner.class)
public class DefaultExternalPingMonitorTest {

	//=================================================================================================
	// members
	@InjectMocks
	private DefaultExternalPingMonitor monitor;

	@Mock
	protected PingMeasurementProperties pingMeasurementProperties;

	@Mock
	private QoSMonitorDriver driver;

	@Mock
	private PingEventProcessor processor;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Before
	public void setUp() throws Exception {

		ReflectionTestUtils.setField(monitor, "externalPingMonitorName", "externalmonitor");
		ReflectionTestUtils.setField(monitor, "externalPingMonitorAddress", "localhost");
		ReflectionTestUtils.setField(monitor, "externalPingMonitorPort", 8888);
		ReflectionTestUtils.setField(monitor, "externalPingMonitorPath", "/path");
		ReflectionTestUtils.setField(monitor, "pingMonitorSecure", true);

		when(pingMeasurementProperties.getTimeout()).thenReturn(1);
		when(pingMeasurementProperties.getTimeToRepeat()).thenReturn(32);

	}

	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void testPingAdressAddressInNull() {

		final String address = null;
		try {

			monitor.ping(address);

		} catch (final InvalidParameterException ex) {

			verify(pingMeasurementProperties, never()).getTimeout();
			verify(pingMeasurementProperties, never()).getTimeToRepeat();
			verify(driver, never()).requestExternalPingMonitorService(any(),any());
			verify(processor, never()).processEvents(any(),anyLong());

			throw ex;
		}

	}

	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void testPingAdressAddressInEmpty() {

		final String address = "";
		try {

			monitor.ping(address);

		} catch (final InvalidParameterException ex) {

			verify(pingMeasurementProperties, never()).getTimeout();
			verify(pingMeasurementProperties, never()).getTimeToRepeat();
			verify(driver, never()).requestExternalPingMonitorService(any(),any());
			verify(processor, never()).processEvents(any(),anyLong());

			throw ex;
		}

	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testPingOK() {

		final String address = "localhost";

		final IcmpPingRequestACK ack = new IcmpPingRequestACK();
		ack.setAckOk("OK");
		ack.setExternalMeasurementUuid(UUID.randomUUID());

		final List<IcmpPingResponse> response = List.of(new IcmpPingResponse());

		when(driver.requestExternalPingMonitorService(any(), any())).thenReturn(ack);
		when(processor.processEvents(any(), anyLong())).thenReturn(response);

		monitor.ping(address);

		verify(pingMeasurementProperties, times(2)).getTimeout();
		verify(pingMeasurementProperties, times(2)).getTimeToRepeat();
		verify(driver, times(1)).requestExternalPingMonitorService(any(),any());
		verify(processor, times(1)).processEvents(any(),anyLong());
	}

	//-------------------------------------------------------------------------------------------------
	@Test(expected = ArrowheadException.class)
	public void testPingACKNull() {

		final String address = "localhost";

		final IcmpPingRequestACK ack = null;

		final List<IcmpPingResponse> response = List.of(new IcmpPingResponse());

		when(driver.requestExternalPingMonitorService(any(), any())).thenReturn(ack);
		when(processor.processEvents(any(), anyLong())).thenReturn(response);

		try {

			monitor.ping(address);

		} catch (final Exception ex) {

			verify(pingMeasurementProperties, times(2)).getTimeout();
			verify(pingMeasurementProperties, times(2)).getTimeToRepeat();
			verify(driver, times(1)).requestExternalPingMonitorService(any(),any());
			verify(processor, never()).processEvents(any(),anyLong());

			throw ex;
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Test(expected = ArrowheadException.class)
	public void testPingNullAckOK() {

		final String address = "localhost";

		final IcmpPingRequestACK ack = new IcmpPingRequestACK();
		ack.setAckOk(null);
		ack.setExternalMeasurementUuid(UUID.randomUUID());

		final List<IcmpPingResponse> response = List.of(new IcmpPingResponse());

		when(driver.requestExternalPingMonitorService(any(), any())).thenReturn(ack);
		when(processor.processEvents(any(), anyLong())).thenReturn(response);

		try {

			monitor.ping(address);

		} catch (final Exception ex) {

			verify(pingMeasurementProperties, times(2)).getTimeout();
			verify(pingMeasurementProperties, times(2)).getTimeToRepeat();
			verify(driver, times(1)).requestExternalPingMonitorService(any(),any());
			verify(processor, never()).processEvents(any(),anyLong());

			throw ex;
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Test(expected = ArrowheadException.class)
	public void testPingACKInvalidAckOK() {

		final String address = "localhost";

		final IcmpPingRequestACK ack = new IcmpPingRequestACK();
		ack.setAckOk("otherThenOK");
		ack.setExternalMeasurementUuid(UUID.randomUUID());

		final List<IcmpPingResponse> response = List.of(new IcmpPingResponse());

		when(driver.requestExternalPingMonitorService(any(), any())).thenReturn(ack);
		when(processor.processEvents(any(), anyLong())).thenReturn(response);

		try {

			monitor.ping(address);

		} catch (final Exception ex) {

			verify(pingMeasurementProperties, times(2)).getTimeout();
			verify(pingMeasurementProperties, times(2)).getTimeToRepeat();
			verify(driver, times(1)).requestExternalPingMonitorService(any(),any());
			verify(processor, never()).processEvents(any(),anyLong());

			throw ex;
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Test(expected = ArrowheadException.class)
	public void testPingACKProcessIdNull() {

		final String address = "localhost";

		final IcmpPingRequestACK ack = new IcmpPingRequestACK();
		ack.setAckOk("OK");
		ack.setExternalMeasurementUuid(null);

		final List<IcmpPingResponse> response = List.of(new IcmpPingResponse());

		when(driver.requestExternalPingMonitorService(any(), any())).thenReturn(ack);
		when(processor.processEvents(any(), anyLong())).thenReturn(response);

		try {

			monitor.ping(address);

		} catch (final Exception ex) {

			verify(pingMeasurementProperties, times(2)).getTimeout();
			verify(pingMeasurementProperties, times(2)).getTimeToRepeat();
			verify(driver, times(1)).requestExternalPingMonitorService(any(),any());
			verify(processor, never()).processEvents(any(),anyLong());

			throw ex;
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Test(expected = ArrowheadException.class)
	public void testPingDriverThrowsArrowheadException() {

		final String address = "localhost";

		final List<IcmpPingResponse> response = List.of(new IcmpPingResponse());

		when(driver.requestExternalPingMonitorService(any(), any())).thenThrow(new ArrowheadException(""));
		when(processor.processEvents(any(), anyLong())).thenReturn(response);

		try {

			monitor.ping(address);

		} catch (final Exception ex) {

			verify(pingMeasurementProperties, times(2)).getTimeout();
			verify(pingMeasurementProperties, times(2)).getTimeToRepeat();
			verify(driver, times(1)).requestExternalPingMonitorService(any(),any());
			verify(processor, never()).processEvents(any(),anyLong());

			throw ex;
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Test(expected = ArrowheadException.class)
	public void testPingDriverThrowsIllegalArgumentException() {

		final String address = "localhost";

		final List<IcmpPingResponse> response = List.of(new IcmpPingResponse());

		when(driver.requestExternalPingMonitorService(any(), any())).thenThrow(new IllegalArgumentException(""));
		when(processor.processEvents(any(), anyLong())).thenReturn(response);

		try {

			monitor.ping(address);

		} catch (final Exception ex) {

			verify(pingMeasurementProperties, times(2)).getTimeout();
			verify(pingMeasurementProperties, times(2)).getTimeToRepeat();
			verify(driver, times(1)).requestExternalPingMonitorService(any(),any());
			verify(processor, never()).processEvents(any(),anyLong());

			throw ex;
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testPingProcessorThrowsIllegalArgumentException() {

		final String address = "localhost";

		final IcmpPingRequestACK ack = new IcmpPingRequestACK();
		ack.setAckOk("OK");
		ack.setExternalMeasurementUuid(UUID.randomUUID());

		when(driver.requestExternalPingMonitorService(any(), any())).thenReturn(ack);
		when(processor.processEvents(any(), anyLong())).thenThrow(new IllegalArgumentException());

		monitor.ping(address);

		verify(pingMeasurementProperties, times(2)).getTimeout();
		verify(pingMeasurementProperties, times(2)).getTimeToRepeat();
		verify(driver, times(1)).requestExternalPingMonitorService(any(),any());
		verify(processor, times(1)).processEvents(any(),anyLong());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testPingProcessorThrowsArrowheadException() {

		final String address = "localhost";

		final IcmpPingRequestACK ack = new IcmpPingRequestACK();
		ack.setAckOk("OK");
		ack.setExternalMeasurementUuid(UUID.randomUUID());

		when(driver.requestExternalPingMonitorService(any(), any())).thenReturn(ack);
		when(processor.processEvents(any(), anyLong())).thenThrow(new ArrowheadException(""));

		monitor.ping(address);

		verify(pingMeasurementProperties, times(2)).getTimeout();
		verify(pingMeasurementProperties, times(2)).getTimeToRepeat();
		verify(driver, times(1)).requestExternalPingMonitorService(any(),any());
		verify(processor, times(1)).processEvents(any(),anyLong());
	}
}