package cps.info.address;

import java.io.Serializable;

public class NetworkAddress implements NetworkAddressI, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4900990061792882728L;
	private String adr;


	public NetworkAddress(String adr) {
		this.adr = adr;
	}
	@Override
	public String getAddress() {return this.adr;}
	@Override	
	public boolean isequalsAddress(AddressI a) {
		return (this.adr.equals( a.getAddress()));
	}

	@Override
    public int hashCode() {
		return this.adr.hashCode();
	}
	

}
