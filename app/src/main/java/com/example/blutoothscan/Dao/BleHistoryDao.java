package com.example.blutoothscan.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.blutoothscan.Model.BleEntity;
import com.example.blutoothscan.Model.BleItemEntity;

import java.util.List;


@Dao
public interface BleHistoryDao {
    @Insert
    void insertBleEntity(BleEntity bleEntity);

    @Query("SELECT * FROM ble_item_name")
    LiveData<List<BleEntity>> getAllBleEntities();

    @Query("SELECT * FROM ble_items WHERE history_ID = :historyId")
    LiveData<List<BleItemEntity>> getBleItemsByHistoryID(String historyId);

    @Delete
    void deleteBleItem(BleEntity bleItem);

}

