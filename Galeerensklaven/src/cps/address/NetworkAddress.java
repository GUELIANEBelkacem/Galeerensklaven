package cps.address;

public class NetworkAddress implements NetworkAddressI {
	private int adr;


	public NetworkAddress(int adr) {
		this.adr = adr;
	}
	
	@Override
	public int getAddress() {
		return this.adr;
	}
	@Override	
	public boolean isequalsAddress(AddressI a) {
		return (this.adr == a.getAddress());
	}


	

}
