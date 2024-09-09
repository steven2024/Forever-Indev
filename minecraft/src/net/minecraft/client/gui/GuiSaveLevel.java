package net.minecraft.client.gui;

import java.awt.Dialog;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public final class GuiSaveLevel extends GuiLoadLevel {

    private final int EDIT_BUTTON_ID_OFFSET = 100;
    private final int DELETE_BUTTON_ID_OFFSET = 200;
    private final int LEVEL_BUTTON_ID_OFFSET = 300;
    private final int CANCEL_BUTTON_ID = 6;  // ID for the Cancel button

    public GuiSaveLevel(GuiScreen parent) {
        super(parent);
        this.title = "Save level";
    }

    protected final FileDialog saveFileDialog() {
        return new FileDialog((Dialog) null, "Save level", FileDialog.SAVE);
    }

    public final void initGui() {
        super.initGui();
        ((GuiButtonText) this.controlList.get(5)).displayString = "Save file...";
    }

    protected final void setLevels(String[] levels) {
        this.controlList.clear();
        int centerX = this.width / 2;
        for (int i = 0; i < levels.length; i++) {
            // Center the level button
            GuiButtonText levelButton = new GuiButtonText(i, centerX - 75, 40 + i * 24, 150, 20, levels[i]);
            this.controlList.add(levelButton);

            // Center the edit button to the right of the level button
            GuiButtonText editButton = new GuiButtonText(EDIT_BUTTON_ID_OFFSET + i, centerX + 85, 40 + i * 24, 20, 20, "E");
            editButton.enabled = !levels[i].equals("-");
            this.controlList.add(editButton);

            // Center the delete button to the right of the edit button
            GuiButtonText deleteButton = new GuiButtonText(DELETE_BUTTON_ID_OFFSET + i, centerX + 110, 40 + i * 24, 20, 20, "-");
            deleteButton.enabled = !levels[i].equals("-");
            this.controlList.add(deleteButton);
        }

        // Add the Cancel button at the bottom
        this.controlList.add(new GuiButtonText(CANCEL_BUTTON_ID, centerX - 75, this.height / 6 + 168, 150, 20, "Cancel"));
    }

    protected final void actionPerformed(GuiButton button) {
        if (button.id < 5) { 
            String levelName = ((GuiButtonText) this.controlList.get(button.id)).displayString;
            if (levelName.equals("-")) {
                // If the slot is empty, open the naming dialog
                this.mc.displayGuiScreen(new GuiNameLevel(this, "", button.id));
            } else {
                // If the slot is not empty, save the level directly
                this.saveLevel(button.id, levelName);
            }
        } else if (button.id >= EDIT_BUTTON_ID_OFFSET && button.id < EDIT_BUTTON_ID_OFFSET + 5) {
            // Edit button functionality
            int levelButtonIndex = button.id - EDIT_BUTTON_ID_OFFSET;
            String currentLevelName = ((GuiButtonText) this.controlList.get(levelButtonIndex)).displayString;
            this.mc.displayGuiScreen(new GuiNameLevel(this, currentLevelName, levelButtonIndex));
        } else if (button.id >= DELETE_BUTTON_ID_OFFSET && button.id < DELETE_BUTTON_ID_OFFSET + 5) {
            // Delete button functionality
            deleteLevel(button.id - DELETE_BUTTON_ID_OFFSET);
        } else if (button.id == CANCEL_BUTTON_ID) {
            // Handle the Cancel button press by going back to the parent screen
            this.mc.displayGuiScreen(this.parent);
        } else {
            super.actionPerformed(button);
        }
    }

    private void saveLevel(int slot, String levelName) {
        File savesDir = new File(this.mc.mcDataDir, "saves");
        File folder = new File(savesDir, "World" + slot);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, "level.mclevel");

        try (FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(levelName);
            System.out.println("Level saved to slot " + slot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.mc.displayGuiScreen(this.parent); // Return to the parent screen after saving
    }

    private void deleteLevel(int slot) {
        File savesDir = new File(this.mc.mcDataDir, "saves");
        File folder = new File(savesDir, "World" + slot);
        if (folder.exists() && folder.isDirectory()) {
            deleteDirectory(folder);
            loadLocalLevels(); // Refresh the levels after deletion
        }
    }

    private boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (File child : dir.listFiles()) {
                if (!deleteDirectory(child)) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
