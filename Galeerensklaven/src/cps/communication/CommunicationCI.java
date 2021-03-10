package cps.communication;

import cps.info.address.AddressI;
import cps.info.address.NodeAddressI;
import cps.message.MessageI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface CommunicationCI extends OfferedCI, RequiredCI {
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception;
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI)throws Exception;
	public void transmitMessage(MessageI m) throws Exception;
	public void hasRouteFor(AddressI address) throws Exception;
	public void ping() throws Exception;
}
