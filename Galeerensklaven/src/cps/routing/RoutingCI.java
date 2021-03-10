package cps.routing;

import java.util.Set;

import cps.info.address.NodeAddressI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface RoutingCI extends OfferedCI, RequiredCI{
	
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfo> routes)throws Exception;
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops)throws Exception;

}
