package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.ChatLine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RenderHelper;
import net.minecraft.client.player.EntityPlayerSP;
import net.minecraft.client.render.entity.RenderItem;
import net.minecraft.game.entity.player.InventoryPlayer;
import net.minecraft.game.item.ItemStack;
import org.lwjgl.opengl.GL11;

public final class GuiIngame extends Gui {
	
	private static RenderItem itemRenderer = new RenderItem();
	private List<ChatLine> chatMessageList = new ArrayList<ChatLine>();
	private Random rand = new Random();
	private Minecraft mc;
	private int updateCounter = 0;

	public GuiIngame(Minecraft mc) {
		this.mc = mc;
	}

	public final void renderGameOverlay(float var1) {
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
		if(this.mc.thePlayer.heartsLife < 10) {
			var20 = false;
		}
		
		this.rand.setSeed((long)(this.updateCounter * 312871));
		
		// draw HUD
		if (this.mc.playerController.shouldDrawHUD()) {
			
			int x, y;
			
			// render armor damage
			x = width / 2 - 101;
			
			if (this.mc.thePlayer.inventory.getPlayerArmorValue() > 0) {
				
				for (int i=0; i<4; i++) {
					
					y = height - 9 - (i << 3);
					
					int armorPieceDura = this.mc.thePlayer.inventory.getPlayerArmorValue(i);
					
					if (armorPieceDura == 1)
						y += this.rand.nextInt(2);
					
					this.drawTexturedModalRect(x, y, 16 + armorPieceDura * 9, 18 + (3 - i) * 9, 9, 9);
				}
			}
			
			// render health
			if (!this.mc.thePlayer.isCreativeMode) {
				
				int playerHealth     = this.mc.thePlayer.health,
					playerPrevHealth = this.mc.thePlayer.prevHealth;
				
				for (int i = 0; i < 10; i++) {
	
					byte var26 = 0;
					if (var20) {
						var26 = 1;
					}
	
					x = width / 2 - 91 + (i << 3);
					y = height - 32;
					if (playerHealth <= 4) {
						y += this.rand.nextInt(2);
					}
	
					this.drawTexturedModalRect(x, y, 16 + var26 * 9, 0, 9, 9);
					if (var20) {
						if((i << 1) + 1 < playerPrevHealth) {
							this.drawTexturedModalRect(x, y, 70, 0, 9, 9);
						}
	
						if((i << 1) + 1 == playerPrevHealth) {
							this.drawTexturedModalRect(x, y, 79, 0, 9, 9);
						}
					}
	
					if ((i << 1) + 1 < playerHealth) {
						this.drawTexturedModalRect(x, y, 52, 0, 9, 9);
					}
	
					if ((i << 1) + 1 == playerHealth) {
						this.drawTexturedModalRect(x, y, 61, 0, 9, 9);
					}
				}
			}

			// render air bubbles on GUI
			if (this.mc.thePlayer.isInsideOfWater()) {
				
				int a = (int) Math.ceil((double)(this.mc.thePlayer.air - 2) * 10.0D / 300.0D);
				int b = (int) Math.ceil((double)this.mc.thePlayer.air       * 10.0D / 300.0D) - a;

				for (int c = 0; c < a + b; c++) {
					if (c < a) {
						this.drawTexturedModalRect(width / 2 - 91 + (c << 3), height - 32 - 9, 16, 9, 9, 9);
					} else {
						this.drawTexturedModalRect(width / 2 - 91 + (c << 3), height - 32 - 9, 25, 9, 9, 9);
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

		// render hotbar
		for(int i = 0; i < 9; i++) {
			
			int var25 = width / 2 - 90 + i * 20 + 2;
			int var21 = height - 16 - 3;
			
			ItemStack var22 = this.mc.thePlayer.inventory.mainInventory[i];
			
			if(var22 != null) {
				float var9 = (float) var22.animationsToGo - var1;
				if(var9 > 0.0F) {
					GL11.glPushMatrix();
					float var26 = 1.0F + var9 / 5.0F;
					GL11.glTranslatef((float)(var25 + 8), (float)(var21 + 12), 0.0F);
					GL11.glScalef(1.0F / var26, (var26 + 1.0F) / 2.0F, 1.0F);
					GL11.glTranslatef((float)(-(var25 + 8)), (float)(-(var21 + 12)), 0.0F);
				}

				itemRenderer.renderItemIntoGUI(this.mc.renderEngine, var22, var25, var21);
				if(var9 > 0.0F) {
					GL11.glPopMatrix();
				}

				itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRenderer, var22, var25, var21);
			}
		}

		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_NORMALIZE);
		
		// showFPS (basically debug settings/F3 menu)
		if (this.mc.options.showFPS) {
		    
		    String text1 = "Minecraft ";
		    String text2 = "Forever Indev ";
		    String text3 = " (" + this.mc.debug + ")";
		    
		    // Draw "Minecraft" in white
		    fontRend.drawStringWithShadow(text1, 2, 2, 16777215);
		    
		    // Draw "Forever Indev" in cyan
		    fontRend.drawStringWithShadow(text2, 2 + fontRend.getStringWidth(text1), 2, 0x00FFFF);
		    
		    // Draw the remaining text in white
		    fontRend.drawStringWithShadow(text3, 2 + fontRend.getStringWidth(text1 + text2), 2, 16777215);
		    
		    Minecraft var23 = this.mc;
		    fontRend.drawStringWithShadow(var23.renderGlobal.getDebugInfoRenders(), 2, 12, 16777215);
		    
		    var23 = this.mc;
		    fontRend.drawStringWithShadow(var23.renderGlobal.getDebugInfoEntities(), 2, 22, 16777215);
		    
		    var23 = this.mc;
		    fontRend.drawStringWithShadow("P: " + var23.effectRenderer.getStatistics() + ". T: " + var23.theWorld.debugSkylightUpdates(), 2, 32, 16777215);
		    
		    // draw held item ID
		    ItemStack currItem = this.mc.thePlayer.inventory.getCurrentItem();
		    fontRend.drawStringWithShadow("Held ID: " + (currItem == null ? "N/A" : currItem.itemID), 2, 42, 16777215);
		    
		    // memory calculations (scary)
		    long maxMem   = Runtime.getRuntime().maxMemory();
		    long totalMem = Runtime.getRuntime().totalMemory();
		    long freeMem  = Runtime.getRuntime().freeMemory();
		    long what     = maxMem - freeMem;
		    
		    String var18 = "Free memory: " + what * 100L / maxMem + "% of " + maxMem / 1024L / 1024L + "MB";
		    drawString(fontRend, var18, width - fontRend.getStringWidth(var18) - 2, 2, 16777215);
		    
		    var18 = "Allocated memory: " + totalMem * 100L / maxMem + "% (" + totalMem / 1024L / 1024L + "MB)";
		    drawString(fontRend, var18, width - fontRend.getStringWidth(var18) - 2, 12, 16777215);
		    
		} else {
		    String text1 = "Minecraft ";
		    String text2 = "Forever Indev";
		    
		    // Draw "Minecraft" in white
		    fontRend.drawStringWithShadow(text1, 2, 2, 16777215);
		    
		    // Draw "Forever Indev" in cyan
		    fontRend.drawStringWithShadow(text2, 2 + fontRend.getStringWidth(text1), 2, 0x00FFFF);
		}


		// draw chat
		for(int i = 0; i < this.chatMessageList.size() && i < 10; i++) {
			
			ChatLine chatline = this.chatMessageList.get(i);
			
			fontRend.drawStringWithShadow(chatline.message, 2, height - 8 - i * 9 - 20, 16777215);
		}
		
		// draw item name
		ItemStack currItem = this.mc.thePlayer.inventory.getCurrentItem();
		if (currItem != null)
			drawCenteredStringWithBackground(fontRend, currItem.getName(), width / 2, height - 48, currItem.getItem().getRarity().color);

	}
	
	public final void updateChatMessages() {
		this.updateCounter++;
		
		for(int i = 0; i < this.chatMessageList.size(); i++) {
			
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
