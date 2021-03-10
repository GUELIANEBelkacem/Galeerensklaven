package cps.node.routing;

import cps.communication.CommunicationCI;
import cps.registration.RegistrationCI;
import cps.routing.RoutingCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

@RequiredInterfaces(required = {RoutingCI.class, CommunicationCI.class, RegistrationCI.class})
public class RoutingNode extends AbstractComponent{

	protected RoutingNode() {
		super(1, 0);
	}

}
