package com.zytan.sdpn.droid;

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

import java.util.ArrayList;
import java.util.Iterator;

import com.zytan.sdpa.peerdroid.R;
import com.zytan.sdpn.msg.JoinMessage;
import com.zytan.sdpn.msg.PeerListMessage;
import com.zytan.sdpn.msg.PingMessage;
import com.zytan.sdpn.peer.SDPFullPeer;
import com.zytan.sdpn.peer.SDPPeerDescriptor;

import android.app.Activity;
import android.widget.Toast;
import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;
import it.unipr.ce.dsg.s2p.peer.NeighborPeerDescriptor;
import it.unipr.ce.dsg.s2p.peer.Peer;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;
import it.unipr.ce.dsg.s2p.sip.Address;

/**
 * Class <code>SimplePeer</code> implements many features of a peer.
 * SimplePeer extend the Peer class of sip2peer.
 * 
 * 
 * @author Fabrizio Caramia , tanzhongyi
 *
 */

public class SimpleSDPPeer extends SDPFullPeer {
	
	private Activity peerActivity=null;
	private Address bootstrapPeer;
	
	
	
	public SimpleSDPPeer(Activity activity, String key, String peerName,
			int peerPort,String apilist) {
		super(null, key, peerName, peerPort,apilist);
		peerActivity = activity;
		initConfig();
		
	}
	
	public SimpleSDPPeer(String pathConfig, String key, String peerName,
			int peerPort) {
		super(pathConfig, key, peerName, peerPort);
		
	}
	
	private void initConfig() {
		this.apiList= peerActivity.getBaseContext().getString(R.string.apilist);
		peerDescriptor.setName(peerActivity.getBaseContext().getString(R.string.peername));
		setBootstrapPeer(new Address(peerActivity.getBaseContext().getString(R.string.bootstrap)));

	}
	
	public String getAddressPeer(){
		
		
		return getAddress().getURL();
	}
	
	public String getContactAddressPeer(){
		
		return peerDescriptor.getContactAddress();
	}
	
	public ArrayList<String> getListAddressPeer(){

		
		ArrayList<String> addressList = new ArrayList<String>();
		
		Iterator<NeighborPeerDescriptor> iter = this.peerList.values().iterator();
		
		PeerDescriptor peerDesc = new PeerDescriptor();
		
		Integer sizeList = new Integer(this.peerList.size());
		
		
		while(iter.hasNext()){
			
			peerDesc = (PeerDescriptor) iter.next();	
			addressList.add(peerDesc.getContactAddress());
			
		}
		
		return addressList;
	}
	
	public void pingToPeer(String address){
		
		PingMessage newPingMsg = new PingMessage(peerDescriptor);

		//!!!!!!send to local address 
		send(new Address(address), newPingMsg);

	}
	
    public void joinToPeer(Address address){
		
    	SDPPeerDescriptor desc = new SDPPeerDescriptor(peerDescriptor.getName(),peerDescriptor.getAddress(),
    			peerDescriptor.getKey(),peerDescriptor.getContactAddress(),apiList);
		JoinMessage newJoinMsg = new JoinMessage(desc);

		//!!!!!!send to local address 
		send(new Address(address), newJoinMsg);

	}
	
	public void setConfiguration(String sbc, String bootstrap, String reachability){
		
		nodeConfig.sbc=sbc;
		nodeConfig.test_address_reachability=reachability;
		//setBootstrapPeer(new Address(bootstrap));
		
	}
	
	public void contactSBC(){
		
		requestPublicAddress();
		
	}

	public Address getBootstrapPeer() {
		return bootstrapPeer;
	}

	private void setBootstrapPeer(Address bootstrapPeer) {
		this.bootstrapPeer = bootstrapPeer;
	}
	
	public String getSBCAddress(){
		
		return nodeConfig.sbc;
	}
	

	@Override
	protected void onReceivedJSONMsg(JSONObject jsonMsg, Address sender) {
		super.onReceivedJSONMsg(jsonMsg, sender);
		
		try {
			
			JSONObject params = jsonMsg.getJSONObject("payload").getJSONObject("params");
			
			if(jsonMsg.get("type").equals(PeerListMessage.MSG_PEER_LIST)){
				
				  PeerActivity.handler.post(new Runnable() {
	                  public void run() {
	             		 	Toast toast = Toast.makeText(peerActivity.getBaseContext(),"Received: "+ PeerListMessage.MSG_PEER_LIST ,Toast.LENGTH_LONG);
	             		 	toast.show();
	             		 	//((PeerActivity)peerActivity).peerListChanged();
	                      }
	              });
				

			}
			
		} catch (JSONException e) {
		
			e.printStackTrace();
		}
		
		
	}

	public void setPeerActivity(PeerActivity peerActivity) {
		this.peerActivity=peerActivity;
		initConfig();
		
	}

	@Override
	protected void onDeliveryMsgFailure(String arg0, Address arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDeliveryMsgSuccess(String arg0, Address arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

		

}
