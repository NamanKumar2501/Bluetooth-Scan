package com.example.blutoothscan.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blutoothscan.Adapter.BleItemAdapter;
import com.example.blutoothscan.Constant.Constants;
import com.example.blutoothscan.MainActivity;
import com.example.blutoothscan.Model.BleEntity;
import com.example.blutoothscan.Model.BleItemEntity;
import com.example.blutoothscan.PrefrenceManager.PreferenceManager;
import com.example.blutoothscan.R;
import com.example.blutoothscan.ViewModel.BleHistoryViewModel;
import com.example.blutoothscan.databinding.FragmentBleItemHistoryNameBinding;

import java.util.ArrayList;
import java.util.List;


public class BleItemHistoryName extends Fragment implements BleItemAdapter.OnItemClickListener {
    FragmentBleItemHistoryNameBinding binding;
    private BleItemAdapter adapter;
    BleItemEntity bleItemEntity;
    MainActivity mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBleItemHistoryNameBinding.inflate(getLayoutInflater(), container, false);
        // Initialize adapter for ListView
        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<BleEntity> bleEntities = new ArrayList<>();
        adapter = new BleItemAdapter(bleEntities,this);
        binding.historyRecyclerView.setAdapter(adapter);

        BleHistoryViewModel viewModel = new ViewModelProvider(requireActivity()).get(BleHistoryViewModel.class);
        viewModel.getAllBleEntities().observe(getViewLifecycleOwner(), entities -> {
            bleEntities.clear();
            bleEntities.addAll(entities);
            adapter.notifyDataSetChanged();
        });

        return  binding.getRoot();
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
    public void onItemClick(BleEntity entity) {

        if (mContext != null) {
            PreferenceManager.setStringValue(getContext(),Constants.SAVE_BLE_LIST, entity.getId());
            mContext.setFragment(new BleHistory(), "History");
        }

    }
}