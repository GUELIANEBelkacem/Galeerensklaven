package cps.communication;

import cps.info.address.AddressI;
import cps.info.address.NodeAddressI;
import cps.message.MessageI;
import fr.sorbonne_u.components.interfaces.OfferedCI;

public interface CommunicationCI extends OfferedCI {
	public void connect(NodeAddressI address, String communicationInboundPortURI);
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI);
	public void transmitMessage(MessageI m);
	public void hasRouteFor(AddressI address);
	public void ping();
}
