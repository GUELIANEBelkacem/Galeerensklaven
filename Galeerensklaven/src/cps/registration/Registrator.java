package cps.registration;

import java.util.HashSet;
import java.util.Set;

import cps.info.ConnectionInfo;
import cps.info.address.NodeAddressI;
import cps.info.position.PositionI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = { RegistrationCI.class })
public class Registrator extends AbstractComponent {
	public static final String RegIP_URI = "rip-uri";
	private Set<ConnectionInfo> cInfo = new HashSet<>();
	protected RegistrationInboundPort rip;

	protected Registrator() throws Exception {
		super(1, 0);
		this.rip = new RegistrationInboundPort(RegIP_URI, this);
		this.rip.publishPort();
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
