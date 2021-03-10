package cps.routing;

import java.util.Set;

import cps.info.address.NodeAddressI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RoutingInboundPort extends AbstractInboundPort implements RoutingCI{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8083095963278024480L;


	public RoutingInboundPort (ComponentI owner) throws Exception {
		super(RoutingCI.class, owner);

	}

	@Override
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfo> routes) {
		this.getOwner().runTask(
				u-> ((Router) r).updateRouting(neighbour, routes));// to be changed 
		
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) {
		this.getOwner().runTask(
				u-> ((Router) r).updateRoutingPoint(neighbour, numberOfHops));
		
	}
}
