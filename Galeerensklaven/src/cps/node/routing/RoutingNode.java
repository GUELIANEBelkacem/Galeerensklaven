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
import cps.connecteurs.RegistrationConnector;
import cps.info.ConnectionInfo;
import cps.info.address.AddressI;
import cps.info.address.NodeAddress;
import cps.info.address.NodeAddressI;
import cps.info.position.Position;
import cps.info.position.PositionI;
import cps.message.Message;
import cps.message.MessageI;
import cps.node.NodeI;
import cps.registration.RegistrationCI;
import cps.registration.RegistrationInboundPort;
import cps.registration.RegistrationOutboundPort;
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
		
	/*testing uri generation methodes*/
	public final String RotIP_URI =     RoutingInboundPort.genURI();
	public final String ComIP_URI = 	CommunicationOutboundPort.generatePortURI();
	public final static String RegOP_URI =     RegistrationOutboundPort.generatePortURI();
	private RoutingInboundPort rotip;
	private CommunicationInboundPort comip;
	private RegistrationOutboundPort regop;
	
	public static int count = 0;
	public static String genAddresse() {
		String s = "RNode " + count;
		count ++;
		return s;
	}
	Random rand = new Random();
	private double range = 2000;
	private final NodeAddressI address= new NodeAddress(RoutingNode.genAddresse()) ;
	private Position pos = new Position(rand.nextInt(50), rand.nextInt(50));   // change this genPos
	private ConnectionInfo conInfo = new ConnectionInfo(this.address, ComIP_URI, RotIP_URI, true, pos, false);

	@Override
	public synchronized void finalise() throws Exception {
		for(CommunicationCI c: this.neighborsCOP.values()) {
			this.doPortDisconnection(((CommunicationOutboundPort)c).getPortURI());
		}
		for(RoutingCI r: this.neighborsROP.values()) {
			this.doPortDisconnection(((RoutingOutboundPort)r).getPortURI());
		}
		this.doPortDisconnection(RotIP_URI);
		this.doPortDisconnection(ComIP_URI);
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

	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		
		this.register();
		
		for(ConnectionInfo c: this.neighbors) {
			if(c.isRouting()) {
				this.neighborsCOP.get(c.getAddress()).connectRouting(this.address, this.ComIP_URI, this.RotIP_URI);
				Thread.sleep(3000L);
			}else {
				this.neighborsCOP.get(c.getAddress()).connect(this.address, this.ComIP_URI);
				Thread.sleep(1000L);
			}
		}
		
		for(AddressI e: this.neighborsCOP.keySet()) {
			this.transmitMessage(new Message(address.getAddress() , 10, e));
		}
	}
	/*
	public void hasRouteFor(AddressI address);
	public void ping();
	*/
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
		}
	}


	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception {
		if(! this.neighborsCOP.containsKey(address)) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC,this);
			pc.publishPort();
			this.doPortConnection(uriTempC, communicationInboundPortURI, CommunicationConnector.class.getCanonicalName());//add connector here 
			this.neighborsCOP.put(address, pc);
			
		}
		
		if(! this.neighborsROP.containsKey(address)) {
			String uriTempR = RoutingOutboundPort.generatePortURI();
			RoutingOutboundPort pr = new RoutingOutboundPort(uriTempR,this);
			pr.publishPort();
			this.doPortConnection(uriTempR, routingInboundPortURI, RegistrationConnector.class.getCanonicalName());//add connector here 
			this.neighborsROP.put(address, pr);
		}
	}
	
	// registrator
	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception{
		
		return this.regop.registerRoutingNode(address, commIpUri, initialPosition,initialRange, routingIpUri);
		
		

	}
	public void unregister(NodeAddressI address) throws Exception{
		this.regop.unregister(address);
	}
	
	
	public void register() throws Exception {
	
		this.neighbors = this.registerRoutingNode(this.address, this.ComIP_URI, this.pos, this.range, this.RotIP_URI);
		for(ConnectionInfo c : this.neighbors) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC,this);
			pc.publishPort();
			this.doPortConnection(uriTempC, c.getCommunicationInboundPortURI(), CommunicationConnector.class.getCanonicalName());//add connector here 
			this.neighborsCOP.put(c.getAddress(), pc);
			
			String uriTempR = RoutingOutboundPort.generatePortURI();
			RoutingOutboundPort pr = new RoutingOutboundPort(uriTempR,this);
			pr.publishPort();
			this.doPortConnection(uriTempR, c.getCommunicationInboundPortURI(), RegistrationConnector.class.getCanonicalName());//add connector here 
			this.neighborsROP.put(c.getAddress(), pr);
		}
		
	}
	
	


	
	

}
