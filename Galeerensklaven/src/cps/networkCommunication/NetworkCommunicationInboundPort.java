package cps.networkCommunication;

import cps.info.address.NetworkAddressI;
import cps.message.MessageI;
import cps.networkAccess.NetworkAccessor;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import cps.classicNetwork.*;


public class NetworkCommunicationInboundPort extends AbstractInboundPort implements NetworkCommunicationCI {

	public NetworkCommunicationInboundPort(ComponentI owner)
			throws Exception {
		super(NetworkCommunicationCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	public NetworkCommunicationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, NetworkCommunicationCI.class, owner);
		// TODO Auto-generated constructor stub
	}



	@Override
	public void transmitMessage(NetworkAddressI naddress, MessageI m) {
		try {
			this.getOwner().runTask(u -> ((ClassicNetwork) u).receiveMessage(m));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
