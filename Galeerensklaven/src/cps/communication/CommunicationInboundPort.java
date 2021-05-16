package cps.communication;

import cps.info.address.AddressI;
import cps.info.address.NodeAddressI;
import cps.message.MessageI;
import cps.node.NodeI;
import cps.node.RoutingI;
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
			this.getOwner().runTask("inpooluri",
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
	
/*
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
		return;
	}
	*/
	
	@Override
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception{
		
		
			this.getOwner().handleRequest("inpooluri",
					u-> {((RoutingI) u).connectRouting(address, communicationInboundPortURI, routingInboundPortURI);
						
						return null;});
					
				

	}

	@Override
	public void transmitMessage(MessageI m) throws Exception{
		
		try {
			this.getOwner().runTask("messagepooluri",
					u-> {
						try {
							((NodeI) u).transmitMessage(m);
						} catch (Exception e) {
							//System.out.println("message to "+m.getAddress().getAddress()+" failed to reach");
						}
					});
		} catch (AssertionError | Exception e) {
			//System.out.println("message to "+m.getAddress().getAddress()+" failed to reach");
		}
		
	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception{
		
		return this.getOwner().handleRequest("inpooluri",
				t -> ((NodeI) t).hasRouteFor(address));
		
	}

	@Override
	public void ping() throws Exception{

	
			this.getOwner().runTask("inpooluri",
					u-> {
						try {
							((NodeI) u).ping();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});

	
	}

}
