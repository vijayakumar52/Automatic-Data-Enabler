package com.adsonik.autodataenabler;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vijay.androidutils.DisplayUtils;

import java.util.List;

public class Listadapter extends BaseAdapter {

    private List<PackageInfo> packageList;
    private List<String> whiteListedList;
    private Activity context;
    private PackageManager packageManager;

    public Listadapter(Activity context, List<PackageInfo> packageList, List<String> whitlistedList) {
        super();
        this.context = context;
        this.packageList = packageList;
        this.whiteListedList = whitlistedList;
        this.packageManager = context.getPackageManager();
    }

    private class ViewHolder {
        TextView apkName;
        CheckBox ck1;
    }

    public int getCount() {
        return packageList.size();
    }

    public Object getItem(int position) {
        return packageList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.all_apps_component, null);
            holder = new ViewHolder();
            holder.apkName = (TextView) convertView.findViewById(R.id.tvAppName);
            holder.ck1 = (CheckBox) convertView.findViewById(R.id.cbSelect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PackageInfo packageInfo = (PackageInfo) getItem(position);

        Drawable appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
        String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();

        int width = (int) DisplayUtils.convertDpToPixel(30, context);
        appIcon.setBounds(0, 0, width, width);
        holder.apkName.setCompoundDrawables(appIcon, null, null, null);
        holder.apkName.setCompoundDrawablePadding(15);
        holder.apkName.setText(appName);
        if (contains(packageInfo.packageName)) {
            holder.ck1.setChecked(true);
        } else {
            holder.ck1.setChecked(false);
        }
        return convertView;
    }

    private boolean contains(String packageName) {
        for (String whiteListedId : whiteListedList) {
            if (whiteListedId.equals(packageName)) {
                return true;
            }
        }
        return false;
    }


}
