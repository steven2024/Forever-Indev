package net.minecraft.client.commands;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.enchant.Enchant;
import net.minecraft.game.item.enchant.EnchantType;

public class CommandEnchant extends Command {
    
    // Create a mapping of enchantment names to their types
    private static final Map<String, EnchantType> ENCHANT_MAP = new HashMap<>();
    
    static {
        for (EnchantType enchant : EnchantType.values()) {
            ENCHANT_MAP.put(enchant.getName().toLowerCase(), enchant);
        }
    }

    public String getName() {
        return "/enchant";
    }

    public void runCommand(Minecraft mc, String[] args) {
        if (args.length == 1) {
            mc.ingameGUI.addChatMessage("Format: /enchant [enchant type] [opt. enchant level]");
            return;
        }

        // Check if enchantment exists using name
        String enchantName = args[1].toLowerCase();
        EnchantType enchantType = ENCHANT_MAP.get(enchantName);
        
        if (enchantType == null) {
            mc.ingameGUI.addChatMessage("Unknown enchant type: " + args[1]);
            return;
        }

        // Check if there's a valid held item to enchant
        ItemStack held = mc.thePlayer.inventory.getCurrentItem();

        if (held == null) {
            mc.ingameGUI.addChatMessage("No item held to enchant.");
            return;
        }

        if (held.getItem().getItemStackLimit() != 1) {
            mc.ingameGUI.addChatMessage("Can only enchant non-stackable items.");
            return;
        }

        // Get enchant level
        int enchantLevel = 1;
        if (args.length > 2) {
            try {
                enchantLevel = Integer.parseInt(args[2]);
            } catch (Exception e) {
                mc.ingameGUI.addChatMessage("Enchant level must be a positive non-zero integer.");
                return;
            }

            if (enchantLevel <= 0) {
                mc.ingameGUI.addChatMessage("Enchant level must be a positive non-zero integer.");
                return;
            }
        }

        // Add enchantment
        Enchant enchant = held.addEnchant(enchantType, enchantLevel);
        mc.ingameGUI.addChatMessage("Enchanted " + held.getItem().getName() + " with " + enchant);
    }

    public void showHelpMessage(Minecraft mc) {
        mc.ingameGUI.addChatMessage("/enchant [enchant type] [opt. enchant level]");
        mc.ingameGUI.addChatMessage("    Enchants the held item with the provided enchant.");
    }
}
