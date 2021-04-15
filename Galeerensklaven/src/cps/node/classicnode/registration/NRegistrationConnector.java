package cps.node.classicnode.registration;

import java.util.Set;

import cps.info.address.NetworkAddressI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class NRegistrationConnector extends AbstractConnector implements NRegistrationCI {


	@Override
	public Set<NConnectionInfo> registerClassicNode(NetworkAddressI address, String commIpUri, int i) throws Exception {
		return ((NRegistrationCI)this.offering).registerClassicNode(address, commIpUri, i);
	}

	@Override
	public Set<NConnectionInfo> registerAccessPoint(NetworkAddressI address, String commIpUri, int i) throws Exception {
		return ((NRegistrationCI)this.offering).registerAccessPoint(address, commIpUri, i);
	}

	@Override
	public void unregister(NetworkAddressI address) throws Exception {
		((NRegistrationCI)this.offering).unregister(address);
		
	}

}
