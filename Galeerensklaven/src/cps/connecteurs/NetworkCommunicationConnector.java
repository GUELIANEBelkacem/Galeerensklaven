package cps.connecteurs;

import cps.info.address.NetworkAddressI;
import cps.message.MessageI;
import cps.networkCommunication.NetworkCommunicationCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class NetworkCommunicationConnector extends AbstractConnector implements NetworkCommunicationCI {


	@Override
	public void transmitMessage(NetworkAddressI naddress, MessageI m) throws Exception {
		((NetworkCommunicationCI) this.offering).transmitMessage(naddress, m);

	}

}
