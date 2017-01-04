package com.example.android.lugloclocator;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ryanfarley on 12/29/16.
 */

public class DeviceListAdapter extends ArrayAdapter<String> {

    private static class ViewHolder {
        RoundCornerProgressBar signalStrength;
        TextView deviceName;
        TextView deviceAddress;
    }

    private LayoutInflater mInflator;
    // for each BLE device address store the RSSI value and name
    private Map<String, Integer> mRssi = new HashMap<String, Integer>();
    private Map<String, String> mName = new HashMap<String, String>();

    public DeviceListAdapter(Context context) {
        super(context, R.layout.device_list_item);
        mInflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addDevice(String address, String name, int rssi) {
        if (mRssi.put(address, rssi) == null) {
            // add a new BLE device to the List
            add(address);
            mName.put(address, name);
        } else {
            // just refresh the List after updating RSSI value
            notifyDataSetChanged();
        }
    }

    private String progressColorString(int rssi) {
        int powerPercentage = powerPercentage(rssi);
        Log.d("POWER_PERCENT", "value: " + powerPercentage);
        if (powerPercentage < 25)
            return "#ff0000";

        if (powerPercentage < 50)
            return "#ffff00";

        if (powerPercentage < 75)
            return "#388E3C";

        return "#8cc63f";
    }

    private int powerPercentage(int rssi) {
        int i = 0;
        if (rssi <= -100) {
            i = 0;
        } else {
            i = 100 + rssi;
        }

        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.device_list_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.signalStrength = (RoundCornerProgressBar) view.findViewById(R.id.signal_strength);
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String address = getItem(i);

        int rssi = mRssi.get(address);
        viewHolder.signalStrength.setProgress(powerPercentage(rssi));
        viewHolder.signalStrength.setProgressColor(Color.parseColor(progressColorString(rssi)));

        String name = mName.get(address);
        viewHolder.deviceName.setText(name);

        viewHolder.deviceAddress.setText(address);
        return view;
    }
}
