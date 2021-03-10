package cps.communication;

import cps.info.address.AddressI;
import cps.info.address.NodeAddressI;
import cps.message.MessageI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = { CommunicationCI.class })
@RequiredInterfaces(required = { CommunicationCI.class })

public class Communicator extends AbstractComponent {
	private CommunicationInboundPort cip;
	private CommunicationOutboundPort cop;
	
	protected Communicator() throws Exception {
		super(1, 0);
		this.cip = new CommunicationInboundPort(this);
		this.cip.publishPort();
		this.cop = new CommunicationOutboundPort(this);
		this.cop.publishPort();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.cip.unpublishPort();
			this.cop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	

	public void connect(NodeAddressI address, String communicationInboundPortURI) {
		
		
	}
	
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) {
	
	}


	public void transmitMessage(MessageI m) {
		
	}

	public int hasRouteFor(AddressI address) {
		return 0;
	}

	public void ping() {
		
	}




}
