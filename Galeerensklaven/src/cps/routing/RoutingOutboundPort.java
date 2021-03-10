package cps.routing;

import java.util.Set;

import cps.info.address.NodeAddressI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RoutingOutboundPort extends AbstractOutboundPort implements RoutingCI{
	



	/**
	 * 
	 */
	private static final long serialVersionUID = 1154586280136787446L;

	public RoutingOutboundPort (ComponentI owner) throws Exception {
		super(RoutingCI.class, owner);

	}

	@Override
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfo> routes) {
		this.getOwner().runTask(
				u-> ((NodeI) r).updateRouting(neighbour, routes));// to be changed 
		
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) {
		this.getOwner().runTask(
				u-> ((AccessPointI) r).updateRoutingPoint(neighbour, numberOfHops));
		
	}
}
