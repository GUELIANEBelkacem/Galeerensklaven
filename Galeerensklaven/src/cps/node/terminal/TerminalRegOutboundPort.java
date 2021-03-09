package cps.node.terminal;

import java.util.Set;

import cps.info.ConnectionInfo;
import cps.info.address.NodeAddressI;
import cps.info.position.PositionI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class TerminalRegOutboundPort extends AbstractOutboundPort implements TerminalRegRequiredCI {
	
	private static final long serialVersionUID = 1L;

	public TerminalRegOutboundPort(ComponentI owner)
			throws Exception {
		super(TerminalRegRequiredCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	public TerminalRegOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, TerminalRegRequiredCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<ConnectionInfo> register(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange) throws Exception{
		return ((TerminalRegRequiredCI) this.getConnector()).register(address, commIpUri, initialPosition, initialRange);
	}

}
