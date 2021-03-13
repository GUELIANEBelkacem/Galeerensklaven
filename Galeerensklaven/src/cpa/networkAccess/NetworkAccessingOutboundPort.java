package cpa.networkAccess;

import java.util.Set;

import cps.info.AccessInfo;
import cps.info.address.NetworkAddressI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class NetworkAccessingOutboundPort extends AbstractOutboundPort implements NetworkAccessorCI {

	public NetworkAccessingOutboundPort(ComponentI owner)
			throws Exception {
		super(NetworkAccessorCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	public NetworkAccessingOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, NetworkAccessorCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerAccessPoint(NetworkAddressI address, String accessorIpUri) throws Exception {
		((NetworkAccessor) this.getConnector()).registerAccessPoint(address, accessorIpUri);

	}

	@Override
	public Set<AccessInfo> getAccessPoints() throws Exception {
		// TODO Auto-generated method stub
		return ((NetworkAccessor) this.getConnector()).getAccessPoints();
	}

}
