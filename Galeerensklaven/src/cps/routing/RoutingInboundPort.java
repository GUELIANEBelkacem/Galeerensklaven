package cps.routing;

import java.util.Set;

import cps.info.address.NodeAddressI;
import cps.node.RoutingI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RoutingInboundPort extends AbstractInboundPort implements RoutingCI{
	public static int count =0;

	public static String genURI() {
		String s = "rot_ip_uri "+count;
		count++;
		return s;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8083095963278024480L;


	public RoutingInboundPort (String uri, ComponentI owner) throws Exception {
		super(uri, RoutingCI.class, owner);

	}
	
	

	@Override
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfo> routes) throws Exception{
		this.getOwner().runTask(
				r-> {
					try {
						((RoutingI) r).updateRouting(neighbour, routes);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
		
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops)throws Exception {
		this.getOwner().runTask(
				r-> {
					try {
						((RoutingI) r).updateAccessPoint(neighbour, numberOfHops);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
		
	}
	
}
