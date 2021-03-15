package cps.classicNetwork;

import cps.info.address.NetworkAddress;
import cps.info.address.NetworkAddressI;
import cps.info.address.NodeAddress;
import cps.info.address.NodeAddressI;
import cps.message.MessageI;
import cps.networkAccess.NetworkAccessingOutboundPort;
import cps.networkCommunication.NetworkCommunicationInboundPort;
import cps.node.routing.RoutingNode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import cps.networkAccess.*;


@OfferedInterfaces(offered = {NetworkAccessorCI.class})
@RequiredInterfaces(required = {NetworkAccessorCI.class})
public class ClassicNetwork extends AbstractComponent {


	public static final String NAOP_URI = NetworkAccessingOutboundPort.generatePortURI();
	public String NCIP_URI = NetworkCommunicationInboundPort.generatePortURI();

	protected NetworkCommunicationInboundPort ncip;
	protected NetworkAccessingOutboundPort naop;

	public static int count = 5;
	public static String genAddresse() {
		String s = "CNode " + count;
		count ++;
		return s;
	}
	
	private final NetworkAddressI naddress= new NetworkAddress(genAddresse()) ;
	
	protected ClassicNetwork() throws Exception {
		super(1, 0);
		this.ncip = new NetworkCommunicationInboundPort(NCIP_URI, this);
		this.ncip.publishPort();
		this.naop = new NetworkAccessingOutboundPort(NAOP_URI, this);
		this.naop.publishPort();
		
		this.toggleLogging();
		this.toggleTracing();
	}

	protected ClassicNetwork(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}

	public void registerAccessPoint() throws Exception {
		naop.registerAccessPoint(naddress, NCIP_URI);
	}

	public void receiveMessage(MessageI m) {
		this.logMessage("network received message :    " + m.getContent().getMessage());
	}
	
	
	public synchronized void execute() throws Exception{
		super.execute();
		naop.registerAccessPoint(naddress, NCIP_URI);
		naop.spreadCo();
		
	}
	
	
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(NAOP_URI);	
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.naop.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
