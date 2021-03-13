package cpa.networkAccess;

import cps.info.address.NetworkAddressI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

import java.util.Set;

import cps.info.AccessInfo;

public interface NetworkAccessorCI extends OfferedCI, RequiredCI {
	
	public void registerAccessPoint(NetworkAddressI address, String accessorIpUri) throws Exception;
	public Set<AccessInfo> getAccessPoints() throws Exception;

}
