package cps.info.address;

public interface AddressI {
	public boolean isNodeAddress();
	public boolean isNetworkAddress();
	public boolean isequalsAddress(AddressI a);
	public int getAddress();

}
