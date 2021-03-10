package cps.connecteurs;

import java.util.Set;

import cps.communication.CommunicationCI;
import cps.info.ConnectionInfo;
import cps.info.address.NodeAddressI;
import cps.info.position.PositionI;
import cps.registration.RegistrationCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RegistrationConnector extends AbstractConnector implements RegistrationCI {


	@Override
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange) throws Exception {
		return ((RegistrationCI)this.offering).registerTerminalNode(address, commIpUri, initialPosition, initialRange);
	}

	@Override
	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {
		return ((RegistrationCI)this.offering).registerRoutingNode(address, commIpUri, initialPosition, initialRange, routingIpUri);
	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {
		return ((RegistrationCI)this.offering).registerAccessPoint(address, commIpUri, initialPosition, initialRange, routingIpUri);
	}

	@Override
	public void unregister(NodeAddressI address) throws Exception {
		((RegistrationCI)this.offering).unregister(address);

	}

}
