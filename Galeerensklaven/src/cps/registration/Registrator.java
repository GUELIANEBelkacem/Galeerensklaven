package cps.registration;

import java.util.HashSet;
import java.util.Set;

import cps.info.ConnectionInfo;
import cps.info.address.NodeAddressI;
import cps.info.position.PositionI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

@OfferedInterfaces(offered = { RegistrationCI.class })
public class Registrator extends AbstractComponent {
	public static final String RegIP_URI = "my-bbb-rip-uri";
	public static final String RegIP_URI2 = "my-bbb-rip-uri";
	private Set<ConnectionInfo> cInfo = new HashSet<>();
	public RegistrationInboundPort rip;
	
	// pooling 
	protected static final String	IN_POOL_URI = "inregpooluri" ;
	protected static final int		ni = 3 ;
	
	
	protected Registrator() {
		super(5, 0);
		try {
			this.rip = new RegistrationInboundPort(RegIP_URI2, this);
			this.rip.publishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.createNewExecutorService(IN_POOL_URI, ni, false) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("kjhgkjhb");
	}

	
	


	@Override
	public synchronized void execute() throws Exception {
		super.execute();
	}
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		
		try {
			this.rip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}



	protected Registrator(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}

	public Set<ConnectionInfo> registerTerminal(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange) {
		Set<ConnectionInfo> res = new HashSet<>();
		for (ConnectionInfo ci : cInfo) {
			if (ci.getPosition().distance(initialPosition) <= initialRange)
				res.add(ci);
		}
		cInfo.add(new ConnectionInfo(address, commIpUri, "", false, initialPosition, false));
		return res;

	}

	public Set<ConnectionInfo> registerRouting(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) {
		System.out.println("contacted routing");
		Set<ConnectionInfo> res = new HashSet<>();
		for (ConnectionInfo ci : cInfo) {
			if (ci.getPosition().distance(initialPosition) <= initialRange)
				res.add(ci);
		}
		cInfo.add(new ConnectionInfo(address, commIpUri, routingIpUri, true, initialPosition, false));
		return res;
	}

	public Set<ConnectionInfo> registerAPoint(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) {
		
		Set<ConnectionInfo> res = new HashSet<>();
		for (ConnectionInfo ci : cInfo) {
			if (ci.getPosition().distance(initialPosition) <= initialRange || ci.isAPoint())
				res.add(ci);
			
				
			
		}
		
		cInfo.add(new ConnectionInfo(address, commIpUri, routingIpUri, true, initialPosition, true));
		
		
		return res;
	}
	
	public void unreg(NodeAddressI address) {
		for (ConnectionInfo ci : cInfo) {
			if(ci.getAddress().isequalsAddress(address)) {
				cInfo.remove(ci);
				break;
			}
		}
	}

}
