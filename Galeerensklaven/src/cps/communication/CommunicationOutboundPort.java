package cps.communication;

import cps.info.address.AddressI;
import cps.info.address.NodeAddressI;
import cps.message.MessageI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class CommunicationOutboundPort  extends AbstractInboundPort implements CommunicationCI{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3787632917561440543L;

	public CommunicationOutboundPort (ComponentI owner) throws Exception {
		super(CommunicationCI.class, owner);

	}
	/*
	public CommunicationInboundPort (String uri, ComponentI owner) throws Exception {
		super(uri, RegistrationCI.class, owner);
	}
	*/

	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) {
		
		try {
			this.getOwner().runTask(
					u-> ((Communicator) u).connect(address, communicationInboundPortURI));
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) {
		
		try {
			this.getOwner().runTask(
					u-> ((Communicator) u).connectRouting(address, communicationInboundPortURI, routingInboundPortURI));
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void transmitMessage(MessageI m) {
		
		try {
			this.getOwner().runTask(
					u-> ((Communicator) u).transmitMessage(m));
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void hasRouteFor(AddressI address) {

		try {
			this.getOwner().runTask(
					u-> ((Communicator) u).hasRouteFor(address));
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void ping() {

		try {
			this.getOwner().runTask(
					u-> ((Communicator) u).ping());
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
