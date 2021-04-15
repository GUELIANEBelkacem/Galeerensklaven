package cps.node.classicnode.registration;

import java.util.Set;

import cps.info.address.NetworkAddressI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class NRegistrationInboundPort extends AbstractInboundPort implements NRegistrationCI {

	private static final long serialVersionUID = 1L;

	public NRegistrationInboundPort(ComponentI owner) throws Exception {
		super(NRegistrationCI.class, owner);

	}

	public NRegistrationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, NRegistrationCI.class, owner);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void unregister(NetworkAddressI address) {
		try {
			this.getOwner().runTask(
					u-> ((NRegistrator) u).unreg(address));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Set<NConnectionInfo> registerClassicNode(NetworkAddressI address, String commIpUri, int i) throws Exception {
		return this.getOwner().handleRequest(
				t -> ((NRegistrator) t).registerClassicNode(address, commIpUri, i));
		
	}

	@Override
	public Set<NConnectionInfo> registerAccessPoint(NetworkAddressI address, String commIpUri, int i) throws Exception {
		return this.getOwner().handleRequest(
				t -> ((NRegistrator) t).registerAccessPoint(address, commIpUri, i));
	}

}
