package cps.cvm;

import cps.connecteurs.RegistrationConnector;
import cps.info.position.Position;
import cps.node.accesspoint.AccessPoint;
import cps.node.classicnode.ClassicalNode;
import cps.node.classicnode.registration.NRegistrator;
import cps.node.routing.RoutingNode;
import cps.node.terminal.TerminalNode;
import cps.registration.RegistrationInboundPort;
import cps.registration.Registrator;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;

public class DistributedCVM extends AbstractDistributedCVM{
	protected static final String rot1 = "routing1";
	protected static final String rot2 = "routing2";
	protected static final String ter = "terminal";
	protected static final String cla = "classical";
	protected static final String acc = "accesspoint";
	protected static final String reg = "registrator";
	protected static final String nreg = "nregistrator";
	
	protected String v1;
	protected String v2;
	
	
	
	@Override
	public void interconnect() throws Exception {
		System.out.println("interconnecting");
		/*
		if(AbstractCVM.getThisJVMURI().equals(rot1)){ 
			this.doPortConnection(v1,RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		}
		else {if(AbstractCVM.getThisJVMURI().equals(rot2)){
			this.doPortConnection(v2,RoutingNode.RegOP_URI , Registrator.RegIP_URI, RegistrationConnector.class.getCanonicalName());
		} 
		}
		*/
		
		super.interconnect();
	}



	public static final String RegIP_URI = "my-bbb-rip-uri";
	public DistributedCVM(String[] args) throws Exception {
		
		super(args);
		System.out.println("dcvm constructor "+args[0]);
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		System.out.println("dcvm instantiateAndPublish");
		
		if(AbstractCVM.getThisJVMURI().equals(rot1)){ 
			System.out.println("rot");
			v1=AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {new Position(6, 5)});

		}
		else {if(AbstractCVM.getThisJVMURI().equals(rot2)){
			System.out.println("ter");
			v2=AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {new Position(5, 5)});
		} 
		else {if(AbstractCVM.getThisJVMURI().equals(ter)){
			System.out.println("ter");
			AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {new Position(4, 5)});
		} 
		else {if(AbstractCVM.getThisJVMURI().equals(cla)){
			AbstractComponent.createComponent(ClassicalNode.class.getCanonicalName(), new Object[] {new Position(10, 10)});

		} 
		else {if(AbstractCVM.getThisJVMURI().equals(acc)){
			System.out.println("acc");
			AbstractComponent.createComponent(AccessPoint.class.getCanonicalName(), new Object[] {new Position(6, 5)});

		} 
		else {if(AbstractCVM.getThisJVMURI().equals(reg)){
			System.out.println("reg");
			AbstractComponent.createComponent(Registrator.class.getCanonicalName(), new Object[] {});
			
		} 
		else {if(AbstractCVM.getThisJVMURI().equals(nreg)){
			AbstractComponent.createComponent(NRegistrator.class.getCanonicalName(), new Object[] {});

		} 
		else {System.out.println("non existant uri");
		}}}}}}}
		
		super.instantiateAndPublish();
		
	}

	

	public static void main(String[] args) {
		try {
			System.out.println("dcvm main "+args[0]);
			DistributedCVM dc = new DistributedCVM(args);
			
			dc.startStandardLifeCycle(5000L);
			System.out.println(dc.isIntantiatedAndPublished());
			Thread.sleep(5000L);
			
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
