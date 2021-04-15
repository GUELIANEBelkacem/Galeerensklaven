package cps.node.terminal;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cps.communication.CommunicationCI;
import cps.communication.CommunicationInboundPort;
import cps.communication.CommunicationOutboundPort;
import cps.connecteurs.CommunicationConnector;
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
import cps.registration.RegistrationCI;
import cps.registration.RegistrationOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;



@RequiredInterfaces(required = {  CommunicationCI.class, RegistrationCI.class })
@OfferedInterfaces(offered = {  CommunicationCI.class, RegistrationCI.class })


public class TerminalNode extends AbstractComponent implements NodeI{
	//a currentHashMap is thread safe without synchronizing the whole map. better than locking the entire map, to be verified with the prof
	private Map<AddressI, CommunicationCI> neighborsCOP = new ConcurrentHashMap<AddressI, CommunicationCI>();
	private Set<ConnectionInfo> neighbors;
		
		
	public String ComIP_URI = CommunicationInboundPort.generatePortURI();
	public static final String RegOP_URI = RegistrationOutboundPort.generatePortURI();
	private CommunicationInboundPort comip;
	private RegistrationOutboundPort regop;
	
	public static int count = 0;

	public static String genAddresse() {
		String s = "TNode " + count;
		count++;
		return s;
	}

	Random rand = new Random();

	private double range =1;
	private final NodeAddressI address= new NodeAddress(TerminalNode.genAddresse()) ;
	//private Position pos = new Position(rand.nextInt(10), rand.nextInt(10));   // change this genPos
	private Position pos = new Position(count-1, 4); 
	
	// pooling 
	protected static final String	POOL_URI = "computations pool" ;
	protected static final int		NTHREADS = 10 ;
		
	protected TerminalNode() {
		super(2, 0);
		
		
		try {
			
			this.comip = new CommunicationInboundPort(ComIP_URI, this);
			this.regop = new RegistrationOutboundPort(RegOP_URI, this);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		try {
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
							
							//Thread.sleep(1000L) ;
							int i = 0;
							while(i<10000) {
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
		
		System.out.println(address.getAddress() + "--------------------------------------------------------");
		System.out.println("\n"+ this.pos);
		for (AddressI e : this.neighborsCOP.keySet()) {
			System.out.println(this.address.getAddress() + " <-------> " + e.getAddress());
		}
		
		for (CommunicationCI c : this.neighborsCOP.values()) {
			this.doPortDisconnection(((CommunicationOutboundPort) c).getPortURI());
		}
		super.finalise();
	}
	
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {

		try {
			for (CommunicationCI c : this.neighborsCOP.values()) {
				((CommunicationOutboundPort) c).unpublishPort();
			}
			
			
			this.comip.unpublishPort();
			this.regop.unpublishPort();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		neighborsCOP.clear();
		super.shutdown();
	}

	// --------------------------Registration------------------------------------------------------------------------

	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange) throws Exception{
		return this.regop.registerTerminalNode(address, commIpUri, initialPosition, initialRange);
	}
	public void unregister(NodeAddressI address) throws Exception {
		this.regop.unregister(address);
	}

	
	
	public void register() throws Exception {

		this.neighbors = this.registerTerminalNode(this.address, this.ComIP_URI, this.pos, this.range);
		for (ConnectionInfo c : this.neighbors) {
			String uriTempC = CommunicationOutboundPort.generatePortURI();
			CommunicationOutboundPort pc = new CommunicationOutboundPort(uriTempC, this);
			pc.publishPort();
			this.doPortConnection(uriTempC, c.getCommunicationInboundPortURI(),
					CommunicationConnector.class.getCanonicalName());// add connector here
			this.neighborsCOP.put(c.getAddress(), pc);

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
	
	
	public void catchUp() throws Exception {
		for (ConnectionInfo c : this.neighbors) {
			this.neighborsCOP.get(c.getAddress()).connect(this.address, this.ComIP_URI);
		}
	}
	
	
	
	
	@Override
	public void transmitMessage(MessageI m) throws Exception {


		if (m.getAddress().isequalsAddress(this.address)) {
			this.logMessage(this.address.getAddress() + " <--- " + m.getContent().getMessage());
		}
		

	}
	
	
	
	public void sendMessage(MessageI m) throws Exception {
		if (this.neighborsCOP.containsKey(m.getAddress())) {
			m.decrementHops();
			this.neighborsCOP.get(m.getAddress()).transmitMessage(
					new Message(this.address.getAddress() + " <--- " + m.getContent().getMessage(), m.getHops(),
							m.getAddress()));
		}
		else {
			m.decrementHops();
			for (Entry<AddressI, CommunicationCI> e : this.neighborsCOP.entrySet()) {
				e.getValue().transmitMessage(
						new Message(this.address.getAddress() + " <--- " + m.getContent().getMessage(),
								m.getHops(), m.getAddress()));
			}
		}
	}
	public void coucou() throws Exception {
		
		//this.sendMessage(new Message(address.getAddress() , 4, new NetworkAddress("RNode 3"))); 
		
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
import cps.info.address.NetworkAddress;
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
			this.transmitMessage(new Message( "envoi au réseau classique de " + address.getAddress(), 10, new NetworkAddress("CNode 0")));
			
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
*/
