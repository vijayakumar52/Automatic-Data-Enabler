package com.adsonik.autodataenabler;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay-3593 on 24/12/17.
 */


public class ActiveAppsListAdapter extends BaseAdapter {

    private List<String> packageList;
    private Activity context;

    public ActiveAppsListAdapter(Activity context, ArrayList<String> packageInfo) {
        super();
        this.context = context;
        this.packageList = packageInfo;
    }

    private class ViewHolder {
        TextView apkName;
        ImageView imgView;
    }

    public int getCount() {
        return packageList.size();
    }

    public String getItem(int position) {
        return packageList.get(position);
    }

    public Drawable getIcon(int position) throws NameNotFoundException {
        String packageName = getItem(position);
        return context.getPackageManager().getApplicationIcon(packageName);
    }

    public String getName(int position) {
        String packageName = getItem(position);
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            String appName = (String) packageManager.getApplicationLabel(info);
            return appName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public List<String> getAllItems() {
        return packageList;
    }

    public long getItemId(int position) {
        return 0;
    }

    public void setItems(List<String> items) {
        this.packageList = items;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.active_apps_component, null);
            holder = new ViewHolder();
            holder.apkName = (TextView) convertView.findViewById(R.id.app_name);
            holder.imgView = (ImageView) convertView.findViewById(R.id.app_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Drawable icon = null;
        try {
            icon = getIcon(position);
            holder.imgView.setImageDrawable(icon);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        holder.apkName.setText(getName(position));
        return convertView;

    }


}
