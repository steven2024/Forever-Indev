package net.minecraft.client.gui;

import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class GuiTopScores extends GuiScreen {

    private static final int MAX_SCORES = 10; // Number of top scores to display
    private static final String SCORE_FILE = "scores.txt"; // File to read scores from
    private List<ScoreEntry> topScores;
    private ScoreEntry hoveredEntry = null; // Score entry currently being hovered over
    private GuiScreen guiIngameMenu; // Store reference to the guiIngameMenu

    // Constructor accepting guiIngameMenu
    public GuiTopScores(GuiScreen guiIngameMenu) {
        this.topScores = new ArrayList<>();
        this.guiIngameMenu = guiIngameMenu;
    }

    @Override
    public final void initGui() {
        this.controlList.clear();
        this.controlList.add(new GuiButtonText(0, this.width / 2 - 100, this.height - 40, "Back to Menu"));

        // Load top scores from file
        loadTopScores();
    }

    @Override
    protected final void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            if (this.guiIngameMenu != null) {
                this.mc.displayGuiScreen(this.guiIngameMenu); // Open guiIngameMenu
            } else {
                this.mc.displayGuiScreen(new GuiMainMenu()); // Fallback to the main menu
            }
        }
    }

    @Override
    public final void drawScreen(int mouseX, int mouseY) {
        // Save OpenGL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            drawDefaultBackground();

            // Center the title
            drawCenteredString(this.fontRenderer, "Top Scores", this.width / 2, 20, 0xFFFFAA);

            // Adjusted width for better centering of the content
            int totalWidth = 200;
            int startX = (this.width - totalWidth) / 2;

            // Calculate the centered positions for each column
            int rankX = startX + totalWidth / 6;
            int scoreLabelX = startX + totalWidth / 2;
            int timeAliveX = startX + 5 * (totalWidth / 6);

            // Move the labels down a bit
            int labelsY = 40;

            // Draw column headers centered above the corresponding columns
            drawCenteredString(this.fontRenderer, "Rank", rankX, labelsY, 0xFFFFFF);
            drawCenteredString(this.fontRenderer, "Score", scoreLabelX, labelsY, 0xFFFFFF);
            drawCenteredString(this.fontRenderer, "Time Alive", timeAliveX, labelsY, 0xFFFFFF);

            int yOffset = 55;  // Move the scores down to align with the labels
            int entryHeight = 12; // Reduced height for compactness
            int backgroundColor = 0x55000000; // Semi-transparent background for entries

            hoveredEntry = null; // Reset hovered entry before checking each score

            for (int i = 0; i < topScores.size(); i++) {
                ScoreEntry entry = topScores.get(i);

                // Calculate the combined width of score and "points"
                String scoreText = String.format("%d points", entry.score);
                int scoreTextWidth = this.fontRenderer.getStringWidth(scoreText);

                // Calculate the position of the score so that the center aligns with the "Score" label center
                int scoreX = scoreLabelX - scoreTextWidth / 2;

                // Draw background for entry
                drawRect(startX, yOffset, startX + totalWidth, yOffset + entryHeight, backgroundColor);

                // Determine the color for the score
                int scoreColor = (i == 0) ? 0xFFD700 : 0xFFFF00; // Gold for highest score, yellow for others

                // Draw rank, score + points, and time alive centered within the available space
                drawCenteredString(this.fontRenderer, String.format("%d.", i + 1), rankX, yOffset + 2, 0xFFFFFF); // Rank number
                drawString(this.fontRenderer, String.format("%d", entry.score), scoreX, yOffset + 2, scoreColor); // Score
                drawString(this.fontRenderer, " points", scoreX + this.fontRenderer.getStringWidth(String.valueOf(entry.score)), yOffset + 2, 0xFFFFFF); // Points label in white
                drawCenteredString(this.fontRenderer, entry.timeAlive, timeAliveX, yOffset + 2, scoreColor); // Time Alive

                // Check if mouse is hovering over this entry
                if (mouseX >= startX && mouseX <= startX + totalWidth &&
                    mouseY >= yOffset && mouseY <= yOffset + entryHeight) {
                    hoveredEntry = entry;
                }

                yOffset += entryHeight;
            }

            // Draw tooltip only if hovering over an entry
            if (hoveredEntry != null) {
                int tooltipWidth = this.fontRenderer.getStringWidth("Date: " + hoveredEntry.date) + 8;
                int tooltipX = mouseX + 6; // Position tooltip closer to the mouse horizontally
                int tooltipY = mouseY - 6; // Position tooltip closer to the mouse vertically

                String tooltipText = String.format("Date: %s", hoveredEntry.date);

             // Draw background for the tooltip
                int tooltipbackgroundColor = 0xAA000000; // More opaque background
                drawRect(tooltipX - 2, tooltipY - 2, tooltipX + tooltipWidth + 2, tooltipY + 14, tooltipbackgroundColor); // Adjusted for padding

                // Draw a border for the tooltip
                int borderColor = 0xFFFFFFFF; // White border
                drawRect(tooltipX - 2, tooltipY - 2, tooltipX + tooltipWidth + 2, tooltipY - 1, borderColor); // Top border
                drawRect(tooltipX - 2, tooltipY + 13, tooltipX + tooltipWidth + 2, tooltipY + 14, borderColor); // Bottom border
                drawRect(tooltipX - 2, tooltipY - 2, tooltipX - 1, tooltipY + 14, borderColor); // Left border
                drawRect(tooltipX + tooltipWidth + 1, tooltipY - 2, tooltipX + tooltipWidth + 2, tooltipY + 14, borderColor); // Right border

                // Draw tooltip text
                drawString(this.fontRenderer, tooltipText, tooltipX + 4, tooltipY + 2, 0xFFFFFF); // White text
            }

            super.drawScreen(mouseX, mouseY); // Call the parent method if needed
        } finally {
            // Restore OpenGL state
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private void loadTopScores() {
        File scoreFile = new File(this.mc.mcDataDir, SCORE_FILE);

        if (!scoreFile.exists()) {
            return; // Do nothing if the file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
            String line;
            while ((line = reader.readLine()) != null && topScores.size() < MAX_SCORES) {
                String[] parts = line.split(",");
                if (parts.length >= 4) { // Adjusted to 4 for date
                    String playerName = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    long timeAlive = Long.parseLong(parts[2]);
                    String date = formatDate(parts[3]);
                    topScores.add(new ScoreEntry(playerName, score, formatTime(timeAlive), date));
                }
            }
            topScores.sort((a, b) -> Integer.compare(b.score, a.score)); // Sort scores in descending order
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatTime(long ticks) {
        long seconds = ticks / 20;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private String formatDate(String dateString) {
        // Convert it to the format "MMMM d, yyyy @ h:mm a"
        java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("MMMM d, yyyy @ h:mm a");

        try {
            java.util.Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return dateString; // Return the original string in case of an error
        }
    }

    private static class ScoreEntry {
        String playerName;
        int score;
        String timeAlive;
        String date;

        ScoreEntry(String playerName, int score, String timeAlive, String date) {
            this.playerName = playerName;
            this.score = score;
            this.timeAlive = timeAlive;
            this.date = date;
        }
    }
}
