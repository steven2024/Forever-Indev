package net.minecraft.client.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.game.level.World;

public class GuiConfirmationScreen extends GuiScreen {

    private final GuiIngameMenu parentScreen;

    public GuiConfirmationScreen(GuiIngameMenu parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        // Only show the confirmation buttons if the setting allows
        if (this.mc.options.showExitConfirmation) {
            this.controlList.clear();
            this.controlList.add(new GuiButtonText(0, this.width / 2 - 100, this.height / 4 + 24, "Save and Exit"));
            this.controlList.add(new GuiButtonText(1, this.width / 2 - 100, this.height / 4 + 48, "Exit Without Saving"));
            this.controlList.add(new GuiButtonText(2, this.width / 2 - 100, this.height / 4 + 72, "Cancel"));
        } else {
            // If not showing confirmation, just exit directly
            exit();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (this.mc.options.showExitConfirmation) {
            switch (button.id) {
                case 0:
                            // Open the GuiSaveLevel screen to allow the user to choose a save location
                            this.mc.displayGuiScreen(new GuiSaveLevel(this.parentScreen));
                    break;

                case 1:
                    // Exit without saving
                    exit();
                    break;

                case 2:
                    // Cancel and return to the game
                    this.mc.displayGuiScreen(this.parentScreen);
                    break;
            }
        }
    }

    private void saveCurrentWorld(File saveFile) {
        try {
            // Replace this with actual world-saving logic
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile));
            out.writeObject(this.mc.theWorld);
            out.close();
            System.out.println("World saved to " + saveFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exit() {
        this.mc.theWorld = null;
        this.mc.displayGuiScreen(null);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (this.mc.options.showExitConfirmation) {
            this.drawDefaultBackground();
            drawCenteredString(this.fontRenderer, "You have unsaved changes. Do you want to save them before exiting?", this.width / 2, 40, 16777215);
            super.drawScreen(mouseX, mouseY);
        }
    }
}
