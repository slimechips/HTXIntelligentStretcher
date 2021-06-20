package com.htx.intelligentstretcher.inventory.db.models;

import androidx.room.Embedded;
import androidx.room.Relation;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryPage;
import com.htx.intelligentstretcher.inventory.db.entity.InventorySubListItem;

public class InventorySubListItemWithPage {
    @Embedded
    public InventorySubListItem subListItem;
    @Relation(
            parentColumn = "subListItemId",
            entityColumn = "subListItemId"
    )
    public InventoryPage page;
}
