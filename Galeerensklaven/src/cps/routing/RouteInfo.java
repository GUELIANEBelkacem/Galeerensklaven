package cps.routing;

import cps.info.address.AddressI;

public class RouteInfo {
	private AddressI destination;
	private int numOfHops;
	
	public RouteInfo(AddressI destination,  int numOfHops) {
		this.destination=destination;
		this.numOfHops = numOfHops;
	}
	
	public AddressI getDestination() {
		return this.destination;
	}
	
	public int getNumberOfHops() {
		return numOfHops;
	}

}
