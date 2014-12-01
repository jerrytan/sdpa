package it.unipr.ce.dsg.s2p;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class JoinPeerActivity extends Activity {

	private Button sendJoinBut;

	private EditText editAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.join_peer);
		
		editAddress = (EditText)findViewById(R.id.editBootAddress);
		
		if(PeerActivity.peer!=null){
			editAddress.setText(PeerActivity.peer.getBootstrapPeer().getURL());
		}
		
		sendJoinBut = (Button)findViewById(R.id.sendJoin);
				
		sendJoinBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(PeerActivity.peer!=null){
					PeerActivity.peer.joinToPeer(PeerActivity.peer.getBootstrapPeer());
				}
				
				finish();
				
			}
		});

		
	}

}
