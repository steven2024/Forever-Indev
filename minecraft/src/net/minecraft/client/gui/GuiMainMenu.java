package net.minecraft.client.gui;

import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.GL11;
import util.MathHelper;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.reflect.Method;

public final class GuiMainMenu extends GuiScreen {
    private static final String[] SPLASHES = new String[] {
        "Not-quite indev!",
        "Chests on the glass door!",
        "NP is not in P!",
        "The blue-twintailed girl is right!",
        "In development hell, did I development sin?",
        "Bubbles from the gum machine!",
        "[INSERT SPLASH HERE]",
        "Full-stop!",
        "[EXTREMELY LOUD INCORRECT BUZZER]",
        "Bricks in the wall!",
        "Catch the falling stars!",
        "Syntax error at line 42!",
        "Glitches in the matrix!",
        "Null pointer exception!",
        "Endless loops ahead!",
        "In the rabbit hole!",
        "Blue screen of life!",
        "Assembly required!",
        "Pixels and polygons!",
        "Legacy code detected!",
        "Unicorns in disguise!",
        "404: Splash not found!",
        "Press F to pay respects!"
    };
    private static final String CURRENT_SPLASH = SPLASHES[(int) (Math.random() * SPLASHES.length)];

    public GuiMainMenu() {
        // No additional initialization needed for splashes
    }

    public final void updateScreen() {}

    protected final void keyTyped(char character, int keycode) {}

    public final void initGui() {
        this.controlList.clear();
        this.controlList.add(new GuiButtonText(1, this.width / 2 - 100, this.height / 4 + 48, "Generate new level..."));
        this.controlList.add(new GuiButtonText(2, this.width / 2 - 100, this.height / 4 + 72, "Load level..."));
        this.controlList.add(new GuiButtonText(0, this.width / 2 - 100, this.height / 4 + 96, "Options..."));
        this.controlList.add(new GuiButtonText(3, this.width / 2 - 100, this.height / 4 + 132, "Exit Game"));

        // Disable load level button if no session
        if (this.mc.session == null) {
            this.controlList.get(1).enabled = false;
        }
    }

    protected final void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.options));
                break;
            case 1:
                this.mc.displayGuiScreen(new GuiNewLevel(this));
                break;
            case 2:
                if (this.mc.session != null) {
                    this.mc.displayGuiScreen(new GuiLoadLevel(this));
                }
                break;
            case 3:
                this.mc.shutdownMinecraftApplet();
                break;
        }
    }

    public final void drawScreen(int mouseX, int mouseY) {
        this.drawDefaultBackground();

        // Set texture filtering to linear
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        // Draw the logo
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/logo.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator.instance.setColorOpaque_I(16777215);
        this.drawTexturedModalRect((this.width - 256) / 2, 30, 0, 0, 256, 64);

        // Draw "Forever Indev" text in cyan under the logo, moved up and larger
        GL11.glPushMatrix();
        GL11.glTranslatef((this.width / 2), 75.0F, 0.0F); // Move it up more
        float scaleIndev = 1.5F; // Make it larger
        GL11.glScalef(scaleIndev, scaleIndev, 1);
        String indevText = "Forever Indev";
        drawCenteredString(this.fontRenderer, indevText, 0, 0, 65535);  // 65535 is the color code for cyan
        GL11.glPopMatrix();

        // Draw other elements
        String text = "Made by Blue. Distribute!";
        drawString(this.fontRenderer, text, this.width - this.fontRenderer.getStringWidth(text) - 2, this.height - 10, 16777215);

        // Memory info
        long maxMem = Runtime.getRuntime().maxMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        long usedMem = totalMem - freeMem;

        text = "Free memory: " + usedMem * 100L / maxMem + "% of " + maxMem / 1024L / 1024L + "MB";
        drawString(this.fontRenderer, text, this.width - this.fontRenderer.getStringWidth(text) - 2, 2, 8421504);

        text = "Allocated memory: " + totalMem * 100L / maxMem + "% (" + totalMem / 1024L / 1024L + "MB)";
        drawString(this.fontRenderer, text, this.width - this.fontRenderer.getStringWidth(text) - 2, 12, 8421504);

        // Draw current date and time in "Month day, Year @ time" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy @ h:mm a");
        String dateTime = dateFormat.format(new Date());
        drawString(this.fontRenderer, "Current time: " + dateTime, 2, 2, 16777215);

        // Draw buttons and splash text
        super.drawScreen(mouseX, mouseY);

        // Draw splash text
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (this.width / 2 + 110), 85.0F, 0.0F);
        GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);

        float scale = 1.8F - MathHelper.abs(MathHelper.sin((float)(System.currentTimeMillis() % 1000L) / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);
        scale = scale * 100.0F / (float)(this.fontRenderer.getStringWidth(CURRENT_SPLASH) + 32);
        GL11.glScalef(scale, scale, 1);
        drawCenteredString(this.fontRenderer, CURRENT_SPLASH, 0, -8, 16776960);

        GL11.glPopMatrix();
    }
}
