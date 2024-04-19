package com.example.blutoothscan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.Manifest;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.blutoothscan.databinding.ActivityBleScanBinding;

import java.util.ArrayList;
import java.util.List;

public class BleScan extends AppCompatActivity {
    private ActivityBleScanBinding binding;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> deviceList;
    private BleDeviceAdapter deviceAdapter;
    private int deviceCounter = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBleScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        deviceList = new ArrayList<>();
        deviceAdapter = new BleDeviceAdapter(deviceList);

        binding.bleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.bleRecyclerView.setAdapter(deviceAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
            }
        });

        if (!isLocationPermissionGranted()) {
            requestLocationPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void startScan() {
        deviceList.clear();
        deviceAdapter.notifyDataSetChanged();

        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, prompt the user to enable it
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        // Bluetooth is already enabled, start scanning for devices
        registerReceiver(scanReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        bluetoothAdapter.startDiscovery();
    }


    @SuppressLint("MissingPermission")
    private void stopScan() {
        bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(scanReceiver);
    }

    private final BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    deviceList.add(device);
                    deviceAdapter.notifyDataSetChanged();
                    deviceCounter++;
                    updateCounter();
                }
            }
        }
    };

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // Bluetooth was successfully enabled by the user, start scanning
                startScan();
            } else {
                // The user did not enable Bluetooth, handle this scenario
                Toast.makeText(this, "Bluetooth is required for scanning", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void updateCounter() {
        binding.tvcounter.setText(getString(R.string.device_counter, deviceCounter));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
    }
}


