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
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;

public class DistributedCVM extends AbstractDistributedCVM{
	protected static final String rot = "routing";
	protected static final String ter = "terminal";
	protected static final String cla = "classical";
	protected static final String acc = "accesspoint";
	protected static final String reg = "registrator";
	protected static final String nreg = "nregistrator";
	public DistributedCVM(String[] args) throws Exception {
		super(args);
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		if(AbstractCVM.getThisJVMURI().equals(rot)){ 
			AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {new Position(5, 5)});

		}
		else {if(AbstractCVM.getThisJVMURI().equals(ter)){
			AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {new Position(4, 5)});
		} 
		else {if(AbstractCVM.getThisJVMURI().equals(cla)){
			AbstractComponent.createComponent(ClassicalNode.class.getCanonicalName(), new Object[] {new Position(10, 10)});

		} 
		else {if(AbstractCVM.getThisJVMURI().equals(acc)){
			AbstractComponent.createComponent(AccessPoint.class.getCanonicalName(), new Object[] {new Position(6, 5)});

		} 
		else {if(AbstractCVM.getThisJVMURI().equals(reg)){
			AbstractComponent.createComponent(Registrator.class.getCanonicalName(), new Object[] {});
			
		} 
		else {if(AbstractCVM.getThisJVMURI().equals(nreg)){
			AbstractComponent.createComponent(NRegistrator.class.getCanonicalName(), new Object[] {});

		} 
		else {System.out.println("non existant uri");
		}}}}}}
		super.instantiateAndPublish();
	}

	
	public static void main(String[] args) {
		try {
			DistributedCVM dc = new DistributedCVM(args);
			dc.startStandardLifeCycle(5000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
