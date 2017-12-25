package com.adsonik.autodataenabler;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by vijay-3593 on 24/12/17.
 */


public class AppDisplay extends AppCompatActivity implements OnItemClickListener {

    ListView lvAllApps;
    ArrayList<String> whiteListedApps;
    PackageInfo pi = new PackageInfo();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.ui_installed_apps);
        setContentView(R.layout.app_display);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lvAllApps = (ListView) findViewById(R.id.lvAllApps);

        lvAllApps.setEmptyView(findViewById(R.id.progressBar));
        List<PackageInfo> userInsalledApps = new ArrayList<>();
        whiteListedApps = getIntent().getStringArrayListExtra(MainActivity.EXTRA_RESULE);
        Listadapter Adapter = new Listadapter(this, userInsalledApps, whiteListedApps);

        lvAllApps.setAdapter(Adapter);
        lvAllApps.setOnItemClickListener(this);

        new Loader().execute();
    }

    class Loader extends AsyncTask<Object, Integer, List<PackageInfo>> {

        @Override
        protected List<PackageInfo> doInBackground(Object[] objects) {
            List<PackageInfo> allApps = new ArrayList<>();
            final List<PackageInfo> packageList = getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA); // all lvAllApps in the phone

            for (int i = 0; i < packageList.size(); i++) {
                PackageInfo packageInfo = packageList.get(i);
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    allApps.add(packageInfo); // add in 2nd list if it is user installed app
                }
            }

            //Sort list

            Collections.sort(allApps, new Comparator<PackageInfo>() {
                public int compare(PackageInfo o1, PackageInfo o2) {
                    return o1.applicationInfo.loadLabel(getPackageManager()).toString()
                            .compareToIgnoreCase(o2.applicationInfo.loadLabel(getPackageManager())
                                    .toString());// compare and return sorted packagelist.

                }
            });

            return allApps;
        }


        @Override
        protected void onPostExecute(List<PackageInfo> allApps) {
            super.onPostExecute(allApps);
            lvAllApps.setAdapter(new Listadapter(AppDisplay.this, allApps, whiteListedApps));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (android.R.id.home == id) {
            finish();
        } else if(R.id.save == id){
            Intent returnIntent = new Intent();
            returnIntent.putStringArrayListExtra(MainActivity.EXTRA_RESULE, whiteListedApps);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
        CheckBox cb = (CheckBox) v.findViewById(R.id.cbSelect);
        TextView tv = (TextView) v.findViewById(R.id.tvAppName);
        pi = (PackageInfo) arg0.getItemAtPosition(arg2);
        cb.performClick();
        if (cb.isChecked()) {
            whiteListedApps.add(pi.packageName);
        } else {
            whiteListedApps.remove(pi.packageName);
        }
    }
}
