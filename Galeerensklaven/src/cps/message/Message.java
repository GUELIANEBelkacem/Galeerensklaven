package cps.message;

import cps.info.address.AddressI;

public class Message implements MessageI {
	private int hopsLeft;
	private MessageContent message;
	private AddressI dest;
	
	public Message(String text, int MaxHops, AddressI dest) {
		this.hopsLeft = MaxHops;
		this.message = new MessageContent(text);
		this.dest = dest;
	}
	@Override
	public AddressI getAddress() {
		return this.dest;
	}

	@Override
	public MessageContent getContent() {
		return this.message;
	}

	@Override
	public boolean stillAlive() {
		return (this.hopsLeft>0);
	}

	@Override
	public void decrementHops() {
		this.hopsLeft--;
		
	}
	public int getHops() {
		return this.hopsLeft;
	}

}
