package net.minecraft.game.level.block;

import net.minecraft.game.level.World;
import net.minecraft.game.level.material.Material;
import java.util.Random;

public class BlockIce extends BlockGravity {

    public BlockIce(String name, int blockID, int blockIndexInTexture) {
        super(name, blockID, blockIndexInTexture);
        this.setSlipperiness(0.98F); // Set the slipperiness for ice
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        // No melting logic here
        super.updateTick(world, x, y, z, rand);
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        // Turn the ice block into a water block when it is removed
        world.setBlockWithNotify(x, y, z, Block.waterSource.blockID);
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0; // Ice doesn’t drop itself
    }
    
    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        this.updateTick(world, x, y, z, world.rand); // Ensure the block updates when added
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID) {
        super.onNeighborBlockChange(world, x, y, z, neighborID);
        this.updateTick(world, x, y, z, world.rand); // Update block state on neighbor change
    }
}
