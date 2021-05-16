package cps.node.classicnode.registration;

import java.io.Serializable;

import cps.info.address.NetworkAddressI;

public class NConnectionInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7488183453515146349L;
	private NetworkAddressI adr;
	private String comipuri;
	private boolean isAPoint;
	int i;
	
	public NConnectionInfo(NetworkAddressI adr, String comipuri, int i, boolean isAPoint) {
		this.adr = adr;
		this.comipuri = comipuri;
		this.i = i;
		this.isAPoint = isAPoint;
	}
	
	public boolean isAPoint() {
		return this.isAPoint;
	}
	public int getSector() {
		return i;
	}
	public NetworkAddressI getAddress() {
		return this.adr;
	}
	public String getCommunicationInboundPortURI() {
		return comipuri;
	} 

}
