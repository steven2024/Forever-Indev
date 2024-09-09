package net.minecraft.client.gui;

import org.lwjgl.opengl.GL11;
import net.minecraft.game.entity.player.EntityPlayerSP;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class GuiGameOver extends GuiScreen {

    private String SCORE_FILE; // Declare SCORE_FILE as a final instance variable
    private int highScore = 0;
    private long highTimeAlive = 0;
    private boolean newHighScore = false;
    private boolean scoreSaved = false;  // Track if the score has been saved

    public GuiGameOver() {
        this.SCORE_FILE = null; // SCORE_FILE is initialized later in initGui()
    }

    public final void initGui() {
        // Initialize SCORE_FILE when mc and mcDataDir are guaranteed to be available
        if (this.mc != null && this.mc.mcDataDir != null) {
            this.SCORE_FILE = new File(this.mc.mcDataDir, "scores.txt").getAbsolutePath();
        }

        // Ensure the scores.txt file exists
        ensureScoreFileExists();

        this.controlList.clear();
        this.controlList.add(new GuiButtonText(1, this.width / 2 - 100, this.height / 4 + 72, "Generate new level..."));
        this.controlList.add(new GuiButtonText(2, this.width / 2 - 100, this.height / 4 + 96, "Load level.."));

        if (this.mc.session == null) {
            this.controlList.get(1).enabled = false;
        }

        // Load the high score from file
        loadHighScore();
    }

    protected final void keyTyped(final char character, final int keycode) {
        // Empty, no key handling needed here
    }

    protected final void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.options));
                break;
            case 1:
                this.mc.displayGuiScreen(new GuiNewLevel(this));
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiLoadLevel(this));
                break;
        }
    }

    public final void drawScreen(final int mouseX, final int mouseY) {
        drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        drawCenteredString(this.fontRenderer, "Game over!", this.width / 2 / 2, 30, 16777215);
        GL11.glPopMatrix();

        final EntityPlayerSP player = this.mc.thePlayer;
        final int currentScore = player.getScore();
        final long currentTimeAlive = player.getTimeAlive();

        // Save the current score and time alive only once
        if (!scoreSaved) {
            saveScoreAndTime(currentScore, currentTimeAlive);
            scoreSaved = true;
        }

        // Check if the current score is a new high score
        newHighScore = currentScore > highScore;

        // Format time alive in minutes and seconds
        final String timeAliveFormatted = formatTime(currentTimeAlive);

        // Adjusted Y position for the score and time alive
        final int yOffset = 100; // Adjusted Y position

        // Determine which label to use based on whether it's a new high score
        final String scoreLabel = newHighScore ? "New High Score: " : "Score: ";

        // Calculate the widths of the text and numbers for proper alignment
        final int scoreTextWidth = this.fontRenderer.getStringWidth(scoreLabel);
        final int scoreWidth = this.fontRenderer.getStringWidth(String.valueOf(currentScore));

        // Draw the score text (white)
        drawCenteredString(this.fontRenderer, scoreLabel, this.width / 2 - (scoreWidth / 2), yOffset, 16777215);

        // Draw the score number (yellow)
        drawCenteredString(this.fontRenderer, String.valueOf(currentScore), this.width / 2 + (scoreTextWidth / 2), yOffset, 16776960);

        // Calculate the widths of the text and numbers for time alive
        final int timeTextWidth = this.fontRenderer.getStringWidth("Time Alive: ");
        final int timeWidth = this.fontRenderer.getStringWidth(timeAliveFormatted);

        // Draw the time alive text (white) and number (yellow) next to each other
        drawCenteredString(this.fontRenderer, "Time Alive: ", this.width / 2 - (timeWidth / 2), yOffset + 12, 16777215);
        drawCenteredString(this.fontRenderer, timeAliveFormatted, this.width / 2 + (timeTextWidth / 2), yOffset + 12, 16776960);

        super.drawScreen(mouseX, mouseY);
    }

    private final void ensureScoreFileExists() {
        if (SCORE_FILE == null) {
            return; // Prevent file operations if SCORE_FILE is not initialized
        }

        final File scoreFile = new File(SCORE_FILE);
        try {
            if (!scoreFile.exists()) {
                scoreFile.createNewFile(); // Create the file if it doesn't exist
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private final void saveScoreAndTime(final int score, final long timeAlive) {
        if (SCORE_FILE == null) {
            return; // Prevent file operations if SCORE_FILE is not initialized
        }

        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE, true))) {
            // Get current date and time
            final String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            // Check if it's a new high score
            final String scoreType = score > highScore ? "High Score" : "Normal Score";

            // Write the entry with score type, score, time alive, and date/time
            writer.write(scoreType + "," + score + "," + timeAlive + "," + dateTime);
            writer.newLine();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private final void loadHighScore() {
        if (SCORE_FILE == null) {
            return; // Prevent file operations if SCORE_FILE is not initialized
        }

        final File scoreFile = new File(SCORE_FILE);
        if (!scoreFile.exists()) {
            return; // Do nothing if the file doesn't exist
        }

        try (final BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] parts = line.split(",");
                if (parts.length >= 3) {
                    final int score = Integer.parseInt(parts[1]);
                    final long timeAlive = Long.parseLong(parts[2]);
                    if (score > highScore) {
                        highScore = score;
                        highTimeAlive = timeAlive;
                    }
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private final String formatTime(final long ticks) {
        long seconds = ticks / 20;
        final long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public final boolean doesGuiPauseGame() {
        return true;  // Pauses the game when this GUI is active
    }
}
