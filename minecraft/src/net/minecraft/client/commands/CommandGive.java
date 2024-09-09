package net.minecraft.client.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.game.entity.misc.EntityItem;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CommandGive extends Command {

    // Create a mapping of item names to their instances
    private static final Map<String, Item> ITEM_MAP = new HashMap<>();

    static {
        for (Item item : Item.itemsList) {
            if (item != null) {
                ITEM_MAP.put(item.getName().toLowerCase(), item);
            }
        }
    }

    public String getName() {
        return "/give";
    }

    public void runCommand(Minecraft mc, String[] args) {
        if (args.length < 2) {
            mc.ingameGUI.addChatMessage("Usage: /give [item name/id] [opt. item amount]");
            return;
        }

        Item item = null;

        // Check if the input is a valid item name
        String itemNameOrId = args[1].toLowerCase();
        item = ITEM_MAP.get(itemNameOrId);

        // If item name not found, try parsing it as an ID
        if (item == null) {
            try {
                int itemID = Integer.parseInt(args[1]);
                if (itemID >= 0 && itemID < Item.itemsList.length) {
                    item = Item.itemsList[itemID];
                }
            } catch (NumberFormatException e) {
                mc.ingameGUI.addChatMessage("Invalid item name or ID: " + args[1]);
                return;
            }
        }

        if (item == null) {
            mc.ingameGUI.addChatMessage("Could not give: No item with name or ID " + args[1] + " exists.");
            return;
        }

        // Parse the count (optional)
        int count = 1;
        if (args.length > 2) {
            try {
                count = Integer.parseInt(args[2]);
                if (count < 1) count = 1; // Ensure count is at least 1
            } catch (Exception e) {
                // Default count is 1 if parsing fails
            }
        }

        // Create the item entity and spawn it in the world
        EntityItem itemEntity = new EntityItem(
                mc.theWorld,
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                new ItemStack(item, count)
        );

        itemEntity.delayBeforeCanPickup = 0;
        mc.theWorld.spawnEntityInWorld(itemEntity);

        mc.ingameGUI.addChatMessage("Gave player " + count + "x " + item.getName());
    }

    public void showHelpMessage(Minecraft mc) {
        mc.ingameGUI.addChatMessage("/give [item name/id] [opt. item amount]");
        mc.ingameGUI.addChatMessage("    Gives the player the specified item.");
    }
}
