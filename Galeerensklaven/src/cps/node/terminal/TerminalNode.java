package cps.node.terminal;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import cps.registration.*;

import cps.communication.CommunicationCI;
import cps.communication.CommunicationInboundPort;
import cps.communication.CommunicationOutboundPort;
import cps.connecteurs.CommunicationConnector;
import cps.info.ConnectionInfo;
import cps.info.address.AddressI;
import cps.info.address.NodeAddress;
import cps.info.position.Position;
import cps.info.position.PositionI;
import cps.message.Message;
import cps.message.MessageI;
import cps.node.NodeI;
import cps.node.routing.RoutingNode;
import cps.info.address.NodeAddressI;

@RequiredInterfaces(required = { CommunicationCI.class, RegistrationCI.class })
@OfferedInterfaces(offered = { CommunicationCI.class })

public class TerminalNode extends AbstractComponent implements NodeI {
	// private NodeAddressI address;
	// private PositionI initialPosition;
	// private double initialRange;

	// protected CommunicationCI comm;
	protected CommunicationInboundPort tcip;
	protected RegistrationOutboundPort trop;
	protected HashMap<AddressI, CommunicationOutboundPort> addressComOPmap = new HashMap<>();

	public static String RegOP_URI = RegistrationOutboundPort.generatePortURI();

	Random rand = new Random();
	private double initialRange = 10000000;
	private final NodeAddressI address = new NodeAddress(RoutingNode.genAddresse());
	private Position initialPosition = new Position(rand.nextInt(50), rand.nextInt(50)); // change this genPos
	// private ConnectionInfo conInfo = new ConnectionInfo(this.address, ComIP_URI,
	// RotIP_URI, true, pos, false);

	private Set<ConnectionInfo> voisins = new HashSet<>();

	// public TerminalNode(NodeAddressI address, PositionI initialPosition, double
	// initialRange) throws Exception {
	public TerminalNode() throws Exception {
		super(1, 0);
		// this.address = address;
		// this.initialPosition = initialPosition;
		// this.initialRange = initialRange;
		
		// this.trop = new
		// RegistrationOutboundPort(RegistrationOutboundPort.generatePortURI(), this);
		this.trop = new RegistrationOutboundPort(this.RegOP_URI, this);
		this.trop.publishPort();
		this.tcip = new CommunicationInboundPort(CommunicationInboundPort.generatePortURI(), this);
		this.tcip.publishPort();

		this.toggleLogging();
		this.toggleTracing();
		// TODO Auto-generated constructor stub
	}

	/*
	 * public TerminalNode(String reflectionInboundPortURI, int nbThreads, int
	 * nbSchedulableThreads) { super(reflectionInboundPortURI, nbThreads,
	 * nbSchedulableThreads); // TODO Auto-generated constructor stub }
	 */

	public synchronized void execute() throws Exception {
		super.execute();
		voisins = this.trop.registerTerminalNode(address, tcip.getPortURI(), initialPosition, initialRange);
		for (ConnectionInfo ci : voisins) {

			addressComOPmap.put(ci.getAddress(), new CommunicationOutboundPort(CommunicationOutboundPort.generatePortURI(), this));
			
			addressComOPmap.get(ci.getAddress()).connect(address, tcip.getPortURI());
			
			addressComOPmap.get(ci.getAddress()).publishPort();
			this.doPortConnection(addressComOPmap.get(ci.getAddress()).getPortURI(), ci.getCommunicationInboundPortURI(), CommunicationConnector.class.getCanonicalName());//add connector here 
			
			for(AddressI e: this.addressComOPmap.keySet()) {
				System.out.println("test map");
				this.transmitMessage(new Message( address.getAddress(), 10, e));
			}
			
			this.logMessage("terminaaaaaaaaaaaaaaaaal");
			//comm.connect(ci.getAddress(), ci.getCommunicationInboundPortURI());
			//ci.getCommunicationInboundPortURI().connect(address, comm); // gros doute ....

		}
		
		
		for(AddressI e: this.addressComOPmap.keySet()) {
			this.transmitMessage(new Message(address.getAddress() , 10, e));
		}

	}

	public void connect(NodeAddressI naddress, String communicationInboundPortURI) {
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

	public void transmitMessage(MessageI m) {

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
		return -1;
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
