package net.minecraft.client.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.animal.EntitySheep;
import net.minecraft.game.entity.animal.EntityPig;
import net.minecraft.game.entity.monster.EntityZombie;
import net.minecraft.game.entity.monster.EntitySpider;
import net.minecraft.game.entity.monster.EntityCreeper;
import net.minecraft.game.entity.monster.EntitySkeleton;

public class CommandSpawnEntity extends Command {

    @Override
    public String getName() {
        return "/spawn";
    }

    @Override
    public void runCommand(Minecraft mc, String[] args) {
        if (args.length < 2) {
            mc.ingameGUI.addChatMessage("Invalid argument. Use /spawn <entity> [x] [y] [z].");
            return;
        }

        try {
            String entityName = args[1].toLowerCase();
            Entity entity = null;

            // Determine which entity to spawn
            switch (entityName) {
                case "sheep":
                    entity = new EntitySheep(mc.theWorld);
                    break;
                case "pig":
                    entity = new EntityPig(mc.theWorld);
                    break;
                case "zombie":
                    entity = new EntityZombie(mc.theWorld);
                    break;
                case "spider":
                    entity = new EntitySpider(mc.theWorld);
                    break;
                case "skeleton":
                    entity = new EntitySkeleton(mc.theWorld);
                    break;
                case "creeper":
                    entity = new EntityCreeper(mc.theWorld);
                    break;
                default:
                    mc.ingameGUI.addChatMessage("Unknown entity. Available: sheep, pig, zombie, spider, skeleton.");
                    return;
            }

            // Get the coordinates from the arguments or use the player's current position
            float x = (float) mc.thePlayer.posX;
            float y = (float) mc.thePlayer.posY;
            float z = (float) mc.thePlayer.posZ;

            if (args.length >= 5) {
                x = Float.parseFloat(args[2]);
                y = Float.parseFloat(args[3]);
                z = Float.parseFloat(args[4]);
            }

            // Set the entity's position and spawn it
            entity.setPosition(x, y, z);
            mc.theWorld.spawnEntityInWorld(entity);
            mc.ingameGUI.addChatMessage("Spawned " + entityName + " at (" + x + ", " + y + ", " + z + ").");

        } catch (Exception e) {
            mc.ingameGUI.addChatMessage("Error: Invalid coordinates or unknown entity.");
        }
    }

    @Override
    public void showHelpMessage(Minecraft mc) {
        mc.ingameGUI.addChatMessage("/spawn <entity> [x] [y] [z]");
        mc.ingameGUI.addChatMessage("    Spawns the specified entity at the given coordinates.");
        mc.ingameGUI.addChatMessage("    If coordinates are not provided, the entity spawns at the player's location.");
        mc.ingameGUI.addChatMessage("    Available entities: sheep, pig, zombie, spider, skeleton.");
    }
}
