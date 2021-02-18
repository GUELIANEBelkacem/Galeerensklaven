package cps.node.terminal;

import cps.info.address.NodeAddressI;
import cps.info.position.PositionI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

import java.util.Set;

import cps.info.ConnectionInfo;

public interface TerminalRegRequiredCI extends RequiredCI {
	
	public Set<ConnectionInfo> register(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange);
	

}
