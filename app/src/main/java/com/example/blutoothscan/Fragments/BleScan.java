package com.example.blutoothscan.Fragments;


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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.blutoothscan.Adapter.BleDeviceAdapter;
import com.example.blutoothscan.Database.BleDb;
import com.example.blutoothscan.MainActivity;
import com.example.blutoothscan.Model.BleDeviceModel;
import com.example.blutoothscan.Model.BleItemEntity;
import com.example.blutoothscan.R;
import com.example.blutoothscan.ViewModel.BleHistoryViewModel;
import com.example.blutoothscan.databinding.FragmentBleScanBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BleScan extends Fragment {
    private FragmentBleScanBinding binding;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private BluetoothAdapter bluetoothAdapter;
    private List<BleDeviceModel> deviceList;
    MainActivity mContext;
    MenuItem bleSave, bleHistory;
    private BleDeviceAdapter deviceAdapter;
    private boolean isScanning = false;
    private int deviceCounter = 0;
    String historyId;
    private BleHistory bleHistoryFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBleScanBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceList = new ArrayList<>();
        deviceAdapter = new BleDeviceAdapter(deviceList);

        binding.bleRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.bleRecyclerView.setAdapter(deviceAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
            return;
        }


        binding.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isScanning) {
                    startScan();
                    binding.btnStop.setVisibility(View.VISIBLE);
                    binding.btnScan.setVisibility(View.GONE);
                }
                if (!isLocationPermissionGranted()) {
                    requestLocationPermission();
                }
            }
        });

        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
                binding.btnStop.setVisibility(View.GONE);
                binding.btnScan.setVisibility(View.VISIBLE);

            }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mContext = (MainActivity) context;
        } else {
            throw new ClassCastException("Parent activity must be MainActivity");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.ble_menu, menu);
        MenuItem bleSave = menu.findItem(R.id.menu_ble_save);
        MenuItem bleHistory = menu.findItem(R.id.menu_ble_history);

        if (bleSave != null && bleHistory != null) {
            bleSave.setVisible(true);
            bleHistory.setVisible(true);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem bleSave = menu.findItem(R.id.menu_ble_save);
        MenuItem bleHistory = menu.findItem(R.id.menu_ble_history);

        if (bleSave != null && bleHistory != null) {
            bleSave.setVisible(true);
            bleHistory.setVisible(true);

            bleSave.setOnMenuItemClickListener(item -> {

                if (isScanning) {
                    Toast.makeText(mContext, "First Stop BLE Scanning", Toast.LENGTH_SHORT).show();
                } else if (deviceList.isEmpty()) {
                    Toast.makeText(mContext, "BLE List is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    displayBottomSheet();
                }

                return false;
            });

            bleHistory.setOnMenuItemClickListener(item -> {
                mContext.setFragment(new BleItemHistoryName(), "History");
                return false;
            });
            super.onPrepareOptionsMenu(menu);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopScan();
        binding = null;
    }

    @SuppressLint("MissingPermission")
    private void startScan() {
        deviceList.clear();
        deviceCounter = 0;
        updateCounter();
        deviceAdapter.notifyDataSetChanged();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        requireContext().registerReceiver(scanReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        bluetoothAdapter.startDiscovery();
        isScanning = true;
    }

    @SuppressLint("MissingPermission")
    private void stopScan() {
        if (isScanning) {
            bluetoothAdapter.cancelDiscovery();
            requireContext().unregisterReceiver(scanReceiver);
            isScanning = false;
        }
    }

    private final BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                if (device != null) {
                    deviceList.add(new BleDeviceModel(device, rssi));
                    deviceAdapter.notifyDataSetChanged();
                    deviceCounter++;
                    updateCounter();
                }
            }
        }
    };

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
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
                Toast.makeText(requireContext(), "Location permission required", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == requireActivity().RESULT_OK) {
                startScan();
            } else {
                Toast.makeText(requireContext(), "Bluetooth is required for scanning", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateCounter() {
        if (binding != null) {
            binding.tvcounter.setText(getString(R.string.device_counter, deviceCounter));
        }
    }

    private void displayBottomSheet() {
        BottomSheetDialog bottomSheetBle;
        LinearLayout bottomSheetRL = null;

        bottomSheetBle = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        View layout = LayoutInflater.from(mContext).inflate(R.layout.bottomlsheet_ble_save, bottomSheetRL);
        bottomSheetBle.setContentView(layout);
        bottomSheetBle.setCancelable(false);
        bottomSheetBle.setCanceledOnTouchOutside(false);
        bottomSheetBle.show();

        EditText etBleType = bottomSheetBle.findViewById(R.id.et_ble);
        Button btCancel = bottomSheetBle.findViewById(R.id.btn_cancel);
        Button btSave = bottomSheetBle.findViewById(R.id.btn_save);
        TextView tvClose = bottomSheetBle.findViewById(R.id.tv_close);


        // Save ================
        btSave.setOnClickListener(v -> {

            List<BleItemEntity> bleItems = new ArrayList<>();
            String itemName = etBleType.getText().toString().trim();

            if (!itemName.isEmpty()) {
                if (bottomSheetBle != null && bottomSheetBle.isShowing() && etBleType != null) {
                    BleHistoryViewModel viewModel = new ViewModelProvider(requireActivity()).get(BleHistoryViewModel.class);
                    historyId = generateUniqueId();
                    viewModel.saveBleEntity(itemName, historyId);
                    Log.d("TAG", "Uniq Id: " + historyId);

                    for (BleDeviceModel device : deviceList) {
                        @SuppressLint("MissingPermission") String deviceName = device.getDevice().getName();
                        int rssi = device.getRssi();
                        String macAddress = device.getDevice().getAddress();
                        BleItemEntity bleItem = new BleItemEntity(deviceName, rssi, macAddress);
                        bleItem.setHistoryID(historyId);
                        bleItems.add(bleItem);
                    }

                    saveBleItemsToDatabase(bleItems);

                    Toast.makeText(requireContext(), "BLE items saved to database", Toast.LENGTH_SHORT).show();

                    bottomSheetBle.dismiss();

                } else {
                    Toast.makeText(requireContext(), "Please enter list name", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Enter List Name", Toast.LENGTH_SHORT).show();
            }

        });


        // Cancel ================
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBle.cancel();
            }
        });

        // Close ================
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBle.cancel();
            }
        });

    }


    private void saveBleItemsToDatabase(List<BleItemEntity> bleItems) {
        BleDb db = BleDb.getInstance(requireContext().getApplicationContext());
        new Thread(() -> {
            db.bleItemsDao().insertAllBleItems(bleItems);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(mContext, "BLE History Save to list", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }


}
