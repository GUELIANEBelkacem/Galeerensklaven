package cps.connecteurs;

import java.util.Set;

import cps.info.AccessInfo;
import cps.info.address.NetworkAddressI;
import cps.networkAccess.NetworkAccessorCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class NetworkAccessConnector extends AbstractConnector implements NetworkAccessorCI {


	@Override
	public void registerAccessPoint(NetworkAddressI address, String accIpUri) throws Exception {
		((NetworkAccessorCI)this.offering).registerAccessPoint(address, accIpUri);

	}

	@Override
	public Set<AccessInfo> getNetworkNodes() throws Exception {
		return ((NetworkAccessorCI) this.offering).getNetworkNodes();
	}

}
