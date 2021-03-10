package cps.info.address;

public class NodeAddress implements NodeAddressI {
	private String adr;


	public NodeAddress(String adr) {
		this.adr = adr;
	}
	
	@Override
	public String getAddress() {
		return this.adr;
	}
	@Override	
	public boolean isequalsAddress(AddressI a) {
		return (this.adr.equals(a.getAddress()));
	}


	

}
