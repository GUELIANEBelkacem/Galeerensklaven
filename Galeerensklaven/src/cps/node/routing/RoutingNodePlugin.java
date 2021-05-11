package cps.node.routing;

import java.util.Set;

import cps.connecteurs.RegistrationConnector;
import cps.cvm.CVM;
import cps.info.ConnectionInfo;
import cps.info.address.NodeAddressI;
import cps.info.position.PositionI;
import cps.registration.RegistrationCI;
import cps.registration.RegistrationOutboundPort;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

public class RoutingNodePlugin extends	AbstractPlugin{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3245702901632755774L;
	
	protected RegistrationOutboundPort regop;
	

	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		
		this.addRequiredInterface(RegistrationCI.class);
		this.regop = new RegistrationOutboundPort(this.getOwner());
		this.regop.publishPort();
		
	}

	@Override
	public void			initialise() throws Exception
	{
		// connect the outbound port.
		this.getOwner().doPortConnection(
				this.regop.getPortURI(),
				CVM.RegIP_URI,
				RegistrationConnector.class.getCanonicalName());

		super.initialise();
	}

	
	
	@Override
	public void			finalise() throws Exception{
		this.getOwner().doPortDisconnection(this.regop.getPortURI());
	}
	
	@Override
	public void			uninstall() throws Exception
	{
		this.regop.unpublishPort();
		this.regop.destroyPort();
		this.removeRequiredInterface(RegistrationCI.class);
	}
	
	
	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {

		return this.regop.registerRoutingNode(address, commIpUri, initialPosition, initialRange, routingIpUri);

	}
	
	
	public void unregister(NodeAddressI address) throws Exception {
		this.regop.unregister(address);
	}
	

}
