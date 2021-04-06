package cps.routing;

import java.util.Set;

import cps.info.address.NodeAddressI;

public interface RoutingAccessingCI {
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops)throws Exception;
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfo> routes) throws Exception;
	
}
