package com.example.blutoothscan.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.blutoothscan.Model.BleItemEntity;

import java.util.List;


@Dao
public interface BleItemsDao {
    @Insert
    void insertBleItem(BleItemEntity bleItem);

    @Insert
    void insertAllBleItems(List<BleItemEntity> bleItems);

    @Query("SELECT * FROM ble_items")
    LiveData<List<BleItemEntity>> getAllBleItems();

    @Delete
    void deleteBleItem(BleItemEntity bleItem);

}

