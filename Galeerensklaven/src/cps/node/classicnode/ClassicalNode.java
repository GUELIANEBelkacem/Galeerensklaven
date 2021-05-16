package cps.node.classicnode;

import java.util.Random;
import java.util.Set;

import cps.communication.CommunicationCI;
import cps.communication.CommunicationInboundPort;
import cps.communication.CommunicationOutboundPort;
import cps.info.address.AddressI;
import cps.info.address.NetworkAddress;
import cps.info.address.NetworkAddressI;
import cps.info.address.NodeAddressI;
import cps.info.position.Position;
import cps.message.MessageI;
import cps.node.NodeI;
import cps.node.classicnode.registration.NConnectionInfo;
import cps.node.classicnode.registration.NRegistrationCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@RequiredInterfaces(required = { CommunicationCI.class })
@OfferedInterfaces(offered = { CommunicationCI.class})

public class ClassicalNode extends AbstractComponent implements NodeI{
	
	public String ComIP_URI = CommunicationInboundPort.generatePortURI();

	private CommunicationInboundPort comip;
	
	public static int count = 0;
	public int id;
	public int idd;
	
	Random rand = new Random();
	boolean stopit = true;
	private NetworkAddressI address;
	
	private Position pos;  
	
	// pooling 
	protected static final String	IN_POOL_URI = "inpooluri" ;
	protected static final int		ni = 6 ;
	
	protected static final String	OUT_POOL_URI = "outpooluri" ;
	protected static final int		no = 4 ;
	
	protected static final String	MESSAGE_POOL_URI = "messagepooluri" ;
	protected static final int		nm = 2 ;	
	String apuri = CommunicationOutboundPort.generatePortURI();
	CommunicationOutboundPort ap ;
		
	//plugin
	protected final static String	CPLUGIN = "cplugin";
	ClassicalNodePlugin plugin = new ClassicalNodePlugin();
			
	protected ClassicalNode(int id, Position p) {
			super(5, 0);
			this.id=id;
			this.idd= id%2;
			this.address= new NetworkAddress("CNode "+id) ;
			this.pos=p;
			try {
				//this.nregop = new NRegistrationOutboundPort(NRegOP_URI, this);
				this.comip = new CommunicationInboundPort(ComIP_URI, this);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			
			
			try {
				//this.nregop.publishPort();
				this.comip.publishPort();
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
			
			plugin.setPluginURI(CPLUGIN);
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
			
			
			this.register();
			
			

		
			/*
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
			
			*/
		
			
		}
		
		
		
		@Override
		public synchronized void finalise() throws Exception {
			stopit = false;
			
			System.out.println(address.getAddress() + "--------------------------------------------------------");
			System.out.println("\n"+ this.pos);
		
			
			super.finalise();
		}
		
		
		@Override
		public synchronized void shutdown() throws ComponentShutdownException {
			
			try {
				//this.nregop.unpublishPort();
				this.comip.unpublishPort();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
			super.shutdown();
		}


		
		
		// --------------------------Registration------------------------------------------------------------------------

		public Set<NConnectionInfo> registerClassicNode(NetworkAddressI address, String commIpUri, int i) throws Exception{
			return this.plugin.registerClassicNode(address, commIpUri, i);
		}
		
		public void unregister(NetworkAddressI address) throws Exception {
			this.plugin.unregister(address);
			
		}
		public void register() throws Exception {
			//Set<NConnectionInfo> cnei = this.registerClassicNode(address, ComIP_URI, id);
			this.registerClassicNode(address, ComIP_URI, idd);
			/*
			System.out.println(((NConnectionInfo)cnei.toArray()[0]).getSector() == this.id);
			((NConnectionInfo)cnei.toArray()[0]).getCommunicationInboundPortURI();
			*/
		}
		
		
		
		// --------------------------Connection------------------------------------------------------------------------

		
		
		// terminal nodes x
		// pool inbound   x
		// semaphore      x
		// deconexion     x
		// plug in        x
		// jvm            
		// tests 
		// apply prof advice 
		
		
		


	@Override
	public void transmitMessage(MessageI m) throws Exception {
		
		if (m.getAddress().isequalsAddress(this.address)) {
			this.logMessage(this.address.getAddress() + " <--- " + m.getContent().getMessage());
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception {
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
