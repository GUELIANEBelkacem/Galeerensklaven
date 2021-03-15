package cps.node.routing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import cps.communication.CommunicationCI;
import cps.communication.CommunicationInboundPort;
import cps.communication.CommunicationOutboundPort;
import cps.connecteurs.CommunicationConnector;
import cps.connecteurs.RoutingConnector;
import cps.info.ConnectionInfo;
import cps.info.address.AddressI;
import cps.info.address.NetworkAddressI;
import cps.info.address.NodeAddress;
import cps.info.address.NodeAddressI;
import cps.info.position.Position;
import cps.info.position.PositionI;
import cps.message.Message;
import cps.message.MessageI;
import cps.node.NodeI;
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

@RequiredInterfaces(required = {RoutingCI.class, CommunicationCI.class, RegistrationCI.class})
@OfferedInterfaces(offered = {RoutingCI.class, CommunicationCI.class, RegistrationCI.class})
public class RoutingNode extends AbstractComponent implements NodeI{
	
	private Map<AddressI,CommunicationCI> neighborsCOP = new HashMap<AddressI,CommunicationCI>();
	private Map<AddressI,RoutingCI> neighborsROP = new HashMap<AddressI,RoutingCI>();
	private Set<ConnectionInfo> neighbors; //new HashSet<ConnectionInfo>();
	private Map<AddressI,RouteInfo> routingTable = new HashMap<AddressI,RouteInfo>();
	private AddressI bestAProute = null;
	private int jumpsToAP = -1;
		
	/*testing uri generation methodes*/
	public final String RotIP_URI =     RoutingInboundPort.genURI();
	public final String ComIP_URI = 	CommunicationOutboundPort.generatePortURI();
	public static final String RegOP_URI =     RegistrationOutboundPort.generatePortURI();
	private RoutingInboundPort rotip;
	private CommunicationInboundPort comip;
	private RegistrationOutboundPort regop;
	
	private boolean check = false;
	public static int count = 0;
	public static String genAddresse() {
		String s = "RNode " + count;
		count ++;
		return s;
	}
	Random rand = new Random();
	private double range = 10000000;
	private final NodeAddressI address= new NodeAddress(RoutingNode.genAddresse()) ;
	private Position pos = new Position(rand.nextInt(50), rand.nextInt(50));   // change this genPos
	private ConnectionInfo conInfo = new ConnectionInfo(this.address, ComIP_URI, RotIP_URI, true, pos, false);

	@Override
	public synchronized void finalise() throws Exception {

		
		for(AddressI e: this.neighborsCOP.keySet()) {
			System.out.println(this.address.getAddress() + " <=====> " + e.getAddress());
		}
		
		
		for(Entry<AddressI, RouteInfo> a: this.routingTable.entrySet()) {
			System.out.println(this.address.getAddress()+ ": " +a.getKey().getAddress() + " <===" + a.getValue().getNumberOfHops() +"===> " + a.getValue().getDestination().getAddress());
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
			for(CommunicationCI c: this.neighborsCOP.values()) {
				((CommunicationOutboundPort)c).unpublishPort();
			}
			for(RoutingCI r: this.neighborsROP.values()) {
				((RoutingOutboundPort)r).unpublishPort();
			}
			this.rotip.unpublishPort();
			this.comip.unpublishPort();
			this.regop.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		super.shutdown();
	}


	protected RoutingNode() {
		super(1, 0);
		try {
			this.rotip = new RoutingInboundPort(RotIP_URI, this);
			this.comip = new CommunicationInboundPort(ComIP_URI, this);
			this.regop = new RegistrationOutboundPort(RegOP_URI, this);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			this.rotip.publishPort();
			this.comip.publishPort();
			this.regop.publishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.toggleLogging();
		this.toggleTracing();
		
	}
	
	
	public AddressI getBestAProute() {
		return bestAProute;
	}
	
	
	public int getJumpsToAP() {
		return jumpsToAP;
	}

	@Override
	public synchronized void execute() throws Exception {
		super.execute();

		
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

	
	
	
	
	
	
	//--------------------------Connection------------------------------------------------------------------------
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
		
	}
	/*
	public void transmitMessage(MessageI m) throws Exception {
		if(m.getAddress().isequalsAddress(this.address)) {
			this.logMessage(this.address.getAddress() + " <--- " + m.getContent().getMessage());
		}
		else {
			if(m.stillAlive()) {
				m.decrementHops();
				for(Entry<AddressI,CommunicationCI> e : this.neighborsCOP.entrySet()){
					e.getValue().transmitMessage(m);
				}
			}else {
				//this.logMessage("message expired");
			}
		}
		
	}
	*/
	
	public void transmitMessage(MessageI m) throws Exception {
		if (m.getAddress().isNetworkAddress()) {
			this.logMessage("message au réseau classique " + m.getContent().getMessage() + " passe par " + address.getAddress());
			if(bestAProute == null) {
				for(Entry<AddressI,CommunicationCI> e : this.neighborsCOP.entrySet()){
					e.getValue().transmitMessage(new Message(this.address.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(), m.getAddress()));
				}
			}else {
				m.decrementHops();
				neighborsCOP.get(bestAProute).transmitMessage(new Message(this.address.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(), m.getAddress() ));
			}
		
		}
		
		
		if(m.getAddress().isequalsAddress(this.address)) {
			this.logMessage(this.address.getAddress() + " <--- " + m.getContent().getMessage());
		}
		else {
			if(m.stillAlive()) {
				
				if(this.routingTable.containsKey(m.getAddress())) {
					
					//this.logMessage("message routed");
					m.decrementHops();
					this.neighborsCOP.get(this.routingTable.get(m.getAddress()).getDestination()).transmitMessage(new Message(this.address.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(), m.getAddress()));
					//---------------------------------------------------------------
					//do <--- trick
					//---------------------------------------------------------------
				}
				
				else {
						
						m.decrementHops();
						for(Entry<AddressI,CommunicationCI> e : this.neighborsCOP.entrySet()){
							e.getValue().transmitMessage(new Message(this.address.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(), m.getAddress()));
						}
					}
			}else {
						//this.logMessage("message expired");
			}
		}
		
	}
	
	
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception {
		if(! this.neighborsCOP.containsKey(address)) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC,this);
			pc.publishPort();
			this.doPortConnection(uriTempC, communicationInboundPortURI, CommunicationConnector.class.getCanonicalName());//add connector here 
			this.neighborsCOP.put(address, pc);
			
			if(!address.isequalsAddress(this.address)) {
			this.routingTable.put(address, new RouteInfo(address,1));
			}
		}
	}


	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception {
	
		if(! this.neighborsCOP.containsKey(address)) {
			
			//this.logMessage(address.getAddress()+" sent its address to me "+this.address.getAddress());
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC,this);
			this.neighborsCOP.put(address, pc);
			pc.publishPort();
			this.doPortConnection(uriTempC, communicationInboundPortURI, CommunicationConnector.class.getCanonicalName());//add connector here 
			
			
			
			
			if(!address.isequalsAddress(this.address)) {
			this.routingTable.put(address, new RouteInfo(address,1));
			}
			
			//this is garbage cheat code ---------------------------------------------------------------
			/*
			this.route();
			this.route();
			this.route();
			this.route();
			this.route();
			for(int i = 0; i<1000000; i++) {}
			this.route();
			this.transmitMessage(new Message(this.address.getAddress() , 2 , address));
			*/
			
			//-------------------------------------------------------------------------------------------
			
			
		}
		
		if(! this.neighborsROP.containsKey(address)) {
			String uriTempR = RoutingOutboundPort.generatePortURI();
			RoutingOutboundPort pr = new RoutingOutboundPort(uriTempR,this);
			pr.publishPort();
			this.doPortConnection(uriTempR, routingInboundPortURI, RoutingConnector.class.getCanonicalName());
			this.neighborsROP.put(address, pr);
			
			
			
			
			
			
			
			if(!address.isequalsAddress(this.address)) {
			this.routingTable.put(address, new RouteInfo(address,1));
			}
		}
	}
	
	public int hasRouteFor(AddressI address) {
		if(this.routingTable.containsKey(address)) {
			return this.routingTable.get(address).getNumberOfHops();
		}else {
			return -1;
		}
	}
	
	public void ping() throws Exception {
		for(Entry<AddressI, CommunicationCI> a: this.neighborsCOP.entrySet()) {
			try {
				((CommunicationOutboundPort)a).ping();
			}
			catch(Exception e){
				throw new java.rmi.ConnectException(a.getKey().getAddress()+" cant be found");
			}
	}


	}
	
	
	
	
	
	
	
	//--------------------------Registration------------------------------------------------------------------------
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception{
		
		return this.regop.registerAccessPoint(address, commIpUri, initialPosition, initialRange, routingIpUri);
		
		

	}
	public void unregister(NodeAddressI address) throws Exception{
		this.regop.unregister(address);
	}
	
	
	public void register() throws Exception {
	
		this.neighbors = this.registerAccessPoint(this.address, this.ComIP_URI, this.pos, this.range, this.RotIP_URI);
		for(ConnectionInfo c : this.neighbors) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC,this);
			pc.publishPort();
			this.doPortConnection(uriTempC, c.getCommunicationInboundPortURI(), CommunicationConnector.class.getCanonicalName());//add connector here 
			this.neighborsCOP.put(c.getAddress(), pc);
			
			if(c.isRouting()) {
			String uriTempR = RoutingOutboundPort.generatePortURI();
			RoutingOutboundPort pr = new RoutingOutboundPort(uriTempR,this);
			pr.publishPort();
			this.doPortConnection(uriTempR, c.getRoutingInboundPortURI(), RoutingConnector.class.getCanonicalName());//add connector here 
			this.neighborsROP.put(c.getAddress(), pr);
			}
			
			
			
			
			if(!c.getAddress().isequalsAddress(this.address)) {
				this.routingTable.put(c.getAddress(), new RouteInfo(c.getAddress(),1));
			}
		}
		
	}
	
	

	
	
	
	
	
	
	
	//--------------------------Routing------------------------------------------------------------------------
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfo> routes) throws Exception{
		System.out.println("blabla");
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
		//this.route();
		for(AddressI e: this.routingTable.keySet()) {
			this.transmitMessage(new Message(address.getAddress() , 2, e));
		}
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


	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception{
		if(jumpsToAP == -1 || numberOfHops < jumpsToAP) {
			jumpsToAP = numberOfHops;
			bestAProute = neighbour;
			for (Entry<AddressI, RoutingCI> e : neighborsROP.entrySet()) {
				e.getValue().updateAccessPoint(address, jumpsToAP + 1);
			}
		}
		
	}
	
	



	
}
