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
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
	final static String TAG = "BootReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.e(TAG,String.format("Unexpected intent %s", intent.getAction()));
			return;
		}

		Intent batteryintent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		if (batteryintent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0) {
			Log.i(TAG,"Plugged in, starting monitoring service");
			context.startService(new Intent(context,ChargeService.class));
		}
		else {
			Log.i(TAG,"Not plugged in");
		}
	}

}
