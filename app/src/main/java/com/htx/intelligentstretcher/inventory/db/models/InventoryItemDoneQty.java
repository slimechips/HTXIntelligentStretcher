package com.htx.intelligentstretcher.inventory.db.models;

public class InventoryItemDoneQty {
    private int itemId;
    private int doneQty;

    public InventoryItemDoneQty(int itemId, int doneQty) {
        this.itemId = itemId;
        this.doneQty = doneQty;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getDoneQty() {
        return doneQty;
    }

    public void setDoneQty(int doneQty) {
        this.doneQty = doneQty;
    }
}
