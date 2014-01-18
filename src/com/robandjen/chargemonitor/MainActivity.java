package com.robandjen.chargemonitor;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent batteryintent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		if (batteryintent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0) {
			startService(new Intent(getApplicationContext(),ChargeService.class));
		}
		
		Notification.Builder builder = new Notification.Builder(this);
		builder.setSmallIcon(android.R.drawable.ic_lock_idle_low_battery)
			.setContentTitle("Charge Monitor")
			.setContentText("Charge Monitor is enabled");
		
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.notify(NotificationIDs.StartMessage,builder.build());
		
		finish();
	}

}
