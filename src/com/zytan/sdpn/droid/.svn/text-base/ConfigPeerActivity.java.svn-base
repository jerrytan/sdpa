package it.unipr.ce.dsg.s2p;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class ConfigPeerActivity extends Activity {

	private Button saveBut;
	private EditText nameEdit;
	private EditText bootstrapEdit;
	private EditText sbcEdit;
	private CheckBox checkbox;
	private String checkReach;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		 setContentView(R.layout.config_peer);
		 
		 checkReach="no";
		
		 //nameEdit = (EditText)findViewById(R.id.nameEdit);
		 bootstrapEdit = (EditText)findViewById(R.id.bootstrapEdit);
		 sbcEdit = (EditText)findViewById(R.id.sbcEdit);
		 bootstrapEdit.setText("bootstrap@192.168.1.154:5080");
		 
		 checkbox = (CheckBox) findViewById(R.id.reachCheckBox);
		 checkbox.setOnClickListener(new OnClickListener() {
		     public void onClick(View v) {
		         // Perform action on clicks, depending on whether it's now checked
		         if (((CheckBox) v).isChecked()) {
		        	 checkReach="yes";
		         } else {
		        	 checkReach="no";
		         }
		     }
		 });
		 
		 
		 saveBut = (Button)findViewById(R.id.save);
		 
		 saveBut.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String bootstrapPeer = bootstrapEdit.getText().toString();
				String sbcAddress = sbcEdit.getText().toString();
				if(PeerActivity.peer!=null)
					PeerActivity.peer.setConfiguration(sbcAddress, bootstrapPeer, checkReach);
				
				finish();
				
			}
		});
	}
	
}
