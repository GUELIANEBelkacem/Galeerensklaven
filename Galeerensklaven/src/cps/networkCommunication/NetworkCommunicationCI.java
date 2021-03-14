package cps.networkCommunication;

import cps.info.address.NetworkAddressI;
import cps.message.MessageI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface NetworkCommunicationCI extends RequiredCI, OfferedCI {
	public void transmitMessage(NetworkAddressI naddress, MessageI m) throws Exception;
}
