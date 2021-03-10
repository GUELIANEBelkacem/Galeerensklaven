package cps.node;

import cps.info.address.NodeAddressI;
import cps.message.MessageI;

public interface NodeI {
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception;
	public void transmitMessage(MessageI m) throws Exception;
}
