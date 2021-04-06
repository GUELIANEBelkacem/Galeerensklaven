package cps.networkAccess;

import cps.info.address.NetworkAddressI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

import java.util.Set;

import cps.info.AccessInfo;

public interface NetworkAccessorCI extends OfferedCI, RequiredCI {
	
	public void registerAccessPoint(NetworkAddressI address, String accIpUri) throws Exception;
	public Set<AccessInfo> getNetworkNodes(String ipuri) throws Exception;
	public void spreadCo() throws Exception;
}
