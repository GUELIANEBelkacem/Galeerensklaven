package cps.networkAccess;

import java.util.Set;
import cps.networkAccess.NetworkAccessorCI;
import cps.node.accesspoint.AccessPoint;

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
	public Set<AccessInfo> getNetworkNodes(String uri) throws Exception{
		return this.getOwner().handleRequest(
				t -> ((NetworkAccessor) t).getNetworkNodes(uri));
	}
	
	public void spreadCo() {
		try {
		this.getOwner().runTask(u -> {
			try {
				((NetworkAccessorCI) u).spreadCo();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
