package de.kswmd.bloodhunger.ui.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import de.kswmd.bloodhunger.components.ItemComponent;

public final class Inventory {

    private int itemCount;
    private Array<InventorySlot> inventorySlots = new Array<>(8);

    private Inventory() {
    }

    public static Inventory create() {
        return new Inventory();
    }

    public void addInventorySlot(InventorySlot slot) {
        inventorySlots.add(slot);
    }

    public boolean removeInventorySlot(InventorySlot slot){
        return inventorySlots.removeValue(slot,true);
    }

    public boolean addItem(ItemComponent item) {
        if (isFull())
            return false;
        inventorySlots.get(itemCount).setItem(item);
        itemCount++;
        return true;
    }

    public boolean isFull() {
        return itemCount == inventorySlots.size;
    }

    public void setSelected(int slotIndex) {
        for (int i = 0; i < inventorySlots.size; i++) {
            if (slotIndex == i)
                inventorySlots.get(i).select();
            else
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
}
