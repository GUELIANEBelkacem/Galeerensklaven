package cps.classicNetwork;

import cps.info.address.NetworkAddressI;
import cps.message.MessageI;
import cps.networkAccess.NetworkAccessingOutboundPort;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface ClassicNetworkCI extends OfferedCI, RequiredCI {
	
	public void transmitMessage(NetworkAddressI nadress, MessageI m) throws Exception;
	
	
}
