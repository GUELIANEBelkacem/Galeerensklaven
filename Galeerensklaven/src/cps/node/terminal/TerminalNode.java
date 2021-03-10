package cps.node.terminal;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import cps.communication.CommunicationCI;
import cps.info.ConnectionInfo;
import cps.info.address.AddressI;
import cps.info.position.PositionI;
import cps.info.address.NodeAddressI;

@RequiredInterfaces(required = { CommunicationCI.class, TerminalRegRequiredCI.class })
@OfferedInterfaces(offered = { CommunicationCI.class })

public class TerminalNode extends AbstractComponent {
	private NodeAddressI address;
	private static int i = 0;
	private PositionI initialPosition;
	private double initialRange;

	public final String COMMIP_URI = "commip-uri-" + i;
	public final String TROP_URI = "trop-uri-" + i;
	protected CommunicationCI comm;
	protected TerminalRegOutboundPort trop;

	private Set<ConnectionInfo> voisins = new HashSet<>();

	public TerminalNode(NodeAddressI address, PositionI initialPosition, double initialRange) throws Exception {
		super(1, 0);
		this.address = address;
		this.initialPosition = initialPosition;
		this.initialRange = initialRange;

		this.trop = new TerminalRegOutboundPort(TROP_URI, this);
		this.trop.publishPort();
		i++;

		// TODO Auto-generated constructor stub
	}

	/*
	 * public TerminalNode(String reflectionInboundPortURI, int nbThreads, int
	 * nbSchedulableThreads) { super(reflectionInboundPortURI, nbThreads,
	 * nbSchedulableThreads); // TODO Auto-generated constructor stub }
	 */

	public synchronized void execute() throws Exception {
		super.execute();
		voisins = this.trop.register(address, COMMIP_URI, initialPosition, initialRange);
		for (ConnectionInfo ci : voisins) {
			comm.connect(ci.getAddress(), ci.getCommunicationInboundPortURI());
			ci.getCommunicationInboundPortURI().connect(address, comm); // gros doute ....
		}

	}

	public void connect(NodeAddressI address, String communicationInboundPortURI) {

	}

}
