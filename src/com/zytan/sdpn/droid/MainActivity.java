package com.zytan.sdpn.droid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zytan.sdpa.peerdroid.R;
import com.zytan.sdpn.peer.SDPFullPeer;
import com.zytan.sdpn.peer.SDPNeighborPeerDescriptor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	
	private ListView peerListView;
	private ListView apiListView;
	private List<String> activity_peerList = new ArrayList<String>();
	private List<String> activity_apiList = new ArrayList<String>();

	private boolean lightOn = false;
	
	private SDPFullPeer peer;
	private ArrayAdapter<String> adapter_api = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.mainsdpactivity);
			
			//set up information for this peer from strings
			initPeer();
			
			//set up light
			initLight();

			//set up peer list information
			initPeerListView();
			
			
	}
	
	private void initPeer() {
		
		peer = new SimpleSDPPeer(this, "4654amv65d4as4d65a4", getString(R.string.peername),  50251,getString(R.string.apilist));
		peer.setBootstrap(getString(R.string.bootstrap));
		peer.joinToBootstrapPeer();

		
	}

	
	private void initPeerListView() {
		peerListView = (ListView) this.findViewById(R.id.peerlist);
		apiListView = (ListView) this.findViewById(R.id.apilist);
		ArrayAdapter<String> adapter_peers = new ArrayAdapter<String>(MainActivity.this,
				android.R.layout.simple_list_item_1, activity_peerList);
		
		Iterator<SDPNeighborPeerDescriptor> peers = (Iterator<SDPNeighborPeerDescriptor>) (peer.getPeerList().iterator());
		
		while(peers.hasNext()){
			SDPNeighborPeerDescriptor peerDesc = peers.next();
			activity_peerList.add(peerDesc.getName());
			
		}
		
		peerListView.setAdapter(adapter_peers);
		
		adapter_api = new ArrayAdapter<String>(MainActivity.this,
				android.R.layout.simple_list_item_1, activity_apiList);
		apiListView.setAdapter(adapter_api);
		
		peerListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
					String peerName = activity_peerList.get(position);
					activity_apiList.clear();
					
					Iterator<SDPNeighborPeerDescriptor> peers = (Iterator<SDPNeighborPeerDescriptor>) (peer.getPeerList().iterator());
					
					while(peers.hasNext()){
						SDPNeighborPeerDescriptor peerDesc = peers.next();
						if (peerName.equals(peerDesc.getName())) {
							String[] apilist = peerDesc.getAPIList().split(",");
							for(int i=0;i< apilist.length;i++) {
								activity_apiList.add(apilist[i]);
							}
							
						}
						
					}
					
					
					adapter_api.notifyDataSetChanged();
			}
			
		});
		
	}

	public void peerListChanged () {
		adapter_api.notifyDataSetChanged();
	}
	private void initLight() {
		Button lightButton = (Button)this.findViewById(R.id.btn_light);
		final ImageView lightImage = (ImageView)this.findViewById(R.id.img_view);
		lightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (lightOn) {
					lightImage.setImageResource(R.drawable.lightoff);
					lightOn = false;
				}
				else {
					lightImage.setImageResource(R.drawable.lighton);
					lightOn = true;
				}
				
			}
			
		});
	}
}
