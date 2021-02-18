package cps.info;

import cps.info.address.NodeAddressI;

public class ConnectionInfo {
	private NodeAddressI adr;
	private String comipuri;
	private String rotipuri;
	private boolean isRouting;
	
	public ConnectionInfo(NodeAddressI adr, String comipuri, String rotipuri, boolean isRouting) {
		this.adr = adr;
		this.comipuri = comipuri;
		this.rotipuri = rotipuri;
		this.isRouting = isRouting;
	}
	
	public boolean isRouting() {
		return this.isRouting;
	}
	public NodeAddressI getAddress() {
		return this.adr;
	}
	public String getCommunicationInboundPortURI() {
		return comipuri;
	} 
	public String getRoutingInboundPortURI() {
		return rotipuri;
	}
}
