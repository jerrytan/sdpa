package com.zytan.sdpn.msg;

/*
 * Copyright (C) 2010 University of Parma - Italy
 * 
 * This source code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Designer(s):
 * Marco Picone (picone@ce.unipr.it)
 * Fabrizio Caramia (fabrizio.caramia@studenti.unipr.it)
 * Michele Amoretti (michele.amoretti@unipr.it)
 * 
 * Developer(s)
 * Fabrizio Caramia (fabrizio.caramia@studenti.unipr.it)
 * 
 */




import it.unipr.ce.dsg.s2p.message.BasicMessage;
import it.unipr.ce.dsg.s2p.message.Payload;
import it.unipr.ce.dsg.s2p.peer.NeighborPeerDescriptor;


/**
 * Class <code>CallAPIMessage</code> implements a simple message sent by the peer to other peer.
 * The payload of CallAPIMessage contains the peer's api names.
 * 
 * @author tanzhongyi
 *
 */
public class CallAPIMessage extends BasicMessage {
	
	public static final String MSG_PEER_CALLAPI="peer_callapi"; 
	
	
	public CallAPIMessage(NeighborPeerDescriptor peerDesc,String callAPI) {

		super();
		
		Payload load = new Payload(peerDesc);
		load.addParam("CallAPI", callAPI);
		setPayload(load);
		setType(MSG_PEER_CALLAPI);
		
	
	}

}

