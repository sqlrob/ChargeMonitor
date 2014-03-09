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

public class NotificationDetail {
	public final int mIconId;
	public final int mTitleId;
	public final int mContentId;
	public final int mNotifyId;
	public final int mPriority;
	
	public NotificationDetail(int IconId,int TitleId,int ContentId,int NotifyId,int Priority) {
		mIconId = IconId;
		mTitleId = TitleId;
		mContentId = ContentId;
		mNotifyId = NotifyId;
		mPriority = Priority;
	}
}
