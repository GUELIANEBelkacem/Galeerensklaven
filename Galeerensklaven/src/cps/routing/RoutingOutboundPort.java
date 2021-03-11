package cps.routing;

import java.util.Set;

import cps.info.address.NodeAddressI;
import cps.node.routing.RoutingNode;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RoutingOutboundPort extends AbstractOutboundPort implements RoutingCI{
	
	public static int count =0;

	public static String genURI() {
		String s = "rot_op_uri "+count;
		count++;
		return s;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1154586280136787446L;

	public RoutingOutboundPort (String uri, ComponentI owner) throws Exception {
		super(uri, RoutingCI.class, owner);

	}
	
	@Override
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfo> routes) throws Exception {
		((RoutingCI)this.getConnector()).updateRouting(neighbour, routes);
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception{
		((RoutingCI)this.getConnector()).updateAccessPoint(neighbour, numberOfHops);
		
	}
	
}
