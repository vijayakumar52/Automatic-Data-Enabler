package com.adsonik.autodataenabler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class CheckRunningApplicationReceiver extends BroadcastReceiver {

	public final String TAG = "CRAR"; // CheckRunningApplicationReceiver
	ArrayList<String> pcklist;
	String[] list;
	WifiManager wifi;
	   ConnectivityManager conman;
	   NetworkInfo mobile;
	   SharedPreferences myPrefs;
	   boolean stateNetwork;
	@Override
	public void onReceive(Context aContext, Intent anIntent) {
	
		 conman= (ConnectivityManager) aContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		 mobile=conman.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		 wifi=(WifiManager)aContext.getSystemService(Context.WIFI_SERVICE);
		String action=anIntent.getAction();
		
	    if(action.equals("my.second.action")){
	    	stateNetwork=false;
		myPrefs=PreferenceManager.getDefaultSharedPreferences(aContext);
		
		int size=myPrefs.getInt("array_size", 0);
		list=new String[size];
		for(int i=0;i<size;i++){
		list[i]=myPrefs.getString("array_"+i, "null");
		}
		String ss=myPrefs.getString("con", "null");

	   if(ss.contentEquals("Wifi")){
		try {
	
			ActivityManager am = (ActivityManager) aContext
					.getSystemService(Context.ACTIVITY_SERVICE);
		
			@SuppressWarnings("deprecation")
			RunningTaskInfo foregroundTaskInfo=am.getRunningTasks(1).get(0);

			

				for(int i=0;i<size;i++){
					
					if(foregroundTaskInfo.topActivity.getPackageName().equals(list[i])){
						stateNetwork=true;
					}
				}
				if(stateNetwork){
					if(!wifi.isWifiEnabled()){
						wifi.setWifiEnabled(true);
						Toast.makeText(aContext, "Wifi is Enabled", Toast.LENGTH_SHORT).show();
					}
				}else{
					if(wifi.isWifiEnabled()){
						wifi.setWifiEnabled(false);
						Toast.makeText(aContext, "Wifi is Disabled", Toast.LENGTH_SHORT).show();
					}
				}
		}catch (Throwable t) {
			Log.i(TAG, "Throwable caught: "
						+ t.getMessage(), t);
		}
	
	}else if (ss.contains("2G")){
		try {
			
			ActivityManager am = (ActivityManager) aContext
					.getSystemService(Context.ACTIVITY_SERVICE);
		
			RunningTaskInfo foregroundTaskInfo=am.getRunningTasks(1).get(0);


			for(int i=0;i<size;i++){
				
				if(foregroundTaskInfo.topActivity.getPackageName().equals(list[i])){
					stateNetwork=true;
				}
			}
			if(stateNetwork){
				if(!mobile.isConnected()){
					setMobileDataEnabled(aContext,true);
					Toast.makeText(aContext, "Mobile Network is Enabled", Toast.LENGTH_SHORT).show();
				}
			}else{
				if(mobile.isConnected()){
					setMobileDataEnabled(aContext,false);
					Toast.makeText(aContext, "Mobile Network is Disabled", Toast.LENGTH_SHORT).show();
				}			
				}
		
		}catch (Throwable t) {
			Log.i(TAG, "Throwable caught: "
						+ t.getMessage(), t);
		}
	}
	}
	else if(action.equals("my.action.receiver")){
		
		ArrayList<String> state=anIntent.getExtras().getStringArrayList("array");
		ArrayList<String> state1=anIntent.getExtras().getStringArrayList("array1");
		String str=anIntent.getExtras().getString("connection");
		SharedPreferences myPrefs=PreferenceManager.getDefaultSharedPreferences(aContext);
		Editor editor=myPrefs.edit();
		editor.putInt("array_size", state.size());
		editor.putInt("array_size1", state1.size());
		editor.putString("con", str);
		for(int i=0;i<state.size();i++){
		editor.putString("array_"+i, state.get(i));
		}
		for(int i=0;i<state1.size();i++){
			editor.putString("array1_"+i, state1.get(i));
			}
		//editor.
		editor.commit();
		
		
	}
	}
	private void setMobileDataEnabled(Context context, boolean enabled) {
	    Class conmanClass = null;
		try {
			conmanClass = Class.forName(conman.getClass().getName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    Field iConnectivityManagerField = null;
		try {
			iConnectivityManagerField = conmanClass.getDeclaredField("mService");
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    iConnectivityManagerField.setAccessible(true);
	    Object iConnectivityManager = null;
		try {
			iConnectivityManager = iConnectivityManagerField.get(conman);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    Class iConnectivityManagerClass = null;
		try {
			iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    Method setMobileDataEnabledMethod = null;
		try {
			setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    setMobileDataEnabledMethod.setAccessible(true);

	    try {
			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

