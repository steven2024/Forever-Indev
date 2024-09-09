package net.minecraft.client.gui;

import java.awt.Dialog;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import net.minecraft.client.PlayerLoader;
import net.minecraft.game.level.World;

public class GuiLoadLevel extends GuiScreen implements Runnable {

    protected GuiScreen parent;
    private boolean finished = false;
    private boolean loaded = false;
    private String[] levels = new String[5];
    private String status = "";
    protected String title = "Load level";
    private boolean frozen = false;
    private File selectedFile;

    private final int DELETE_BUTTON_ID_OFFSET = 100;

    public GuiLoadLevel(GuiScreen parent) {
        this.parent = parent;
        // Initialize the levels array with default values
        for (int i = 0; i < levels.length; i++) {
            levels[i] = "-";
        }
    }

    public final void updateScreen() {
        if (this.selectedFile != null) {
            if (!this.selectedFile.getName().endsWith(".mclevel")) {
                this.selectedFile = new File(this.selectedFile.getAbsolutePath() + ".mclevel");
            }

            this.openLevel(this.selectedFile);
            this.selectedFile = null;
            this.mc.displayGuiScreen((GuiScreen) null);
        }
    }

    public void run() {
        try {
            this.status = "Getting level list...";
            loadLocalLevels();
            this.loaded = true;
        } catch (Exception e) {
            e.printStackTrace();
            this.status = "Failed to load levels";
            this.finished = true;
        }
    }

    protected void loadLocalLevels() {
        File savesDir = new File(this.mc.mcDataDir, "saves");
        for (int i = 0; i < 5; i++) {
            File folder = new File(savesDir, "World" + i);
            if (folder.exists() && folder.isDirectory()) {
                File file = new File(folder, "level.mclevel");
                if (file.exists()) {
                    levels[i] = readWorldName(file);
                } else {
                    levels[i] = "-";
                }
            } else {
                levels[i] = "-";
            }
        }
        setLevels(levels);
    }

    private String readWorldName(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (String) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "-";
    }

    protected void setLevels(String[] levels) {
        // Ensure that levels is not null
        if (levels == null) {
            levels = new String[]{"-", "-", "-", "-", "-"};
        }

        this.controlList.clear();
        int centerX = this.width / 2;
        for (int i = 0; i < 5; i++) {
            GuiButtonText levelButton = new GuiButtonText(i, centerX - 75, this.height / 6 + i * 24, 150, 20, levels[i]);
            levelButton.enabled = !levels[i].equals("-"); // Disable the button if there's no level
            this.controlList.add(levelButton);

            // Add Delete Button
            GuiButtonText deleteButton = new GuiButtonText(DELETE_BUTTON_ID_OFFSET + i, centerX + 85, this.height / 6 + i * 24, 20, 20, "-");
            deleteButton.enabled = !levels[i].equals("-");
            this.controlList.add(deleteButton);
        }
        this.controlList.add(new GuiButtonText(5, centerX - 75, this.height / 6 + 120 + 12, 150, 20, "Load file..."));
        this.controlList.add(new GuiButtonText(6, centerX - 75, this.height / 6 + 168, 150, 20, "Cancel"));
    }

    public void initGui() {
        new Thread(this).start();
        this.controlList.clear();

        // Ensuring that levels array is initialized and set
        if (levels == null || levels.length == 0) {
            levels = new String[]{"-", "-", "-", "-", "-"};
        }

        this.setLevels(levels);
    }

    protected void actionPerformed(GuiButton button) {
        if (this.frozen || !button.enabled) {
            return;
        }

        if (button.id < 5 && this.loaded) {
            this.openLevel(button.id);
        } else if (button.id >= DELETE_BUTTON_ID_OFFSET && button.id < DELETE_BUTTON_ID_OFFSET + 5) {
            // Delete button functionality
            this.deleteLevel(button.id - DELETE_BUTTON_ID_OFFSET);
        } else if (button.id == 5) {
            this.frozen = true;
            GuiLevelDialog dialog = new GuiLevelDialog(this);
            dialog.setDaemon(true);
            dialog.start();
        } else if (button.id == 6) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    protected FileDialog saveFileDialog() {
        return new FileDialog((Dialog) null, "Load level", FileDialog.LOAD);
    }

    protected void openLevel(int slot) {
        File savesDir = new File(this.mc.mcDataDir, "saves");
        File folder = new File(savesDir, "World" + slot);
        if (folder.exists() && folder.isDirectory()) {
            File file = new File(folder, "level.mclevel");
            if (file.exists()) {
                openLevel(file);
            }
        }
        this.mc.displayGuiScreen(null);
        this.mc.setIngameFocus();
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

    public final void drawScreen(int mouseX, int mouseY) {
        this.drawDefaultBackground();
        if (this.fontRenderer != null) {
            drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
        }
        if (!this.loaded) {
            if (this.fontRenderer != null) {
                drawCenteredString(this.fontRenderer, this.status, this.width / 2, this.height / 2 - 4, 16777215);
            }
        }
        if (this.controlList != null) {
            super.drawScreen(mouseX, mouseY);  // Proceed with drawing the controls if everything is initialized
        }
    }

    protected void openLevel(File file) {
        try (FileInputStream in = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(in)) {
            String worldName = (String) ois.readObject();
            World world = (new PlayerLoader(this.mc, this.mc.loadingScreen)).load(in);
            this.mc.setLevel(world);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static File a(GuiLoadLevel var0, File var1) {
        return var0.selectedFile = var1;
    }

    static boolean unknown(GuiLoadLevel var0, boolean var1) {
        return var0.frozen = false;
    }
}
