package cps.info;

import cps.info.address.NodeAddressI;
import cps.info.position.PositionI;

public class ConnectionInfo {
	private NodeAddressI adr;
	private String comipuri;
	private String rotipuri;
	private boolean isRouting;
	private PositionI pos;
	private boolean isAPoint;
	
	public ConnectionInfo(NodeAddressI adr, String comipuri, String rotipuri, boolean isRouting, PositionI pos, boolean isAPoint) {
		this.adr = adr;
		this.comipuri = comipuri;
		this.rotipuri = rotipuri;
		this.isRouting = isRouting;
		this.pos = pos;
		this.isAPoint = isAPoint;
	}
	
	public boolean isRouting() {
		return this.isRouting;
	}
	
	public boolean isAPoint() {
		return this.isAPoint;
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
	
	public PositionI getPosition() {
		return pos;
	}
}
