package com.zytan.sdpn.peer;

import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;

/**
 * smart device peer descriptor
 * @author tanzhongyi
 *
 */

public class SDPPeerDescriptor extends PeerDescriptor {
	
	//this sdpeer's apilist 
	private String apilist;
	
	public SDPPeerDescriptor(String name, String address, String key,String contactAddress, String api) {
		super(name,address,key,contactAddress);
		apilist = api;
	}
	
	public SDPPeerDescriptor() {
		super();
	}

	public String getAPIList() {
		return apilist;
	}
	
	public void setAPIList(String newAPIList) {
		apilist = newAPIList;
		
	}
	
	@Override 
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");
		builder.append(super.toString());
		builder.append("apilist: " + getAPIList() + NEW_LINE);   
        return builder.toString();
				
	}

}
