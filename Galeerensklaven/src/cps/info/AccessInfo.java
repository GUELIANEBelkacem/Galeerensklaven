package cps.info;

import java.io.Serializable;

import cps.info.address.NetworkAddressI;

public class AccessInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1369353759585128563L;
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
