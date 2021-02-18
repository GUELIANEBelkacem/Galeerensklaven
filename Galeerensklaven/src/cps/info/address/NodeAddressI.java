package cps.info.address;

public interface NodeAddressI
extends AddressI{
	@Override
	default public boolean isNodeAddress(){return true;};
	@Override
	default public boolean isNetworkAddress() {return false;};

}
