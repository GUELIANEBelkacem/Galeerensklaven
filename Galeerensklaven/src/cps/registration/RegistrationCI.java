package cps.registration;

import java.util.Set;

import cps.info.ConnectionInfo;
import cps.info.address.NodeAddressI;
import cps.info.position.PositionI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface RegistrationCI extends OfferedCI, RequiredCI {
	
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange) throws Exception;

	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception;

	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception;

	public void unregister(NodeAddressI address) throws Exception;
}
