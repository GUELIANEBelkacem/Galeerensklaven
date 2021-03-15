package cps.networkAccess;

import java.util.HashSet;
import java.util.Set;

import cps.registration.RegistrationCI;
import cps.registration.RegistrationInboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import cps.connecteurs.NetworkAccessConnector;
import cps.info.AccessInfo;
import cps.info.address.NetworkAddressI;

@OfferedInterfaces(offered = { NetworkAccessorCI.class })
@RequiredInterfaces(required = {NetworkAccessorCI.class})
public class NetworkAccessor extends AbstractComponent implements NetworkAccessorCI{

	public static final String NaIP_URI = "naip-uri";
	public static final String NaOP_URI = "naop-uri";
	private Set<AccessInfo> ainfo = new HashSet<>();
	protected NetworkAccessingInboundPort naip;
	protected NetworkAccessingOutboundPort naop;
	private Set<NetworkAccessingOutboundPort> spreader = new HashSet<>();
	
	protected NetworkAccessor() throws Exception {
		super(1, 0);
		this.naip = new NetworkAccessingInboundPort(NaIP_URI, this);
		this.naop = new NetworkAccessingOutboundPort(NaOP_URI, this);
		this.naip.publishPort();
		this.naop.publishPort();
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
		spreadCo();
	}
	
	public Set<AccessInfo> getNetworkNodes(String ipuri) throws Exception{
		String tempUri = NetworkAccessingOutboundPort.generatePortURI();
		NetworkAccessingOutboundPort tempPort = new NetworkAccessingOutboundPort(tempUri, this);
		spreader.add(tempPort);
		this.doPortConnection(tempUri, ipuri, NetworkAccessConnector.class.getCanonicalName());
		
		return ainfo;
	}

	@Override
	public void spreadCo() {
		for(NetworkAccessingOutboundPort e : spreader) {
			e.spreadCo();
		}
		
	}


}
