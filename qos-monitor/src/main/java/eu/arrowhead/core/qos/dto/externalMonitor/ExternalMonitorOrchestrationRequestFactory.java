package eu.arrowhead.core.qos.dto.externalMonitor;

import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.CoreCommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.dto.internal.ServiceDefinitionRequestDTO;
import eu.arrowhead.common.dto.internal.ServiceInterfaceRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.dto.shared.ServiceSecurityType;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.core.qos.QosMonitorConstants;

@Service
public class ExternalMonitorOrchestrationRequestFactory {

	//=================================================================================================
	// members

	@Autowired
	protected SSLProperties sslProperties;

	@Value(CoreCommonConstants.$CORE_SYSTEM_NAME)
	private String coreSystemName;

	@Value(CoreCommonConstants.$SERVER_ADDRESS)
	private String coreSystemAddress;
	
	@Value(CoreCommonConstants.$SERVER_PORT)
	private int coreSystemPort;

	@Resource(name = CommonConstants.ARROWHEAD_CONTEXT)
	private Map<String,Object> arrowheadContext;

	private Logger logger = LogManager.getLogger(ExternalMonitorOrchestrationRequestFactory.class);

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public OrchestrationFormRequestDTO createExternalMonitorOrchestrationRequest() {
		logger.debug("createExternalMonitorOrchestrationRequest started...");

		final SystemRequestDTO requester = createExternalMonitorSystemRequestDTO();
		final ServiceQueryFormDTO requestedService = createExternalPingMonitorServiceQueryFormDTO();

		final OrchestrationFormRequestDTO orchestrationForm = new OrchestrationFormRequestDTO.Builder( requester )
				.requestedService(requestedService)
				.flag(Flag.ENABLE_INTER_CLOUD, false)
				.flag(Flag.MATCHMAKING, false)
				.flag(Flag.ENABLE_QOS, false)
				.flag(Flag.OVERRIDE_STORE, true)
				.build();

		return orchestrationForm;
	}

	//-------------------------------------------------------------------------------------------------
	public SystemRequestDTO createExternalMonitorSystemRequestDTO() {
		logger.debug("createExternalMonitorSystemRequestDTO started...");

		final PublicKey publicKey = (PublicKey) arrowheadContext.get(CommonConstants.SERVER_PUBLIC_KEY);

		final SystemRequestDTO system = new SystemRequestDTO();
		system.setSystemName(coreSystemName);
		system.setAddress(coreSystemAddress);
		system.setPort(coreSystemPort);
		system.setMetadata(null);
		if (sslProperties.isSslEnabled()) {
			system.setAuthenticationInfo(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
		}

		return system;
	}

	//-------------------------------------------------------------------------------------------------
	public ServiceDefinitionRequestDTO createExternalPingMonitorServiceDefinitionRequestDTO() {
		logger.debug("createExternalMonitorServiceDefinitionRequestDTO started...");

		final ServiceDefinitionRequestDTO externalPingMonitorServiceDefinition = new ServiceDefinitionRequestDTO();
		externalPingMonitorServiceDefinition.setServiceDefinition(QosMonitorConstants.EXTERNAL_PING_MONITORING_SERVICE_DEFINITION);

		return externalPingMonitorServiceDefinition;
	}

	//-------------------------------------------------------------------------------------------------
	public ServiceInterfaceRequestDTO createExternalPingMonitorServiceInterfaceRequestDTO() {
		logger.debug("createExternalPingMonitorServiceInterfaceRequestDTO started...");

		final ServiceInterfaceRequestDTO externalPingMonitorServiceInterface = new ServiceInterfaceRequestDTO();
		externalPingMonitorServiceInterface.setInterfaceName(QosMonitorConstants.EXTERNAL_PING_MONITORING_SERVICE_INTERFACE);

		return externalPingMonitorServiceInterface;
	}

	//-------------------------------------------------------------------------------------------------
	public ServiceQueryFormDTO createExternalPingMonitorServiceQueryFormDTO() {
		logger.debug("createExternalPingMonitorServiceQueryFormDTO started...");

		final ServiceQueryFormDTO externalPingMonitorServiceQueryForm = new ServiceQueryFormDTO();
		externalPingMonitorServiceQueryForm.setInterfaceRequirements(List.of(QosMonitorConstants.EXTERNAL_PING_MONITORING_SERVICE_INTERFACE));
		externalPingMonitorServiceQueryForm.setServiceDefinitionRequirement(QosMonitorConstants.EXTERNAL_PING_MONITORING_SERVICE_DEFINITION);
		externalPingMonitorServiceQueryForm.setSecurityRequirements(List.of(ServiceSecurityType.CERTIFICATE));
		externalPingMonitorServiceQueryForm.setPingProviders(false);

		return externalPingMonitorServiceQueryForm;
	}
}