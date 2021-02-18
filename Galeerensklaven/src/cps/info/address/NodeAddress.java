package cps.info.address;

public class NodeAddress implements NodeAddressI {
	private int adr;


	public NodeAddress(int adr) {
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
