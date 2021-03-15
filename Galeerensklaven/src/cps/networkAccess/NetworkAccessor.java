package cps.networkAccess;

import java.util.HashSet;
import java.util.Set;

import cps.registration.RegistrationCI;
import cps.registration.RegistrationInboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import cps.info.AccessInfo;
import cps.info.address.NetworkAddressI;

@OfferedInterfaces(offered = { NetworkAccessorCI.class })
@RequiredInterfaces(required = {NetworkAccessorCI.class})
public class NetworkAccessor extends AbstractComponent {

	public static final String NaIP_URI = "naip-uri";
	private Set<AccessInfo> ainfo = new HashSet<>();
	protected NetworkAccessingInboundPort naip;
	
	protected NetworkAccessor() throws Exception {
		super(1, 0);
		this.naip = new NetworkAccessingInboundPort(NaIP_URI, this);
		this.naip.publishPort();
	}
	
	/*
	public NetworkAccessor(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}

	public NetworkAccessor(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}
	*/
	
	
	
	public void registerAccessPoint(NetworkAddressI address, String accessorIpUri) {
		ainfo.add(new AccessInfo(address, accessorIpUri));
	}
	
	public Set<AccessInfo> getNetworkNodes(){
		
		return ainfo;
	}


}
