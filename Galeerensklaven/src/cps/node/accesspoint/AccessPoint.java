package cps.node.accesspoint;

import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;

import cps.communication.CommunicationInboundPort;
import cps.communication.CommunicationOutboundPort;
import cps.connecteurs.CommunicationConnector;
import cps.info.address.AddressI;
import cps.info.address.NodeAddress;
import cps.info.address.NodeAddressI;
import cps.info.position.Position;
import cps.message.MessageI;
import cps.node.NodeI;
import cps.node.routing.RoutingNode;
import cps.registration.RegistrationOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;

public class AccessPoint extends AbstractComponent implements NodeI {

	protected CommunicationInboundPort tcip;
	protected RegistrationOutboundPort trop;
	public static final String RegOP_URI = RegistrationOutboundPort.generatePortURI();

	protected HashMap<AddressI, CommunicationOutboundPort> addressComOPmap = new HashMap<>();
	
	
	Random rand = new Random();
	private double initialRange = 10000000;
	private final NodeAddressI address = new NodeAddress(RoutingNode.genAddresse());
	private Position initialPosition = new Position(rand.nextInt(50), rand.nextInt(50));
	
	
	
	public AccessPoint() throws Exception{
		super(1, 0);
		this.trop = new RegistrationOutboundPort(RegOP_URI, this);
		this.trop.publishPort();
		this.tcip = new CommunicationInboundPort(CommunicationInboundPort.generatePortURI(), this);
		this.tcip.publishPort();
		// TODO Auto-generated constructor stub
	}

	/*
	
	public AccessPoint(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}


*/
	@Override
	public void connect(NodeAddressI naddress, String communicationInboundPortURI) throws Exception {
		// TODO Auto-generated method stub
		try {
			if (!addressComOPmap.containsKey(naddress)) {
				CommunicationOutboundPort tempPort = new CommunicationOutboundPort(CommunicationOutboundPort.generatePortURI(), this);
				tempPort.publishPort();
				addressComOPmap.put(naddress,
						tempPort);
				this.doPortConnection(tempPort.getPortURI(), communicationInboundPortURI, CommunicationConnector.class.getCanonicalName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		// TODO Auto-generated method stub
		if(m.getAddress().isequalsAddress(this.address)) {
			this.logMessage(this.address.getAddress() + " <===== " + m.getContent().getMessage());
		}
		else {
			if(m.stillAlive()) {

				m.decrementHops();
				for (Entry<AddressI, CommunicationOutboundPort> e : addressComOPmap.entrySet()) {
					try {
						e.getValue().transmitMessage(m);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}

	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
