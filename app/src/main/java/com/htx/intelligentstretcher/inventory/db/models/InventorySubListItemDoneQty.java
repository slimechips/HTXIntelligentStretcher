package com.htx.intelligentstretcher.inventory.db.models;

import androidx.room.Ignore;
import androidx.room.PrimaryKey;

public class InventorySubListItemDoneQty {
    private int subListItemId;

    private int totalQtyDone;


    private int parentSubListItemId;

    public InventorySubListItemDoneQty(int subListItemId, int totalQtyDone, int parentSubListItemId) {
        this.subListItemId = subListItemId;
        this.totalQtyDone = totalQtyDone;
        this.parentSubListItemId = parentSubListItemId;
    }

    public int getSubListItemId() {
        return subListItemId;
    }

    public void setSubListItemId(int subListItemId) {
        this.subListItemId = subListItemId;
    }


    public int getTotalQtyDone() {
        return totalQtyDone;
    }

    public void setTotalQtyDone(int totalQtyDone) {
        this.totalQtyDone = totalQtyDone;
    }

    public int getParentSubListItemId() {
        return parentSubListItemId;
    }

    public void setParentSubListItemId(int parentSubListItemId) {
        this.parentSubListItemId = parentSubListItemId;
    }
}
