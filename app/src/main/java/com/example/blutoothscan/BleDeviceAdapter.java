package com.example.blutoothscan;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.DeviceViewHolder>{

    private List<BluetoothDevice> devices;

    public BleDeviceAdapter(List<BluetoothDevice> devices) {
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
        BluetoothDevice device = devices.get(position);
//        BluetoothDevice device = deviceModel.getDevice();
//        int rssi = deviceModel.getRssi();

        holder.tvName.setText(device.getName());
        holder.tvMacId.setText(device.getAddress());
//        holder.tvRssi.setText("RSSI: " + rssi + "\ndBm"); // RSSI ko dBm mein display karen

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvMacId;
        TextView tvRssi;
        TextView tvDistance;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMacId = itemView.findViewById(R.id.tvmacId);
            tvRssi = itemView.findViewById(R.id.tvRssi);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }
}
