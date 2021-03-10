package cps.node.routing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import cps.communication.CommunicationCI;
import cps.communication.CommunicationInboundPort;
import cps.info.ConnectionInfo;
import cps.info.address.AddressI;
import cps.info.address.NodeAddress;
import cps.info.address.NodeAddressI;
import cps.info.position.Position;
import cps.info.position.PositionI;
import cps.message.MessageI;
import cps.node.NodeI;
import cps.registration.RegistrationCI;
import cps.registration.RegistrationInboundPort;
import cps.routing.RoutingCI;
import cps.routing.RoutingInboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@RequiredInterfaces(required = {RoutingCI.class, CommunicationCI.class, RegistrationCI.class})
@OfferedInterfaces(offered = {RoutingCI.class, CommunicationCI.class, RegistrationCI.class})
public class RoutingNode extends AbstractComponent implements NodeI{
	
	private Map<AddressI,CommunicationCI> neighborsAOP = new HashMap<AddressI,CommunicationCI>();
	private Map<AddressI,RoutingCI> neighborsROP = new HashMap<AddressI,RoutingCI>();
	private Set<ConnectionInfo> neighbors = new HashSet<ConnectionInfo>();		
		
	
	public static final String RotIP_URI = RoutingInboundPort.genURI();
	public static final String ComIP_URI = 	CommunicationInboundPort.genURI();
	public static final String RegIP_URI = "reg_ip_uri";
	private RoutingInboundPort rotip;
	private CommunicationInboundPort comip;
	private RegistrationInboundPort regip;
	
	public static int count = 0;
	public static String genAddresse() {
		String s = "RNode " + count;
		count ++;
		return s;
	}
	Random rand = new Random();
	private final NodeAddressI address= new NodeAddress(RoutingNode.genAddresse()) ;
	private Position pos = new Position(rand.nextInt(50), rand.nextInt(50));   // change this genPos
	private ConnectionInfo conInfo = new ConnectionInfo(this.address, ComIP_URI, RotIP_URI, true, pos, false);

	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(RotIP_URI);
		this.doPortDisconnection(ComIP_URI);
		this.doPortDisconnection(RegIP_URI);
		super.finalise();
	}
	

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		
		try {
			this.rotip.unpublishPort();
			this.comip.unpublishPort();
			this.regip.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		super.shutdown();
	}


	protected RoutingNode() {
		super(1, 0);
		try {
			rotip = new RoutingInboundPort(RotIP_URI, this);
			comip = new CommunicationInboundPort(ComIP_URI, this);
			regip = new RegistrationInboundPort(RegIP_URI, this);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			this.rotip.publishPort();
			this.comip.publishPort();
			this.regip.publishPort();
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
	}
	/*
	public void connect(NodeAddressI address, String communicationInboundPortURI);
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI);
	public void transmitMessage(MessageI m);
	public void hasRouteFor(AddressI address);
	public void ping();
	*/
	public void transmitMessage(MessageI m) {
		if(m.getAddress().isequalsAddress(this.address)) {
			this.traceMessage(this.address.getAddress() + "recieves message" + m.getContent().getMessage());
		}
		else {
			if(m.stillAlive()) {
				m.decrementHops();
				for(Entry<AddressI,CommunicationCI> e : this.neighborsAOP.entrySet()){
					e.getValue().transmitMessage(m);
				}
			}
		}
		
	}
	
	/*
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange) throws Exception;

	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception;

	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception;

	public void unregister(NodeAddressI address) throws Exception;
	*/
	
	
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String commIpUri, PositionI initialPosition, double initialRange) throws Exception{
		
		
		return null;
	}

	
	

}
