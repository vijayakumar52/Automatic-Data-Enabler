package com.adsonik.autodataenabler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton

import java.util.ArrayList

class MainActivity : AppCompatActivity(), OnCheckedChangeListener {
    internal var alarmManager: AlarmManager? = null
    internal var ServiceManagementIntent: PendingIntent
    internal var bt1: Button
    internal var btnapp: Button
    internal var rg: RadioGroup
    internal var tbtn: ToggleButton
    internal var conn: String
    internal var arraylist: ArrayList<String>
    internal var arraylist1: ArrayList<String>
    internal var llvv: ListView
    internal var i7: Intent
    internal val startupID = 1111111
    internal var myPrefs: SharedPreferences
    internal var t1: TextView
    internal var t2: TextView
    internal var t3: TextView
    internal var t4: TextView
    internal var t5: TextView
    internal var t6: TextView
    internal var list: ArrayList<String>
    internal var list1: ArrayList<String>
    internal var listAdapter: ListAdapter1
    internal var listAdapter1: ListAdapter1
    internal var checking = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        supportActionBar!!.setDisplayUseLogoEnabled(true)

        supportActionBar!!.setLogo(R.drawable.ic_launcher1)
        supportActionBar!!.setTitle("Automatic Data Enabler")
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.rgb(248, 174, 16)))

        alarmManager = applicationContext
                .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        i7 = Intent("my.second.action")
        ServiceManagementIntent = PendingIntent.getBroadcast(applicationContext,
                startupID, i7, 0)
        arraylist = ArrayList()
        arraylist1 = ArrayList()
        list = ArrayList()
        list1 = ArrayList()
        bt1 = findViewById<View>(R.id.button1) as Button
        btnapp = findViewById<View>(R.id.btnpickapp) as Button
        rg = findViewById<View>(R.id.radioGroup1) as RadioGroup
        llvv = findViewById<View>(R.id.listView21) as ListView
        tbtn = findViewById<View>(R.id.toggleBtn) as ToggleButton
        t1 = findViewById<View>(R.id.txtTitle) as TextView
        t2 = findViewById<View>(R.id.txtVw1) as TextView
        t3 = findViewById<View>(R.id.txtVw2) as TextView
        t4 = findViewById<View>(R.id.txtVwHelp) as TextView
        t6 = findViewById<View>(R.id.textView1) as TextView
        t5 = findViewById<View>(R.id.txtViewApp) as TextView
        val objj = myPrefs.getString("con", "a")
        if (objj!!.length > 1) {
            tbtn.isChecked = true
            t5.visibility = View.INVISIBLE
        } else {
            tbtn.isChecked = false
            t5.visibility = View.VISIBLE
        }

        val size = myPrefs.getInt("array_size", 0)
        if (size != 0) {
            for (i in 0 until size) {
                list.add(myPrefs.getString("array_" + i, "null"))
            }
        }
        val size1 = myPrefs.getInt("array_size1", 0)
        if (size1 != 0) {
            for (i in 0 until size1) {
                list1.add(myPrefs.getString("array1_" + i, "null"))
            }
        }

        listAdapter = ListAdapter1(this, list1, list)
        llvv.adapter = listAdapter

        t4.setOnClickListener {
            // TODO Auto-generated method stub
            val newintent = Intent(this@MainActivity, Help::class.java)
            startActivity(newintent)
        }
        btnapp.setOnClickListener {
            // TODO Auto-generated method stub
            val iii = Intent(this@MainActivity, AppDisplay::class.java)
            startActivityForResult(iii, 1)
        }
        tbtn.setOnCheckedChangeListener(this)

        bt1.setOnClickListener {
            // TODO Auto-generated method stub
            if (checking) {
                Toast.makeText(this@MainActivity, "Automatic Data Enabler Activated", Toast.LENGTH_SHORT).show()

                try {

                    alarmManager!!.setRepeating(AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime(),
                            5000, ServiceManagementIntent)
                    myPrefs.edit().putBoolean("alarm", true).commit()
                    val id = rg.checkedRadioButtonId
                    if (id == R.id.radioWifi) {
                        conn = "Wifi"

                    } else if (id == R.id.radio2G) {
                        conn = "2G"
                    }

                    val intent = Intent("my.action.receiver")

                    val bucket = Bundle()
                    bucket.putString("connection", conn)
                    bucket.putStringArrayList("array", arraylist)
                    bucket.putStringArrayList("array1", arraylist1)
                    intent.putExtras(bucket)
                    sendBroadcast(intent)
                } catch (e: Exception) {
                    Log.i(TAG, "Exception : " + e)
                }

            } else {
                Toast.makeText(this@MainActivity, "Please select atleast one Application", Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_share -> {
                val id = Intent(this@MainActivity, Help::class.java)
                startActivity(id)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            llvv.visibility = View.VISIBLE
            checking = true
            arraylist = data.getStringArrayListExtra("aaa")
            arraylist1 = data.getStringArrayListExtra("aaa1")
            listAdapter1 = ListAdapter1(this, arraylist1, arraylist)
            llvv.adapter = listAdapter1
            if (tbtn.isChecked) {
                tbtn.isChecked = false
            }
            tbtn.isChecked = true


        }
    }

    override fun onCheckedChanged(arg0: CompoundButton, arg1: Boolean) {
        // TODO Auto-generated method stub
        if (arg1 == true) {
            //Toast.makeText(MainActivity.this,"checked",Toast.LENGTH_SHORT).show();
            t5.visibility = View.INVISIBLE
            llvv.visibility = View.VISIBLE

        } else {
            t5.visibility = View.VISIBLE
            llvv.visibility = View.INVISIBLE
            if (myPrefs.getBoolean("alarm", false)) {
                alarmManager!!.cancel(ServiceManagementIntent)
                Toast.makeText(this@MainActivity, "Automatic Data Enabler Deactivated", Toast.LENGTH_SHORT).show()
                myPrefs.edit().clear().commit()
            }
        }
    }

    companion object {
        internal val TAG = "SR"
    }

}
