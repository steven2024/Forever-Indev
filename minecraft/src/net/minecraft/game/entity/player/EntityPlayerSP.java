package net.minecraft.game.entity.player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mojang.nbt.NBTTagCompound;
import com.mojang.nbt.NBTTagList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.client.effect.EntityPickupFX;
import net.minecraft.client.gui.container.GuiChest;
import net.minecraft.client.gui.container.GuiCrafting;
import net.minecraft.client.gui.container.GuiFurnace;
import net.minecraft.client.player.MovementInput;
import net.minecraft.game.IInventory;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.World;
import net.minecraft.game.level.block.tileentity.TileEntityFurnace;

// TODO since there's no multiplayer, we should merge this class with the EntityPlayer class
public class EntityPlayerSP extends EntityPlayer {
    
    private int score = 0;  // Field to store the player's score
    private long timeAlive = 0;  // Field to store the time alive in ticks
    public boolean isCreativeMode = false;
    public MovementInput movementInput;
    private Minecraft mc;

    public EntityPlayerSP(Minecraft mc, World world, Session session) {
        super(world);
        
        this.mc = mc;
        
        
        if (session != null) {
            try {
                String username = session.username;

                // Convert username to UUID using Mojang's API
                String uuid = getUUIDFromUsername(username);
                
                if (uuid != null) {
                    // Fetch the skin URL using the UUID
                    this.skinUrl = getSkinUrlFromUUID(uuid);
                    
                    // Fetch the cape URL (using a third-party service or Mojang's API)
                    this.capeUrl = getCapeUrlFromUUID(uuid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to convert username to UUID
    private String getUUIDFromUsername(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();

            // Parse the JSON response to extract the UUID
            JSONObject jsonResponse = new JSONObject(content.toString());
            return jsonResponse.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper method to get the skin URL from UUID
    private String getSkinUrlFromUUID(String uuid) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();

            // Extract the skin URL from the profile JSON
            JSONObject jsonResponse = new JSONObject(content.toString());
            JSONArray properties = jsonResponse.getJSONArray("properties");
            for (int i = 0; i < properties.length(); i++) {
                JSONObject property = properties.getJSONObject(i);
                if (property.getString("name").equals("textures")) {
                    String value = property.getString("value");
                    String decodedValue = new String(Base64.getDecoder().decode(value));
                    JSONObject texturesJson = new JSONObject(decodedValue);
                    return texturesJson.getJSONObject("textures").getJSONObject("SKIN").getString("url");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

 // Helper method to get the cape URL from UUID
    private String getCapeUrlFromUUID(String uuid) {
        try {
            // Using a third-party service such as Ashcon API to get the cape URL
            URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + uuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();

            // Extract the cape URL from the response
            JSONObject jsonResponse = new JSONObject(content.toString());
            if (jsonResponse.has("textures")) {
                JSONObject textures = jsonResponse.getJSONObject("textures");
                if (textures.has("cape")) {
                    // Assuming cape is a JSONObject containing a URL field
                    JSONObject cape = textures.getJSONObject("cape");
                    if (cape.has("url")) {
                        return cape.getString("url");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
        
    public boolean attackThisEntity(Entity attacker, int damage) {
        if (!isCreativeMode)
            return super.attackThisEntity(attacker, damage);
        
        return false;
    }

    public final void updatePlayerActionState() {
        this.moveStrafing = this.movementInput.moveStrafe;
        this.moveForward = this.movementInput.moveForward;
        this.isJumping = this.movementInput.jump;
    }

    public final void onLivingUpdate() {
        this.timeAlive++;  // Increment time alive
        this.movementInput.updatePlayerMoveState();
        super.onLivingUpdate();
    }

    // Method to add score to the player
    public void addScore(int scoreToAdd) {
        this.score += scoreToAdd;
    }

    // Method to get the current score
    public int getScore() {
        return this.score;
    }

    // Method to get the time alive
    public long getTimeAlive() {
        return this.timeAlive;
    }

    protected final void writeEntityToNBT(NBTTagCompound var1) {
        super.writeEntityToNBT(var1);
        var1.setInteger("Score", this.getScore());  // Save the score to NBT
        var1.setLong("TimeAlive", this.getTimeAlive());  // Save the time alive to NBT

        InventoryPlayer var10002 = this.inventory;
        NBTTagList var2 = new NBTTagList();
        InventoryPlayer var5 = var10002;

        int var3;
        NBTTagCompound var4;
        for (var3 = 0; var3 < var5.mainInventory.length; ++var3) {
            if (var5.mainInventory[var3] != null) {
                var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                var5.mainInventory[var3].writeToNBT(var4);
                var2.setTag(var4);
            }
        }

        for (var3 = 0; var3 < var5.armorInventory.length; ++var3) {
            if (var5.armorInventory[var3] != null) {
                var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) (var3 + 100));
                var5.armorInventory[var3].writeToNBT(var4);
                var2.setTag(var4);
            }
        }

        var1.setTag("Inventory", var2);
    }

    protected final void readEntityFromNBT(NBTTagCompound var1) {
        super.readEntityFromNBT(var1);
        this.score = var1.getInteger("Score");  // Load the score from NBT
        this.timeAlive = var1.getLong("TimeAlive");  // Load the time alive from NBT

        NBTTagList var6 = var1.getTagList("Inventory");
        NBTTagList var2 = var6;
        InventoryPlayer var7 = this.inventory;
        var7.mainInventory = new ItemStack[36];
        var7.armorInventory = new ItemStack[4];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
            int var5 = var4.getByte("Slot") & 255;
            if (var5 >= 0 && var5 < var7.mainInventory.length) {
                var7.mainInventory[var5] = new ItemStack(var4);
            }

            if (var5 >= 100 && var5 < var7.armorInventory.length + 100) {
                var7.armorInventory[var5 - 100] = new ItemStack(var4);
            }
        }
    }

    protected final String getEntityString() {
        return "LocalPlayer";
    }

    public final void displayGUIChest(IInventory var1) {
        this.mc.displayGuiScreen(new GuiChest(this.inventory, var1));
    }

    public final void displayWorkbenchGUI() {
        this.mc.displayGuiScreen(new GuiCrafting(this.inventory));
    }

    public final void displayGUIFurnace(TileEntityFurnace var1) {
        this.mc.displayGuiScreen(new GuiFurnace(this.inventory, var1));
    }

    public final void destroyCurrentEquippedItem() {
        this.inventory.setInventorySlotContents(this.inventory.currentItem, (ItemStack) null);
    }

    public final void onItemPickup(Entity var1) {
        this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, var1, this, -0.5F));
    }

	public void setHealth(int i) {
		this.health = i;
		
	}
}