package cps.node.classicnode;

import java.util.Set;

import cps.cvm.CVM;
import cps.info.address.NetworkAddressI;
import cps.node.classicnode.registration.NConnectionInfo;
import cps.node.classicnode.registration.NRegistrationCI;
import cps.node.classicnode.registration.NRegistrationConnector;
import cps.node.classicnode.registration.NRegistrationOutboundPort;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

public class ClassicalNodePlugin extends	AbstractPlugin{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3684919915311580765L;
	
	
	protected NRegistrationOutboundPort nregop;
	

	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		
		
		
		this.addRequiredInterface(NRegistrationCI.class);
		this.nregop = new NRegistrationOutboundPort(this.getOwner());
		this.nregop.publishPort();
		
	}

	@Override
	public void			initialise() throws Exception
	{
		// connect the outbound port.
	
		
		this.getOwner().doPortConnection(
				this.nregop.getPortURI(),
				CVM.NRegIP_URI,
				NRegistrationConnector.class.getCanonicalName());

		super.initialise();
	}

	
	
	@Override
	public void			finalise() throws Exception{
	
		this.getOwner().doPortDisconnection(this.nregop.getPortURI());
	}
	
	@Override
	public void			uninstall() throws Exception
	{
		this.nregop.unpublishPort();
		this.nregop.destroyPort();
		this.removeRequiredInterface(NRegistrationCI.class);
	}
	
	
	public Set<NConnectionInfo> registerClassicNode(NetworkAddressI address, String commIpUri, int i) throws Exception{
		return this.nregop.registerClassicNode(address, commIpUri, i);
	}
	
	public void unregister(NetworkAddressI address) throws Exception {
		this.nregop.unregister(address);
		
	}


}
