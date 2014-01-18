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
	final int delay = 1000;
	private IntentFilter mFilter;
	int mLastPercent = -1;
	Notification.Builder mBuilder;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.i("ChargeService","Creating charge service");
		
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
						mBuilder.setContentText("Battery is not charging");
					} else if (mLastPercent > curpct) {
						mBuilder.setContentText("Battery is discharging");
					} else if  (curpct - mLastPercent < threshold && curlevel != curscale) {
						mBuilder.setContentText("Battery is charging slowly");
					} else {
						bShowMessage = false;
					}
					
					mLastPercent = curpct;
				}
				
			}
			
			if (bShowMessage) {
				nm.notify(1,mBuilder.build());
			} else {
				nm.cancel(1);
			}
		
		}
		mHandler.postDelayed(this, delay);
	}	
}
