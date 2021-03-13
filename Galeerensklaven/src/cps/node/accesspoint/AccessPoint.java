package cps.node.accesspoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import cps.communication.CommunicationCI;
import cps.communication.CommunicationInboundPort;
import cps.communication.CommunicationOutboundPort;
import cps.connecteurs.CommunicationConnector;
import cps.connecteurs.RoutingConnector;
import cps.info.ConnectionInfo;
import cps.info.address.AddressI;
import cps.info.address.NodeAddress;
import cps.info.address.NodeAddressI;
import cps.info.position.Position;
import cps.info.position.PositionI;
import cps.message.MessageI;
import cps.node.NodeI;
import cps.node.routing.RoutingNode;
import cps.registration.RegistrationCI;
import cps.registration.RegistrationOutboundPort;
import cps.routing.RouteInfo;
import cps.routing.RoutingCI;
import cps.routing.RoutingInboundPort;
import cps.routing.RoutingOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

@RequiredInterfaces(required = { RoutingCI.class, CommunicationCI.class, RegistrationCI.class })
@OfferedInterfaces(offered = { RoutingCI.class, CommunicationCI.class })

public class AccessPoint extends AbstractComponent implements NodeI {

	private Map<AddressI, RouteInfo> routingTable = new HashMap<AddressI, RouteInfo>();

	public final String RotIP_URI = RoutingInboundPort.genURI();
	public final String ComIP_URI = CommunicationOutboundPort.generatePortURI();
	public static final String RegOP_URI = RegistrationOutboundPort.generatePortURI();

	protected CommunicationInboundPort rcip;
	protected RegistrationOutboundPort rrop;
	private RoutingInboundPort rotip;

	private Map<AddressI, CommunicationCI> neighborsCOP = new HashMap<AddressI, CommunicationCI>();
	private Map<AddressI, RoutingCI> neighborsROP = new HashMap<AddressI, RoutingCI>();
	private Set<ConnectionInfo> neighbors; // new HashSet<ConnectionInfo>();

	protected HashMap<AddressI, CommunicationOutboundPort> addressComOPmap = new HashMap<>();

	Random rand = new Random();
	private double initialRange = 10000000;
	private final NodeAddressI address = new NodeAddress(RoutingNode.genAddresse());
	private Position initialPosition = new Position(rand.nextInt(50), rand.nextInt(50));

	public AccessPoint() throws Exception {
		super(1, 0);
		this.rrop = new RegistrationOutboundPort(RegOP_URI, this);
		this.rrop.publishPort();
		this.rcip = new CommunicationInboundPort(CommunicationInboundPort.generatePortURI(), this);
		this.rcip.publishPort();
		// TODO Auto-generated constructor stub
	}

	/*
	 * 
	 * public AccessPoint(String reflectionInboundPortURI, int nbThreads, int
	 * nbSchedulableThreads) { super(reflectionInboundPortURI, nbThreads,
	 * nbSchedulableThreads); // TODO Auto-generated constructor stub }
	 * 
	 * 
	 */
	@Override
	public void connect(NodeAddressI naddress, String communicationInboundPortURI) throws Exception {
		// TODO Auto-generated method stub
		try {
			if (!addressComOPmap.containsKey(naddress)) {
				CommunicationOutboundPort tempPort = new CommunicationOutboundPort(
						CommunicationOutboundPort.generatePortURI(), this);
				tempPort.publishPort();
				addressComOPmap.put(naddress, tempPort);
				this.doPortConnection(tempPort.getPortURI(), communicationInboundPortURI,
						CommunicationConnector.class.getCanonicalName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		// TODO Auto-generated method stub
		if (m.getAddress().isNetworkAddress()) {
			this.logMessage("Message transmis au réseau classique à l'adresse " + m.getAddress().getAddress());
			for (Entry<AddressI, RoutingCI> e : neighborsROP.entrySet()) {
				e.getValue().updateAccessPoint(address, 1);
			}
		} else {

			if (m.getAddress().isequalsAddress(this.address)) {
				this.logMessage(this.address.getAddress() + " <===== " + m.getContent().getMessage());
			} else {
				if (m.stillAlive()) {

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

	}
	
	
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) {
		//rien
	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		if (this.routingTable.containsKey(address)) {
			return this.routingTable.get(address).getNumberOfHops();
		} else {
			return -1;
		}
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub

	}

	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {

		return this.rrop.registerRoutingNode(address, commIpUri, initialPosition, initialRange, routingIpUri);

	}

	public void register() throws Exception {

		this.neighbors = this.registerRoutingNode(this.address, this.ComIP_URI, this.initialPosition, this.initialRange,
				this.RotIP_URI);
		for (ConnectionInfo c : this.neighbors) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC, this);
			pc.publishPort();
			this.doPortConnection(uriTempC, c.getCommunicationInboundPortURI(),
					CommunicationConnector.class.getCanonicalName());// add connector here
			this.neighborsCOP.put(c.getAddress(), pc);

			if (c.isRouting()) {
				String uriTempR = RoutingOutboundPort.generatePortURI();
				RoutingOutboundPort pr = new RoutingOutboundPort(uriTempR, this);
				pr.publishPort();
				this.doPortConnection(uriTempR, c.getRoutingInboundPortURI(),
						RoutingConnector.class.getCanonicalName());// add connector here
				this.neighborsROP.put(c.getAddress(), pr);
			}

			if (!c.getAddress().isequalsAddress(this.address)) {
				this.routingTable.put(c.getAddress(), new RouteInfo(c.getAddress(), 1));
			}
		}

	}

}
