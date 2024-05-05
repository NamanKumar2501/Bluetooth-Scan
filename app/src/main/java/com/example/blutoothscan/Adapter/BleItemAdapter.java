package com.example.blutoothscan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.blutoothscan.Database.BleDb;
import com.example.blutoothscan.Model.BleEntity;
import com.example.blutoothscan.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BleItemAdapter extends RecyclerView.Adapter<BleItemAdapter.ViewHolder> {
    private List<BleEntity> bleEntities;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(BleEntity entity);
    }

    public BleItemAdapter(List<BleEntity> bleEntities, OnItemClickListener itemClickListener) {
        this.bleEntities = bleEntities;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ble_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BleEntity entity = bleEntities.get(position);
        holder.tvItemName.setText(entity.getItemName());

        // Display current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = dateFormat.format(new Date());

        if (holder.tvDateTime != null) {
            holder.tvDateTime.setText(currentTime);
        }


        // Delete
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemFromDatabase(v.getContext(), entity);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBleHistoryFragment(entity);
            }
        });

    }

    @Override
    public int getItemCount() {
        return bleEntities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvDelete, tvDateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.item_name);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
        }
    }

    private void deleteItemFromDatabase(Context context, BleEntity bleItem) {
        BleDb db = BleDb.getInstance(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.bleHistoryDao().deleteBleItem(bleItem);
            }
        }).start();
    }

    public void openBleHistoryFragment(BleEntity entity) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(entity);
        }
    }

}
