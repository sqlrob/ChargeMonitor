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
			.setContentTitle(getString(R.string.app_name))
			.setContentText(getString(R.string.enabled))
			.setAutoCancel(true);
		
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.notify(NotificationIDs.StartMessage,builder.build());
		
		finish();
	}

}
