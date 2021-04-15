package cps.node.accesspoint;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cps.communication.CommunicationCI;
import cps.communication.CommunicationInboundPort;
import cps.communication.CommunicationOutboundPort;
import cps.connecteurs.CommunicationConnector;
import cps.connecteurs.RoutingConnector;
import cps.info.ConnectionInfo;
import cps.info.address.AddressI;
import cps.info.address.NetworkAddress;
import cps.info.address.NodeAddress;
import cps.info.address.NodeAddressI;
import cps.info.position.Position;
import cps.info.position.PositionI;
import cps.message.Message;
import cps.message.MessageI;
import cps.node.NodeI;
import cps.node.RoutingI;
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
@OfferedInterfaces(offered = { RoutingCI.class, CommunicationCI.class, RegistrationCI.class })
public class AccessPoint extends AbstractComponent implements NodeI, RoutingI{
	private Map<AddressI, CommunicationCI> neighborsCOP = new ConcurrentHashMap<AddressI, CommunicationCI>();
	private Set<ConnectionInfo> neighbors;
	private Map<AddressI, CommunicationCI> APCOP = new ConcurrentHashMap<AddressI, CommunicationCI>(); // a dedicated table to other access points
	private Map<AddressI, RouteInfo> routingTable = new ConcurrentHashMap<AddressI, RouteInfo>();
	private Map<AddressI, RoutingCI> neighborsROP = new ConcurrentHashMap<AddressI, RoutingCI>();
	
	public final String RotIP_URI = RoutingInboundPort.genURI();
	public String ComIP_URI = CommunicationInboundPort.generatePortURI();
	public static final String RegOP_URI = RegistrationOutboundPort.generatePortURI();
	private RoutingInboundPort rotip;
	private CommunicationInboundPort comip;
	private RegistrationOutboundPort regop;

	
	public static int count = 0;

	public static String genAddresse() {
		String s = "ANode " + count;
		count+=4;
		return s;
	}

	Random rand = new Random();

	private double range =1;
	private final NodeAddressI nodeAddress= new NodeAddress(AccessPoint.genAddresse()) ;
	//private final NetworkAddressI networkAddress= new NetworkAddress(AccessPoint.genAddresse()) ;
	//private Position pos = new Position(rand.nextInt(10), rand.nextInt(10));   // change this genPos
	private Position pos = new Position(count-4, 6); 
	boolean stopit = true;
	
	// pooling 
	protected static final String	POOL_URI = "computations pool" ;
	protected static final int		NTHREADS = 10 ;
	
	protected AccessPoint() {
		super(5, 0);
		
		
		try {
			
			this.rotip = new RoutingInboundPort(RotIP_URI, this);
			this.comip = new CommunicationInboundPort(ComIP_URI, this);
			this.regop = new RegistrationOutboundPort(RegOP_URI, this);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		try {
			this.rotip.publishPort();
			this.comip.publishPort();
			this.regop.publishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		this.toggleLogging();
		this.toggleTracing();
		
		try {
			this.createNewExecutorService(POOL_URI, NTHREADS, false) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		
		
		this.register();
		
		

		
		
		this.runTaskOnComponent(
				POOL_URI,
				new AbstractComponent.AbstractTask() {
					
					@Override
					public void run() {
						try {
							
							Thread.sleep(100L) ;
							catchUp();
						
						} catch (Exception e) {
							e.printStackTrace();
						}
						}});
		
		this.runTaskOnComponent(
				POOL_URI,
				new AbstractComponent.AbstractTask() {
					
					@Override
					public void run() {
						
						try {
							
							while(stopit) {
							Thread.sleep(100L) ;
							
							route();
							
							}
						
						} catch (Exception e) {
							e.printStackTrace();
						}
						}});
		
		
		this.runTaskOnComponent(
				POOL_URI,
				new AbstractComponent.AbstractTask() {
					
					@Override
					public void run() {
						try {
							
							
							while(stopit) {
							Thread.sleep(1000L) ;
							coucou();
							}
						
						} catch (Exception e) {
							e.printStackTrace();
						}
						}});
		
		
	
		
	}
	
	
	
	@Override
	public synchronized void finalise() throws Exception {
		stopit = false;
		
		System.out.println(nodeAddress.getAddress() + "--------------------------------------------------------");
		System.out.println("\n"+ this.pos);
		for (AddressI e : this.neighborsCOP.keySet()) {
			System.out.println(this.nodeAddress.getAddress() + " <-------> " + e.getAddress());
		}
		
		for (Entry<AddressI, RouteInfo> a : this.routingTable.entrySet()) {
			System.out.println(this.nodeAddress.getAddress() + ": " + a.getKey().getAddress() + " <==="
					+ a.getValue().getNumberOfHops() + "===> " + a.getValue().getDestination().getAddress());
		}
		System.out.println(nodeAddress.getAddress() + "--------------------------------------------------------");
		routingTable.clear();
		neighborsCOP.clear();
		neighborsROP.clear();
		for (CommunicationCI c : this.neighborsCOP.values()) {
			this.doPortDisconnection(((CommunicationOutboundPort) c).getPortURI());
		}
		for (RoutingCI r : this.neighborsROP.values()) {
			this.doPortDisconnection(((RoutingOutboundPort) r).getPortURI());
		}
		
		super.finalise();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		
		try {
			for (CommunicationCI c : this.neighborsCOP.values()) {
				((CommunicationOutboundPort) c).unpublishPort();
			}
			
			this.rotip.unpublishPort();
			this.comip.unpublishPort();
			this.regop.unpublishPort();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		routingTable.clear();
		neighborsCOP.clear();
		neighborsROP.clear();
		
		super.shutdown();
	}



	
	

	
	
	
	
	
	
	
	
	// --------------------------Registration------------------------------------------------------------------------
	
	
	public Set<ConnectionInfo> registerAPoint(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {
		return this.regop.registerAccessPoint(address, commIpUri, initialPosition, initialRange, routingIpUri);
	}
	
	
	
	public void unregister(NodeAddressI address) throws Exception {
		this.regop.unregister(address);
	}

	
	
	

	public void register() throws Exception {

		this.neighbors = this.registerAPoint(this.nodeAddress, this.ComIP_URI, this.pos, this.range, this.RotIP_URI);
		for (ConnectionInfo c : this.neighbors) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC, this);
			pc.publishPort();
			this.doPortConnection(uriTempC, c.getCommunicationInboundPortURI(),
					CommunicationConnector.class.getCanonicalName());// add connector here
			if(c.isAPoint()) {this.APCOP.put(c.getAddress(), pc);}
			this.neighborsCOP.put(c.getAddress(), pc);

			if (c.isRouting()) {
				String uriTempR = RoutingOutboundPort.generatePortURI();
				RoutingOutboundPort pr = new RoutingOutboundPort(uriTempR, this);
				pr.publishPort();
				this.doPortConnection(uriTempR, c.getRoutingInboundPortURI(),
						RoutingConnector.class.getCanonicalName());// add connector here
				this.neighborsROP.put(c.getAddress(), pr);
			}

			if (!c.getAddress().isequalsAddress(this.nodeAddress)) {
				this.routingTable.put(c.getAddress(), new RouteInfo(c.getAddress(), 1));
			}
		}

	}
	
	
	
	
	
	
	
	
	

	// --------------------------Connection------------------------------------------------------------------------
	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception {
		if (!this.neighborsCOP.containsKey(address)) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC, this);
			pc.publishPort();
			this.doPortConnection(uriTempC, communicationInboundPortURI,
					CommunicationConnector.class.getCanonicalName());// add connector here
			this.neighborsCOP.put(address, pc);

			
		}
	}
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI)
			throws Exception {

		if (!this.neighborsCOP.containsKey(address)) {

			
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC, this);
			this.neighborsCOP.put(address, pc);
			pc.publishPort();
			this.doPortConnection(uriTempC, communicationInboundPortURI,
					CommunicationConnector.class.getCanonicalName());

			if (!address.isequalsAddress(this.nodeAddress)) {
				this.routingTable.put(address, new RouteInfo(address, 1));
			}


		}

		if (!this.neighborsROP.containsKey(address)) {
			String uriTempR = RoutingOutboundPort.generatePortURI();
			RoutingOutboundPort pr = new RoutingOutboundPort(uriTempR, this);
			pr.publishPort();
			this.doPortConnection(uriTempR, routingInboundPortURI, RoutingConnector.class.getCanonicalName());
			this.neighborsROP.put(address, pr);

			if (!address.isequalsAddress(this.nodeAddress)) {
				this.routingTable.put(address, new RouteInfo(address, 1));
			}
		}
	}

	
	public void catchUp() throws Exception {

		for (ConnectionInfo c : this.neighbors) {
			if (c.isRouting()) {
				this.neighborsCOP.get(c.getAddress()).connectRouting(this.nodeAddress, this.ComIP_URI, this.RotIP_URI);
			} else {
				this.neighborsCOP.get(c.getAddress()).connect(this.nodeAddress, this.ComIP_URI);

			}
		}

	}
	
	
	
	
	@Override
	public void transmitMessage(MessageI m) throws Exception {


		if (m.getAddress().isequalsAddress(this.nodeAddress)) {
			this.logMessage(this.nodeAddress.getAddress() + " <--- " + m.getContent().getMessage());
		} else {
			if (m.stillAlive()) {

				if (this.routingTable.containsKey(m.getAddress())) {

					
					m.decrementHops();
					this.neighborsCOP.get(this.routingTable.get(m.getAddress()).getDestination()).transmitMessage(
							new Message(this.nodeAddress.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(),
									m.getAddress()));
				}

				else {
					
					m.decrementHops();
					for (Entry<AddressI, CommunicationCI> e : this.neighborsCOP.entrySet()) {
						e.getValue().transmitMessage(
								new Message(this.nodeAddress.getAddress() + " <--- " + m.getContent().getMessage(),
										m.getHops(), m.getAddress()));
					}
				}
			} else {
			}
		}

	}

	
	
	
	
	public void coucou() throws Exception {
		
		
			for(AddressI e: this.routingTable.keySet()) { 
				this.transmitMessage(new Message(nodeAddress.getAddress() , 15, e)); 
			}
		
			//this.transmitMessage(new Message(nodeAddress.getAddress() , 8, new NodeAddress("RNode 4"))); 
	}
	
	
	
	
	
	
	
	
	
	
	// --------------------------Routing------------------------------------------------------------------------
	
	
	
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfo> routes) throws Exception {
		for (RouteInfo r : routes) {
			if (this.routingTable.containsKey(r.getDestination())) {
				if (this.routingTable.get(r.getDestination()).getNumberOfHops() > r.getNumberOfHops()) {
					
					this.routingTable.put(r.getDestination(), new RouteInfo(neighbour, r.getNumberOfHops() + 1));
				}
			}

			else {
				if (!r.getDestination().isequalsAddress(this.nodeAddress)) {
					this.routingTable.put(r.getDestination(), new RouteInfo(neighbour, r.getNumberOfHops() + 1));

				}

			}
		}
		
	}
	
	public void route() throws Exception {

		Set<RouteInfo> routes = this.getRouteInfo();
		for (Entry<AddressI, RoutingCI> a : this.neighborsROP.entrySet()) {
			((RoutingOutboundPort) a.getValue()).updateRouting(this.nodeAddress, routes);
		}
	}
	

	public Set<RouteInfo> getRouteInfo() {

		Set<RouteInfo> routes = new HashSet<RouteInfo>();
		for (Entry<AddressI, RouteInfo> a : this.routingTable.entrySet()) {
			routes.add(new RouteInfo(a.getKey(), a.getValue().getNumberOfHops()));
		}
		return routes;
	}

	
	
	
	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		// TODO Auto-generated method stub
		
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





































































































































/*
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
import cps.connecteurs.NetworkAccessConnector;
import cps.connecteurs.NetworkCommunicationConnector;
import cps.connecteurs.RegistrationConnector;
import cps.connecteurs.RoutingConnector;
import cps.info.AccessInfo;
import cps.info.ConnectionInfo;
import cps.info.address.AddressI;
import cps.info.address.NetworkAddress;
import cps.info.address.NetworkAddressI;
import cps.info.address.NodeAddress;
import cps.info.address.NodeAddressI;
import cps.info.position.Position;
import cps.info.position.PositionI;
import cps.message.Message;
import cps.message.MessageI;
import cps.networkAccess.NetworkAccessingInboundPort;
import cps.networkAccess.NetworkAccessingOutboundPort;
import cps.networkAccess.NetworkAccessor;
import cps.networkAccess.NetworkAccessorCI;
import cps.networkCommunication.NetworkCommunicationCI;
import cps.networkCommunication.NetworkCommunicationInboundPort;
import cps.networkCommunication.NetworkCommunicationOutboundPort;
import cps.node.NodeI;
import cps.node.routing.RoutingNode;
import cps.registration.RegistrationCI;
import cps.registration.RegistrationOutboundPort;
import cps.registration.Registrator;
import cps.routing.RouteInfo;
import cps.routing.RoutingAccessingCI;
import cps.routing.RoutingCI;
import cps.routing.RoutingInboundPort;
import cps.routing.RoutingOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@RequiredInterfaces(required = { RoutingCI.class, CommunicationCI.class, RegistrationCI.class, NetworkAccessorCI.class, NetworkCommunicationCI.class })
@OfferedInterfaces(offered = { RoutingCI.class, CommunicationCI.class, NetworkAccessorCI.class, NetworkCommunicationCI.class })

public class AccessPoint extends AbstractComponent implements NodeI,RoutingAccessingCI{

	

	public final String RotIP_URI = RoutingInboundPort.generatePortURI();
	public final String ComIP_URI = CommunicationInboundPort.generatePortURI();
	public final static String RegOP_URI = RegistrationOutboundPort.generatePortURI();
	public final static String NaOP_URI = NetworkAccessingOutboundPort.generatePortURI();
	public final String NcIP_URI = NetworkCommunicationInboundPort.generatePortURI();
	public final static String NaIP_URI = NetworkAccessingInboundPort.generatePortURI();
	
	protected CommunicationInboundPort acip;
	protected RegistrationOutboundPort arop;
	private RoutingInboundPort arotip;
	private NetworkAccessingOutboundPort naop;
	private NetworkAccessingInboundPort naip;
	private NetworkCommunicationInboundPort ncip;

	public static int count = 0;
	public static String genAddresse() {
		String s = "ANode " + count;
		count ++;
		return s;
	}
	
	private final NodeAddressI address= new NodeAddress(AccessPoint.genAddresse()) ;
	
	private Set<AccessInfo> networks;
	private Map<AddressI, CommunicationCI> neighborsCOP = new HashMap<AddressI, CommunicationCI>();
	private Map<AddressI, RoutingCI> neighborsROP = new HashMap<AddressI, RoutingCI>();
	private Set<ConnectionInfo> neighbors; // new HashSet<ConnectionInfo>();
	private Map<AddressI, RouteInfo> routingTable = new HashMap<AddressI, RouteInfo>();

	private Map<NetworkAddressI, NetworkCommunicationCI> networkOP = new HashMap<>();


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
		this.naip = new NetworkAccessingInboundPort(NaIP_URI, this);
		this.naip.publishPort();
		this.doPortConnection(AccessPoint.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(AccessPoint.NaOP_URI , NetworkAccessor.NaIP_URI, NetworkAccessConnector.class.getCanonicalName());
		
		this.toggleLogging();
		this.toggleTracing();
		
		// this.ncop = new NetworkCommunicationOutboundPort(uri, this);
		// this.ncip = new NetworkCommunicationInboundPort(uri, this)
		// TODO Auto-generated constructor stub
	}
	
	
	
	

	@Override
	public synchronized void finalise() throws Exception {
		System.out.println(address.getAddress() + "--------------------------------------------------------");
		for(Entry<NetworkAddressI, NetworkCommunicationCI> e : networkOP.entrySet()) {
			System.out.println(e.getKey().getAddress() + "        connected network to ANode " + address.getAddress());
		}
		
		for(Entry<AddressI, CommunicationCI> e: this.neighborsCOP.entrySet()) {
			System.out.println(e.getKey().getAddress() + "\n");
		}
		
		
		for(AddressI e: this.neighborsCOP.keySet()) {
			System.out.println(this.address.getAddress() + " <=====> " + e.getAddress());
		}
		
		
		for(Entry<AddressI, RouteInfo> a: this.routingTable.entrySet()) {
			System.out.println(this.address.getAddress()+ ": " +a.getKey().getAddress() + " <===" + a.getValue().getNumberOfHops() +"===> " + a.getValue().getDestination().getAddress());
		}
		System.out.println(address.getAddress() + "--------------------------------------------------------");
		
		for(AddressI e: networkOP.keySet()) {
			System.out.println(this.address.getAddress() + "linked for " + e.getAddress());
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
		//this.connectNetwork();
		this.catchUp();
		this.route();
		
		
		
		
		
		
		
		
	
		for(AddressI e: this.routingTable.keySet()) {
			this.transmitMessage(new Message(address.getAddress() , 2, e));
		}
		spreadCo();
		for(AddressI e: networkOP.keySet()) {
			transmitMessage(new Message(address.getAddress(), 3, e));
		}
		

		
		
		
		for(AddressI e: this.neighborsCOP.keySet()) {
			this.logMessage(this.address.getAddress() + " <=====> " + e.getAddress());
		}
		
		//this.logMessage("end");
	}

	
	
	
	
	
	

	@Override
	public void connect(NodeAddressI naddress, String communicationInboundPortURI) throws Exception {
		// TODO Auto-generated method stub
		try {
			if (!neighborsCOP.containsKey(naddress)) {
				CommunicationOutboundPort tempPort = new CommunicationOutboundPort(
						CommunicationOutboundPort.generatePortURI(), this);
				tempPort.publishPort();
				neighborsCOP.put(naddress, tempPort);
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
		//this.logMessage("message passe par accesspoint " + address.getAddress());
		if (m.getAddress().isNetworkAddress()) {
			
			/*if(!networkOP.containsKey(m.getAddress())) {
				if(m.stillAlive()) {
					m.decrementHops();
					for(ConnectionInfo e : neighbors) {
						if(e.getKey().is) {}
				}
				
				}
				transmitMessage(m);
			}
			networkOP.get(m.getAddress()).transmitMessage((NetworkAddressI) m.getAddress(), m);
			//this.logMessage("Message transmis au réseau classique à l'adresse " + m.getAddress().getAddress());
			for (Entry<AddressI, RoutingCI> e : neighborsROP.entrySet()) {
				e.getValue().updateAccessPoint(address, 1);
			}
		} else {

			if (m.getAddress().isequalsAddress(this.address)) {
				this.logMessage(this.address.getAddress() + " <===== " + m.getContent().getMessage());
			} else {
				if (m.stillAlive()) {

					m.decrementHops();
					for (Entry<AddressI, CommunicationCI> e : neighborsCOP.entrySet()) {
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
/*
	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {

		return this.arop.registerAccessPoint(address, commIpUri, initialPosition, initialRange, routingIpUri);

	}

	public void register() throws Exception {

		this.neighbors = arop.registerAccessPoint(this.address, this.ComIP_URI, this.initialPosition, this.initialRange,
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
		route();

	}

	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI)
			throws Exception {
		spreadCo();
		//connectNetwork();
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
		spreadCo();
		for(ConnectionInfo c: this.neighbors) {
			if(c.isRouting()) {
				this.neighborsCOP.get(c.getAddress()).connectRouting(this.address, this.ComIP_URI, this.RotIP_URI);
				//this.logMessage("me "+ this.address.getAddress()+" is connecting to "+c.getAddress().getAddress());
			}else {
				this.neighborsCOP.get(c.getAddress()).connect(this.address, this.ComIP_URI);
				
			}
		}
		
	}
	
	
	
	public void route() throws Exception {
		spreadCo();
		Set<RouteInfo> routes = this.getRouteInfo();
		//this.logMessage("updating routing");
		for(Entry<AddressI, RoutingCI> a: this.neighborsROP.entrySet()) {
			((RoutingOutboundPort)a.getValue()).updateRouting(this.address, routes);
		}
	}
	
	public Set<RouteInfo> getRouteInfo() throws Exception{
		spreadCo();
		Set<RouteInfo> routes = new HashSet<RouteInfo>();
		for(Entry<AddressI, RouteInfo> a: this.routingTable.entrySet()) {
			routes.add(new RouteInfo(a.getKey(), a.getValue().getNumberOfHops()));
		}
		return routes;
	}
	
	public void spreadCo() throws Exception {
		
		networks = naop.getNetworkNodes(NaIP_URI);
		
		
		String tempUri;
		int i = 0;
		for(AccessInfo e : networks) {
			
			
			tempUri = e.getAccessInboundPortURI();
			NetworkCommunicationOutboundPort ni = new NetworkCommunicationOutboundPort(NetworkCommunicationOutboundPort.generatePortURI(), this);
			networkOP.put(e.getAddress(), ni);
			ni.publishPort();
			this.doPortConnection(ni.getPortURI(), tempUri, NetworkCommunicationConnector.class.getCanonicalName());
			//System.out.println(" ----------------------   " + tempUri + "      /////    " + ni.getPortURI());
			i++;
		}
		/*
		for(AddressI e: this.routingTable.keySet()) {
			this.transmitMessage(new Message(address.getAddress() , 2, e));
		}
		for(AddressI e: networkOP.keySet()) {
			transmitMessage(new Message(address.getAddress(), 3, e));
		}
		transmitMessage(new Message(address.getAddress(), 3, new NetworkAddress("CNode 1")));
		transmitMessage(new Message(address.getAddress(), 3, new NetworkAddress("CNode 0")));
		
		
		
	}

	
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfo> routes) throws Exception{
		
		for(RouteInfo r: routes) {
			if(this.routingTable.containsKey(r.getDestination())) {
				if(this.routingTable.get(r.getDestination()).getNumberOfHops()>r.getNumberOfHops()) {
					this.routingTable.put(r.getDestination(), new RouteInfo(neighbour, r.getNumberOfHops()+1));
				} 
			}
			
			else {
				if(! r.getDestination().isequalsAddress(this.address)){
					this.routingTable.put(r.getDestination(), new RouteInfo(neighbour, r.getNumberOfHops()+1));
					
				}
				
			}
		}
		spreadCo();
		//this.route();
	/*	for(AddressI e: this.routingTable.keySet()) {
			this.transmitMessage(new Message(address.getAddress() , 2, e));
		}
		for(AddressI e: networkOP.keySet()) {
			transmitMessage(new Message(address.getAddress(), 3, e));
		}
		
		
	}
	
	
	

}
*/
