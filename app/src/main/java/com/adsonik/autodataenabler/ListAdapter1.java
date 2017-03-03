package com.adsonik.autodataenabler;

import java.util.ArrayList;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter1 extends BaseAdapter{

	ArrayList<String> packageList;
	ArrayList<String> packageList1;
	Activity context;
	PackageManager pm;
	public ListAdapter1(Activity context, ArrayList<String> packageList,ArrayList<String> packageList1
			) {
		super();
		this.context = context;
		this.packageList = packageList;
		this.packageList1=packageList1;
        pm = context.getPackageManager();

		
	}

	private class ViewHolder {
		TextView apkName;
		ImageView imgView;
	}

	public int getCount() {
		return packageList.size();
	}
	public int getCount1() {
		return packageList1.size();
	}

	public Object getItem(int position) {
		return packageList.get(position);
	}
	public Object getItem1(int position) {
		return packageList1.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		
		LayoutInflater inflater = context.getLayoutInflater();
        Typeface tf=Typeface.createFromAsset(context.getAssets(), "othercontent.TTF");

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.app_display1, null);
			holder = new ViewHolder();

			holder.apkName = (TextView) convertView
					.findViewById(R.id.textView123);
			holder.imgView = (ImageView) convertView
					.findViewById(R.id.imageView123);

			convertView.setTag(holder);
			// holder.ck1.setTag(packageList.get(position));

		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}
		// ViewHolder holder = (ViewHolder) convertView.getTag();
		String packageInfo = (String) getItem(position);
		String packageInfo1 = (String) getItem1(position);
		Drawable icon=null;
		try {
			icon = pm.getApplicationIcon(packageInfo1);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.apkName.setText(packageInfo);
		holder.apkName.setTypeface(tf);
		
		holder.imgView.setImageDrawable(icon);
	//	holder.imgView.setImageDrawable();

		return convertView;

	}



}
