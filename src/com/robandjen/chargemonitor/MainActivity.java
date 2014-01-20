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

import android.app.Activity;
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
		
		finish();
	}

}
