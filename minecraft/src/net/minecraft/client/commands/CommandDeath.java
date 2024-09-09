package net.minecraft.client.commands;

import net.minecraft.client.Minecraft;

public class CommandDeath extends Command {

    @Override
    public String getName() {
        return "/death";
    }

    @Override
    public void runCommand(Minecraft mc, String[] args) {
        if (args.length != 1) {
            mc.ingameGUI.addChatMessage("Invalid argument. Use /death to instantly die.");
            return;
        }

        try {
            // Set the player's health to 0 to trigger death
            mc.thePlayer.setHealth(0);
            mc.ingameGUI.addChatMessage("You have died.");
        } catch (Exception e) {
            mc.ingameGUI.addChatMessage("An error occurred while trying to execute the /death command.");
        }
    }

    @Override
    public void showHelpMessage(Minecraft mc) {
        mc.ingameGUI.addChatMessage("/death");
        mc.ingameGUI.addChatMessage("    Causes your player to die instantly.");
    }
}
