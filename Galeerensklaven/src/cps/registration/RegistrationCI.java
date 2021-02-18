package cps.registration;

import java.util.Set;


import fr.sorbonne_u.components.interfaces.OfferedCI;

public interface RegistrationCI extends OfferedCI {
	
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange) throws Exception;

	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception;

	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception;

	public void unregister(NodeAddressI address);
}
