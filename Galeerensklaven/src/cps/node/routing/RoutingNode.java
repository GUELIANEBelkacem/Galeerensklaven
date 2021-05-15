package cps.node.routing;

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
@OfferedInterfaces(offered = { RoutingCI.class, CommunicationCI.class })
public class RoutingNode extends AbstractComponent implements NodeI, RoutingI{
	//a currentHashMap is thread safe without synchronizing the whole map. better than locking the entire map, to be verified with the prof
	// mutex for 2 modifications at the same time
	private Map<AddressI, CommunicationCI> neighborsCOP = new ConcurrentHashMap<AddressI, CommunicationCI>();
	private Map<AddressI, RoutingCI> neighborsROP = new ConcurrentHashMap<AddressI, RoutingCI>();
	private Set<ConnectionInfo> neighbors;
	private Map<AddressI, RouteInfo> routingTable = new ConcurrentHashMap<AddressI, RouteInfo>();
	private AddressI bestAProute = null;
	private int jumpsToAP = -1;
	Random rand = new Random();
	public final String RotIP_URI = RoutingInboundPort.generatePortURI();
	public final String ComIP_URI = CommunicationInboundPort.generatePortURI();
	public  final String RegOP_URI = "Rnode"+rand.nextInt(10); //dPort.generatePortURI();
	private RoutingInboundPort rotip;
	private CommunicationInboundPort comip;
	//private RegistrationOutboundPort regop;

	public static int count = 0;


	private int t=count;
	
	public static String genAddresse() {
		String s = "RNode " + count;
		count++;
		return s;
	}

	
	
	private double range =1.6;
	private final NodeAddressI address= new NodeAddress(RoutingNode.genAddresse()) ;
	//private Position pos = new Position(rand.nextInt(10), rand.nextInt(10));   // change this genPos
	private Position pos;// = new Position(count-1, 5); 

	// closest access point 
	int apDistance = 9999999;
	NodeAddressI apGateway = this.address;
	
	
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
	boolean stopit = true;
	
	//plugin
	protected final static String	RPLUGIN = "tplugin";
	RoutingNodePlugin plugin = new RoutingNodePlugin(RegOP_URI);
	
	protected RoutingNode(Position p) {
		super(2, 0);
		System.out.println(this.RegOP_URI);
		this.pos=p;
		try {
			
			this.rotip = new RoutingInboundPort(RotIP_URI, this);
			this.comip = new CommunicationInboundPort(ComIP_URI, this);
			//this.regop = new RegistrationOutboundPort(RegOP_URI, this);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		try {
			this.rotip.publishPort();
			this.comip.publishPort();
			//this.regop.publishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		this.toggleLogging();
		this.toggleTracing();
		
		try {
			this.createNewExecutorService(OUT_POOL_URI, no, false) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.createNewExecutorService(IN_POOL_URI, ni, false) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.createNewExecutorService(MESSAGE_POOL_URI, nm, false) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.hashMapLock = new ReentrantReadWriteLock() ;
		
		plugin.setPluginURI(RPLUGIN);
		try {
			this.installPlugin(plugin);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
	
	
	
	
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		this.logMessage("jefjelfejl");
		this.register();
		
		

		
		
		this.runTaskOnComponent(
				OUT_POOL_URI,
				new AbstractComponent.AbstractTask() {
					
					@Override
					public void run() {
						try {
							register();
							Thread.sleep(100L) ;
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
							register();
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
							
							register();
							while(stopit) {
							Thread.sleep(1000L) ;
							//checkDisconnection();
							coucou();
							}
						
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
							
							if(t==3) {
							Thread.sleep(3000L) ;

								disconnect();
							
							
							}
						
						} catch (Exception e) {
							e.printStackTrace();
						}
						}});
		
	
		
		
	
		
	}
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public synchronized void finalise() throws Exception {
		stopit = false;
		
		
		
		System.out.println(address.getAddress() + "--------------------------------------------------------");
		System.out.println("the closest access point: "+ this.apGateway.getAddress());
		System.out.println("\n"+ this.pos);
		for (AddressI e : this.neighborsCOP.keySet()) {
			System.out.println(this.address.getAddress() + " <-------> " + e.getAddress());
		}
		for (AddressI e : this.missingNodes) {
			System.out.println(this.address.getAddress() + " <xxxxxxx> " + e.getAddress());
		}
		
		
		for (Entry<AddressI, RouteInfo> a : this.rentrySet()) {
			System.out.println(this.address.getAddress() + ": " + a.getKey().getAddress() + " <==="
					+ a.getValue().getNumberOfHops() + "===> " + a.getValue().getDestination().getAddress());
		}
		System.out.println(address.getAddress() + "--------------------------------------------------------");
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
			for (RoutingCI r : this.neighborsROP.values()) {
				((RoutingOutboundPort) r).unpublishPort();
			}
			this.rotip.unpublishPort();
			this.comip.unpublishPort();
			//this.regop.unpublishPort();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		routingTable.clear();
		neighborsCOP.clear();
		neighborsROP.clear();
		super.shutdown();
	}



	
	

	
	
	
	
	
	
	
	
	
	
	
	
	

	// --------------------------Connection------------------------------------------------------------------------
	
	// this part is verified it works 100% of the time 
	
	public void catchUp() throws Exception {

		for (ConnectionInfo c : this.neighbors) {
			if (c.isRouting()) {
				this.ncget(c.getAddress()).connectRouting(this.address, this.ComIP_URI, this.RotIP_URI);
			} else {
				this.ncget(c.getAddress()).connect(this.address, this.ComIP_URI);

			}
		}

	}

	public void coucou() throws Exception {
		for(Entry<AddressI, RouteInfo> a : this.rentrySet()) { 
			try {
				this.transmitMessage(new Message(address.getAddress() , 15, a.getKey()));
			}catch(Exception e) {
				this.logMessage("failed to send to " + a.getKey().getAddress());
			}
		}
	}
	public void transmitMessage(MessageI m) throws Exception {
		// classical network
				if(m.getAddress().isNetworkAddress()) {
					
					if(m.stillAlive()) {
						
							m.decrementHops();
							try {
							this.ncget(this.apGateway).transmitMessage(new Message(this.address.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(),m.getAddress()));
							}catch(Exception e) {
						this.logMessage("failed to send to " + m.getAddress().getAddress());
					}
						
						
					}
				}
				// normal network 
				else {

		if (m.getAddress().isequalsAddress(this.address)) {
			this.logMessage(this.address.getAddress() + " <--- " + m.getContent().getMessage());
		} else {
			if (m.stillAlive()) {

				if (this.rcontainsKey(m.getAddress())) {

					
					m.decrementHops();
					try {
					this.ncget(this.rget(m.getAddress()).getDestination()).transmitMessage(
							new Message(this.address.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(),
									m.getAddress()));
				}catch(Exception e) {
					this.logMessage("failed to send to " + m.getAddress().getAddress());
				}
				}

				else {

					m.decrementHops();
					for (Entry<AddressI, CommunicationCI> e : this.ncentrySet()) {
						try {
						e.getValue().transmitMessage(
								new Message(this.address.getAddress() + " <--- " + m.getContent().getMessage(),
										m.getHops(), m.getAddress()));
						}catch(Exception ee) {
							this.logMessage("failed to send to " + m.getAddress().getAddress());
						}
						}
				}
			} else {
			}
			
		}
				}
	}

	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception {
		if (!this.nccontainsKey(address)) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC, this);
			pc.publishPort();
			this.doPortConnection(uriTempC, communicationInboundPortURI,
					CommunicationConnector.class.getCanonicalName());// add connector here
			this.ncput(address, pc);

			if (!address.isequalsAddress(this.address)) {
				this.rput(address, new RouteInfo(address, 1));
			}
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

			if (!address.isequalsAddress(this.address)) {
				this.rput(address, new RouteInfo(address, 1));
			}


		}

		if (!this.nrcontainsKey(address)) {
			String uriTempR = RoutingOutboundPort.generatePortURI();
			RoutingOutboundPort pr = new RoutingOutboundPort(uriTempR, this);
			pr.publishPort();
			this.doPortConnection(uriTempR, routingInboundPortURI, RoutingConnector.class.getCanonicalName());
			this.nrput(address, pr);

			if (!address.isequalsAddress(this.address)) {
				this.rput(address, new RouteInfo(address, 1));
			}
		}
	}


	
	
	
	public void disconnect() throws Exception {
		this.unregister(address);
		stopit = false;
		this.logMessage("disconnecting......");
		routingTable.clear();
		neighborsCOP.clear();
		neighborsROP.clear();
		try {
		//this.shutdownExecutorService(MESSAGE_POOL_URI);
		this.shutdownNowExecutorService(IN_POOL_URI);
		this.shutdownExecutorService(OUT_POOL_URI);
		this.shutdown();
		this.shutdownNow();
		
		}catch(Exception e) {
			
		}
		
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
					
					((RoutingOutboundPort) a.getValue()).updateRouting(this.address, routes);
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
	

	// --------------------------Registration------------------------------------------------------------------------
	
	// this part is verified it works 100% of the time 
	
	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {

		return this.plugin.registerRoutingNode(address, commIpUri, initialPosition, initialRange, routingIpUri);

	}
	
	
	
	
	public void unregister(NodeAddressI address) throws Exception {
		this.plugin.unregister(address);
	}

	
	
	public void register() throws Exception {
		System.out.println("hey1");
		Set<ConnectionInfo> bbb =this.registerRoutingNode(this.address, this.ComIP_URI, this.pos, this.range, this.RotIP_URI);
		System.out.println("hey2");
		System.out.println(bbb);
		this.neighbors = bbb;
		
		for (ConnectionInfo c : this.neighbors) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC, this);
			pc.publishPort();
			this.doPortConnection(uriTempC, c.getCommunicationInboundPortURI(),
					CommunicationConnector.class.getCanonicalName());// add connector here
			this.ncput(c.getAddress(), pc);

			if (c.isRouting()) {
				String uriTempR = RoutingOutboundPort.generatePortURI();
				RoutingOutboundPort pr = new RoutingOutboundPort(uriTempR, this);
				pr.publishPort();
				this.doPortConnection(uriTempR, c.getRoutingInboundPortURI(),
						RoutingConnector.class.getCanonicalName());// add connector here
				this.nrput(c.getAddress(), pr);
			}

			if (!c.getAddress().isequalsAddress(this.address)) {
				this.rput(c.getAddress(), new RouteInfo(c.getAddress(), 1));
			}
		}

	}

	
	
	
	
	
	
	
	
	
	// --------------------------Routing------------------------------------------------------------------------
	
	// this part is verified it works 100% of the time 
	
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
							((RoutingOutboundPort) a.getValue()).updateRouting(this.address, mis);
						}
					}
					hashMapLock.writeLock().unlock();
				}
			}
			else {
			if (this.rcontainsKey(r.getDestination())) {
				if (this.rget(r.getDestination()).getNumberOfHops() > r.getNumberOfHops()) {
					this.rput(r.getDestination(), new RouteInfo(neighbour, r.getNumberOfHops() + 1));// mutex teeeeeeest
				}
			}

			else {
				/*
				boolean missing = false;
				for(AddressI aa: missingNodes) {
					if(aa.isequalsAddress(r.getDestination())) {
						missing = true;
					}
				}
				*/
				if (!r.getDestination().isequalsAddress(this.address) ) {
					
					this.rput(r.getDestination(), new RouteInfo(neighbour, r.getNumberOfHops() + 1));
					
				}

			}
		}
		}
	}
	
	public void route() throws Exception {

		Set<RouteInfo> routes = this.getRouteInfo();
		for (Entry<AddressI, RoutingCI> a : this.nrentrySet()) {
			((RoutingOutboundPort) a.getValue()).updateRouting(this.address, routes);
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
		if(!neighbour.isequalsAddress(this.address) && numberOfHops < this.apDistance && this.nccontainsKey(neighbour)) {
			this.apDistance = numberOfHops;
			this.apGateway = neighbour;
			for(Entry<AddressI, RoutingCI> a : this.nrentrySet()) {
				a.getValue().updateAccessPoint(this.address, numberOfHops+1);
			}
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	// --------------------------garbage code------------------------------------------------------------------------
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public int hasRouteFor(AddressI address) {
		if (this.rcontainsKey(address)) {
			return this.rget(address).getNumberOfHops();
		} else {
			return -1;
		}
	}

	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public AddressI getBestAProute() {
		return bestAProute;
	}

	public int getJumpsToAP() {
		return jumpsToAP;
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	
	if (m.getAddress().isNetworkAddress()) {
		this.logMessage(this.address.getAddress() + " <--- " + m.getContent().getMessage());
		if (bestAProute == null) {
			for (Entry<AddressI, CommunicationCI> e : this.neighborsCOP.entrySet()) {
				e.getValue().transmitMessage(
						new Message(this.address.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(),
								m.getAddress()));
			}
		} else {
			m.decrementHops();
			neighborsCOP.get(bestAProute)
					.transmitMessage(new Message(this.address.getAddress() + " <--- " + m.getContent().getMessage(),
							m.getHops(), m.getAddress()));
		}

	}
	
	
	*/
	
	
	
	
	/// REACTIVERRRRRRRRRRRRRRRRRRRRRRRRRRRR

			/*
			 * for(AddressI e: this.routingTable.keySet()) { this.transmitMessage(new
			 * Message(address.getAddress() , 2, e)); }
			 */

			// transmitMessage(new Message(address.getAddress(), 50, new
			// NetworkAddress("CNode 1")));
			// transmitMessage(new Message(address.getAddress(), 50, new
			// NetworkAddress("CNode 0")));
			/*
			 * for(AddressI e: this.neighborsCOP.keySet()) {
			 * this.logMessage(this.address.getAddress() + " <=====> " + e.getAddress()); }
			 */
			// this.logMessage("end");
	
	
	
	
	
	
	/*
	 * public void transmitMessage(MessageI m) throws Exception {
	 * if(m.getAddress().isequalsAddress(this.address)) {
	 * this.logMessage(this.address.getAddress() + " <--- " +
	 * m.getContent().getMessage()); } else { if(m.stillAlive()) {
	 * m.decrementHops(); for(Entry<AddressI,CommunicationCI> e :
	 * this.neighborsCOP.entrySet()){ e.getValue().transmitMessage(m); } }else {
	 * //this.logMessage("message expired"); } }
	 * 
	 * }
	 */
	
	
	
	
	
	
	
	
	// this is garbage cheat code
	// ---------------------------------------------------------------
	/*
	 * this.route(); this.route(); this.route(); this.route(); this.route(); for(int
	 * i = 0; i<1000000; i++) {} this.route(); this.transmitMessage(new
	 * Message(this.address.getAddress() , 2 , address));
	 */

	// -------------------------------------------------------------------------------------------

	
	
	
	
	// this.route();

			// REACTIVERRRRRRRRRRR

			/*
			 * for(AddressI e: this.routingTable.keySet()) { this.transmitMessage(new
			 * Message(address.getAddress() , 2, e)); }
			 */
			// transmitMessage(new Message(address.getAddress(), 50, new
			// NetworkAddress("CNode 1")));
			// transmitMessage(new Message(address.getAddress(), 50, new
			// NetworkAddress("CNode 0")));
	
	
	
	
	
	/*
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		if (jumpsToAP == -1 || numberOfHops < jumpsToAP) {
			jumpsToAP = numberOfHops;
			bestAProute = neighbour;
			for (Entry<AddressI, RoutingCI> e : neighborsROP.entrySet()) {
				e.getValue().updateAccessPoint(address, jumpsToAP + 1);
			}
		}
		// transmitMessage(new Message(address.getAddress(), 3, new
		// NetworkAddress("CNode 1")));
		// transmitMessage(new Message(address.getAddress(), 3, new
		// NetworkAddress("CNode 0")));
	}
	*/
}
