package de.kswmd.bloodhunger.ui.inventory;

import com.badlogic.gdx.utils.Array;
import de.kswmd.bloodhunger.components.ItemComponent;
import de.kswmd.bloodhunger.components.PlayerComponent;

public final class Inventory {

    private int itemCount;
    private int selectedSlotIndex;
    private final Array<InventorySlot> inventorySlots = new Array<>(8);
    private final Array<InventoryListener> inventoryListeners = new Array<>(1);

    private Inventory() {
    }

    public static Inventory create() {
        return new Inventory();
    }

    public void addInventorySlot(InventorySlot slot) {
        slot.setInventory(this);
        inventorySlots.add(slot);
    }

    public boolean removeInventorySlot(InventorySlot slot) {
        slot.setInventory(null);
        return inventorySlots.removeValue(slot, true);
    }

    public boolean addItem(ItemComponent item) {
        if (isFull())
            return false;
        for(int i = 0; i < inventorySlots.size; i++){
            InventorySlot slot = inventorySlots.get(i);
            if(!slot.hasItem()){
                slot.setItem(item);
                return true;
            }
        }
        return false;
    }

    public boolean isFull() {
        return itemCount == inventorySlots.size;
    }

    public void setSelected(int slotIndex) {
        for (int i = 0; i < inventorySlots.size; i++) {
            if (slotIndex == i) {
                inventorySlots.get(i).select();
                selectedSlotIndex = i;
            } else
                inventorySlots.get(i).unselect();
        }
    }

    public void setHover(int slotIndex) {
        for (int i = 0; i < inventorySlots.size; i++) {
            InventorySlot slot = inventorySlots.get(i);
            if (slotIndex == i && !slot.isSelected())
                inventorySlots.get(i).hoverIn();
            else
                inventorySlots.get(i).hoverOut();
        }
    }

    public void unhover() {
        for (int i = 0; i < inventorySlots.size; i++) {
            InventorySlot slot = inventorySlots.get(i);
            inventorySlots.get(i).hoverOut();
        }
    }

    public void addListener(InventoryListener listener) {
        inventoryListeners.add(listener);
    }

    public boolean removeListener(InventoryListener listener) {
        return inventoryListeners.removeValue(listener, true);
    }

    void notifyOnItemAdded(InventorySlot slot, ItemComponent itemComponent) {
        itemCount++;
        inventoryListeners.forEach(listener -> listener.onItemAdded(slot, itemComponent));
    }

    void notifyOnItemRemoved(InventorySlot slot, ItemComponent itemComponent) {
        itemCount--;
        inventoryListeners.forEach(listener -> listener.onItemRemoved(slot, itemComponent));
    }

    public InventorySlot getSelectedSlot() {
        return inventorySlots.get(selectedSlotIndex);
    }

    public int getSlotIndex(InventorySlot inventorySlot) {
        return inventorySlots.indexOf(inventorySlot,true);
    }
}
