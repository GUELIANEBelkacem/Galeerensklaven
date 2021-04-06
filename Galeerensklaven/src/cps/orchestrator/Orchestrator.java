package cps.orchestrator;

import cps.classicNetwork.ClassicNetwork;
import cps.communication.CommunicationCI;
import cps.communication.CommunicationInboundPort;
import cps.communication.CommunicationOutboundPort;
import cps.connecteurs.CommunicationConnector;
import cps.connecteurs.NetworkAccessConnector;
import cps.connecteurs.RegistrationConnector;
import cps.info.address.NetworkAddress;
import cps.message.Message;
import cps.networkAccess.NetworkAccessor;
import cps.networkCommunication.NetworkCommunicationCI;
import cps.networkCommunication.NetworkCommunicationOutboundPort;
import cps.node.accesspoint.AccessPoint;
import cps.node.routing.RoutingNode;
import cps.node.terminal.TerminalNode;
import cps.registration.Registrator;
import cps.routing.RoutingCI;
import cps.routing.RoutingOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;


@RequiredInterfaces(required = {CommunicationCI.class})
public class Orchestrator extends AbstractComponent {
	
	private CommunicationOutboundPort comop1;
	private CommunicationOutboundPort comop2;
	
	
	String[] rot = new String[1000];
	String[] ter = new String[1000];
	String[] ap = new String[1000];

	protected Orchestrator() throws Exception{
		super(1, 0);
		
		
		comop1 = new CommunicationOutboundPort("orch1", this);
		comop2 = new CommunicationOutboundPort("orch2", this);
		// TODO Auto-generated constructor stub
		
	}
/*
	public Orchestrator(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}
*/
	
	
	public synchronized void execute() throws Exception{
		super.execute();
		String[] ter = new String[1000];

		
		AbstractComponent.createComponent(Registrator.class.getCanonicalName(), new Object[] {});
		AbstractComponent.createComponent(NetworkAccessor.class.getCanonicalName(), new Object[] {});
		//CommunicationInboundPort comip = new CommunicationInboundPort("comip_uri", this)
		
		
		
		// -------------------------------------------------- Routing Node
		
		String rn1CIP_URI = CommunicationInboundPort.generatePortURI();
		String rn2CIP_URI = CommunicationInboundPort.generatePortURI();
		String rn1 = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {rn1CIP_URI});
		String rn2 = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {rn2CIP_URI});
		

		
		doPortConnection("orch1", rn1CIP_URI, CommunicationConnector.class.getCanonicalName());
		doPortConnection("orch2", rn2CIP_URI, CommunicationConnector.class.getCanonicalName());
		
		
		
		/*
		this.doPortConnection(RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		*/
		
		
		
		for(int i=0; i<2;i++) {
			ap[i] = AbstractComponent.createComponent(AccessPoint.class.getCanonicalName(), new Object[] {});
			
		}
		
		
		for(int i=0; i<2;i++) {
			ap[i] = AbstractComponent.createComponent(ClassicNetwork.class.getCanonicalName(), new Object[] {});
			
		}
		
		comop1.transmitMessage(new Message("premier message", 5, new NetworkAddress("CNode 0")));
		/*
		for(int i=0; i<4;i++) {
			ter[i] = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {});
			this.doPortConnection(TerminalNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		}
		
		*/
	/*	
	 * 
	 * 
		
		// -------------------------------------------------- Access Point
		
		String ap1CIP_URI = CommunicationInboundPort.generatePortURI();
		String ap2CIP_URI = CommunicationInboundPort.generatePortURI();
		String ap1 = AbstractComponent.createComponent(AccessPoint.class.getCanonicalName(), new Object[] {ap1CIP_URI});
		String ap2 = AbstractComponent.createComponent(AccessPoint.class.getCanonicalName(), new Object[] {ap2CIP_URI});
		
		
		this.doPortConnection(ap1,AccessPoint.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(ap2,AccessPoint.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		
		// -------------------------------------------------- Terminal Node
		
		String nt1CIP_URI = CommunicationInboundPort.generatePortURI();
		String nt2CIP_URI = CommunicationInboundPort.generatePortURI();
		String nt1 = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {ap1CIP_URI});
		String nt2 = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {ap2CIP_URI});
		
		
		this.doPortConnection(nt1,AccessPoint.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(nt2,AccessPoint.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		
		
		// -------------------------------------------------- Classic Network Component
		
		
		for(int i=0; i<2;i++) {
			ter[i] = AbstractComponent.createComponent(ClassicNetwork.class.getCanonicalName(), new Object[] {});
			this.doPortConnection(ter[i],ClassicNetwork.NAOP_URI , NetworkAccessor.NaIP_URI, NetworkAccessConnector.class.getCanonicalName());
		}
		
		*/
		
	}
	
	/*
	@Override
	public synchronized void finalise() throws Exception {
		
		this.doPortDisconnection(((CommunicationOutboundPort) comop1).getPortURI());
		this.doPortDisconnection(((CommunicationOutboundPort) comop2).getPortURI());
		super.finalise();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		
		try {
			
			
			((CommunicationOutboundPort) comop1).unpublishPort();
			comop2.unpublishPort();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		super.shutdown();
	}

	*/
}
