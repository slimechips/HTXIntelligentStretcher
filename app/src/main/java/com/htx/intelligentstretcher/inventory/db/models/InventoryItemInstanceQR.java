package com.htx.intelligentstretcher.inventory.db.models;


import com.htx.intelligentstretcher.inventory.db.entity.InventoryItemInstance;

import java.text.ParseException;

public class InventoryItemInstanceQR {
    private int instanceId;

    private int itemId;

    private String expiry;

    public InventoryItemInstanceQR(int instanceId, int itemId, String expiry) {
        this.instanceId = instanceId;
        this.itemId = itemId;
        this.expiry = expiry;
    }

    public InventoryItemInstance toInstance() throws ParseException {
        return new InventoryItemInstance(
                instanceId,
                itemId,
                InventoryItemInstance.EXPIRY_DATE_FORMAT.parse(expiry)
        );
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
