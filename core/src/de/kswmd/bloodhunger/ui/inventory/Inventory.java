package de.kswmd.bloodhunger.ui.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import de.kswmd.bloodhunger.Assets;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.ItemComponent;

public final class Inventory {

    private static final String TAG = Inventory.class.getSimpleName();
    public static final int SIZE = 33;
    public static final int SLOTS = 8;
    public static final int BACKPACK_SIZE = SIZE - SLOTS;

    private int itemCount;
    private int selectedSlotIndex;
    private final Array<InventorySlot> inventorySlots = new Array<>(SLOTS);
    private final Array<InventoryListener> inventoryListeners = new Array<>(1);
    private boolean backpack;

    private Inventory() {
    }

    public static Inventory create() {
        TextureAtlas uiTextureAtlas = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_UI);
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"), uiTextureAtlas);
        DragAndDrop dnd = new DragAndDrop();
        Inventory inventory = new Inventory();
        for (int i = 0; i < SIZE; i++) {
            InventorySlot inventorySlot = new InventorySlot(skin, "inventory_box");
            inventorySlot.addListener(new InputListener() {

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    InventorySlot slot = ((InventorySlot) event.getListenerActor());
                    slot.hoverIn();
                    Gdx.app.debug(TAG, "Mouse enter on slot " + slot);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if (toActor == null || toActor.getParent() != event.getListenerActor()) {
                        InventorySlot slot = ((InventorySlot) event.getListenerActor());
                        slot.hoverOut();
                        Gdx.app.debug(TAG, "Mouse exit on slot " + slot);
                    }
                }
            });
            dnd.addSource(new DragAndDrop.Source(inventorySlot) {


                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    DragAndDrop.Payload payload = null;
                    InventorySlot slot = (InventorySlot) getActor();
                    if (slot.hasItem()) {
                        payload = new DragAndDrop.Payload();
                        payload.setObject(slot.getItemComponent());
                        payload.setDragActor(slot.getItemImage());
                        slot.removeItem();
                        dnd.setDragActorPosition(x, y - slot.getHeight());
                        Gdx.app.debug(TAG, "Drag start at slot " + slot);
                    }
                    return payload;
                }

                @Override
                public void drag(InputEvent event, float x, float y, int pointer) {
                    super.drag(event, x, y, pointer);
                    //Gdx.app.debug("Slot at pos ", ""+event);
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                    super.dragStop(event, x, y, pointer, payload, target);
                    InventorySlot sourceSlot = (InventorySlot) getActor();
                    if (target == null) {
                        sourceSlot.setItem((ItemComponent) payload.getObject());
                        Gdx.app.debug(TAG, "No target detected, snap back");
                    } else {
                        InventorySlot targetSlot = (InventorySlot) target.getActor();
                        if (targetSlot.hasItem()) {
                            sourceSlot.setItem(targetSlot.getItemComponent());
                            Gdx.app.debug(TAG, "Switch item with slot " + targetSlot);
                        } else {
                            Gdx.app.debug(TAG, "Set item on slot " + targetSlot);
                        }
                        targetSlot.setItem((ItemComponent) payload.getObject());

                    }
                }
            });
            dnd.addTarget(new DragAndDrop.Target(inventorySlot) {
                @Override
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    //we must hover and unhover here manually because the actor overlays the mouse cursor so no mouse entered and exit are registered
                    boolean doHoverAction = payload != null;
                    if (doHoverAction) {
                        InventorySlot target = ((InventorySlot) getActor());
                        target.getInventory().unhover();
                        target.hoverIn();
                    }
                    return doHoverAction;
                }

                @Override
                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                }
            });
            if(i == 0)
                inventorySlot.select();
            inventory.addInventorySlot(inventorySlot);
        }
        return inventory;
    }

    public boolean hasBackpack(){
        return backpack;
    }

    public void setBackpack(boolean backpack) {
        this.backpack = backpack;
    }

    private void addInventorySlot(InventorySlot slot) {
        slot.setInventory(this);
        inventorySlots.add(slot);
    }

    private boolean removeInventorySlot(InventorySlot slot) {
        slot.setInventory(null);
        return inventorySlots.removeValue(slot, true);
    }

    public boolean addItem(ItemComponent item) {
        if (isFull())
            return false;
        for (int i = 0; i < inventorySlots.size; i++) {
            InventorySlot slot = inventorySlots.get(i);
            if (!slot.hasItem()) {
                slot.setItem(item);
                return true;
            }
        }
        return false;
    }

    public boolean isFull() {
        return itemCount == SLOTS && !backpack || itemCount == inventorySlots.size;
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
        return inventorySlots.indexOf(inventorySlot, true);
    }

    public int size() {
        return inventorySlots.size;
    }

    public InventorySlot get(int i) {
        return inventorySlots.get(i);
    }
}
