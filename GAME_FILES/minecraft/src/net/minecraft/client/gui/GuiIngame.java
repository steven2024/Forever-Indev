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

		int playerHealth     = this.mc.thePlayer.health,
			playerPrevHealth = this.mc.thePlayer.prevHealth;
		
		this.rand.setSeed((long)(this.updateCounter * 312871));
		int var10;
		int var12;
		
		// draw HUD
		if (this.mc.playerController.shouldDrawHUD()) {
			
			EntityPlayerSP var8 = this.mc.thePlayer;
			var10 = var8.inventory.getPlayerArmorValue();

			int var11;
			int var13;
			for(var11 = 0; var11 < 10; ++var11) {
				var12 = height - 32;
				if(var10 > 0) {
					var13 = width / 2 + 91 - (var11 << 3) - 9;
					if((var11 << 1) + 1 < var10) {
						this.drawTexturedModalRect(var13, var12, 34, 9, 9, 9);
					}

					if((var11 << 1) + 1 == var10) {
						this.drawTexturedModalRect(var13, var12, 25, 9, 9, 9);
					}

					if((var11 << 1) + 1 > var10) {
						this.drawTexturedModalRect(var13, var12, 16, 9, 9, 9);
					}
				}

				byte var26 = 0;
				if(var20) {
					var26 = 1;
				}

				int var14 = width / 2 - 91 + (var11 << 3);
				if(playerHealth <= 4) {
					var12 += this.rand.nextInt(2);
				}

				this.drawTexturedModalRect(var14, var12, 16 + var26 * 9, 0, 9, 9);
				if(var20) {
					if((var11 << 1) + 1 < playerPrevHealth) {
						this.drawTexturedModalRect(var14, var12, 70, 0, 9, 9);
					}

					if((var11 << 1) + 1 == playerPrevHealth) {
						this.drawTexturedModalRect(var14, var12, 79, 0, 9, 9);
					}
				}

				if((var11 << 1) + 1 < playerHealth) {
					this.drawTexturedModalRect(var14, var12, 52, 0, 9, 9);
				}

				if((var11 << 1) + 1 == playerHealth) {
					this.drawTexturedModalRect(var14, var12, 61, 0, 9, 9);
				}
			}

			if(this.mc.thePlayer.isInsideOfWater()) {
				var11 = (int)Math.ceil((double)(this.mc.thePlayer.air - 2) * 10.0D / 300.0D);
				var12 = (int)Math.ceil((double)this.mc.thePlayer.air * 10.0D / 300.0D) - var11;

				for(var13 = 0; var13 < var11 + var12; ++var13) {
					if(var13 < var11) {
						this.drawTexturedModalRect(width / 2 - 91 + (var13 << 3), height - 32 - 9, 16, 18, 9, 9);
					} else {
						this.drawTexturedModalRect(width / 2 - 91 + (var13 << 3), height - 32 - 9, 25, 18, 9, 9);
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

		for(var10 = 0; var10 < 9; ++var10) {
			int var25 = width / 2 - 90 + var10 * 20 + 2;
			int var21 = height - 16 - 3;
			ItemStack var22 = this.mc.thePlayer.inventory.mainInventory[var10];
			if(var22 != null) {
				float var9 = (float)var22.animationsToGo - var1;
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
			
			fontRend.drawStringWithShadow("Minecraft Forever Indev (" + this.mc.debug + ")", 2, 2, 16777215);
			
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
			fontRend.drawStringWithShadow("Minecraft Forever Indev", 2, 2, 16777215);
		}

		// draw chat
		for(int i = 0; i < this.chatMessageList.size() && i < 10; i++) {
			
			ChatLine chatline = this.chatMessageList.get(i);
			
			fontRend.drawStringWithShadow(chatline.message, 2, height - 8 - i * 9 - 20, 16777215);
		}
		
		// draw item name
		ItemStack currItem = this.mc.thePlayer.inventory.getCurrentItem();
		if (currItem != null)
			drawCenteredString(fontRend, currItem.getName(), width / 2, height - 48, 16777215);

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
