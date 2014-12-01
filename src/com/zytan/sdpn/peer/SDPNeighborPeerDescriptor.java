package com.zytan.sdpn.peer;

import it.unipr.ce.dsg.s2p.peer.NeighborPeerDescriptor;


public class SDPNeighborPeerDescriptor extends NeighborPeerDescriptor {
	private SDPPeerDescriptor sdpdesc;
	
	public SDPNeighborPeerDescriptor(SDPPeerDescriptor desc) {
		super(desc);
		sdpdesc = desc;
		
	}
	
	
	
	@Override
	public String toString() {
		return sdpdesc.toString();
	}

	public String getAPIList() {
		return sdpdesc.getAPIList();
	}
}
