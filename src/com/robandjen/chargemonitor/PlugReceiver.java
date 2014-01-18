package com.robandjen.chargemonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PlugReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent svcintent = new Intent(context,ChargeService.class);
		if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
			context.startService(svcintent);
		} else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
			context.stopService(svcintent);
		}
	}
}
