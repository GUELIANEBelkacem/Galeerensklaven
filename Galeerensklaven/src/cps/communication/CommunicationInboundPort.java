package cps.communication;

import cps.info.address.AddressI;
import cps.info.address.NodeAddressI;
import cps.message.MessageI;
import cps.node.routing.RoutingNode;
import cps.node.NodeI;
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
					u-> {
						try {
							((NodeI) u).connect(address, communicationInboundPortURI);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception{
		
		try {
			this.getOwner().runTask(
					u-> {
						try {
							((RoutingNode) u).connectRouting(address, communicationInboundPortURI, routingInboundPortURI);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception{
		
		try {
			this.getOwner().runTask(
					u-> {
						try {
							((NodeI) u).transmitMessage(m);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
		} catch (AssertionError | Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
/*
	@Override
	public int hasRouteFor(AddressI address) throws Exception{
		
		return this.getOwner().handleRequest(
				t -> ((Communicator) t).hasRouteFor(address));
		
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
	*/

}
