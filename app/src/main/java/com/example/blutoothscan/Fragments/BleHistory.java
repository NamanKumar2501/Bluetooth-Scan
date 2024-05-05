package com.example.blutoothscan.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blutoothscan.Adapter.HistoryAdapter;
import com.example.blutoothscan.Constant.Constants;
import com.example.blutoothscan.Database.BleDb;
import com.example.blutoothscan.Model.BleItemEntity;
import com.example.blutoothscan.PrefrenceManager.PreferenceManager;
import com.example.blutoothscan.R;
import com.example.blutoothscan.databinding.FragmentBleHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class BleHistory extends Fragment {
    private FragmentBleHistoryBinding binding;
    private HistoryAdapter adapter;
    private List<BleItemEntity> bleItemsList;

    public BleHistory() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBleHistoryBinding.inflate(getLayoutInflater(), container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        binding.bleHistoryRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        bleItemsList = new ArrayList<>();

        adapter = new HistoryAdapter(bleItemsList, requireContext());
        binding.bleHistoryRecycler.setAdapter(adapter);

        String historyIdToFilter = PreferenceManager.getStringValue(getContext(),Constants.SAVE_BLE_LIST);
        Log.d("TAG", "History ID to filter: " + historyIdToFilter);

        loadItemsFromDatabase(historyIdToFilter);

    }


    private void loadItemsFromDatabase(String historyId) {
        Log.d("TAG", "Loading items from database for history ID: " + historyId);
        BleDb db = BleDb.getInstance(getContext());

        db.bleHistoryDao().getBleItemsByHistoryID(historyId).observe(getViewLifecycleOwner(), new Observer<List<BleItemEntity>>() {
            @Override
            public void onChanged(List<BleItemEntity> bleItems) {
                try {
                    if (bleItems != null && !bleItems.isEmpty()) {
                        Log.d("TAG", "Received " + bleItems.size() + " items from database");

                        bleItemsList.clear();
                        bleItemsList.addAll(bleItems);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("TAG", "No items found in database for history ID: " + historyId);
                    }
                } catch (Exception e) {
                    Log.e("TAG", "onChanged: " + e);
                }

            }
        });


    }


}
