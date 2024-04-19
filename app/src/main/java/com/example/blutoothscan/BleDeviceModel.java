package com.example.blutoothscan;

import android.bluetooth.BluetoothDevice;

public class BleDeviceModel {
    private BluetoothDevice device;
    private int rssi;

    public BleDeviceModel(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public int getRssi() {
        return rssi;
    }
}

