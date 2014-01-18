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
