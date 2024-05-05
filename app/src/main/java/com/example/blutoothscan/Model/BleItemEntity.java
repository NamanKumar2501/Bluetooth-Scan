package com.example.blutoothscan.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ble_items")
public class BleItemEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "device_name")
    private String deviceName;

    @ColumnInfo(name = "rssi")
    private int rssi;

    @ColumnInfo(name = "mac_id")
    private String macAddress;


    @ColumnInfo(name = "history_ID")
    private String historyID;

    public BleItemEntity(String deviceName, int rssi, String macAddress) {
        this.deviceName = deviceName;
        this.rssi = rssi;
        this.macAddress = macAddress;
    }

    public String getHistoryID() {
        return historyID;
    }

    public void setHistoryID(String historyID) {
        this.historyID = historyID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}


