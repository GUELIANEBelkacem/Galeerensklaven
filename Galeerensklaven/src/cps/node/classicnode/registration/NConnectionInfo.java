package cps.node.classicnode.registration;

import cps.info.address.NetworkAddressI;

public class NConnectionInfo {
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
