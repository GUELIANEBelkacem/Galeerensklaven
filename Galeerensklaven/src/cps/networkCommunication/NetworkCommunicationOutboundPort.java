package cps.networkCommunication;

import cps.info.address.NetworkAddressI;
import cps.message.MessageI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class NetworkCommunicationOutboundPort extends AbstractOutboundPort implements NetworkCommunicationCI {

	public NetworkCommunicationOutboundPort(ComponentI owner)
			throws Exception {
		super(NetworkCommunicationCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	public NetworkCommunicationOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, NetworkCommunicationCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void transmitMessage(NetworkAddressI naddress, MessageI m) throws Exception{
		((NetworkCommunicationCI)this.getConnector()).transmitMessage(naddress, m);

	}

}
