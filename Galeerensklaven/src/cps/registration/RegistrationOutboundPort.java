package cps.registration;

import java.util.Set;
import cps.info.ConnectionInfo;
import cps.info.address.NodeAddressI;
import cps.info.position.PositionI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RegistrationOutboundPort extends AbstractOutboundPort implements RegistrationCI {

	private static final long serialVersionUID = 1L;

	public RegistrationOutboundPort(ComponentI owner) throws Exception {
		super(RegistrationCI.class, owner);

	}

	public RegistrationOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RegistrationCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange) throws Exception {
		return ((RegistrationCI)this.getConnector()).registerTerminalNode(address, commIpUri, initialPosition, initialRange);
	}

	@Override
	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {
		return ((RegistrationCI)this.getConnector()).registerRoutingNode(address, commIpUri, initialPosition, initialRange, routingIpUri);
	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {
		return ((RegistrationCI)this.getConnector()).registerAccessPoint(address, commIpUri, initialPosition, initialRange, routingIpUri);
	}

	@Override
	public void unregister(NodeAddressI address) throws Exception{
		((RegistrationCI)this.getConnector()).unregister(address);
		

	}

}
