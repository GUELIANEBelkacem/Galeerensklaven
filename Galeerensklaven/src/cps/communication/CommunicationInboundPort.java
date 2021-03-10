package cps.communication;

import cps.info.address.AddressI;
import cps.info.address.NodeAddressI;
import cps.message.MessageI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class CommunicationInboundPort extends AbstractInboundPort implements CommunicationCI {
	public static int count =0;

	public static String genURI() {
		String s = "com_ip_uri "+count;
		count++;
		return s;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2601324781658680231L;

	public CommunicationInboundPort (String uri, ComponentI owner) throws Exception {
		super(uri, CommunicationCI.class, owner);

	}
	/*
	public CommunicationInboundPort (String uri, ComponentI owner) throws Exception {
		super(uri, RegistrationCI.class, owner);
	}
	*/

	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception{
		
		try {
			this.getOwner().runTask(
					u-> ((Communicator) u).connect(address, communicationInboundPortURI));
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception{
		
		try {
			this.getOwner().runTask(
					u-> ((Communicator) u).connectRouting(address, communicationInboundPortURI, routingInboundPortURI));
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception{
		
		try {
			this.getOwner().runTask(
					u-> ((Communicator) u).transmitMessage(m));
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void hasRouteFor(AddressI address) throws Exception{

		try {
			this.getOwner().runTask(
					u-> ((Communicator) u).hasRouteFor(address));
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void ping() throws Exception{

		try {
			this.getOwner().runTask(
					u-> ((Communicator) u).ping());
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}