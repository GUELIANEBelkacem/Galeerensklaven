package cps.message;

import java.io.Serializable;

import cps.info.address.AddressI;

public class Message implements MessageI, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8886141569728791240L;
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
