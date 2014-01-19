//  This file is part of ChargeMonitor.
//
//  	ChargeMonitor is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    ChargeMonitor is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with ChargeMonitor.  If not, see <http://www.gnu.org/licenses/>.
//    	
/* Copyright 2014, Robert Myers */
package com.robandjen.chargemonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PlugReceiver extends BroadcastReceiver {

	static final String TAG = "PlugReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent svcintent = new Intent(context,ChargeService.class);
		if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
			context.startService(svcintent);
			Log.i(TAG,"Received power connection event");
		} else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
			context.stopService(svcintent);
			Log.i(TAG,"Received power disconnection event");
		} else {
			Log.w(TAG,String.format("Received unexpected event %s",intent.getAction()));
		}
	}
}
