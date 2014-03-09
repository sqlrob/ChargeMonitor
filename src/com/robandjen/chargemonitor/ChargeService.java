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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class ChargeService extends Service implements Runnable {

	final static String TAG = "ChargeService";
	
	private Handler mHandler;
	private BroadcastReceiver mBatteryReceiver;
	private IntentFilter mFilter;
	
	//Want to reinstall the battery broadcast receiver after 
	//Seeing a screen on notification
	//Doesn't seem like it's always persistent after a wake
	private BroadcastReceiver mScreenReceiver;
	
	final int delay = 10*60*1000;
	final int threshold = 1;
	
	int mLastPercent = -1; //Last percentage value received
	int mStartPercent = -1; //What percentage was when this current time segment was run
	
	boolean mSeenCharge = false;
	
	NotificationDetail mLastNotification;
	
	void rescheduleTimer(int curPercent) {
		mHandler.removeCallbacks(this);
		mStartPercent = curPercent;
		mHandler.postDelayed(this, delay);
	}
	
	void displayNotification(NotificationDetail latest) {
		Notification.Builder builder = new Notification.Builder(this);
		
		builder.setSmallIcon(latest.mIconId)
			.setPriority(latest.mPriority)
			.setOngoing(true)
			.setOnlyAlertOnce(true)
			.setContentTitle(getString(latest.mTitleId))
			.setContentText(getString(latest.mContentId));
		
		Notification notification = builder.build();
		
		if (mLastNotification == null || latest.mNotifyId != mLastNotification.mNotifyId) {
			startForeground(latest.mNotifyId, notification);
		} else {
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			nm.notify(latest.mNotifyId, notification);
		}
		mLastNotification = latest;
		
	}
	
	class ScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent screenintent) {
			if (isInitialStickyBroadcast()) {
				return;
			}
			unregisterReceiver(mBatteryReceiver); //Get rid of the old one, just in case
			registerReceiver(mBatteryReceiver,mFilter);
		}
		
	}
	class BatteryReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent batteryintent) {
			int curlevel = batteryintent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int curscale = batteryintent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
			int curplug = batteryintent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
			
			//If all of I sudden I see that I'm unplugged, I didn't get the unplug notification
			//Stop the service instead of waiting for a possibly never coming unplug event
			//Don't just check the first intent that comes in, since it may be a cached one
			//that shows "unplugged"
			//References #4
			if (curplug != 0) {
				mSeenCharge = true;
			} else if (mSeenCharge) {
				stopSelf();
				return;
			}
			
			int curpct = (curlevel * 100) / curscale;
			
			if (isInitialStickyBroadcast()) {
				mLastPercent = curpct;
				if (mLastNotification != null) {
					displayNotification(mLastNotification);
				} else {
					displayNotification(curlevel == curscale ? NotificationDetails.Charged : NotificationDetails.Unknown);
				}
				
				rescheduleTimer(curpct);
				return;
			}
			
			if (curlevel == curscale) { 
				//Fully charged
				displayNotification(NotificationDetails.Charged);
			} else if ((curpct - mLastPercent) >= threshold && mLastPercent >=0) {
				//One reading was enough
				displayNotification(NotificationDetails.Charging);
			} else if ((curpct - mStartPercent) >= threshold && mStartPercent >= 0) {
				//A couple of readings was enough
				displayNotification(NotificationDetails.Charging);
			} else if (curpct < mStartPercent) {
				//It's going down
				displayNotification(NotificationDetails.Discharging);
			}
			
			//Don't worry about not charging or slow charges, that's handled in the timer			
			mLastPercent = curpct;
		}
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.i(TAG,String.format("Creating charge service, delay=%d",delay));
		
		mHandler = new Handler();
		mFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		mBatteryReceiver = new BatteryReceiver();
		registerReceiver(mBatteryReceiver, mFilter);
		
		mScreenReceiver = new ScreenReceiver();
		registerReceiver(mScreenReceiver,new IntentFilter(Intent.ACTION_SCREEN_ON));
	}

	@Override
	public void onDestroy() {
		mHandler.removeCallbacks(this);
		mHandler = null;
		unregisterReceiver(mBatteryReceiver);
		unregisterReceiver(mScreenReceiver);

		super.onDestroy();
	}

	@Override
	public void run() {
		
		final int batteryDelta = mLastPercent - mStartPercent;
		
		if (mLastPercent == 100) {
			displayNotification(NotificationDetails.Charged);
		} else if (batteryDelta == 0) {
			displayNotification(NotificationDetails.NotCharging);
		} else if (batteryDelta < 0) {
			displayNotification(NotificationDetails.Discharging);
		} else if (batteryDelta >= threshold) {
			displayNotification(NotificationDetails.Charging);
		} else {
			displayNotification(NotificationDetails.SlowCharging);
		}
		
		rescheduleTimer(mLastPercent);
	}	
}
