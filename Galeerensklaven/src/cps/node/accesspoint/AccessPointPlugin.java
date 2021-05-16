package cps.node.accesspoint;

import java.util.Set;

import cps.connecteurs.RegistrationConnector;
import cps.info.ConnectionInfo;
import cps.info.address.NetworkAddressI;
import cps.info.address.NodeAddressI;
import cps.info.position.PositionI;
import cps.node.classicnode.registration.NConnectionInfo;
import cps.node.classicnode.registration.NRegistrationCI;
import cps.node.classicnode.registration.NRegistrationConnector;
import cps.node.classicnode.registration.NRegistrationOutboundPort;
import cps.node.classicnode.registration.NRegistrator;
import cps.registration.RegistrationCI;
import cps.registration.RegistrationOutboundPort;
import cps.registration.Registrator;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

public class AccessPointPlugin extends	AbstractPlugin{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3473130740095769938L;
	public  final String RegOP_URI = RegistrationOutboundPort.generatePortURI();
	public  final String NRegOP_URI = NRegistrationOutboundPort.generatePortURI();
	protected RegistrationOutboundPort regop;
	protected NRegistrationOutboundPort nregop;
	

	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		
		this.addRequiredInterface(RegistrationCI.class);
		this.regop = new RegistrationOutboundPort(RegOP_URI,this.getOwner());
		this.regop.publishPort();
		
		this.addRequiredInterface(NRegistrationCI.class);
		this.nregop = new NRegistrationOutboundPort(NRegOP_URI,this.getOwner());
		this.nregop.publishPort();
		
	}

	@Override
	public void			initialise() throws Exception
	{
		// connect the outbound port.
		this.getOwner().doPortConnection(
				this.regop.getPortURI(),
				Registrator.RegIP_URI,
				RegistrationConnector.class.getCanonicalName());
		
		this.getOwner().doPortConnection(
				this.nregop.getPortURI(),
				NRegistrator.NRegIP_URI,
				NRegistrationConnector.class.getCanonicalName());

		super.initialise();
	}

	
	
	@Override
	public void			finalise() throws Exception{
		this.getOwner().doPortDisconnection(this.regop.getPortURI());
		this.getOwner().doPortDisconnection(this.nregop.getPortURI());
	}
	
	@Override
	public void			uninstall() throws Exception
	{
		this.regop.unpublishPort();
		this.regop.destroyPort();
		this.removeRequiredInterface(RegistrationCI.class);
		this.nregop.unpublishPort();
		this.nregop.destroyPort();
		this.removeRequiredInterface(NRegistrationCI.class);
	}
	
	
	public Set<ConnectionInfo> registerAPoint(NodeAddressI address, String commIpUri, PositionI initialPosition,
			double initialRange, String routingIpUri) throws Exception {
		return this.regop.registerAccessPoint(address, commIpUri, initialPosition, initialRange, routingIpUri);
	}
	
	public Set<NConnectionInfo> registerAccessPoint(NetworkAddressI address, String commIpUri, int i) throws Exception{
		
		return this.nregop.registerAccessPoint(address, commIpUri, i);
		
	}
	
	public void unregister(NodeAddressI address) throws Exception {
		this.regop.unregister(address);
		
	}
	public void unregister(NetworkAddressI address) throws Exception {
		this.nregop.unregister(address);
		
	}

}
