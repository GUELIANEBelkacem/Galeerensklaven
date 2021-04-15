package cps.node.classicnode.registration;

import java.util.Set;

import cps.info.address.NetworkAddressI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface NRegistrationCI extends OfferedCI, RequiredCI {
	
	public Set<NConnectionInfo> registerClassicNode(NetworkAddressI address, String commIpUri, int i) throws Exception;

	public Set<NConnectionInfo> registerAccessPoint(NetworkAddressI address, String commIpUri, int i) throws Exception;

	public void unregister(NetworkAddressI  address) throws Exception;
}
