package cps.node.accesspoint;

import java.rmi.ConnectException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cps.communication.CommunicationCI;
import cps.communication.CommunicationInboundPort;
import cps.communication.CommunicationOutboundPort;
import cps.connecteurs.CommunicationConnector;
import cps.connecteurs.RoutingConnector;
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
import cps.node.NodeI;
import cps.node.RoutingI;
import cps.node.classicnode.registration.NConnectionInfo;
import cps.routing.RouteInfo;
import cps.routing.RoutingCI;
import cps.routing.RoutingInboundPort;
import cps.routing.RoutingOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;



@RequiredInterfaces(required = { RoutingCI.class, CommunicationCI.class})
@OfferedInterfaces(offered = { RoutingCI.class, CommunicationCI.class})
public class AccessPoint extends AbstractComponent implements NodeI, RoutingI{
	private Map<AddressI, CommunicationCI> neighborsCOP = new ConcurrentHashMap<AddressI, CommunicationCI>();
	private Set<ConnectionInfo> neighbors;
	private Map<AddressI, CommunicationCI> APCOP = new ConcurrentHashMap<AddressI, CommunicationCI>(); // a dedicated table for other access points
	private Map<AddressI, CommunicationCI> CNCOP = new ConcurrentHashMap<AddressI, CommunicationCI>(); // a dedicated table for other classic nodes
	private Map<AddressI, RouteInfo> routingTable = new ConcurrentHashMap<AddressI, RouteInfo>();
	private Map<AddressI, RoutingCI> neighborsROP = new ConcurrentHashMap<AddressI, RoutingCI>();
	
	protected final String RotIP_URI = RoutingInboundPort.generatePortURI();
	protected final  String ComIP_URI = CommunicationInboundPort.generatePortURI();
	private RoutingInboundPort rotip;
	private CommunicationInboundPort comip;
	


	public int subNet;
	public int id;
	

	Random rand = new Random();
	
	private double range =1.6;
	
	
	
	private NodeAddressI nodeAddress;
	private NetworkAddressI networkAddress;
	//private Position pos = new Position(rand.nextInt(10), rand.nextInt(10));   // change this genPos
	private Position pos; //= new Position(count-step, 6); 
	boolean stopit = true;
	
	// pooling 
	protected static final String	IN_POOL_URI = "inpooluri" ;
	protected static final int		ni = 6 ;
	
	protected static final String	OUT_POOL_URI = "outpooluri" ;
	protected static final int		no = 4 ;
	
	protected static final String	MESSAGE_POOL_URI = "messagepooluri" ;
	protected static final int		nm = 2 ;
	
	
	//mutex
	protected final ReentrantReadWriteLock		hashMapLock ;
	
	
	//disconnection
	private Set<AddressI> missingNodes = new HashSet<AddressI>();
	
	//plugin
	protected final static String	APLUGIN = "aplugin";
	AccessPointPlugin plugin = new AccessPointPlugin();
		
		

	protected AccessPoint(int id, Position p) {
		
		super(5, 0);
		
		this.id=id;
		this.subNet=id%2;
		this.nodeAddress= new NodeAddress("ANode "+id) ;
		this.networkAddress= new NetworkAddress("NANode "+id) ;
		
		this.pos = p;
		try {
			
			this.rotip = new RoutingInboundPort(RotIP_URI, this);
			this.comip = new CommunicationInboundPort(ComIP_URI, this);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		try {
			this.rotip.publishPort();
			this.comip.publishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		this.toggleLogging();
		this.toggleTracing();
		
		try {
			this.createNewExecutorService(IN_POOL_URI, ni, false) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.createNewExecutorService(OUT_POOL_URI, no, false) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.createNewExecutorService(MESSAGE_POOL_URI, nm, false) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.hashMapLock = new ReentrantReadWriteLock() ;
		
		plugin.setPluginURI(APLUGIN);
		try {
			this.installPlugin(plugin);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	
	
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		
		
		this.register();
		
		

		
		
		this.runTaskOnComponent(
				OUT_POOL_URI,
				new AbstractComponent.AbstractTask() {
					
					@Override
					public void run() {
						try {
							
							Thread.sleep(1000L) ;
							catchUp();
						
						} catch (Exception e) {
							e.printStackTrace();
						}
						}});
		
		this.runTaskOnComponent(
				OUT_POOL_URI,
				new AbstractComponent.AbstractTask() {
					
					@Override
					public void run() {
						
						try {
							Thread.sleep(3000L) ;
							while(stopit) {
							Thread.sleep(100L) ;
							checkDisconnection();
							route();
							
							}
						
						} catch (Exception e) {
							e.printStackTrace();
						}
						}});
		
		
		this.runTaskOnComponent(
				MESSAGE_POOL_URI,
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
		System.out.println("connected classical nodes:");
		for (AddressI e : this.CNCOP.keySet()) {
			System.out.println(this.nodeAddress.getAddress() + " <________> " + e.getAddress());
		}
		System.out.println("neighbours:");
		for (AddressI e : this.neighborsCOP.keySet()) {
			System.out.println(this.nodeAddress.getAddress() + " <-------> " + e.getAddress());
		}
		System.out.println("disconnected nodes:");
		for (AddressI e : this.missingNodes) {
			System.out.println(this.nodeAddress.getAddress() + " <xxxxxxx> " + e.getAddress());
		}
		System.out.println("routing table:");
		for (Entry<AddressI, RouteInfo> a : this.rentrySet()) {
			System.out.println(this.nodeAddress.getAddress() + ": " + a.getKey().getAddress() + " <=== hops: "
					+ a.getValue().getNumberOfHops() + "===> gateWay: " + a.getValue().getDestination().getAddress());
		}
		System.out.println(nodeAddress.getAddress() + "--------------------------------------------------------\n");
		
		for (CommunicationCI c : this.neighborsCOP.values()) {
			this.doPortDisconnection(((CommunicationOutboundPort) c).getPortURI());
			((CommunicationOutboundPort) c).unpublishPort();
		}
		for (RoutingCI r : this.neighborsROP.values()) {
			this.doPortDisconnection(((RoutingOutboundPort) r).getPortURI());
			((RoutingOutboundPort) r).unpublishPort();
		}
		for (CommunicationCI c : this.APCOP.values()) {
			this.doPortDisconnection(((CommunicationOutboundPort) c).getPortURI());
			((CommunicationOutboundPort) c).unpublishPort();
		}
		for (CommunicationCI c : this.CNCOP.values()) {
			this.doPortDisconnection(((CommunicationOutboundPort) c).getPortURI());
			((CommunicationOutboundPort) c).unpublishPort();
		}
		routingTable.clear();
		neighborsCOP.clear();
		neighborsROP.clear();
		this.APCOP.clear();
		CNCOP.clear();
		super.finalise();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		
		try {
			this.rotip.unpublishPort();
			this.comip.unpublishPort();
			this.rotip.destroyPort();
			this.comip.destroyPort();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		super.shutdown();
		
	}



	
	

	
	
	
	
	
	
	
	
	// --------------------------Registration------------------------------------------------------------------------
	
	
	public Set<ConnectionInfo> registerAPoint(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {
		return this.plugin.registerAPoint(address, commIpUri, initialPosition, initialRange, routingIpUri);
	}
	
	public Set<NConnectionInfo> registerAccessPoint(NetworkAddressI address, String commIpUri, int i) throws Exception{
		
		return this.plugin.registerAccessPoint(address, commIpUri, i);
		
	}
	
	public void unregister(NodeAddressI address) throws Exception {
		this.plugin.unregister(address);
		
	}
	public void unregister(NetworkAddressI address) throws Exception {
		this.plugin.unregister(address);
		
	}

	
	
	

	public void register() throws Exception {
		hashMapLock.writeLock().lock();
		try {
		this.neighbors = this.registerAPoint(this.nodeAddress, this.ComIP_URI, this.pos, this.range, this.RotIP_URI);
		
		Set<NConnectionInfo> cnei = this.registerAccessPoint(networkAddress, ComIP_URI, subNet);
		
		for (ConnectionInfo c : this.neighbors) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC, this);
			pc.publishPort();
			this.doPortConnection(uriTempC, c.getCommunicationInboundPortURI(),
					CommunicationConnector.class.getCanonicalName());
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
		
		
		
		
		
		
		
		
		// classical network
		for (NConnectionInfo c : cnei) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC, this);
			pc.publishPort();
			this.doPortConnection(uriTempC, c.getCommunicationInboundPortURI(),CommunicationConnector.class.getCanonicalName());// add connector here
			this.CNCOP.put(c.getAddress(), pc);
		}
	}finally {
		hashMapLock.writeLock().unlock();
	}

	}
	
	
	
	
	
	
	
	
	

	// --------------------------Connection------------------------------------------------------------------------
	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception {
	
		if (!this.nccontainsKey(address)) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC, this);
			pc.publishPort();
			this.doPortConnection(uriTempC, communicationInboundPortURI,
					CommunicationConnector.class.getCanonicalName());// add connector here
			this.ncput(address, pc);

			
		}
	}
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI)
			throws Exception {
		
		if (!this.nccontainsKey(address)) {

			
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC, this);
			this.ncput(address, pc);
			pc.publishPort();
			this.doPortConnection(uriTempC, communicationInboundPortURI,
					CommunicationConnector.class.getCanonicalName());

			if (!address.isequalsAddress(this.nodeAddress)) {
				this.rput(address, new RouteInfo(address, 1));
			}


		}

		if (!this.nrcontainsKey(address)) {
			String uriTempR = RoutingOutboundPort.generatePortURI();
			RoutingOutboundPort pr = new RoutingOutboundPort(uriTempR, this);
			pr.publishPort();
			this.doPortConnection(uriTempR, routingInboundPortURI, RoutingConnector.class.getCanonicalName());
			this.nrput(address, pr);

			if (!address.isequalsAddress(this.nodeAddress)) {
				this.rput(address, new RouteInfo(address, 1));
			}
		}
	}

	
	public void catchUp() throws Exception {
		
		for (ConnectionInfo c : this.neighbors) {
			if (c.isRouting()) {
				this.ncget(c.getAddress()).connectRouting(this.nodeAddress, this.ComIP_URI, this.RotIP_URI);
			} else {
				this.ncget(c.getAddress()).connect(this.nodeAddress, this.ComIP_URI);

			}
		}

	}
	
	
	
	
	@Override
	public void transmitMessage(MessageI m) throws Exception {
		// classical network
		if(m.getAddress().isNetworkAddress()) {
			if (m.getAddress().isequalsAddress(this.networkAddress)) {
				this.logMessage(this.nodeAddress.getAddress() + " <--- " + m.getContent().getMessage());
			}
			else {
			if(m.stillAlive()) {
				if(CNCOP.containsKey(m.getAddress())) {
					m.decrementHops();
					this.CNCOP.get(m.getAddress()).transmitMessage(
							new Message(this.networkAddress.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(),
									m.getAddress()));
				}
				else {
					for (Entry<AddressI, CommunicationCI> e : this.APCOP.entrySet()) {
						e.getValue().transmitMessage(
								new Message(this.networkAddress.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(),
										m.getAddress()));
					}
					for (Entry<AddressI, CommunicationCI> e : this.CNCOP.entrySet()) {
						e.getValue().transmitMessage(
								new Message(this.networkAddress.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(),
										m.getAddress()));
					}
				}
			}
		}}
		// normal network 
		else {


		if (m.getAddress().isequalsAddress(this.nodeAddress)) {
			this.logMessage(this.nodeAddress.getAddress() + " <--- " + m.getContent().getMessage());
		} else {
			if (m.stillAlive()) {

				if (this.rcontainsKey(m.getAddress())) {

					
					m.decrementHops();
					this.ncget(this.rget(m.getAddress()).getDestination()).transmitMessage(
							new Message(this.nodeAddress.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(),
									m.getAddress()));
				}

				else {
					
					m.decrementHops();
					for (Entry<AddressI, CommunicationCI> e : this.ncentrySet()) {
						e.getValue().transmitMessage(
								new Message(this.nodeAddress.getAddress() + " <--- " + m.getContent().getMessage(),
										m.getHops(), m.getAddress()));
					}
				}
			} else {
			}
		}
		}
	}

	
	
	
	
	public void coucou() throws Exception {
		
		
			for(Entry<AddressI, RouteInfo> a : this.rentrySet()) { 
				this.transmitMessage(new Message(nodeAddress.getAddress() , 15, a.getKey())); 
			}
		
			this.transmitMessage(new Message(nodeAddress.getAddress() , 20, new NetworkAddress("CNode 3"))); 
			this.transmitMessage(new Message(nodeAddress.getAddress() , 20, new NetworkAddress("CNode 1"))); 
			this.transmitMessage(new Message(nodeAddress.getAddress() , 20, new NetworkAddress("CNode 2"))); 
	}
	
	
	
	
	
	
	public void checkDisconnection() throws Exception {
		try {
			this.pingNeighbours();
			

		} catch (ConnectException ce) {
			
			hashMapLock.writeLock().lock();
			NodeAddress tempAddr = new NodeAddress(ce.getMessage());
			
			this.logMessage("node "+tempAddr.getAddress()+" disconnected (neighbour)");
			missingNodes.add(tempAddr);
			
			Set<RouteInfo> routes = new HashSet<RouteInfo>();
			routes.add(new RouteInfo(tempAddr, -1));
			
			for (Entry<AddressI, RoutingCI> a : this.neighborsROP.entrySet()) {
				if(!a.getKey().isequalsAddress(tempAddr)) {
					
					((RoutingOutboundPort) a.getValue()).updateRouting(this.nodeAddress, routes);
				}
			}
			
			for (Entry<AddressI, CommunicationCI> a : this.neighborsCOP.entrySet()) {
				if (a.getKey().isequalsAddress(tempAddr)) {
					this.neighborsCOP.remove(a.getKey());
				}
			}
			for (Entry<AddressI, RoutingCI> a : this.neighborsROP.entrySet()) {
				if (a.getKey().isequalsAddress(tempAddr)) {
					this.neighborsROP.remove(a.getKey());
				}
			}
			for (Entry<AddressI, RouteInfo> a : this.routingTable.entrySet()) {
				if (a.getKey().isequalsAddress(tempAddr) || a.getValue().getDestination().isequalsAddress(tempAddr)) {
					this.routingTable.remove(a.getKey());
				}
			}
			hashMapLock.writeLock().unlock();
			
		} 
		
	}
	
	public void pingNeighbours() throws ConnectException {
		for (Entry<AddressI, CommunicationCI> a : this.ncentrySet()) {
			try {
				
				a.getValue().ping();
			} catch (Exception e) {
		
				

				throw new java.rmi.ConnectException(a.getKey().getAddress());
			}
		}
	}
	
	public void ping() throws Exception {
		

	}
	
	
	
	// --------------------------Routing------------------------------------------------------------------------
	
	
	
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfo> routes) throws Exception {
		
		for (RouteInfo r : routes) {
			if(r.getNumberOfHops()==-1) {
				this.logMessage("node "+r.getDestination().getAddress()+" disconnected");
				missingNodes.add(r.getDestination());
				boolean f = false;
				for (Entry<AddressI, RouteInfo> a : this.routingTable.entrySet()) {
					
					if (a.getKey().isequalsAddress(r.getDestination()) || a.getValue().getDestination().isequalsAddress(r.getDestination())) {
						f=true;
						this.routingTable.remove(a.getKey());
					}
				}
				
				if(f) {
					hashMapLock.writeLock().lock();
					
					Set<RouteInfo> mis = new HashSet<RouteInfo>();
					mis.add(new RouteInfo(r.getDestination(), -1));
					
					for (Entry<AddressI, RoutingCI> a : this.neighborsROP.entrySet()) {
						if(!a.getKey().isequalsAddress(r.getDestination())) {
							((RoutingOutboundPort) a.getValue()).updateRouting(this.nodeAddress, mis);
						}
					}
					hashMapLock.writeLock().unlock();
				}
			}
			else {
			if (this.rcontainsKey(r.getDestination())) {
				if (this.rget(r.getDestination()).getNumberOfHops() > r.getNumberOfHops()) {
					
					this.rput(r.getDestination(), new RouteInfo(neighbour, r.getNumberOfHops() + 1));
				}
			}

			else {
				if (!r.getDestination().isequalsAddress(this.nodeAddress)) {
					this.rput(r.getDestination(), new RouteInfo(neighbour, r.getNumberOfHops() + 1));

				}

			}
		}}
	}
	
	public void route() throws Exception {

		Set<RouteInfo> routes = this.getRouteInfo();
		for (Entry<AddressI, RoutingCI> a : this.nrentrySet()) {
			//normal network
			((RoutingOutboundPort) a.getValue()).updateRouting(this.nodeAddress, routes);
			//classical network
			((RoutingOutboundPort) a.getValue()).updateAccessPoint(this.nodeAddress, 1);;
		}
	}
	

	public Set<RouteInfo> getRouteInfo() {

		Set<RouteInfo> routes = new HashSet<RouteInfo>();
		for (Entry<AddressI, RouteInfo> a : this.rentrySet()) {
			routes.add(new RouteInfo(a.getKey(), a.getValue().getNumberOfHops()));
		}
		return routes;
	}

	
	
	
	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		for(Entry<AddressI, RoutingCI> a : this.nrentrySet()) {
			a.getValue().updateAccessPoint(this.nodeAddress, numberOfHops+1);
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}





	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// maps
	//routing table 
	public RouteInfo rput(AddressI key, RouteInfo value) 
	{
		RouteInfo res ;
		this.hashMapLock.writeLock().lock() ;
		try {
			res = this.routingTable.put(key, value) ;
		} finally {
			this.hashMapLock.writeLock().unlock() ;
		}
		return res ;
	}
	
	public RouteInfo rget(AddressI key) 
	{
		RouteInfo res ;
		this.hashMapLock.readLock().lock() ;
		try {
			res = this.routingTable.get(key) ;
		} finally {
			this.hashMapLock.readLock().unlock() ;
		}
		return res ;
	}
	
	public boolean rcontainsKey(AddressI key) 
	{
		boolean res ;
		this.hashMapLock.readLock().lock() ;
		try {
			res = this.routingTable.containsKey(key) ;
		} finally {
			this.hashMapLock.readLock().unlock() ;
		}
		return res ;
	}
	public Set<Entry<AddressI, RouteInfo>> rentrySet(){
		Set<Entry<AddressI, RouteInfo>> res ;
		this.hashMapLock.readLock().lock() ;
		try {
			res = this.routingTable.entrySet() ;
		} finally {
			this.hashMapLock.readLock().unlock() ;
		}
		return res ;
	}
	
	
	
	
	//neighborsCOP
	public CommunicationCI ncput(AddressI key, CommunicationCI value) 
	{
		CommunicationCI res ;
		this.hashMapLock.writeLock().lock() ;
		try {
			res = this.neighborsCOP.put(key, value) ;
		} finally {
			this.hashMapLock.writeLock().unlock() ;
		}
		return res ;
	}
	
	public CommunicationCI ncget(AddressI key) 
	{
		CommunicationCI res ;
		this.hashMapLock.readLock().lock() ;
		try {
			res = this.neighborsCOP.get(key) ;
		} finally {
			this.hashMapLock.readLock().unlock() ;
		}
		return res ;
	}
	
	public boolean nccontainsKey(AddressI key) 
	{
		boolean res ;
		this.hashMapLock.readLock().lock() ;
		try {
			res = this.neighborsCOP.containsKey(key) ;
		} finally {
			this.hashMapLock.readLock().unlock() ;
		}
		return res ;
	}
	
	public Set<Entry<AddressI, CommunicationCI>> ncentrySet(){
		Set<Entry<AddressI, CommunicationCI>> res ;
		this.hashMapLock.readLock().lock() ;
		try {
			res = this.neighborsCOP.entrySet() ;
		} finally {
			this.hashMapLock.readLock().unlock() ;
		}
		return res ;
	}
	
	
	
	
	
	
	//neighborsROP
	public RoutingCI nrput(AddressI key, RoutingCI value) 
	{
		RoutingCI res ;
		this.hashMapLock.writeLock().lock() ;
		try {
			res = this.neighborsROP.put(key, value) ;
		} finally {
			this.hashMapLock.writeLock().unlock() ;
		}
		return res ;
	}
	
	public RoutingCI nrget(AddressI key) 
	{
		RoutingCI res ;
		this.hashMapLock.readLock().lock() ;
		try {
			res = this.neighborsROP.get(key) ;
		} finally {
			this.hashMapLock.readLock().unlock() ;
		}
		return res ;
	}
	
	public boolean nrcontainsKey(AddressI key) 
	{
		boolean res ;
		this.hashMapLock.readLock().lock() ;
		try {
			res = this.neighborsROP.containsKey(key) ;
		} finally {
			this.hashMapLock.readLock().unlock() ;
		}
		return res ;
	}
	
	public Set<Entry<AddressI, RoutingCI>> nrentrySet(){
		Set<Entry<AddressI, RoutingCI>> res ;
		this.hashMapLock.readLock().lock() ;
		try {
			res = this.neighborsROP.entrySet() ;
		} finally {
			this.hashMapLock.readLock().unlock() ;
		}
		return res ;
	}
	
	
	


	
	
	
	
	
}



