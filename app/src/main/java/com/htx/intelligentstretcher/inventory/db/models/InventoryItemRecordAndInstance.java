package com.htx.intelligentstretcher.inventory.db.models;

import androidx.room.Embedded;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItemInstance;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItemRecord;

public class InventoryItemRecordAndInstance {
    @Embedded public InventoryItemRecord record;
    @Embedded public InventoryItemInstance instance;
}
