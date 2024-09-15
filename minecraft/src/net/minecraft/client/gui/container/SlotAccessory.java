package net.minecraft.client.gui.container;

import net.minecraft.game.IInventory;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemShield;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.entity.player.EntityPlayer;

public final class SlotAccessory extends Slot {

    // Constants for accessory types
    public static final int ACCESSORY_QUIVER = 0;
    public static final int ACCESSORY_ARROW = 1;
    public static final int ACCESSORY_SHIELD = 2; // Accessory type for shields

    private int accessoryType;
    private IInventory inventory;  // Store the inventory reference

    public SlotAccessory(GuiContainer guiHandler, IInventory inventory, int slotIndex, int x, int y, int accessoryType) {
        super(guiHandler, inventory, slotIndex, x, y);
        this.accessoryType = accessoryType;
        this.inventory = inventory;  // Assign the inventory reference
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;

        Item item = stack.getItem();

        switch (this.accessoryType) {
            case ACCESSORY_QUIVER:
                // Only allow the quiver item
                return item == Item.quiver && stack.stackSize == 1;
            case ACCESSORY_ARROW:
                // Only allow arrows if a quiver is present
                if (item == Item.arrow) {
                    return isQuiverInInventory();  // Only allow arrows if a quiver is equipped
                }
                return false;
            case ACCESSORY_SHIELD:
                // Only allow shields
                return item instanceof ItemShield && stack.stackSize == 1;
            default:
                return false;
        }
    }

    @Override
    public int getBackgroundIconIndex() {
        // Check if there's a quiver in inventory
        boolean hasQuiver = isQuiverInInventory();

        switch (this.accessoryType) {
            case ACCESSORY_QUIVER:
                return 15 + (4 << 4); // Positioned under the boots (4 corresponds to the quiver)
            case ACCESSORY_ARROW:
                // Use arrow disabled icon or appropriate index if no quiver
                return hasQuiver ? 15 + (5 << 4) : 15 + (6 << 4); // Use index 6 for arrow disabled icon
            case ACCESSORY_SHIELD:
                // Use an appropriate background icon for the shield slot (e.g., index 7)
                return 15 + (7 << 4); // Index for the shield icon
            default:
                return -1;
        }
    }

    private boolean isQuiverInInventory() {
        // Check slot 36 specifically for a quiver
        ItemStack quiverSlotStack = inventory.getStackInSlot(36);
        return quiverSlotStack != null && quiverSlotStack.getItem() == Item.quiver;
    }
}