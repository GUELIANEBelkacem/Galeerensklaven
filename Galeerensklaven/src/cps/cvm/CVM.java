package cps.cvm;

import cps.connecteurs.RegistrationConnector;
import cps.node.routing.RoutingNode;
import cps.node.terminal.TerminalNode;
import cps.registration.Registrator;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {

	public CVM() throws Exception {}

	@Override
	public void deploy() throws Exception {
		String[] rot = new String[10];
		String[] ter = new String[10];
		
		AbstractComponent.createComponent(Registrator.class.getCanonicalName(), new Object[] {});
<<<<<<< HEAD
		
		for(int i=0; i<10;i++) {
=======
		/*
		for(int i=0; i<2;i++) {
>>>>>>> 44940bd (debut Accesspoint)
			rot[i] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {});
			this.doPortConnection(rot[i],RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		} */
		
		
<<<<<<< HEAD
		/*
		for(int i=0; i<5;i++) {
			ter[i] = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {});
			this.doPortConnection(ter[i],TerminalNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		}
=======
		for(int i=0; i<2;i++) {
			ter[i] = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {});
			this.doPortConnection(ter[i],TerminalNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		}
		
>>>>>>> 44940bd (debut Accesspoint)
		
		*/
		
		
		
		
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM c = new CVM();
			c.startStandardLifeCycle(50000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
