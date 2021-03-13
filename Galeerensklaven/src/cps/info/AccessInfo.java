package cps.info;

import cps.info.address.NetworkAddressI;
import cps.info.address.NodeAddressI;

public class AccessInfo {

	private NetworkAddressI address;
	private String accessIpUri;
		
	public AccessInfo(NetworkAddressI address, String accessIpUri) {
		// TODO Auto-generated constructor stub
		this.accessIpUri = accessIpUri;
		this.address = address;
	}
	
	public NetworkAddressI getAddress() {
		return address;
	}
	public String getAccessInboundPortURI() {
		return accessIpUri;
	}

}
