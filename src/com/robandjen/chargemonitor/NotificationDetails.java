package com.robandjen.chargemonitor;

import android.app.Notification;

//This file is part of ChargeMonitor.
//
//	ChargeMonitor is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//ChargeMonitor is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with ChargeMonitor.  If not, see <http://www.gnu.org/licenses/>.
//	
/* Copyright 2014, Robert Myers */
public class NotificationDetails {
	public final static NotificationDetail Charging = new NotificationDetail(
			android.R.drawable.ic_lock_idle_charging,R.string.app_name,R.string.charging,
			NotificationIDs.OKMessage,Notification.PRIORITY_LOW);
	
	public final static NotificationDetail Charged = new NotificationDetail(
			android.R.drawable.ic_lock_idle_charging,R.string.app_name,R.string.charged,
			NotificationIDs.OKMessage,Notification.PRIORITY_LOW);
	
	public final static NotificationDetail Unknown = new NotificationDetail(
			R.drawable.stat_sys_battery_unknown,R.string.app_name,R.string.checking,
			NotificationIDs.OKMessage,Notification.PRIORITY_LOW);
	
	public static final NotificationDetail NotCharging = new NotificationDetail(
			android.R.drawable.ic_lock_idle_low_battery,R.string.warning_title,R.string.not_charging,
			NotificationIDs.WarningMessage,Notification.PRIORITY_HIGH);
	
	public static final NotificationDetail SlowCharging = new NotificationDetail(
			android.R.drawable.ic_lock_idle_low_battery,R.string.warning_title,R.string.slow_charge,
			NotificationIDs.WarningMessage,Notification.PRIORITY_HIGH);
	
	public static final NotificationDetail Discharging = new NotificationDetail(
			android.R.drawable.ic_lock_idle_low_battery,R.string.warning_title,R.string.discharging,
			NotificationIDs.WarningMessage,Notification.PRIORITY_HIGH);

}
