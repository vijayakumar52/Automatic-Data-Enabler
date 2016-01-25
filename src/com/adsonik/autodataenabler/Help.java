package com.adsonik.autodataenabler;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Help extends ActionBarActivity{

	TextView hee;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(248,174,16)));
		 getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getSupportActionBar().setTitle("Help");
		setContentView(R.layout.help);
		
		Typeface font4 = Typeface.createFromAsset(getApplicationContext().getAssets(), "othercontent.TTF");
		hee=(TextView)findViewById(R.id.tvvhelp);
		hee.setTypeface(font4);
		hee.setTextSize(20);
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
		
         case android.R.id.home:
             
             Intent upIntent = new Intent(this, MainActivity.class);
             if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                
                 TaskStackBuilder.from(this)
                        
                         .addNextIntent(upIntent)
                         .startActivities();
                 finish();
             } else {
                 
                 NavUtils.navigateUpTo(this, upIntent);
             }
             return true;
     }
     return super.onOptionsItemSelected(item);
	}
}
