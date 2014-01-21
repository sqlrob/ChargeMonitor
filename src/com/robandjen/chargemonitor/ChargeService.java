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
	
	final int delay = 10*60*1000;
	final int threshold = 1;
	
	int mLastPercent = -1; //Last percentage value received
	int mStartPercent = -1; //What percentage was when this current time segment was run
	
	void rescheduleTimer(int curPercent) {
		mHandler.removeCallbacks(this);
		mStartPercent = curPercent;
		mHandler.postDelayed(this, delay);
	}
	
	static enum ChargingType {
		Charging(R.string.charging,android.R.drawable.ic_lock_idle_charging),
		Charged(R.string.charged,android.R.drawable.ic_lock_idle_charging),
		Unknown(R.string.checking,R.drawable.stat_sys_battery_unknown);
		
		final int id;
		final int icon;
		ChargingType(int id,int icon) {
			this.id = id;
			this.icon = icon;
		}
		
		int id() {
			return id;
		}
		
		int icon() {
			return icon;
		}
	}
	
	void displayCharging(ChargingType type) {
		Notification.Builder builder = new Notification.Builder(this);
		builder.setSmallIcon(type.icon())
			.setPriority(Notification.PRIORITY_LOW)
			.setOngoing(true)
			.setOnlyAlertOnce(true)
			.setContentTitle(getString(R.string.app_name))
			.setContentText(getString(type.id()));
		
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(NotificationIDs.WarningMessage);
		nm.notify(NotificationIDs.OKMessage, builder.build());
	}
	
	static enum WarningType { 
		NotCharging(R.string.not_charging),
		SlowCharging(R.string.slow_charge),
		Discharging(R.string.discharging);
		WarningType(int id) { 
			this.id = id;
		}
		final int id;
		int id() { 
			return id; 
		}
	};
	
	void displayWarning(WarningType type) {
		Notification.Builder builder = new Notification.Builder(this);
		builder.setSmallIcon(android.R.drawable.ic_lock_idle_low_battery)
			.setPriority(Notification.PRIORITY_HIGH)
			.setOngoing(true)
			.setOnlyAlertOnce(true)
			.setContentTitle(getString(R.string.warning_title))
			.setContentText(getString(type.id()));
		
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(NotificationIDs.OKMessage);
		nm.notify(NotificationIDs.WarningMessage, builder.build());
	}
	
	class BatteryReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent batteryintent) {
			int curlevel = batteryintent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int curscale = batteryintent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
			int curpct = (curlevel * 100) / curscale;
			
			if (isInitialStickyBroadcast()) {
				mLastPercent = curpct;
				displayCharging(curlevel == curscale ? ChargingType.Charged : ChargingType.Unknown);
				rescheduleTimer(curpct);
				return;
			}
			
			if (curlevel == curscale) { 
				//Fully charged
				displayCharging(ChargingType.Charged);
			} else if ((curpct - mLastPercent) >= threshold && mLastPercent >=0) {
				//One reading was enough
				displayCharging(ChargingType.Charging);
			} else if ((curpct - mStartPercent) >= threshold && mStartPercent >= 0) {
				//A couple of readings was enough
				displayCharging(ChargingType.Charging);
			} else if (curpct < mStartPercent) {
				//It's going down
				displayWarning(WarningType.Discharging);
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
	}

	@Override
	public void onDestroy() {
		mHandler.removeCallbacks(this);
		mHandler = null;
		unregisterReceiver(mBatteryReceiver);
		
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancelAll();
		super.onDestroy();
	}

	@Override
	public void run() {
		
		final int batteryDelta = mLastPercent - mStartPercent;
		
		if (mLastPercent == 100) {
			displayCharging(ChargingType.Charged);
		} else if (batteryDelta == 0) {
			displayWarning(WarningType.NotCharging);
		} else if (batteryDelta < 0) {
			displayWarning(WarningType.Discharging);
		} else if (batteryDelta >= threshold) {
			displayCharging(ChargingType.Charging);
		} else {
			displayWarning(WarningType.SlowCharging);
		}
		
		rescheduleTimer(mLastPercent);
	}	
}
