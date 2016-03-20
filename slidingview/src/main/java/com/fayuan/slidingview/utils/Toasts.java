package com.fayuan.slidingview.utils;

import android.content.Context;
import android.widget.Toast;

public class Toasts {

	static Toast toast;
	
	public static void show(Context context, String msg) {
		if (toast == null) {
			toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		}
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setText(msg);	
		toast.show();
	}
	
	public static void show(Context context, int msgId) {
		if (toast == null) {
			toast = Toast.makeText(context, msgId, Toast.LENGTH_LONG);
		}
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setText(msgId);	
		toast.show();
	}	
	
	public static void showShort(Context context, String msg) {
		if (toast == null) {
			toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		}
		
		toast.setText(msg);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public static void showShort(Context context, int msgId) {
		if (toast == null) {
			toast = Toast.makeText(context, msgId, Toast.LENGTH_SHORT);
		}
		
		toast.setText(msgId);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}		
}