package com.adsonik.autodataenabler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AppDisplay extends ActionBarActivity implements OnItemClickListener {

	ListView apps;
	Button btnn;
	PackageManager packageManager;
	int n;
	ArrayList<String> checkedValue;
	ArrayList<String> checkedValue1;
	PackageInfo pi = new PackageInfo();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_display);
		 getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(248,174,16)));
		 getSupportActionBar().setDisplayShowTitleEnabled(true);
		 getSupportActionBar().setTitle("Installed Applications");
		apps = (ListView) findViewById(R.id.listView1);
        apps.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        packageManager = getPackageManager();
        checkedValue = new ArrayList<String>();
        checkedValue1 = new ArrayList<String>();
        btnn=(Button)findViewById(R.id.btnreturn);

        final List<PackageInfo> packageList = packageManager
				.getInstalledPackages(PackageManager.GET_META_DATA); // all apps in the phone
		final List<PackageInfo> packageList1 = packageManager
				.getInstalledPackages(0);

				try {
						packageList1.clear();
						for ( n = 0; n < packageList.size(); n++) 
						{

						 PackageInfo PackInfo = packageList.get(n);
							
							if (((PackInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) != true) 
								//check weather it is system app or user installed app
							{
								try 
								{
									
										packageList1.add(packageList.get(n)); // add in 2nd list if it is user installed app 
											Collections.sort(packageList1,new Comparator<PackageInfo>() 
													// this will sort App list on the basis of app name
											{
												public int compare(PackageInfo o1,PackageInfo o2) 
												{
													return o1.applicationInfo.loadLabel(getPackageManager()).toString()
															.compareToIgnoreCase(o2.applicationInfo.loadLabel(getPackageManager())
																			.toString());// compare and return sorted packagelist.
													
												}
											});
											
									
								} catch (NullPointerException e) {
									e.printStackTrace();
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				Listadapter Adapter = new Listadapter(this,packageList1, packageManager);
				
				apps.setAdapter(Adapter);
				apps.setOnItemClickListener(this);
				btnn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent returnIntent=new Intent();
						returnIntent.putStringArrayListExtra("aaa", checkedValue);
						returnIntent.putStringArrayListExtra("aaa1", checkedValue1);
						setResult(RESULT_OK,returnIntent);
						finish();
						
					}
				});
				
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		// TODO Auto-generated method stub
		CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);
		TextView tv=(TextView)v.findViewById(R.id.textView1);
		pi = (PackageInfo) arg0.getItemAtPosition(arg2);
		cb.performClick();
		if (cb.isChecked()) {
			checkedValue.add(pi.packageName);
			  checkedValue1.add(tv.getText().toString());
			
		} else {
			checkedValue.remove(pi.packageName);
			checkedValue1.remove(tv.getText().toString());
			 
			
			
		}
		
	}
	

}
