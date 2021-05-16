package cps.routing;

import java.io.Serializable;

import cps.info.address.AddressI;

public class RouteInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5409069318712764877L;
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
