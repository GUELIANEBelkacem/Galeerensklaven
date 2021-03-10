package cps.registration;

import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

import cps.info.ConnectionInfo;
import cps.info.address.NodeAddressI;
import cps.info.position.PositionI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RegistrationInboundPort extends AbstractInboundPort implements RegistrationCI {

	private static final long serialVersionUID = 1L;

	public RegistrationInboundPort(ComponentI owner) throws Exception {
		super(RegistrationCI.class, owner);

	}

	public RegistrationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RegistrationCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange) throws Exception {
		return this.getOwner().handleRequest(
				t -> ((Registrator) t).registerTerminal(address, commIpUri, initialPosition, initialRange));
	}

	@Override
	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {
		return this.getOwner().handleRequest(r -> ((Registrator) r).registerRouting(address, commIpUri, initialPosition,
				initialRange, routingIpUri));
	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {
		return this.getOwner().handleRequest(
				a -> ((Registrator) a).registerAPoint(address, commIpUri, initialPosition, initialRange, routingIpUri));
	}

	@Override
	public void unregister(NodeAddressI address) {
		try {
			this.getOwner().runTask(
					u-> ((Registrator) u).unreg(address));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
