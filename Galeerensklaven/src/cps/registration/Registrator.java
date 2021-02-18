package cps.registration;

import java.util.HashSet;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;

@OfferedInterfaces(offered = {RegistrationCI.class})
public class Registrator extends AbstractComponent {
	public static final String RegIP_URI;
	private Set<ConnectionInfo> cInfo = new HashSet<>();

	protected Registrator() {
		super(1, 0);
		// TODO Auto-generated constructor stub
	}

	protected Registrator(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}
	
	
	public Set<ConnectionInfo> registerTerminal()

}
