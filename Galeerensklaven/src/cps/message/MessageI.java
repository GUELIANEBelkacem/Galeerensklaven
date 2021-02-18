package cps.message;

import cps.info.address.AddressI;

public interface MessageI {
	
	public AddressI getAddress();
	public MessageContent getContent();
	public boolean stillAlive();
	public void decrementHops();

}
