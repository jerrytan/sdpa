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

import java.util.Iterator;

import it.unipr.ce.dsg.s2p.message.BasicMessage;
import it.unipr.ce.dsg.s2p.message.parser.JSONParser;
import it.unipr.ce.dsg.s2p.org.json.JSONException;
import it.unipr.ce.dsg.s2p.org.json.JSONObject;
import it.unipr.ce.dsg.s2p.peer.NeighborPeerDescriptor;
import it.unipr.ce.dsg.s2p.peer.Peer;
import it.unipr.ce.dsg.s2p.peer.PeerListManager;
import it.unipr.ce.dsg.s2p.sip.Address;
import it.unipr.ce.dsg.s2p.util.FileHandler;

import org.zoolu.tools.Log;

import com.google.gson.Gson;
import com.zytan.sdpn.msg.JoinMessage;
import com.zytan.sdpn.msg.PeerListMessage;
import com.zytan.sdpn.msg.PingMessage;

 

/**
 * Class <code>BootstrapPeer</code> implements a simple Bootstrap Peer.
 * BootstrapPeer manages JOIN peer message.
 * 
 * 
 * @author Fabrizio Caramia
 * @author tanzhongyi
 * 
 */

public class SDPBootstrapPeer extends Peer {

	private Log log;
	private FileHandler fileHandler;
	private PeerListManager sdppeerList;

	public SDPBootstrapPeer(String pathConfig, String key) {
		super(pathConfig, key);
		init();
	}

	private void init() {

		if (nodeConfig.log_path != null) {

			// handler for write and read file
			fileHandler = new FileHandler();

			if (!fileHandler.isDirectoryExists(nodeConfig.log_path))
				fileHandler.createDirectory(nodeConfig.log_path);

			log = new Log(nodeConfig.log_path + "info_"
					+ peerDescriptor.getAddress() + ".log", Log.LEVEL_MEDIUM);
			sdppeerList = new PeerListManager();
		}

		while (true) {

			// wait for 30 seconds
			try {
				Thread.sleep(10000);
				//empty list for every 30s, wait ping
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// ping to full peer

			updatePeerListToPeers();

		}
	}

	/*
	 * update peer list of this bootstrap to all peers
	 */
	private void updatePeerListToPeers() {

		if (!sdppeerList.isEmpty()) {

			PeerListMessage newPLMsg = null;

			// create message and add the peer list
			newPLMsg = new PeerListMessage(this.sdppeerList);

			//log.println("  In updatePeerListToPeers() PeerList Mesg is \n"+newPLMsg.toString());

			Iterator<String> iter = sdppeerList.keySet().iterator();
			while (iter.hasNext()) {

				String key = iter.next();

				NeighborPeerDescriptor neighborPeer = sdppeerList.get(key);

				send(neighborPeer, newPLMsg);

				log.println("This bootstrap is call updatePeerListToPeers to peer\n---- "
								+ neighborPeer.getAddress() +"\n" + newPLMsg.toString());
			}
		}

	}
	
	/**
	 * To send peer message through <code>toContactAddress</code>
	 * 
	 * @param toAddress destination address (local)
	 * @param toContactAddress contact address (address)
	 * @param message the peer message
	 */

	public void send(Address toAddress, Address toContactAddress, BasicMessage message){
		
		if(nodeConfig.content_msg.equals("text")){
			sendMessage(toAddress, toContactAddress, new Address(peerDescriptor.getAddress()), message.toString(), message.getType());
		}
		else{
			// Convert the message into JSON format
			Gson gson = new Gson();
			String jsonString = gson.toJson(message);
			//log.println("Gson message is "+ jsonString);

			sendMessage(toAddress, toContactAddress, new Address(peerDescriptor.getAddress()), jsonString, "application/json");
		}

	}

	@Override
	protected void onReceivedJSONMsg(JSONObject peerMsg, Address sender) {

		try {
			/*
			 * log - print info received message
			 */

			if (nodeConfig.log_path != null) {

				String typeMsg = peerMsg.get("type").toString();
				int lengthMsg = peerMsg.toString().length();

				JSONObject info = new JSONObject();
				info.put("timestamp", System.currentTimeMillis());
				info.put("type", "recv");
				info.put("typeMessage", typeMsg);
				info.put("byte", lengthMsg);
				info.put("sender", sender.getURL());
				printJSONLog(info, log, false);
			}

			// add peer descriptor to list
			if (peerMsg.get("type").equals(JoinMessage.MSG_PEER_JOIN)) {

				log.print(
						  "\nThis network has "
								+ sdppeerList.size() + " peers "
								+ "and this Bootstrap receive a Join message.");

				JSONObject params = peerMsg.getJSONObject("payload")
						.getJSONObject("params");
				log.println("\n Peer----" + params.get("name").toString()
						+ " is asking to join ");
				log.println("----It has several APIs like "
						+ params.get("api_list").toString());

				SDPPeerDescriptor neighborPD = new SDPPeerDescriptor(params
						.get("name").toString(), params.get("address")
						.toString(), params.get("key").toString(), params.get(
						"contactAddress").toString(), params.get("api_list")
						.toString());
				// log.println("----This neigbor's desc is "+neighborPD.toString());
				NeighborPeerDescriptor neighborPeer = addNeighborPeer(neighborPD);
     			log.println("----This peer's desc is \n"
					  + neighborPeer.toString());

				log.println( "----This network has "
						+ sdppeerList.size() + " peers now\n");

			}
			// else no list to send
			// update peerlist based on ping
			if (peerMsg.get("type").equals(PingMessage.MSG_PEER_PING)) {
				log.println("\nGet Ping message.");
				JSONObject params = peerMsg.getJSONObject("payload")
						.getJSONObject("params");
				log.println("----"+ params.get("name").toString()
						+ " is ping to bootstrap \n ");

				// need to update sdppeerlist

				// log.println("\n it has several APIs like "+
				// params.get("api_list").toString() + "  \n ");

			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	protected void onDeliveryMsgFailure(String peerMsg, Address receiver,
			String contentType) {

		String typeMessage = null;
		JSONObject jsonMsg = null;
		long rtt = 0;

		if (contentType.equals(JSONParser.MSG_JSON)) {
			try {
				jsonMsg = new JSONObject(peerMsg);
				typeMessage = (String) jsonMsg.get("type");

				long sendedTime = (Long) jsonMsg.get("timestamp");
				long receivedTime = System.currentTimeMillis();
				rtt = receivedTime - sendedTime;

			} catch (JSONException e) {
				e.printStackTrace();
			}

			/*
			 * log - print info sent message
			 */
			if (nodeConfig.log_path != null) {

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
	protected void onDeliveryMsgSuccess(String peerMsg, Address receiver,
			String contentType) {

		String typeMessage = null;
		JSONObject jsonMsg = null;
		long rtt = 0;

		if (contentType.equals(JSONParser.MSG_JSON)) {

			try {
				jsonMsg = new JSONObject(peerMsg);
				typeMessage = (String) jsonMsg.get("type");

				long sendedTime = (Long) jsonMsg.get("timestamp");
				long receivedTime = System.currentTimeMillis();
				rtt = receivedTime - sendedTime;

			} catch (JSONException e) {
				e.printStackTrace();
			}

			/*
			 * log - print info sent message
			 */
			if (nodeConfig.log_path != null) {

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

	// NeighborPeerDescriptor neighborPeer = addNeighborPeer(neighborPD);

	public NeighborPeerDescriptor addNeighborPeer(SDPPeerDescriptor desc) {
		SDPNeighborPeerDescriptor neighbor = new SDPNeighborPeerDescriptor(desc);

		sdppeerList.put(desc.getKey(), neighbor);
		return neighbor;

	}

	public static void main(String[] args) {

		if (args.length != 0) {
			System.out.printf("Begin to bootstrap with paramaters %s %S\n",
					args[0], args[1]);
			SDPBootstrapPeer peer = new SDPBootstrapPeer(args[0], args[1]);
			System.out.println(peer.toString() + "Bootstrap is running");
			
				
		} else
			System.out.println("run python command: startBootstrapPeer.py");

	}

}
