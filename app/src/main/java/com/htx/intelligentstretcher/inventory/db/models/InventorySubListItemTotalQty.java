package com.htx.intelligentstretcher.inventory.db.models;

public class InventorySubListItemTotalQty {
    private int subListItemId;

    private int totalQty;

    private int parentSubListItemId;

    public InventorySubListItemTotalQty(int subListItemId, int totalQty, int parentSubListItemId) {
        this.subListItemId = subListItemId;
        this.totalQty = totalQty;
        this.parentSubListItemId = parentSubListItemId;
    }

    public int getSubListItemId() {
        return subListItemId;
    }

    public void setSubListItemId(int subListItemId) {
        this.subListItemId = subListItemId;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public int getParentSubListItemId() {
        return parentSubListItemId;
    }

    public void setParentSubListItemId(int parentSubListItemId) {
        this.parentSubListItemId = parentSubListItemId;
    }
}
