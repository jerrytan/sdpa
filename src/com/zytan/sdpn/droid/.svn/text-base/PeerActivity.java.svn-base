package it.unipr.ce.dsg.s2p;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PeerActivity extends Activity {
	
	private EditText addressEdit;
	private Button sendBut;
	private TextView addressPeer;
	private Button clearBut;
	private TextView contactAddressPeer;
	private Button closeBut;

	private static final int MENU_CONFIG = 1;
	private static final int MENU_BOOT = 2;
	private static final int MENU_LIST = 3;
	private static final int MENU_SBC = 4;
	
	private static final int DIALOG_CONFIG = 5;
	private static final int DIALOG_BOOTSTRAP = 6;
	
	private static final int ACTIVITY_PEER_LIST=7;
	
	public static Handler handler = new Handler();

	
	public static PeerDroid peer = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		
		addressPeer = (TextView)findViewById(R.id.addressPeer);
		contactAddressPeer = (TextView)findViewById(R.id.contAddressPeer);
		addressEdit = (EditText)findViewById(R.id.addressEditor);
		
		sendBut = (Button)findViewById(R.id.sendPing);
		clearBut = (Button)findViewById(R.id.clearButton);
		closeBut = (Button)findViewById(R.id.closeApp);
		
		sendBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(peer!=null){
					peer.pingToPeer(addressEdit.getText().toString());
					addressEdit.setText("");
				}
				else
					showDialog(DIALOG_CONFIG);

			}
		});
		
		clearBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				addressEdit.setText("");

			}
		});
		
		closeBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				peer.halt();
				finish();

			}
		});

	}

	
	
	@Override
	protected void onStart() {
		super.onStart();
		if(peer==null){
			init("peerDroid");
		
			addressPeer.setText("Address: "+peer.getAddressPeer());
		}
	}

	
	

	@Override
	protected void onResume() {
		
		super.onResume();
		contactAddressPeer.setText("Contact Address: "+peer.getContactAddressPeer());
		
	}



	private void init(String name){
		
		peer = new PeerDroid(null, "4654amv65d4as4d65a4", name,  50250);
		peer.setMainGUI(this);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		boolean result = super.onCreateOptionsMenu(menu);
		//create menu
		MenuItem configuration = menu.add(Menu.NONE, MENU_CONFIG, 1, "Configuration");
		configuration.setIcon(R.drawable.config);
		MenuItem bootstrap = menu.add(Menu.NONE, MENU_BOOT, 2, "Bootstrap");
		bootstrap.setIcon(R.drawable.boot);
		MenuItem peerlist = menu.add(Menu.NONE, MENU_LIST, 3, "Peer List");
		peerlist.setIcon(R.drawable.list);
		MenuItem sbc = menu.add(Menu.NONE, MENU_SBC, 4, "SBC");
		sbc.setIcon(R.drawable.sbc);

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//select menu
		int id = item.getItemId();
		switch(id){
		case MENU_CONFIG:
			configPeer();
			return true;
		case MENU_BOOT:
			contactBootstrap();
			return true;
		case MENU_LIST:
			viewPeerList();
			return true;
		case MENU_SBC:
			contactSBC();
			return true;	

		}

		return super.onOptionsItemSelected(item);


	}

	private void contactSBC() {
		
		peer.contactSBC();

	}

	private void viewPeerList() {
		
		//call activity PeerList
		Intent intent = new Intent(this, PeerListActivity.class);
		startActivityForResult(intent, ACTIVITY_PEER_LIST);
	}

	private void contactBootstrap() {

		if(peer.getBootstrapPeer()!=null){
			Intent intent = new Intent(this, JoinPeerActivity.class);
			startActivity(intent);
		}
		else
			showDialog(DIALOG_CONFIG);
	}

	private void configPeer() {

		//call activity ConfigPeer
		Intent intent = new Intent(this, ConfigPeerActivity.class);
		startActivity(intent);

	}
	


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		Bundle extras = data.getExtras();
		 
		switch(requestCode) {
        case ACTIVITY_PEER_LIST:
        	String peerAddress = extras.getString(PeerListActivity.PEER_ADDRESS);
        	addressEdit.setText(peerAddress);
        	break;
		}
        	
	}



	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		
	    switch(id) {
	    case DIALOG_CONFIG:
	    	AlertDialog.Builder builderConf = new AlertDialog.Builder(this);
	    	builderConf.setMessage("Set peer configuration!")
	    			.setTitle("Warning")
	    	       .setCancelable(false)
	    	       .setPositiveButton("Close", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                dismissDialog(DIALOG_CONFIG);
	    	           }
	    	       });
	    	dialog = builderConf.create();
	        break;
	    case DIALOG_BOOTSTRAP:
	    	AlertDialog.Builder builderBoot = new AlertDialog.Builder(this);
	    	builderBoot.setMessage("Set the address of BootstrapPeer in the Configuration section!")
	    			.setTitle("Warning")
	    	       .setCancelable(false)
	    	       .setPositiveButton("Close", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                dismissDialog(DIALOG_BOOTSTRAP);
	    	           }
	    	       });
	    	dialog = builderBoot.create();
	        break;
	    default:
	        dialog = null;
	    }
	    return dialog;

		
	}
	
	
}