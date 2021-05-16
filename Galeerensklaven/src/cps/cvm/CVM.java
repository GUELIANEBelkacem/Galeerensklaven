package cps.cvm;

import cps.info.position.Position;
import cps.node.accesspoint.AccessPoint;
import cps.node.classicnode.ClassicalNode;
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
		
		
		cla[0] = AbstractComponent.createComponent(ClassicalNode.class.getCanonicalName(), new Object[] {1, new Position(10, 10)});
		cla[1] = AbstractComponent.createComponent(ClassicalNode.class.getCanonicalName(), new Object[] {2, new Position(0, 0)});
		cla[2] = AbstractComponent.createComponent(ClassicalNode.class.getCanonicalName(), new Object[] {3, new Position(9, 9)});
		

		rot[0] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {1, new Position(1, 5)});			
		rot[1] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {2, new Position(2, 5)});
		rot[2] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {3, new Position(3, 5)});
		rot[3] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {4, new Position(4, 6)});
		rot[4] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {5, new Position(4, 5)});
		rot[5] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {6, new Position(5, 5)});
		rot[6] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {7, new Position(6, 5)});
		
		
		ter[0] = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {1, new Position(2, 6)});
		ter[1] = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {2, new Position(5, 6)});
			
		
		acc[0] = AbstractComponent.createComponent(AccessPoint.class.getCanonicalName(), new Object[] {1, new Position(1, 6)});
		acc[1] = AbstractComponent.createComponent(AccessPoint.class.getCanonicalName(), new Object[] {2, new Position(6, 4)});
			

		
		
		
		
		
		
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