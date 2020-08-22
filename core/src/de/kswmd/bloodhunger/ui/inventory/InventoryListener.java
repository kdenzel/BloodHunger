package de.kswmd.bloodhunger.ui.inventory;

import de.kswmd.bloodhunger.components.ItemComponent;

public interface InventoryListener {

    void onItemAdded(InventorySlot slot, ItemComponent itemComponent);

    void onItemRemoved(InventorySlot slot, ItemComponent itemComponent);

}
