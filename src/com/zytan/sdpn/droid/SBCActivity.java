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

import com.zytan.sdpa.peerdroid.R;

import com.zytan.sdpn.droid.SimpleSDPPeer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SBCActivity extends Activity {

	private EditText sbcAddressEdit;
	private Button sbcBut;

/*	@Override
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
*/
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.cont_sbc);

		sbcAddressEdit = (EditText)findViewById(R.id.sbcAddress);
		sbcBut = (Button)findViewById(R.id.sbcButton);

		sbcBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				showAudioInputDialog();

			}
		});

	}

	/**
	 * 
	 */
	private void showAudioInputDialog() {
		
	}
}
