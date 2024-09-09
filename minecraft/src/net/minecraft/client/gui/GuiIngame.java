package net.minecraft.client.gui;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.minecraft.client.ChatLine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RenderHelper;
import net.minecraft.client.gui.container.SlotAccessory;
import net.minecraft.client.player.EntityPlayerSP;
import net.minecraft.client.player.MovementInputFromOptions;
import net.minecraft.client.render.entity.RenderItem;
import net.minecraft.game.entity.player.InventoryPlayer;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemQuiver;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.enchant.Enchant;
import net.minecraft.game.level.World;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public final class GuiIngame extends Gui {

    private static RenderItem itemRenderer = new RenderItem();
    private List<ChatLine> chatMessageList = new ArrayList<ChatLine>();
    private Random rand = new Random();
    private Minecraft mc;
    private int updateCounter = 0;
    private long lastUpdateTime = 0;
    private ItemStack lastItemStack = null;
    private boolean itemWasDeselected = false;
    private List<Enchant> lastEnchants = new ArrayList<>();

    // Cached system information
    private String cachedOSInfo = null;
    private String cachedCPUInfo = null;

    public GuiIngame(Minecraft mc) {
        this.mc = mc;

        // Initialize OSHI to get system information
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();
        cachedCPUInfo = processor.getProcessorIdentifier().getName();

        OperatingSystem os = systemInfo.getOperatingSystem();
        cachedOSInfo = os.toString();
    }

    private static boolean isLaunchedWithClientLaunch() {
        // Get the main class name from the stack trace
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 0) {
            String mainClass = stackTrace[0].getClassName();
            return "Client.launch".equals(mainClass);
        }
        return false;
    }

    public final void renderGameOverlay(float var1) {
        // Check if the GUI should be hidden
        if (this.mc.hideGui) {
            ScaledResolution scaledResolution = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);

            int width = scaledResolution.getScaledWidth(),
                height = scaledResolution.getScaledHeight();

            FontRenderer fontRend = this.mc.fontRenderer;

            this.mc.entityRenderer.setupOverlayRendering();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/gui.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_BLEND);
            return;
        }
        ScaledResolution scaledResolution = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);

        int width = scaledResolution.getScaledWidth(),
            height = scaledResolution.getScaledHeight();

        FontRenderer fontRend = this.mc.fontRenderer;

        this.mc.entityRenderer.setupOverlayRendering();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/gui.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        InventoryPlayer var5 = this.mc.thePlayer.inventory;
        this.zLevel = -90.0F;
        this.drawTexturedModalRect(width / 2 - 91, height - 22, 0, 0, 182, 22);
        this.drawTexturedModalRect(width / 2 - 91 - 1 + var5.currentItem * 20, height - 22 - 1, 0, 22, 24, 22);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/icons.png"));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
        this.drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
        GL11.glDisable(GL11.GL_BLEND);
        boolean var20 = this.mc.thePlayer.heartsLife / 3 % 2 == 1;
        if (this.mc.thePlayer.heartsLife < 10) {
            var20 = false;
        }

        this.rand.setSeed((long) (this.updateCounter * 312871));

        // draw HUD
        if (this.mc.playerController.shouldDrawHUD()) {

            int x, y;

            // Render armor damage
            x = width / 2 - 102;

            // Check if a shield is equipped in slot 38
            ItemStack shieldItem = this.mc.thePlayer.inventory.getStackInSlot(38);

            if (this.mc.thePlayer.inventory.getPlayerArmorValue() > 0) {
                for (int i = 0; i < 4; i++) { // Keep loop limit to 4 for the 4 armor slots
                    if (shieldItem == null) {
                        // If no shield is equipped, shift the armor to the bottom of the screen
                        y = height - 9 - (i << 3);
                    } else {
                        // Default armor position
                        y = height - 20 - (i << 3);
                    }

                    ItemStack armorPiece = this.mc.thePlayer.inventory.armorInventory[i];
                    if (armorPiece != null) {
                        // Determine the color based on armor type
                        if (armorPiece.getItem() == Item.helmetIron || armorPiece.getItem() == Item.chestplateIron || armorPiece.getItem() == Item.leggingsIron || armorPiece.getItem() == Item.bootsIron) {
                            GL11.glColor4f(0.75F, 0.75F, 0.75F, 1.0F); // Gray for Iron
                        } else if (armorPiece.getItem() == Item.helmetGold || armorPiece.getItem() == Item.chestplateGold || armorPiece.getItem() == Item.leggingsGold || armorPiece.getItem() == Item.bootsGold) {
                            GL11.glColor4f(1.0F, 0.85F, 0.0F, 1.0F); // Gold color
                        } else if (armorPiece.getItem() == Item.helmetDiamond || armorPiece.getItem() == Item.chestplateDiamond || armorPiece.getItem() == Item.leggingsDiamond || armorPiece.getItem() == Item.bootsDiamond) {
                            GL11.glColor4f(0.5F, 0.8F, 1.0F, 1.0F); // Light blue for Diamond
                        } else {
                            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); // Default white
                        }

                        int armorPieceDura = this.mc.thePlayer.inventory.getPlayerArmorValue(i);

                        if (armorPieceDura == 1) {
                            y += this.rand.nextInt(2);
                        }

                        // Determine the texture position based on the durability of the armor piece
                        int textureX = 16 + armorPieceDura * 9;
                        int textureY = 18 + (3 - i) * 9;
                        this.drawTexturedModalRect(x, y, textureX, textureY, 9, 9);

                        // Reset color to white after drawing armor piece
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    }
                }
            }

            if (shieldItem != null) {
                int shieldTextureY = 54; // Default Y position of the shield icon

                // Shift the shield texture down by 10 pixels if it's not an iron shield
                if (shieldItem.getItem() != Item.ironShield) {
                    shieldTextureY += 10;
                }

                // Set color based on shield type
                if (shieldItem.getItem() == Item.ironShield) {
                    GL11.glColor4f(0.75F, 0.75F, 0.75F, 1.0F); // Gray for Iron Shield, matching Iron armor
                } else if (shieldItem.getItem() == Item.redClothCoveredShield) {
                    GL11.glColor4f(0.871F, 0.196F, 0.196F, 1.0F); // Red Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.orangeClothCoveredShield) {
                    GL11.glColor4f(0.871F, 0.529F, 0.196F, 1.0F); // Orange Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.yellowClothCoveredShield) {
                    GL11.glColor4f(0.871F, 0.871F, 0.196F, 1.0F); // Yellow Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.chartreuseClothCoveredShield) {
                    GL11.glColor4f(0.529F, 0.871F, 0.196F, 1.0F); // Chartreuse Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.greenClothCoveredShield) {
                    GL11.glColor4f(0.196F, 0.871F, 0.196F, 1.0F); // Green Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.springGreenClothCoveredShield) {
                    GL11.glColor4f(0.196F, 0.871F, 0.529F, 1.0F); // Springgreen Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.cyanClothCoveredShield) {
                    GL11.glColor4f(0.196F, 0.871F, 0.871F, 1.0F); // Cyan Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.capriClothCoveredShield) {
                    GL11.glColor4f(0.408F, 0.639F, 0.871F, 1.0F); // Capri Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.ultramarineClothCoveredShield) {
                    GL11.glColor4f(0.471F, 0.471F, 0.871F, 1.0F); // Ultramarine Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.violetClothCoveredShield) {
                    GL11.glColor4f(0.529F, 0.196F, 0.871F, 1.0F); // Violet Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.purpleClothCoveredShield) {
                    GL11.glColor4f(0.682F, 0.29F, 0.871F, 1.0F); // Purple Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.magentaClothCoveredShield) {
                    GL11.glColor4f(0.871F, 0.196F, 0.871F, 1.0F); // Magenta Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.roseClothCoveredShield) {
                    GL11.glColor4f(0.871F, 0.196F, 0.529F, 1.0F); // Rose Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.darkGrayClothCoveredShield) {
                    GL11.glColor4f(0.302F, 0.302F, 0.302F, 1.0F); // Darkgray Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.grayClothCoveredShield) {
                    GL11.glColor4f(0.561F, 0.561F, 0.561F, 1.0F); // Gray Cloth-Covered Shield
                } else if (shieldItem.getItem() == Item.whiteClothCoveredShield) {
                    GL11.glColor4f(0.871F, 0.871F, 0.871F, 1.0F); // White Cloth-Covered Shield
                }

                // Move the initial texture coordinates 36 pixels to the right
                int initialTextureX = 16 + 36;

                // Determine shield's durability shift based on itemDamage
                int shieldDurabilityShift = (int) ((double) shieldItem.itemDamage / shieldItem.isItemStackDamageable() * 9);

                // Render the shield icon one row down from the boots
                int shieldX = x + shieldDurabilityShift; // Position on screen (unchanged)
                int shieldY = height - 11; // Y position on screen (unchanged)
                int shieldHeight = shieldItem.getItem() == Item.ironShield ? 10 : 15; // Height based on shield type

                // Texture position is moved 36 pixels to the right, then adjusted left by durability shift
                int textureX = initialTextureX - shieldDurabilityShift * 9;

                this.drawTexturedModalRect(shieldX, shieldY, textureX, shieldTextureY, 9, shieldHeight);

                // Reset color to white after drawing the shield
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            // Render health with sprint-based visual effects
            if (!this.mc.thePlayer.isCreativeMode) {
                int playerHealth = this.mc.thePlayer.health;
                int playerPrevHealth = this.mc.thePlayer.prevHealth;
                float staminaRemaining = MovementInputFromOptions.staminaRemaining; // Access current stamina
                float maxStamina = MovementInputFromOptions.maxStamina;
                int redHearts = (int) Math.ceil((staminaRemaining / maxStamina) * 10); // Calculate number of hearts affected by stamina

                // Calculate if stamina is low enough to trigger flashing
                boolean shouldFlashFourthHeart = (staminaRemaining / maxStamina) < 0.2f;
                // Use System.currentTimeMillis() for time-based flashing effect
                boolean flashOn = (System.currentTimeMillis() / 500) % 2 == 0; // Toggle every 500ms (half a second)

                for (int i = 0; i < 10; i++) {
                    byte var26 = 0;
                    if (var20) {
                        var26 = 1;
                    }

                    x = width / 2 - 91 + (i << 3); // Calculate x position for each heart
                    y = height - 32; // Base y position for hearts

                    // Render background heart (default empty heart)
                    this.drawTexturedModalRect(x, y, 16 + var26 * 9, 0, 9, 9);

                    // Handle the overlay of full and half hearts based on the player's previous health state
                    if (var20) {
                        if ((i << 1) + 1 < playerPrevHealth) {
                            this.drawTexturedModalRect(x, y, 70, 0, 9, 9);
                        }

                        if ((i << 1) + 1 == playerPrevHealth) {
                            this.drawTexturedModalRect(x, y, 79, 0, 9, 9);
                        }
                    }

                    // Render the fourth heart with a flashing effect if stamina is low
                    if (i == 3 && shouldFlashFourthHeart && !flashOn) {
                        // Skip rendering to create the flashing effect
                        continue;
                    }

                    // Render hearts based on current health with stamina overlay effects
                    for (int i1 = 0; i1 < 10; i1++) {
                        int heartX = width / 2 - 91 + (i1 * 8); // Calculate x position for each heart
                        int heartY = height - 32; // Base y position for hearts

                        // Render background heart (default empty heart)
                        this.drawTexturedModalRect(heartX, heartY, 16, 0, 9, 9);

                        // Determine if we are dealing with full or half hearts
                        int heartHealthValue = i1 * 2; // Each heart index represents 2 health points (full heart)

                        if (heartHealthValue + 1 < playerHealth) {
                            // The current heart is a full heart
                            if (staminaRemaining <= heartHealthValue) {
                                // Render the full heart with the stamina overlay (light gray)
                                GL11.glColor4f(0.75F, 0.8F, 0.8F, 1.0F); // Light gray
                                this.drawTexturedModalRect(heartX, heartY, 52, 0, 9, 9);
                                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); // Reset to white after drawing
                            } else {
                                // Render a normal full heart
                                this.drawTexturedModalRect(heartX, heartY, 52, 0, 9, 9);
                            }
                        } else if (heartHealthValue + 1 == playerHealth) {
                            // The current heart is a half heart
                            if (staminaRemaining <= heartHealthValue) {
                                // Render the half heart with the stamina overlay (light gray)
                                GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F); // Light gray
                                this.drawTexturedModalRect(heartX, heartY, 61, 0, 9, 9);
                                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); // Reset to white after drawing
                            } else {
                                // Render a normal half heart
                                this.drawTexturedModalRect(heartX, heartY, 61, 0, 9, 9);
                            }
                        }
                    }

                    // Reset color to default after drawing each heart
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

                }
            }

            // render air bubbles on GUI
            if (this.mc.thePlayer.isInsideOfWater() && !this.mc.thePlayer.isCreativeMode) {

                int a = (int) Math.ceil((double)(this.mc.thePlayer.air - 2) * 10.0D / 300.0D);
                int b = (int) Math.ceil((double)this.mc.thePlayer.air       * 10.0D / 300.0D) - a;

                for (int c = 0; c < a + b; c++) {
                    if (c < a) {
                        this.drawTexturedModalRect(width / 2 - 91 + (c << 3), height - 32 - 9, 16, 9, 9, 9);
                    } else {
                        this.drawTexturedModalRect(width / 2 - 91 + (c << 3), height - 32 - 9, 25, 9, 9, c);
                    }
                }
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_NORMALIZE);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();

        // Enable transparency for all items that have it
        // render hotbar
        for (int i = 0; i < 9; i++) {

            int var25 = width / 2 - 90 + i * 20 + 2;
            int var21 = height - 16 - 3;

            ItemStack var22 = this.mc.thePlayer.inventory.mainInventory[i];

            if (var22 != null) {
                float var9 = (float) var22.animationsToGo - var1;
                if (var9 > 0.0F) {
                    GL11.glPushMatrix();
                    float var26 = 1.0F + var9 / 5.0F;
                    GL11.glTranslatef((float) (var25 + 8), (float) (var21 + 12), 0.0F);
                    GL11.glScalef(1.0F / var26, (var26 + 1.0F) / 2.0F, 1.0F);
                    GL11.glTranslatef((float) (-(var25 + 8)), (float) (-(var21 + 12)), 0.0F);
                }

                // Enable blending to use the texture's alpha values
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                // Render the item using the texture's own alpha channel
                itemRenderer.renderItemIntoGUI(this.mc.renderEngine, var22, var25, var21);
                if (var9 > 0.0F) {
                    GL11.glPopMatrix();
                }

                itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRenderer, var22, var25, var21);

                // Disable blending after rendering
                GL11.glDisable(GL11.GL_BLEND);
            }
        }

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_NORMALIZE);

        // Player coordinates
        double playerX = this.mc.thePlayer.posX;
        double playerY = this.mc.thePlayer.posY;
        double playerZ = this.mc.thePlayer.posZ;

        // Always draw "Minecraft Forever Indev"
        String text1 = "Minecraft ";
        String text2 = "Forever Indev";

        // Draw "Minecraft" in white
        fontRend.drawStringWithShadow(text1, 2, 2, 16777215);
 
        // Draw "Forever Indev" in cyan
        fontRend.drawStringWithShadow(text2, 2 + fontRend.getStringWidth(text1), 2, 0x00FFFF);

        // Add additional text if running inside an IDE
        if (isLaunchedWithClientLaunch()) {
            String ideText = " (Running in IDE)";
            fontRend.drawStringWithShadow(ideText, 2 + fontRend.getStringWidth(text1 + text2), 2, 16711680); // Red color
        }

        // Show FPS (basically debug/F3 menu)
        if (this.mc.currentScreen == null && this.mc.options.showFPS) {
            String text3 = " (" + this.mc.debug + ")";

            // Draw the remaining text in white
            fontRend.drawStringWithShadow(text3, 2 + fontRend.getStringWidth(text1 + text2), 2, 16777215);

            Minecraft var23 = this.mc;
            fontRend.drawStringWithShadow(var23.renderGlobal.getDebugInfoRenders(), 2, 12, 16777215);

            var23 = this.mc;
            fontRend.drawStringWithShadow(var23.renderGlobal.getDebugInfoEntities(), 2, 22, 16777215);

            var23 = this.mc;
            fontRend.drawStringWithShadow("Particles: " + var23.effectRenderer.getStatistics() + ". Skylight Updates: " + var23.theWorld.debugSkylightUpdates(), 2, 32, 16777215);

            // Draw held item ID
            ItemStack currItem = this.mc.thePlayer.inventory.getCurrentItem();
            fontRend.drawStringWithShadow("Held ID: " + (currItem == null ? "N/A" : currItem.itemID), 2, 42, 16777215);

            // Memory calculations
            long maxMem   = Runtime.getRuntime().maxMemory();
            long totalMem = Runtime.getRuntime().totalMemory();
            long freeMem  = Runtime.getRuntime().freeMemory();
            long what     = maxMem - freeMem;

            // Calculate the new Y position for the memory info
            int memoryInfoStartY = 12;  // 2 pixels below the score

            // Draw the memory information below the score
            String var18 = "Free memory: " + what * 100L / maxMem + "% of " + maxMem / 1024L / 1024L + "MB";
            drawString(fontRend, var18, width - fontRend.getStringWidth(var18) - 2, memoryInfoStartY, 16777215);

            var18 = "Allocated memory: " + totalMem * 100L / maxMem + "% (" + totalMem / 1024L / 1024L + "MB)";
            drawString(fontRend, var18, width - fontRend.getStringWidth(var18) - 2, memoryInfoStartY + 10, 16777215);

            // Fetch GPU information using LWJGL
            String gpu = GL11.glGetString(GL11.GL_RENDERER);
            String gpuDriver = GL11.glGetString(GL11.GL_VERSION);  // Get GPU driver version

            // Fetch Java version
            String javaVersion = System.getProperty("java.version");

            // Fetch CPU architecture information
            String cpuArch = System.getProperty("os.arch");

            // Determine if the game is in fullscreen or windowed mode
            String screenMode = Display.isFullscreen() ? "(Fullscreen)" : "(Windowed)";

            // Fetch display resolution
            DisplayMode displayMode = Display.getDisplayMode();
            String resolution = displayMode.getWidth() + "x" + displayMode.getHeight() + " " + screenMode;

            String coordinates = String.format("XYZ: %.1f / %.1f / %.1f", playerX, playerY, playerZ);

            float lookingPitch = this.mc.thePlayer.rotationPitch;
            float lookingYaw = this.mc.thePlayer.rotationYaw;

            String direction = "";
            String axis = "";
            float yaw = this.mc.thePlayer.rotationYaw;
            yaw = yaw % 360;
            if (yaw < 0) {
                yaw += 360;
            }

            if (yaw >= 45 && yaw < 135) {
                direction = "north";
                axis = "Towards negative Z";
            } else if (yaw >= 135 && yaw < 225) {
                direction = "east";
                axis = "Towards positive X";
            } else if (yaw >= 225 && yaw < 315) {
                direction = "south";
                axis = "Towards positive Z";
            } else {
                direction = "west";
                axis = "Towards negative X";
            }

            String lookingPosition = String.format("Facing: %s (%s) (Yaw: %.1f / Pitch: %.1f)", direction, axis, lookingYaw, lookingPitch);
            int worldLevelType = World.levelType;
            // Predefined world themes
            String[] worldTheme = new String[]{"Normal", "Hell", "Paradise", "Woods"};

            // Drawing additional debug information
            fontRend.drawStringWithShadow("OS: " + cachedOSInfo, 2, 52, 16777215);
            fontRend.drawStringWithShadow("CPU: " + cachedCPUInfo, 2, 62, 16777215);
            fontRend.drawStringWithShadow("GPU: " + gpu, 2, 72, 16777215);
            fontRend.drawStringWithShadow("GPU Driver: " + gpuDriver, 2, 82, 16777215);  // Display GPU driver version
            fontRend.drawStringWithShadow("Java Version: " + javaVersion, 2, 92, 16777215);  // Display Java version
            fontRend.drawStringWithShadow("CPU Arch: " + cpuArch, 2, 112, 16777215);  // Display CPU architecture
            fontRend.drawStringWithShadow("Resolution: " + resolution, 2, 122, 16777215);  // Display screen resolution
            fontRend.drawStringWithShadow(coordinates, 2, 132, 16777215);
            fontRend.drawStringWithShadow(lookingPosition, 2, 142, 16777215);

            // Ensure the levelType is within the range of the worldTheme array
            if (worldLevelType >= 0 && worldLevelType < worldTheme.length) {
                // Display the world type dynamically based on the levelType
                String displayedTheme = "World Type: " + worldTheme[worldLevelType];
                fontRend.drawStringWithShadow(displayedTheme, 2, 152, 16777215);
            } else {
                // Default in case the levelType is out of range
                fontRend.drawStringWithShadow("World Type: Unknown", 2, 152, 16777215);
            }

            // Draw current date and time in "Month day, Year @ time" format
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy @ h:mm a");
            String dateTime = dateFormat.format(new Date());
            drawString(fontRend, "Current time: " + dateTime, 2, 162, 16777215);
        }
        
        InventoryPlayer inventoryPlayer = this.mc.thePlayer.inventory;
        int arrowCount = 0;

        // Check if a quiver is equipped in slot 36
        ItemStack quiverStack = inventoryPlayer.getStackInSlot(36);
        boolean isQuiverEquipped = quiverStack != null && quiverStack.getItem() == Item.quiver;

        if (isQuiverEquipped) {
            // Check if arrows are in slot 37
            ItemStack arrowStack = inventoryPlayer.getStackInSlot(37);
            if (arrowStack != null && arrowStack.getItem() == Item.arrow) {
                arrowCount = arrowStack.stackSize;
            }
        }

        // Display arrow count only if the quiver is equipped
        String arrowString = "Arrows: " + arrowCount;

        if (isQuiverEquipped) {
            fontRend.drawStringWithShadow(arrowString, width / 2 + 8, height - 33, 0xFFFFFF);
        }

     // Render chat messages
        int chatLineHeight = 10; // Adjust height according to font size
        int maxVisibleLines = 10; // Number of visible chat lines, adjust as needed

        for (int i = 0; i < Math.min(this.chatMessageList.size(), maxVisibleLines); i++) {
            ChatLine chatline = this.chatMessageList.get(i);
            String message = "> " + chatline.message; // Add '>' in front of each message

            int xPos = 2; // X position for chat messages
            int yPos = height - 40 - (i * chatLineHeight); // Y position based on index and height

            fontRend.drawStringWithShadow(message, xPos, yPos, 16777215); // Render the chat message in white
        }

     // Custom score rendering: label in white, number in yellow
        String scoreLabel = "Score: ";
        int scoreNumber = this.mc.thePlayer.getScore();
        String scoreNumberStr = String.valueOf(scoreNumber);  // Convert the score to a string
        int scoreLabelWidth = fontRend.getStringWidth(scoreLabel);
        int scoreNumberWidth = fontRend.getStringWidth(scoreNumberStr);

        // Draw the label in white
        fontRend.drawStringWithShadow(scoreLabel, width - scoreLabelWidth - scoreNumberWidth - 2, 2, 0xFFFFFF);

        // Draw the score number in yellow
        fontRend.drawStringWithShadow(scoreNumberStr, width - scoreNumberWidth - 2, 2, 0xFFFF00);


        ItemStack currItem = this.mc.thePlayer.inventory.getCurrentItem();
        if (currItem != null) {
            Collection<Enchant> enchantments = currItem.getEnchants();
            if (enchantments != null) {
                // Detect if the item has been switched or if the enchantments have changed
                boolean itemChanged = lastItemStack == null || !currItem.equals(lastItemStack);
                boolean enchantmentsChanged = !enchantments.equals(lastEnchants); // Track the last enchantments list

                // Reset the animation if the item changes, enchantments change, or the item is reselected
                if (itemChanged || enchantmentsChanged || itemWasDeselected) {
                    lastItemStack = currItem;
                    lastEnchants = new ArrayList<>(enchantments); // Update the last enchantments list
                    lastUpdateTime = System.currentTimeMillis(); // Reset the animation timer
                    itemWasDeselected = false; // Reset the deselection flag
                }
            } else {
                // If no item is selected, mark that the item was deselected
                itemWasDeselected = true;
            }

            int enchantmentCount = enchantments.size();
            int dynamicDisplayTime = 4000 + enchantmentCount * 500; // Base 4 seconds + 0.5 seconds per enchantment

            // Only render if within the calculated time of the last update
            if (currItem != null && System.currentTimeMillis() - lastUpdateTime <= dynamicDisplayTime) {
                // Determine the width of the box based on the widest line (either item name or enchantments).
                int maxWidth = fontRend.getStringWidth(currItem.getName());
                for (Enchant enchant : enchantments) {
                    maxWidth = Math.max(maxWidth, fontRend.getStringWidth(enchant.toString()));
                }

                // Calculate box dimensions with padding
                int boxPadding = 2;
                int lineHeight = 9; // Use getFontHeight() instead of FONT_HEIGHT
                int boxWidth = maxWidth + boxPadding * 2;
                int boxHeight = (lineHeight + boxPadding) * (1 + enchantments.size());

                // Calculate initial position for the box
                int xPos = (width / 2) - (boxWidth / 2);
                int targetYPos = height - 34 - boxHeight; // Target Y position (final position)

                // Implement smooth downward animation
                float animationProgress = (System.currentTimeMillis() - lastUpdateTime) / 500.0f; // Animation duration of 0.5 seconds
                int yPos = (int) (targetYPos - (10 * (1.0f - Math.min(1.0f, animationProgress)))); // Start 20 pixels above and move down smoothly

                // Draw the background box with exact padding
                drawRect(xPos, yPos, xPos + boxWidth, yPos + boxHeight, new Color(0, 0, 0, 150).getRGB());

                // Draw the item name centered inside the box
                fontRend.drawStringWithShadow(currItem.getName(), (width / 2) - (fontRend.getStringWidth(currItem.getName()) / 2), yPos + boxPadding, currItem.getItem().getRarity().color);

                // Draw the enchantments below the item name
                if (!enchantments.isEmpty()) {
                    int yOffset = yPos + lineHeight + boxPadding;

                    long currentTime = System.currentTimeMillis();
                    float hue = (currentTime % 10000L) / 10000.0F; // Rainbow effect cycling
                    float brightness = 0.7F; // Fixed brightness
                    int enchantCount = enchantments.size();

                    int index = 0;
                    for (Enchant enchant : enchantments) {
                        // Calculate the rainbow color for each enchantment
                        float offsetHue = (hue + (index / (float) enchantCount)) % 1.0F;
                        int color = Color.HSBtoRGB(offsetHue, 0.7F, brightness);

                        // Draw each enchantment centered inside the box
                        fontRend.drawStringWithShadow(enchant.toString(), (width / 2) - (fontRend.getStringWidth(enchant.toString()) / 2), yOffset, color);

                        yOffset += lineHeight + boxPadding; // Move down for the next line
                        index++;
                    }
                }
            }
        } else {
            // If no item is selected, mark that the item was deselected
            itemWasDeselected = true;
        }
    }

    public final void updateChatMessages() {
        this.updateCounter++;

        for (int i = 0; i < this.chatMessageList.size(); i++) {
            ChatLine chatline = this.chatMessageList.get(i);

            if (chatline.updateCounter > 200) {
                this.chatMessageList.remove(this.chatMessageList.size() - 1);
            } else {
                chatline.updateCounter++;
            }
        }
    }

    public final void addChatMessage(String message) {
        chatMessageList.add(0, new ChatLine(message));
    }
}
