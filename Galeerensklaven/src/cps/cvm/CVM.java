package cps.cvm;

import cps.classicNetwork.ClassicNetwork;
import cps.connecteurs.NetworkAccessConnector;
import cps.connecteurs.RegistrationConnector;
import cps.networkAccess.NetworkAccessor;
import cps.node.accesspoint.AccessPoint;
import cps.node.routing.RoutingNode;
import cps.node.terminal.TerminalNode;
import cps.registration.Registrator;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {

	public CVM() throws Exception {}

	@Override
	public void deploy() throws Exception {
		String[] rot = new String[1000];
		String[] ter = new String[1000];
		String[] ap = new String[1000];
		
		AbstractComponent.createComponent(Registrator.class.getCanonicalName(), new Object[] {});
		AbstractComponent.createComponent(NetworkAccessor.class.getCanonicalName(), new Object[] {});
		
		
		for(int i=0; i<4;i++) {
			ter[i] = AbstractComponent.createComponent(ClassicNetwork.class.getCanonicalName(), new Object[] {});
			this.doPortConnection(ter[i],ClassicNetwork.NAOP_URI , NetworkAccessor.NaIP_URI, NetworkAccessConnector.class.getCanonicalName());
		}
		for(int i=0; i<3;i++) {
			ap[i] = AbstractComponent.createComponent(AccessPoint.class.getCanonicalName(), new Object[] {});
			this.doPortConnection(ap[i],AccessPoint.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
			this.doPortConnection(ap[i],AccessPoint.NaOP_URI , NetworkAccessor.NaIP_URI, NetworkAccessConnector.class.getCanonicalName());
		}

		
		for(int i=0; i<3;i++) {
			ter[i] = AbstractComponent.createComponent(ClassicNetwork.class.getCanonicalName(), new Object[] {});
			this.doPortConnection(ter[i],ClassicNetwork.NAOP_URI , NetworkAccessor.NaIP_URI, NetworkAccessConnector.class.getCanonicalName());
		}
		
		
		
		for(int i=0; i<4;i++) {


			rot[i] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {});
			this.doPortConnection(rot[i],RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		} 
		
		
		
		for(int i=0; i<4;i++) {
			ter[i] = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {});
			this.doPortConnection(ter[i],TerminalNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		}
		
		
		
		

		
		
		
		
		
		
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM c = new CVM();
			c.startStandardLifeCycle(20000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
