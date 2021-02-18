package cps.info.address;

public interface NetworkAddressI
extends AddressI{
	@Override
	default public boolean isNodeAddress(){return false;};
	@Override
	default public boolean isNetworkAddress() {return true;};

}
