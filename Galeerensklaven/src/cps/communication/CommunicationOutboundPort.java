package cps.communication;

import cps.info.address.AddressI;
import cps.info.address.NodeAddressI;
import cps.message.MessageI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class CommunicationOutboundPort  extends AbstractOutboundPort implements CommunicationCI{


	private static final long serialVersionUID = -3787632917561440543L;

	public CommunicationOutboundPort (String uri, ComponentI owner) throws Exception {

		super(uri, CommunicationCI.class, owner);
	 
	}
	/*
	public CommunicationInboundPort (String uri, ComponentI owner) throws Exception {
		super(uri, RegistrationCI.class, owner);
	}
	*/

	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception{
		
		((CommunicationCI)this.getConnector()).connect(address, communicationInboundPortURI);
		
	}
	

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception{
		
		((CommunicationCI)this.getConnector()).connectRouting(address, communicationInboundPortURI, routingInboundPortURI);
		
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception{
		
		((CommunicationCI)this.getConnector()).transmitMessage(m);
		
		
	}
	

	@Override
	public int hasRouteFor(AddressI address) throws Exception{

		return ((CommunicationCI)this.getConnector()).hasRouteFor(address);
		
		
	}

	@Override
	public void ping() throws Exception{

		((CommunicationCI)this.getConnector()).ping();
		
	}
	 

}
