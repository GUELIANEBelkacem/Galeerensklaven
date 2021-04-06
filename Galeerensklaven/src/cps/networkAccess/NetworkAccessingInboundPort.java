package cps.networkAccess;

import java.util.Set;

import cps.info.AccessInfo;
import cps.info.address.NetworkAddressI;
import cps.registration.Registrator;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class NetworkAccessingInboundPort extends AbstractInboundPort implements NetworkAccessorCI {

	public NetworkAccessingInboundPort(ComponentI owner) throws Exception {
		super(NetworkAccessorCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	public NetworkAccessingInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, NetworkAccessorCI.class, owner);
		// TODO Auto-generated constructor stub
	}



	@Override
	public void registerAccessPoint(NetworkAddressI address, String accessorIpUri) {
		
		try {
			this.getOwner().runTask(u -> ((NetworkAccessor) u).registerAccessPoint(address, accessorIpUri));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<AccessInfo> getNetworkNodes() throws Exception{
		return this.getOwner().handleRequest(
				t -> ((NetworkAccessor) t).getNetworkNodes());
	}

}