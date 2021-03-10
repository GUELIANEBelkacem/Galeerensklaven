package cps.info.address;

public class NetworkAddress implements NetworkAddressI {
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


	

}
