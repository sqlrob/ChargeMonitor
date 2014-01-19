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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class ChargeService extends Service implements Runnable {

	private Handler mHandler;
	final int delay = 10*60*1000;
	private IntentFilter mFilter;
	int mLastPercent = -1;
	Notification.Builder mBuilder;
	final static String TAG = "ChargeService";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.i(TAG,String.format("Creating charge service, delay=%d",delay));
		
		mBuilder = new Notification.Builder(this);
		mBuilder.setSmallIcon(android.R.drawable.ic_lock_idle_low_battery)
			.setContentTitle("Battery charge warning")
			.setOngoing(true);
		
		mHandler = new Handler();
		mFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		mHandler.post(this);
	}

	@Override
	public void onDestroy() {
		mHandler.removeCallbacks(this);
		mHandler = null;
		super.onDestroy();
	}

	@Override
	public void run() {
		Intent batteryintent = registerReceiver(null, mFilter);
		if (batteryintent != null) {
			boolean bPlugged = batteryintent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0;
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			boolean bShowMessage = true;
			
			if (!bPlugged) {
				//Not plugged in, wipe out difference
				//Don't stop, just in case I'm reading something immediately after start
				//Let the broadcast receiver worry about starting and stopping
				Log.i(TAG,"Not plugged in, skipping work");
				mLastPercent = -1;
				bShowMessage = false;
			} else {
				int curlevel = batteryintent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
				int curscale = batteryintent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
				int curpct = (curlevel * 100) / curscale;
				final int threshold = 1;
				if (mLastPercent == -1) {
					mLastPercent = curpct;
					bShowMessage = false;
				} else  {
					if (mLastPercent == curpct && curlevel != curscale) {
						mBuilder.setContentText(getString(R.string.not_charging));
						Log.w(TAG,String.format("Battery not charging, currently at %d%%",curpct));
					} else if (mLastPercent > curpct) {
						mBuilder.setContentText(getString(R.string.discharging));
						Log.w(TAG,String.format("Battery is discharging, currently %d%%, last %d%%",curpct,mLastPercent));
					} else if  (curpct - mLastPercent < threshold && curlevel != curscale) {
						mBuilder.setContentText(getString(R.string.slow_charge));
						Log.w(TAG,String.format("Battery charging slowly, currently %d%%, last %d%%",curpct,mLastPercent));
					} else {
						bShowMessage = false;
					}
					
					mLastPercent = curpct;
				}
				
			}
			
			if (bShowMessage) {
				nm.notify(NotificationIDs.WarningMessage,mBuilder.build());
			} else {
				nm.cancel(NotificationIDs.WarningMessage);
			}
		
		}
		
		//This uses polling rather than push just in case things don't change,
		//And results in worrying about fewer messages
		mHandler.postDelayed(this, delay);
	}	
}
