package de.kswmd.bloodhunger.ui.inventory;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import de.kswmd.bloodhunger.Assets;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.ItemComponent;

public class InventorySlot extends WidgetGroup {

    private Inventory inventory;
    private ItemComponent item;
    private Image itemImage;
    private Skin skin;
    private boolean selected;

    public InventorySlot(Skin skin) {
        super();
        this.skin = skin;
    }

    public InventorySlot(Skin skin, String drawable) {
        super();
        this.skin = skin;
        addActor(new Image(skin, drawable));
    }

    public InventorySlot(Skin skin, Actor... actors) {
        super(actors);
        this.skin = skin;
    }

    @Override
    public void pack() {
        super.pack();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void setItem(ItemComponent item) {
        removeItem();
        this.item = item;
        TextureAtlas images = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_IMAGES);
        this.itemImage = new Image(images.findRegion(item.itemType.resourceImage));
        this.itemImage.setSize(getWidth(), getHeight());
        addActor(itemImage);
        inventory.notifyOnItemAdded(this, item);
    }

    public void removeItem() {
        removeActor(itemImage);
        itemImage = null;
        inventory.notifyOnItemRemoved(this, item);
        this.item = null;
    }

    public boolean hasItem() {
        return item != null;
    }

    public boolean isEmpty() {
        return !hasItem();
    }

    public void select() {
        Image background = (Image) getChild(0);
        background.setDrawable(skin, "inventory_box_selected");
        selected = true;
    }

    public void unselect() {
        Image background = (Image) getChild(0);
        background.setDrawable(skin, "inventory_box");
        selected = false;
    }

    public ItemComponent getItemComponent() {
        return item;
    }

    public Image getItemImage() {
        return itemImage;
    }

    public boolean isSelected() {
        return selected;
    }

    public void hoverIn() {
        Image background = (Image) getChild(0);
        if (isSelected()) {
            background.setDrawable(skin, "inventory_box_hover_selected");
        } else {
            background.setDrawable(skin, "inventory_box_hover");
        }
    }

    public void hoverOut() {
        if (isSelected()) {
            select();
        } else {
            unselect();
        }
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory(){
        return this.inventory;
    }

    @Override
    public String toString() {
        return "InventorySlot{" +
                "inventoryIndex=" + inventory.getSlotIndex(this) +
                '}';
    }
}
