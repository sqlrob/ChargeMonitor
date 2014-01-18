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
