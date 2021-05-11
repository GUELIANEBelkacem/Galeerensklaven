package cps.cvm;

import cps.connecteurs.RegistrationConnector;
import cps.info.position.Position;
import cps.node.accesspoint.AccessPoint;
import cps.node.classicnode.ClassicalNode;
import cps.node.classicnode.registration.NRegistrationConnector;
import cps.node.classicnode.registration.NRegistrator;
import cps.node.routing.RoutingNode;
import cps.node.terminal.TerminalNode;
import cps.registration.Registrator;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {
	public static final String RegIP_URI = "rip-uri";
	public static final String NRegIP_URI = "nrip-uri";
	public CVM() throws Exception {}

	@Override
	public void deploy() throws Exception {
		String[] rot = new String[10];
		String[] ter = new String[10];
		String[] acc = new String[10];
		String[] cla = new String[10];

		
		AbstractComponent.createComponent(Registrator.class.getCanonicalName(), new Object[] {});
		
		AbstractComponent.createComponent(NRegistrator.class.getCanonicalName(), new Object[] {});
		
		
		//for(int i=0; i<3;i++) {
			cla[0] = AbstractComponent.createComponent(ClassicalNode.class.getCanonicalName(), new Object[] {new Position(10, 10)});
			//this.doPortConnection(ter[0],ClassicalNode.NRegOP_URI , NRegistrator.NRegIP_URI, NRegistrationConnector.class.getCanonicalName());
			
			cla[1] = AbstractComponent.createComponent(ClassicalNode.class.getCanonicalName(), new Object[] {new Position(0, 0)});
			//this.doPortConnection(ter[0],ClassicalNode.NRegOP_URI , NRegistrator.NRegIP_URI, NRegistrationConnector.class.getCanonicalName());
			
			cla[2] = AbstractComponent.createComponent(ClassicalNode.class.getCanonicalName(), new Object[] {new Position(9, 9)});
			//this.doPortConnection(ter[0],ClassicalNode.NRegOP_URI , NRegistrator.NRegIP_URI, NRegistrationConnector.class.getCanonicalName());
		//}
		
		//for(int i=0; i<5;i++) {


			rot[0] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {new Position(1, 5)});
			//this.doPortConnection(rot[0],RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
			
			rot[1] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {new Position(2, 5)});
			//this.doPortConnection(rot[1],RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
			
			rot[2] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {new Position(3, 5)});
			//this.doPortConnection(rot[2],RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
			
			rot[3] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {new Position(4, 6)});
			//this.doPortConnection(rot[3],RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
			
			rot[4] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {new Position(4, 5)});
			//this.doPortConnection(rot[4],RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
			
			rot[5] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {new Position(5, 5)});
			//this.doPortConnection(rot[5],RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
			
			rot[6] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {new Position(6, 5)});
			//this.doPortConnection(rot[6],RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		
			
			//} 
		
		
		
		//for(int i=0; i<2;i++) {
			ter[0] = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {new Position(2, 6)});
			//this.doPortConnection(ter[0],TerminalNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
			
			
			ter[1] = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {new Position(5, 6)});
			//this.doPortConnection(ter[1],TerminalNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
	
	//}
		
		
		//for(int i=0; i<2;i++) {
			acc[0] = AbstractComponent.createComponent(AccessPoint.class.getCanonicalName(), new Object[] {new Position(1, 6)});
			//this.doPortConnection(ter[0],AccessPoint.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
			//this.doPortConnection(ter[0],AccessPoint.NRegOP_URI , NRegistrator.NRegIP_URI, NRegistrationConnector.class.getCanonicalName());
			
			acc[1] = AbstractComponent.createComponent(AccessPoint.class.getCanonicalName(), new Object[] {new Position(6, 4)});
			//this.doPortConnection(ter[0],AccessPoint.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
			//this.doPortConnection(ter[0],AccessPoint.NRegOP_URI , NRegistrator.NRegIP_URI, NRegistrationConnector.class.getCanonicalName());
		//}
		
		
		

		
		
		
		
		
		
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