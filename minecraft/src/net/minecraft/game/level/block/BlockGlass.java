package net.minecraft.game.level.block;

import net.minecraft.game.level.World;
import net.minecraft.game.level.material.Material;

public final class BlockGlass extends Block {

    public BlockGlass(String name, int blockID, int blockIndexInTexture, Material material) {
        super(name, blockID, blockIndexInTexture, material);
        this.setLightOpacity(0); // Ensure the block is transparent
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

    @Override
    public boolean shouldSideBeRendered(World world, int x, int y, int z, int side) {
        int adjacentBlockId = world.getBlockId(x + getAdjacentXOffset(side), y + getAdjacentYOffset(side), z + getAdjacentZOffset(side));
        return adjacentBlockId != this.blockID || super.shouldSideBeRendered(world, x, y, z, side);
    }

    // Adjust offsets for sides
    private int getAdjacentXOffset(int side) {
        return (side == 0) ? -1 : (side == 1) ? 1 : 0;
    }

    private int getAdjacentYOffset(int side) {
        return (side == 2) ? -1 : (side == 3) ? 1 : 0;
    }

    private int getAdjacentZOffset(int side) {
        return (side == 4) ? -1 : (side == 5) ? 1 : 0;
    }
}
