package cps.node.classicnode.registration;

import java.util.HashSet;
import java.util.Set;

import cps.info.address.NetworkAddressI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = { NRegistrationCI.class })
public class NRegistrator extends AbstractComponent {
	public static final String NRegIP_URI = "nrip-uri";
	private Set<NConnectionInfo> cInfo = new HashSet<>();
	protected NRegistrationInboundPort rip;

	protected NRegistrator() throws Exception {
		super(1, 0);
		this.rip = new NRegistrationInboundPort(NRegIP_URI, this);
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



	protected NRegistrator(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}


	public Set<NConnectionInfo> registerAccessPoint(NetworkAddressI address, String commIpUri, int i) {
		
		Set<NConnectionInfo> res = new HashSet<>();
		for (NConnectionInfo ci : cInfo) {
			if (!ci.isAPoint() && ci.getSector() == i)
				res.add(ci);
		}
		cInfo.add(new NConnectionInfo(address, commIpUri, i, true));
		return res;
	}
	public Set<NConnectionInfo> registerClassicNode(NetworkAddressI address, String commIpUri, int i) throws Exception{
		
		Set<NConnectionInfo> res = new HashSet<>();
		for (NConnectionInfo ci : cInfo) {
			if (ci.isAPoint() && ci.getSector() == i)
				res.add(ci);
		}
		cInfo.add(new NConnectionInfo(address, commIpUri, i, false));
		return res;
	}
	public void unreg(NetworkAddressI address) {
		for (NConnectionInfo ci : cInfo) {
			if(ci.getAddress().isequalsAddress(address)) {
				cInfo.remove(ci);
				break;
			}
		}
	}
	
	


}
