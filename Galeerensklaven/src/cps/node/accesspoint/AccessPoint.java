package cps.node.accesspoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import cps.communication.CommunicationCI;
import cps.communication.CommunicationInboundPort;
import cps.communication.CommunicationOutboundPort;
import cps.connecteurs.CommunicationConnector;
import cps.connecteurs.NetworkCommunicationConnector;
import cps.connecteurs.RoutingConnector;
import cps.info.AccessInfo;
import cps.info.ConnectionInfo;
import cps.info.address.AddressI;
import cps.info.address.NetworkAddressI;
import cps.info.address.NodeAddress;
import cps.info.address.NodeAddressI;
import cps.info.position.Position;
import cps.info.position.PositionI;
import cps.message.Message;
import cps.message.MessageI;
import cps.networkAccess.NetworkAccessingInboundPort;
import cps.networkAccess.NetworkAccessingOutboundPort;
import cps.networkCommunication.NetworkCommunicationCI;
import cps.networkCommunication.NetworkCommunicationInboundPort;
import cps.networkCommunication.NetworkCommunicationOutboundPort;
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
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@RequiredInterfaces(required = { RoutingCI.class, CommunicationCI.class, RegistrationCI.class })
@OfferedInterfaces(offered = { RoutingCI.class, CommunicationCI.class })

public class AccessPoint extends AbstractComponent implements NodeI {

	

	public final String RotIP_URI = RoutingInboundPort.genURI();
	public final String ComIP_URI = CommunicationInboundPort.generatePortURI();
	public final static String RegOP_URI = RegistrationOutboundPort.generatePortURI();
	public final static String NaOP_URI = NetworkAccessingOutboundPort.generatePortURI();
	public final String NcIP_URI = NetworkCommunicationInboundPort.generatePortURI();
	
	
	protected CommunicationInboundPort acip;
	protected RegistrationOutboundPort arop;
	private RoutingInboundPort arotip;
	private NetworkAccessingOutboundPort naop;
	private NetworkCommunicationInboundPort ncip;

	public static int count = 0;
	public static String genAddresse() {
		String s = "ANode " + count;
		count ++;
		return s;
	}
	
	private final NodeAddressI address= new NodeAddress(AccessPoint.genAddresse()) ;
	
	private Set<AccessInfo> networks;
	private Map<NetworkAddressI, NetworkCommunicationCI> networkOP = new HashMap<>();
	private Map<AddressI, CommunicationCI> neighborsCOP = new HashMap<AddressI, CommunicationCI>();
	private Map<AddressI, RoutingCI> neighborsROP = new HashMap<AddressI, RoutingCI>();
	private Set<ConnectionInfo> neighbors; // new HashSet<ConnectionInfo>();
	private Map<AddressI, RouteInfo> routingTable = new HashMap<AddressI, RouteInfo>();

	protected HashMap<AddressI, CommunicationOutboundPort> addressComOPmap = new HashMap<>();
	

	Random rand = new Random();
	private double initialRange = 10000000;
	private Position initialPosition = new Position(rand.nextInt(50), rand.nextInt(50));

	private ConnectionInfo conInfo = new ConnectionInfo(this.address, ComIP_URI, RotIP_URI, true, initialPosition, true);
	
	protected AccessPoint() throws Exception {
		super(1, 0);
		this.arop = new RegistrationOutboundPort(RegOP_URI, this);
		this.arop.publishPort();
		this.acip = new CommunicationInboundPort(ComIP_URI, this);
		this.acip.publishPort();
		this.naop = new NetworkAccessingOutboundPort(NaOP_URI, this);
		this.naop.publishPort();
		this.ncip = new NetworkCommunicationInboundPort(NcIP_URI, this);
		this.ncip.publishPort();
		this.arotip = new RoutingInboundPort(RotIP_URI, this);
		this.arotip.publishPort();
		
		this.toggleLogging();
		this.toggleTracing();
		
		// this.ncop = new NetworkCommunicationOutboundPort(uri, this);
		// this.ncip = new NetworkCommunicationInboundPort(uri, this)
		// TODO Auto-generated constructor stub
	}
	
	
	
	

	@Override
	public synchronized void finalise() throws Exception {
		
		for(Entry<NetworkAddressI, NetworkCommunicationCI> e: networkOP.entrySet()) {
			System.out.println(e.getKey().getAddress() + "\n");
		}
		
		
		for(AddressI e: this.neighborsCOP.keySet()) {
			System.out.println(this.address.getAddress() + " <=====> " + e.getAddress());
		}
		
		
		for(Entry<AddressI, RouteInfo> a: this.routingTable.entrySet()) {
			System.out.println(this.address.getAddress()+ ": " +a.getKey().getAddress() + " <===" + a.getValue().getNumberOfHops() +"===> " + a.getValue().getDestination().getAddress());
		}
		
		for(NetworkCommunicationCI n : this.networkOP.values()) {
			this.doPortDisconnection(((NetworkCommunicationOutboundPort)n).getPortURI());
		}
		for(CommunicationCI c: this.neighborsCOP.values()) {
			this.doPortDisconnection(((CommunicationOutboundPort)c).getPortURI());
		}
		for(RoutingCI r: this.neighborsROP.values()) {
			this.doPortDisconnection(((RoutingOutboundPort)r).getPortURI());
		}
		//this.doPortDisconnection(RotIP_URI);
		//this.doPortDisconnection(ComIP_URI);
		this.doPortDisconnection(RegOP_URI);
		super.finalise();
	}
	

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		
		try {
			
			for(NetworkCommunicationCI n : this.networkOP.values()) {
				((NetworkCommunicationOutboundPort)n).unpublishPort();
			}
			
			for(CommunicationCI c: this.neighborsCOP.values()) {
				((CommunicationOutboundPort)c).unpublishPort();
			}
			for(RoutingCI r: this.neighborsROP.values()) {
				((RoutingOutboundPort)r).unpublishPort();
			}
			this.arotip.unpublishPort();
			this.acip.unpublishPort();
			this.arop.unpublishPort();
			this.naop.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		super.shutdown();
	}

	
	
	
	
	
	public synchronized void execute() throws Exception {
		
		super.execute();

		this.connectNetwork();
		
		this.catchUp();
		
		
		this.route();
		
	
		for(AddressI e: this.routingTable.keySet()) {
			this.transmitMessage(new Message(address.getAddress() , 2, e));
		}
		
		
		/*
		for(AddressI e: this.neighborsCOP.keySet()) {
			this.logMessage(this.address.getAddress() + " <=====> " + e.getAddress());
		}
		*/
		//this.logMessage("end");
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
		this.logMessage("message passe par accesspoint " + address.getAddress());
		if (m.getAddress().isNetworkAddress()) {
			networkOP.get(m.getAddress()).transmitMessage((NetworkAddressI) m.getAddress(), m);
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
		// rien
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
		for (Entry<AddressI, CommunicationCI> a : this.neighborsCOP.entrySet()) {
			try {
				((CommunicationOutboundPort) a).ping();
			} catch (Exception e) {
				throw new java.rmi.ConnectException(a.getKey().getAddress() + " cant be found");
			}
		}

	}

	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {

		return this.arop.registerAccessPoint(address, commIpUri, initialPosition, initialRange, routingIpUri);

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

	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI)
			throws Exception {
		connectNetwork();
		if (!this.neighborsCOP.containsKey(address)) {

			// this.logMessage(address.getAddress()+" sent its address to me
			// "+this.address.getAddress());
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC, this);
			this.neighborsCOP.put(address, pc);
			pc.publishPort();
			this.doPortConnection(uriTempC, communicationInboundPortURI,
					CommunicationConnector.class.getCanonicalName());// add connector here

			if (!address.isequalsAddress(this.address)) {
				this.routingTable.put(address, new RouteInfo(address, 1));
			}

		}

		if (!this.neighborsROP.containsKey(address)) {
			String uriTempR = RoutingOutboundPort.generatePortURI();
			RoutingOutboundPort pr = new RoutingOutboundPort(uriTempR, this);
			pr.publishPort();
			this.doPortConnection(uriTempR, routingInboundPortURI, RoutingConnector.class.getCanonicalName());
			this.neighborsROP.put(address, pr);

			if (!address.isequalsAddress(this.address)) {
				this.routingTable.put(address, new RouteInfo(address, 1));
			}
		}
	}
	
	
	public void catchUp() throws Exception {
		this.register();
		
		for(ConnectionInfo c: this.neighbors) {
			if(c.isRouting()) {
				this.neighborsCOP.get(c.getAddress()).connectRouting(this.address, this.ComIP_URI, this.RotIP_URI);
				//this.logMessage("me "+ this.address.getAddress()+" is connecting to "+c.getAddress().getAddress());
			}else {
				this.neighborsCOP.get(c.getAddress()).connect(this.address, this.ComIP_URI);
				
			}
		}
		connectNetwork();
	}
	
	
	
	public void route() throws Exception {
		
		Set<RouteInfo> routes = this.getRouteInfo();
		//this.logMessage("updating routing");
		for(Entry<AddressI, RoutingCI> a: this.neighborsROP.entrySet()) {
			((RoutingOutboundPort)a.getValue()).updateRouting(this.address, routes);
		}
	}
	
	public Set<RouteInfo> getRouteInfo() {
		
		Set<RouteInfo> routes = new HashSet<RouteInfo>();
		for(Entry<AddressI, RouteInfo> a: this.routingTable.entrySet()) {
			routes.add(new RouteInfo(a.getKey(), a.getValue().getNumberOfHops()));
		}
		return routes;
	}
	
	public void connectNetwork() throws Exception {
		networks = naop.getNetworkNodes();
		this.logMessage("connecting");
		String tempUri;
		System.out.println("NETWORKS\n\n\n\n");
		for(AccessInfo e : networks) {
			System.out.println(e.getAddress().getAddress());
			tempUri = e.getAccessInboundPortURI();
			NetworkCommunicationOutboundPort ni = new NetworkCommunicationOutboundPort(NetworkCommunicationOutboundPort.generatePortURI(), this);
			networkOP.put(e.getAddress(), ni);
			ni.publishPort();
			this.doPortConnection(ni.getPortURI(), tempUri, NetworkCommunicationConnector.class.getCanonicalName());
			
		}
	}

	
	
	
	

}
