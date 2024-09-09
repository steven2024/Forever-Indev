package net.minecraft.game.level.block;

import net.minecraft.game.entity.player.EntityPlayer;
import net.minecraft.game.level.World;
import net.minecraft.game.level.material.Material;

public class BlockSnow extends Block {

	// Constructor for the snow block. Uses the provided block ID and texture index.
	public BlockSnow(String name, int blockID, int blockIndexInTexture) {
	    super(name, blockID, blockIndexInTexture, Material.snow);
	    this.setBlockBoundsForSingleLayer(); // Set bounds to a fixed height (single layer)_
	    }

    // Adjust the block bounds for a single layer (or a custom height).
    private void setBlockBoundsForSingleLayer() {
        float height = 0.125F; // Snow layer height (fixed)
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, height, 1.0F);
    }

    // Prevent block placement above snow by checking if the space above is empty.
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        // Allow placement of the snow block if the block below is solid
        int blockBelow = world.getBlockId(x, y - 1, z);
        int blockAbove = world.getBlockId(x, y + 1, z);

        // Prevent placement if the block above is not air (blockID 0)
        return blockBelow != 0 && Block.blocksList[blockBelow].isOpaqueCube() && blockAbove == 0;
    }

    // Override to customize rendering behavior for the snow block.
    @Override
    public boolean renderAsNormalBlock() {
        return false; // Snow is not a full block, so don't render it as a normal opaque cube
    }

    @Override
    public boolean isOpaqueCube() {
        return false; // Snow is not opaque, so it doesn't block light
    }

    // Prevent right-click block placement interaction like furnaces or crafting tables
    @Override
    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        return true; // Prevent block placement interaction on right-click
    }

    // Fix to ensure the top face is rendered, even when a block is placed above it.
    @Override
    public boolean shouldSideBeRendered(World world, int x, int y, int z, int side) {
        if (side == 1) { // 1 represents the top face
            return true; // Always render the top face
        }
        int blockId = world.getBlockId(x, y, z);
        return blockId == 0 || !Block.blocksList[blockId].isOpaqueCube();
    }

    // Notify neighbors when a block is placed or removed, updating the snow block's rendering
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockId) {
        // Notify neighbors that their rendering might need to be updated
        world.markBlockForUpdate(x, y, z);
    }
}
