package it.unipr.ce.dsg.s2p;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SBCActivity extends Activity {

	private EditText sbcAddressEdit;
	private Button sbcBut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		sbcAddressEdit = (EditText)findViewById(R.id.sbcAddress);
		sbcBut = (Button)findViewById(R.id.sbcButton);

		sbcBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(PeerActivity.peer!=null){
					PeerActivity.peer.contactSBC();
				}
			}
		});

	}

}
