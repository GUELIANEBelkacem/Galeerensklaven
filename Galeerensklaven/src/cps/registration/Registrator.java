package cps.registration;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cps.info.ConnectionInfo;
import cps.info.address.NodeAddressI;
import cps.info.position.PositionI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = { RegistrationCI.class })
public class Registrator extends AbstractComponent {
	public static final String RegIP_URI = "reg-inbound-port-uri";
	//mutex
	protected final ReentrantReadWriteLock		hashMapLock ;
	
	
	
	private Set<ConnectionInfo> cInfo = new HashSet<>();
	public RegistrationInboundPort rip;
	
	@Override
	public synchronized void finalise() throws Exception {
		
		super.finalise();
	}

	// pooling 
	protected static final String	IN_POOL_URI = "inregpooluri" ;
	protected static final int		ni = 3 ;
	
	
	protected Registrator() {
		super(5, 0);
		this.hashMapLock = new ReentrantReadWriteLock() ;
		try {
			this.rip = new RegistrationInboundPort(RegIP_URI, this);
			this.rip.publishPort();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		try {
			this.createNewExecutorService(IN_POOL_URI, ni, false) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	


	@Override
	public synchronized void execute() throws Exception {
		
		super.execute();
		Thread.sleep(4377L);
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



	

	public Set<ConnectionInfo> registerTerminal(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange) {
		this.hashMapLock.writeLock().lock() ;
		Set<ConnectionInfo> res = new HashSet<>();
		for (ConnectionInfo ci : cInfo) {
			if (ci.getPosition().distance(initialPosition) <= initialRange)
				res.add(ci);
		}
		cInfo.add(new ConnectionInfo(address, commIpUri, "", false, initialPosition, false));
		this.hashMapLock.writeLock().unlock() ;
		return res;

	}

	public Set<ConnectionInfo> registerRouting(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) {
		this.hashMapLock.writeLock().lock() ;
		
		Set<ConnectionInfo> res = new HashSet<>();
		for (ConnectionInfo ci : cInfo) {
			if (ci.getPosition().distance(initialPosition) <= initialRange)
				res.add(ci);
		}
		cInfo.add(new ConnectionInfo(address, commIpUri, routingIpUri, true, initialPosition, false));
		this.hashMapLock.writeLock().unlock() ;
		return res;
	}

	public Set<ConnectionInfo> registerAPoint(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) {
		this.hashMapLock.writeLock().lock() ;
		Set<ConnectionInfo> res = new HashSet<>();
		for (ConnectionInfo ci : cInfo) {
			if (ci.getPosition().distance(initialPosition) <= initialRange || ci.isAPoint())
				res.add(ci);
			
				
			
		}
		
		cInfo.add(new ConnectionInfo(address, commIpUri, routingIpUri, true, initialPosition, true));
		
		this.hashMapLock.writeLock().unlock() ;
		return res;
	}
	
	public void unreg(NodeAddressI address) {
		this.hashMapLock.writeLock().lock() ;
		for (ConnectionInfo ci : cInfo) {
			if(ci.getAddress().isequalsAddress(address)) {
				cInfo.remove(ci);
				break;
			}
		}
		this.hashMapLock.writeLock().unlock() ;
	}
	
	

}
