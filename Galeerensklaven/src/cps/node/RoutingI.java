package cps.node;

import java.util.Set;

import cps.info.address.NodeAddressI;
import cps.routing.RouteInfo;

public interface RoutingI {
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfo> routes) throws Exception;
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops)throws Exception;
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI)throws Exception;
}
