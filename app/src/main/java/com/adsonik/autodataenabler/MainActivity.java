package com.adsonik.autodataenabler;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity implements OnCheckedChangeListener{
	AlarmManager alarmManager=null;
	PendingIntent ServiceManagementIntent;
	Button bt1,btnapp;
	RadioGroup rg;
	ToggleButton tbtn;
	String conn;
	static final String TAG = "SR";
	ArrayList<String> arraylist;
	ArrayList<String> arraylist1;
	ListView llvv;
	Intent i7;
	final int startupID = 1111111;
	SharedPreferences myPrefs;
	TextView t1,t2,t3,t4,t5,t6;
	PackageManager packageManager;
	ArrayList<String> list;
	ArrayList<String> list1;
	ListAdapter1 listAdapter,listAdapter1;
	boolean checking=false;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface tf=Typeface.createFromAsset(getApplicationContext().getAssets(), "othercontent.TTF");
        Typeface tf1=Typeface.createFromAsset(getApplicationContext().getAssets(), "LBRITED.TTF");
        Typeface tf2=Typeface.createFromAsset(getApplicationContext().getAssets(), "title.TTF");
		myPrefs=PreferenceManager.getDefaultSharedPreferences(this);
		
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		
		 getSupportActionBar().setLogo(R.drawable.ic_launcher1);
		 getSupportActionBar().setTitle("Automatic Data Enabler");
		 //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		 getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		 getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(248, 174, 16)));
		 
        alarmManager = (AlarmManager) getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);
        i7 = new Intent("my.second.action");
        ServiceManagementIntent = PendingIntent.getBroadcast(getApplicationContext(),
					startupID, i7, 0);
        arraylist=new ArrayList<String>();
        arraylist1=new ArrayList<String>();
        list=new ArrayList<String>();
        list1=new ArrayList<String>();
        bt1 = (Button) findViewById(R.id.button1);
        btnapp=(Button)findViewById(R.id.btnpickapp);
        rg=(RadioGroup)findViewById(R.id.radioGroup1);
        llvv=(ListView)findViewById(R.id.listView21);
        tbtn=(ToggleButton)findViewById(R.id.toggleBtn);
        t1=(TextView)findViewById(R.id.txtTitle);
        t2=(TextView)findViewById(R.id.txtVw1);
        t3=(TextView)findViewById(R.id.txtVw2);
        t4=(TextView)findViewById(R.id.txtVwHelp);
        t6=(TextView)findViewById(R.id.textView1);
       t5=(TextView)findViewById(R.id.txtViewApp);
        t1.setTypeface(tf1);
        t2.setTypeface(tf2);
        t3.setTypeface(tf2);
        t6.setTypeface(tf2);
        t5.setTypeface(tf);
        t4.setTypeface(tf);
      String objj= myPrefs.getString("con", "a");
       if(objj.length()>1){
    	   tbtn.setChecked(true);
    	   t5.setVisibility(View.INVISIBLE);
       }else{
    	   tbtn.setChecked(false);
    	   t5.setVisibility(View.VISIBLE);
       }
      
		int size=myPrefs.getInt("array_size", 0);
		if(size!=0){
		for(int i=0;i<size;i++){
		list.add(myPrefs.getString("array_"+i, "null"));
		}
		}
		int size1=myPrefs.getInt("array_size1", 0);
		if(size1!=0){
		for(int i=0;i<size1;i++){
		list1.add(myPrefs.getString("array1_"+i, "null"));
		}
		}
		
   	listAdapter=new ListAdapter1(this,list1,list);
   	llvv.setAdapter(listAdapter);
   	
        t4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent newintent=new Intent(MainActivity.this,Help.class);
				startActivity(newintent);
				
			}
		});
        btnapp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent iii=new Intent(MainActivity.this,AppDisplay.class);
				startActivityForResult(iii,1);
				
			}
		});
        tbtn.setOnCheckedChangeListener(this);
		
				bt1.setOnClickListener(new View.OnClickListener() {
					
				@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
					if(checking){
					Toast.makeText(MainActivity.this,"Automatic Data Enabler Activated",Toast.LENGTH_SHORT).show();
			
					try{

							alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
									SystemClock.elapsedRealtime(), 
									5000, ServiceManagementIntent);
							myPrefs.edit().putBoolean("alarm", true).commit();
							int id=rg.getCheckedRadioButtonId();
							if(id==R.id.radioWifi){
								conn="Wifi";
								
							}else if(id==R.id.radio2G){
								conn="2G";
							}
						
							Intent intent=new Intent("my.action.receiver");

							Bundle bucket=new Bundle();
							bucket.putString("connection", conn);
							bucket.putStringArrayList("array", arraylist);
							bucket.putStringArrayList("array1", arraylist1);
							intent.putExtras(bucket);
							sendBroadcast(intent);
						} catch (Exception e) {
							Log.i(TAG, "Exception : "+e);
						}	
				}else{
					Toast.makeText(MainActivity.this,"Please select atleast one Application",Toast.LENGTH_LONG).show();
				}
				}
			});
				
			
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
		 case R.id.menu_item_share:
			 Intent id=new Intent(MainActivity.this,Help.class);
				startActivity(id);
				break;
		 }
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode==RESULT_OK){
			llvv.setVisibility(View.VISIBLE);
			checking=true;
			arraylist=data.getStringArrayListExtra("aaa");
			arraylist1=data.getStringArrayListExtra("aaa1");
			listAdapter1=new ListAdapter1(this,arraylist1,arraylist);
			llvv.setAdapter(listAdapter1);
		if(tbtn.isChecked()){
			tbtn.setChecked(false);
		}
		tbtn.setChecked(true);
		
			
			}
	}
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		if(arg1==true){
			//Toast.makeText(MainActivity.this,"checked",Toast.LENGTH_SHORT).show();
			t5.setVisibility(View.INVISIBLE);
			llvv.setVisibility(View.VISIBLE);

		}else{
			t5.setVisibility(View.VISIBLE);
			llvv.setVisibility(View.INVISIBLE);
			if(myPrefs.getBoolean("alarm", false)){
			alarmManager.cancel(ServiceManagementIntent);
			Toast.makeText(MainActivity.this,"Automatic Data Enabler Deactivated",Toast.LENGTH_SHORT).show();
			myPrefs.edit().clear().commit();
		}
		}
	}
    
}
