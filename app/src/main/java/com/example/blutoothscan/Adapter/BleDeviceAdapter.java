package com.example.blutoothscan.Adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.blutoothscan.Model.BleDeviceModel;
import com.example.blutoothscan.R;

import java.util.List;
import java.util.Random;

public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.DeviceViewHolder> {
    private List<BleDeviceModel> devices;

    public BleDeviceAdapter(List<BleDeviceModel> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ble_scan_list, parent, false);
        return new DeviceViewHolder(view);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        BleDeviceModel deviceModel = devices.get(position);
        BluetoothDevice device = deviceModel.getDevice();
        int rssi = deviceModel.getRssi();

        String deviceName = device.getName();
        if (deviceName != null && !deviceName.isEmpty()) {
            holder.tvName.setText(deviceName);
        } else {
            holder.tvName.setText("N/A");
        }

        holder.tvMacId.setText("Mac Id: " + device.getAddress());
        holder.tvRssi.setText(rssi + "\ndBm");


        // Calculate distance asynchronously
        calculateDistanceAsync(holder, rssi);

        // Random color
        setRandomBackgroundColor(holder);

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMacId, tvRssi, tvDistance;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMacId = itemView.findViewById(R.id.tvmacId);
            tvRssi = itemView.findViewById(R.id.tvRssi);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }

    private void setRandomBackgroundColor(DeviceViewHolder holder) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setSize(102, 100);

        int[] colors = {Color.LTGRAY, Color.BLUE, Color.RED, Color.GRAY, Color.MAGENTA};
        Random random = new Random();
        int randomColor = colors[random.nextInt(colors.length)];

        drawable.setColor(randomColor);
        holder.tvRssi.setBackground(drawable);
    }

    @SuppressLint("StaticFieldLeak")
    private void calculateDistanceAsync(DeviceViewHolder holder, int rssi) {
        new AsyncTask<Integer, Void, Double>() {
            @Override
            protected Double doInBackground(Integer... calValue) {
                return calculateDistance(calValue[0]);
            }

            @Override
            protected void onPostExecute(Double distance) {
                if (distance >= 0) {
                    holder.tvDistance.setText("Aprx Distance: " + String.format("%.2f", distance) + " m");
                } else {
                    holder.tvDistance.setText("Aprx Distance: N/A");
                }
            }
        }.execute(rssi);
    }

   /* private double calculateDistance(int rssi) {
        double txPower = -59;
        double signalLoss = rssi - txPower;
        double exponent = (-signalLoss) / (10 * 2);
        return Math.pow(10, exponent);
    }
*/


    private double calculateDistance(int rssi) {
        double txPower = -59; // Example value for RSSI at 1 meter
        double signalPropagationExponent = 2.0; // Example path loss exponent (adjust as needed)

        // Calculate the signal loss (difference between txPower and RSSI)
        double signalLoss = txPower - rssi;

        // Calculate the estimated distance in meters using the log-distance path loss model
        double distance = Math.pow(10.0, (signalLoss) / (10.0 * signalPropagationExponent));

        return distance;
    }


}
