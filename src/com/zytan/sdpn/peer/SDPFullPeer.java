package com.zytan.sdpn.peer;

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


import it.unipr.ce.dsg.s2p.message.parser.JSONParser;
import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;
import it.unipr.ce.dsg.s2p.peer.NeighborPeerDescriptor;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;
import it.unipr.ce.dsg.s2p.peer.PeerListManager;

import it.unipr.ce.dsg.s2p.sip.Address;
import it.unipr.ce.dsg.s2p.util.FileHandler;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.zoolu.tools.Log;

import com.zytan.sdpn.msg.CallAPIMessage;
import com.zytan.sdpn.msg.JoinMessage;
import com.zytan.sdpn.msg.PeerListMessage;
import com.zytan.sdpn.msg.PingMessage;

/**
 * Class <code>SDPFullPeer</code> implements many features of a peer.
 * SDPFullPeer manages PEERLIST message and PING message. 
 * 
 * 
 * @author Fabrizio Caramia
 * @author tanzhongyi
 *
 */


public class SDPFullPeer extends FullPeer {

	protected SDPPeerConfig peerConfig;
	
	private FileHandler fileHandler;

	private Log log;
	
	protected String apiList;

	protected String bootstrap_addr;
	

	public SDPFullPeer(String pathConfig, String key) {
		super(pathConfig, key);

		init(pathConfig);
	}
	
	public void setBootstrap(String boot) {
		this.bootstrap_addr = boot;
	}

	public SDPFullPeer(String pathConfig, String key, String peerName, int peerPort) {
		super(pathConfig, key, peerName, peerPort);

		init(pathConfig);
	}

	public SDPFullPeer(String pathConfig, String key, String peerName, int peerPort,String apis) {
		super(pathConfig, key, peerName, peerPort);
		if (pathConfig !=null ) init(pathConfig);
	}
	
	
	private void init(String pathConfig){

		//peer configuration 
		this.peerConfig = new SDPPeerConfig(pathConfig);
		
		if (null != peerConfig.api_list) {
			apiList = peerConfig.api_list;			
			
		}

		//handler for write and read file
		fileHandler = new FileHandler();

		/*
		 * log - new istance
		 */
		if(nodeConfig.log_path!=null){
			if(!fileHandler.isDirectoryExists(nodeConfig.log_path))
				fileHandler.createDirectory(nodeConfig.log_path);

			log = new Log(nodeConfig.log_path+"info_"+peerDescriptor.getAddress()+".log", Log.LEVEL_MEDIUM); 

		}

	}


	@Override
	protected void onReceivedJSONMsg(JSONObject peerMsg, Address sender) {

		try {

			JSONObject params = peerMsg.getJSONObject("payload").getJSONObject("params");

			/*
			 * log - print info received message 
			 */
			if(nodeConfig.log_path!=null){
				String typeMsg = peerMsg.get("type").toString();
				int lengthMsg = peerMsg.toString().length();

				JSONObject info = new JSONObject();
				info.put("timestamp", System.currentTimeMillis());
				info.put("type", "recv");
				info.put("typeMessage", typeMsg);
				info.put("byte", lengthMsg);
				info.put("sender", sender.getURL());
				printJSONLog(info, log, false);
				//log.println(info.toString());

			}

			//add peer descriptor to list
			if(peerMsg.get("type").equals(PingMessage.MSG_PEER_PING)){

				SDPPeerDescriptor neighborPeerDesc = new SDPPeerDescriptor(params.get("name").toString(), params.get("address").toString(), params.get("key").toString(),
						params.get("contactAddress").toString(),params.getString("apiList").toString());
				addNeighborPeer(neighborPeerDesc);

				/*
				 * peer list - write 
				 */
				if(nodeConfig.list_path!=null){

					if(!fileHandler.isDirectoryExists(nodeConfig.list_path))
						fileHandler.createDirectory(nodeConfig.list_path);

					peerList.writeList(fileHandler.openFileToWrite(nodeConfig.list_path+peerDescriptor.getAddress()+".json"));

				}
				
				
				log.println("Another peer "+ params.get("name").toString() +" is ping me");

			}
			else if(peerMsg.get("type").equals(CallAPIMessage.MSG_PEER_CALLAPI)){
				
				String api = params.get("CallAPI").toString();
				onCallAPI(api);
				
			}
			else if(peerMsg.get("type").equals(PeerListMessage.MSG_PEER_LIST)){

				log.println("This peer get peerlist msg from \n"+peerMsg.toString());
				
				Iterator<String> iter = params.keys();
				
				while(iter.hasNext()){

					String key = (String) iter.next();

					JSONObject keyPeer = params.getJSONObject(key);
					
					JSONObject sdpdesc = keyPeer.getJSONObject("sdpdesc");
					String apilist= sdpdesc.getString("apilist");
					

					
					SDPPeerDescriptor neighborPeerDesc = new SDPPeerDescriptor(keyPeer.get("name").toString(), 
							keyPeer.get("address").toString(), keyPeer.get("key").toString(),keyPeer.get("contactAddress").toString(),apilist);

					if(keyPeer.get("contactAddress").toString()!="null")
						neighborPeerDesc.setContactAddress(keyPeer.get("contactAddress").toString());
					
					//log.println(neighborPeerDesc.toString());
					if (!neighborPeerDesc.getName().equals(peerDescriptor.getName())) {
						
						addPeer(new SDPNeighborPeerDescriptor(neighborPeerDesc));
						//log.println("neighbor "+neighborPeerDesc.getAddress()+ " has apis like "+neighborPeerDesc.getAPIList());

					}

					

				}
				
				printPeers();
					

			}

		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

	}

	public void printPeers() {
		log.println("Print peer list, size is " + peerList.size());
		//print peer list msg
		Iterator<NeighborPeerDescriptor> peers = (Iterator<NeighborPeerDescriptor>) peerList.values().iterator();
		
		while(peers.hasNext()){
			SDPNeighborPeerDescriptor peerDesc = (SDPNeighborPeerDescriptor)peers.next();
			log.println("----peer " + peerDesc.getName() +" has api like "+  peerDesc.getAPIList());
			callPeerAPI("lightOn",peerDesc);
			
		}
	}
	
	public void addPeer(SDPNeighborPeerDescriptor neighborPeer){
		peerList.put(neighborPeer.getKey(), neighborPeer);
	}
	
	@Override
	protected void onDeliveryMsgFailure(String peerMsg, Address receiver, String contentType) {

		String typeMessage = null;
		JSONObject jsonMsg = null;
		long rtt = 0;
		
		if(contentType.equals(JSONParser.MSG_JSON)){
			try {
				jsonMsg = new JSONObject(peerMsg);
				typeMessage= (String) jsonMsg.get("type");

				long sendedTime = (Long) jsonMsg.get("timestamp");
				long receivedTime = System.currentTimeMillis();
				rtt = receivedTime - sendedTime;

			} catch (JSONException e) {
				e.printStackTrace();
			}

			/*
			 *log - print info sent message 
			 */
			if(nodeConfig.log_path!=null){

				try {
					JSONObject info = new JSONObject();
					info.put("timestamp", System.currentTimeMillis());
					info.put("type", "sent");
					info.put("typeMessage", typeMessage);
					info.put("transaction", "failed");
					info.put("receiver", receiver.getURL());
					info.put("RTT", rtt);
					info.put("byte", peerMsg.length());
					printJSONLog(info, log, false);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}

	}

	@Override
	protected void onDeliveryMsgSuccess(String peerMsg, Address receiver, String contentType) {

		String typeMessage = null;
		JSONObject jsonMsg = null;
		long rtt = 0;
		
		if(contentType.equals(JSONParser.MSG_JSON)){
			try {
				jsonMsg = new JSONObject(peerMsg);
				typeMessage= (String) jsonMsg.get("type");

				long sendedTime = (Long) jsonMsg.get("timestamp");
				long receivedTime = System.currentTimeMillis();
				rtt = receivedTime - sendedTime;

			} catch (JSONException e) {
				e.printStackTrace();
			}

			/*
			 *log - print info sent message 
			 */
			if(nodeConfig.log_path!=null){

				try {
					JSONObject info = new JSONObject();
					info.put("timestamp", System.currentTimeMillis());
					info.put("type", "sent");
					info.put("typeMessage", typeMessage);
					info.put("transaction", "successful");
					info.put("receiver", receiver.getURL());
					info.put("RTT", rtt);
					info.put("byte", peerMsg.length());
					printJSONLog(info, log, false);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}

	}



	public void joinToBootstrapPeer(){

		if( bootstrap_addr!=null || peerConfig.bootstrap_peer!=null ){
            SDPPeerDescriptor sdpeerDescriptor = new SDPPeerDescriptor(peerDescriptor.getName(),peerDescriptor.getAddress(),
            		peerDescriptor.getKey(),peerDescriptor.getContactAddress(),apiList);
			JoinMessage newJoinMsg = new JoinMessage(sdpeerDescriptor);
			//newJoinMsg.setNumPeerList(peerConfig.req_npeer);

			if (bootstrap_addr !=null) {
				send(new Address(bootstrap_addr), newJoinMsg);
			}
			else {
				send(new Address(peerConfig.bootstrap_peer), newJoinMsg);
			}

		}

	}

	
	public void pingToPeer(String address){

		PingMessage newPingMsg = new PingMessage(peerDescriptor);

		//!!!!!!send to local address 
		//log.println(this.peerDescriptor + " address "+  address);
		log.println("This peer is ping another peer "+ address);
		send(new Address(address), null, newPingMsg);

	}
	
	
	public void pingToPeerFromList(){

		PingMessage newPingMsg = new PingMessage(peerDescriptor);

		if(!peerList.isEmpty()){

			Iterator<String> iter = peerList.keySet().iterator();

			//send pingMessage to first peer in the PeerListManager
			String key = iter.next();

			NeighborPeerDescriptor neighborPeer = peerList.get(key);

			send(neighborPeer, newPingMsg);
			
			//add by zytan
			log.println("this peer "+  peerDescriptor.getAddress() + " is ping another peer "+neighborPeer.getAddress());
			
		}
	}

	public void pingToPeerRandomFromList(){

		PingMessage newPingMsg = new PingMessage(peerDescriptor);
		NeighborPeerDescriptor neighborPeer;

		if(!peerList.isEmpty()){
			//get set size
			int nKeys =  peerList.keySet().size();
			//get a random number
			int indexKey = (int) (Math.random()*nKeys);
			Iterator<String> iter = peerList.keySet().iterator();
			int i=0;
			String key = null;
			//break while when i is equal to random number
			while(iter.hasNext()){
				key = iter.next();

				if(i==indexKey){
					break;
				}
				i++;
			}
			//send ping message to peer	
			if(key!=null){
				neighborPeer = peerList.get(key);
				send(neighborPeer, newPingMsg);
			}


		}
	}

	/*
	 * get api list for this peer
	 */
	public  String getAPIList() {
		return apiList;
	}
	
	public String[] getAPIAsList() {
		return apiList.split(",");
	}
	
	


	public static void main(String[] args) {

		boolean active = true;

		if(args.length!=0){
			SDPFullPeer peer = null;
			if(args.length==3){
				//args[0]=file peer configuration args[1]=key
				peer = new SDPFullPeer(args[0], args[1]);

			}
			else if(args.length==5){
				//args[0]=file peer configuration args[1]=key args[2]=peer name args[3]=peer port
				peer = new SDPFullPeer(args[0], args[1], args[2], new Integer(args[3]));

			}
			for(int i=0; i<args.length; i++){

				/*
				 * join to bootstrapPeer
				 */
				if(args[i].equals("-j")){ 
					peer.joinToBootstrapPeer();

				}
				/*
				 * request public address from SBC
				 */
				else if(args[i].equals("-s")){
					peer.contactSBC();
				}
				/*
				 * join to bootstrapPeer, wait and send ping message to random peer
				 */
				else if(args[i].equals("-jp")){

					peer.joinToBootstrapPeer();
					//wait for 3 seconds
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					peer.pingToPeerRandomFromList();
				}
				/*
				 * join to bootstrapPeer, wait and send ping message to random peer recursively
				 */
				else if(args[i].equals("-jr")){

					peer.joinToBootstrapPeer();

					while(active){

						//wait for 15 seconds
						try {
							Thread.sleep(15000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//ping bootstrap to keep live
						peer.pingToPeer(peer.peerConfig.bootstrap_peer);
						
						//call  peer api
						//peer.callPeerAPI("lightOn");
					}
				}

				else if(args[i].equals("-p")){

					peer.pingToPeer(args[5]);
				}

				else if(args[i].equals("-sd")){

					peer.contactSBC();
					try {
						Thread.sleep(7000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					peer.disconnectGWP();

				}
				/*
				 * contact SBC, wait, join to bootstrapPeer, wait and send ping message to random peer recursively
				 */
				else if(args[i].equals("-a")){

					peer.contactSBC();

					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					peer.joinToBootstrapPeer();

					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					peer.pingToPeerRandomFromList();
				}


			}

		}
	}

	private void callPeerAPI(String callAPI,SDPNeighborPeerDescriptor neighborPeer ) {

		

	
			
			CallAPIMessage callAPIMsg = new CallAPIMessage(neighborPeer,callAPI);
			send(neighborPeer, callAPIMsg);
			
			log.println("\nThis peer "+  peerDescriptor.getName() + " is call another peer "+neighborPeer.getName()+ " API like "+ callAPI);
			
		
		
	}
	
	protected void onCallAPI(String api) {
		log.println("API "+api+" is being called");
	}
	
	public ArrayList<SDPNeighborPeerDescriptor> getPeerList(){

		
		ArrayList<SDPNeighborPeerDescriptor> addressList = new ArrayList<SDPNeighborPeerDescriptor>();
		
		Iterator<NeighborPeerDescriptor> iter = this.peerList.values().iterator();
		
		NeighborPeerDescriptor peerDesc = new NeighborPeerDescriptor();
		
		Integer sizeList = new Integer(this.peerList.size());
		
		
		while(iter.hasNext()){
			
			peerDesc = (SDPNeighborPeerDescriptor) iter.next();	
			addressList.add((SDPNeighborPeerDescriptor) peerDesc);
			
		}
		
		return addressList;
	}

}
