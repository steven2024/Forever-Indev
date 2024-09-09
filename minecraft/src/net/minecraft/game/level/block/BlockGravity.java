package net.minecraft.game.level.block;

import net.minecraft.game.level.World;
import net.minecraft.game.level.material.Material;
import java.util.Random;

public class BlockGravity extends Block {

    private final boolean isWetSand;
    private final int fallDelay = 200; // Initial delay before wet sand starts falling (in ticks)

    public BlockGravity(String name, int blockID, int blockIndexInTexture) {
        super(name, blockID, blockIndexInTexture, Material.sand);
        this.isWetSand = name.equalsIgnoreCase("Wet Sand");
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (isWetSand) {
            // Set the initial metadata (timer) to the delay value
            world.setBlockMetadata(x, y, z, fallDelay);
            // Schedule the first update
            world.scheduleBlockUpdate(x, y, z, this.blockID);
        } else {
            // Regular sand starts falling immediately
            world.scheduleBlockUpdate(x, y, z, this.blockID);
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (isWetSand) {
            int timer = world.getBlockMetadata(x, y, z);
            if (timer > 0) {
                // Decrease the timer
                world.setBlockMetadata(x, y, z, timer - 1);
                // Schedule another update until the timer reaches 0
                world.scheduleBlockUpdate(x, y, z, this.blockID);
                return;
            }
        }

        if (canFall(world, x, y, z)) {
            // Move the block down by one block
            world.setBlockWithNotify(x, y, z, 0); // Remove the block from the current position
            world.setBlockWithNotify(x, y - 1, z, this.blockID); // Place it at the new position

            // Set metadata to 0 at the new position
            world.setBlockMetadata(x, y - 1, z, 0);

            // Schedule the next step of the fall
            world.scheduleBlockUpdate(x, y - 1, z, this.blockID);
        }
    }

    protected boolean canFall(World world, int x, int y, int z) {
        if (y <= 0) return false; // Prevent falling below the world
        int blockBelowID = world.getBlockId(x, y - 1, z);
        return blockBelowID == 0 || Block.blocksList[blockBelowID].material == Material.water || Block.blocksList[blockBelowID].material == Material.lava;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockID) {
        world.scheduleBlockUpdate(x, y, z, this.blockID);
    }
}
