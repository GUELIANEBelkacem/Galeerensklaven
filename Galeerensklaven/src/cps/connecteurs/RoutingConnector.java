package cps.connecteurs;

import java.util.Set;

import cps.info.address.NodeAddressI;
import cps.routing.RouteInfo;
import cps.routing.RoutingCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RoutingConnector extends AbstractConnector implements RoutingCI {
	
	


	@Override
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfo> routes) throws Exception {
		((RoutingCI)this.offering).updateRouting(neighbour, routes);
		
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		((RoutingCI)this.offering).updateAccessPoint(neighbour, numberOfHops);
		
	}

}
