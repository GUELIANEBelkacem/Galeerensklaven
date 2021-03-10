package cps.node.terminal;

import cps.communication.CommunicationCI;
import cps.info.address.AddressI;
import cps.info.address.NodeAddressI;
import cps.message.MessageI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class TermCommOutboundPort extends AbstractOutboundPort implements CommunicationCI{

	public TermCommOutboundPort(ComponentI owner) throws Exception {
		super(CommunicationCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	public TermCommOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, CommunicationCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transmitMessage(MessageI m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hasRouteFor(AddressI address) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ping() {
		// TODO Auto-generated method stub
		
	}

}
