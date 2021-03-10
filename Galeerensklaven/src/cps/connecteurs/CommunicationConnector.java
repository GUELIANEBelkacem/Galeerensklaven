package cps.connecteurs;

import cps.communication.CommunicationCI;
import cps.info.address.AddressI;
import cps.info.address.NodeAddressI;
import cps.message.MessageI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class CommunicationConnector extends AbstractConnector implements CommunicationCI {
	
	
	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception {
		((CommunicationCI)this.offering).connect(address, communicationInboundPortURI);

	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI)
			throws Exception {
		((CommunicationCI)this.offering).connectRouting(address, communicationInboundPortURI, routingInboundPortURI);

	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		((CommunicationCI)this.offering).transmitMessage(m);

	}

	@Override
	public void hasRouteFor(AddressI address) throws Exception {
		((CommunicationCI)this.offering).hasRouteFor(address);

	}

	@Override
	public void ping() throws Exception {
		((CommunicationCI)this.offering).ping();

	}

}
