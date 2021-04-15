package cps.node.classicnode.registration;

import java.util.Set;

import cps.info.address.NetworkAddressI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class NRegistrationOutboundPort extends AbstractOutboundPort implements NRegistrationCI {

	private static final long serialVersionUID = 1L;

	public NRegistrationOutboundPort(ComponentI owner) throws Exception {
		super(NRegistrationCI.class, owner);

	}

	public NRegistrationOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, NRegistrationCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void unregister(NetworkAddressI address) throws Exception{
		((NRegistrationCI)this.getConnector()).unregister(address);
		

	}

	@Override
	public Set<NConnectionInfo> registerClassicNode(NetworkAddressI address, String commIpUri, int i) throws Exception {
		return ((NRegistrationCI)this.getConnector()).registerClassicNode(address, commIpUri, i);

	}

	@Override
	public Set<NConnectionInfo> registerAccessPoint(NetworkAddressI address, String commIpUri, int i) throws Exception {
		return ((NRegistrationCI)this.getConnector()).registerAccessPoint(address, commIpUri, i);
	}

}
