package cps.info.address;

import java.io.Serializable;

public class NodeAddress implements NodeAddressI, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8164580842828063919L;
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

	@Override 
	public boolean equals(Object o) {
		
	    if (this == o) return true;
	    if (o == null) return false;
	    if (getClass() != o.getClass()) return false;
	    NodeAddress e = (NodeAddress) o;
	    
	    return this.isequalsAddress(e);
		
	}

	@Override
    public int hashCode() {
		return this.adr.hashCode();
	}
	  
	

}
