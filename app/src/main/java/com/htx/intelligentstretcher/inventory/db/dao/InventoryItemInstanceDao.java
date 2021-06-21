package com.htx.intelligentstretcher.inventory.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItemInstance;

import java.util.List;

@Dao
public interface InventoryItemInstanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(InventoryItemInstance instance);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(List<InventoryItemInstance> instances);
}
