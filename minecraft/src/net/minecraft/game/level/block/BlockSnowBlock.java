package net.minecraft.game.level.block;

import net.minecraft.game.item.Item;
import net.minecraft.game.level.World;
import net.minecraft.game.level.material.Material;
import java.util.Random;

public class BlockSnowBlock extends BlockGravity {

    public BlockSnowBlock(String name, int blockID, int blockIndexInTexture) {
        super(name, blockID, blockIndexInTexture);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        if (world.getBlockLightValue(x, y, z) > 11) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
        } else {
            super.updateTick(world, x, y, z, rand);
        }
    }

    @Override
    public int idDropped(int metadata, Random rand) {
        return Item.snowball.shiftedIndex;
    }

    @Override
    public int quantityDropped(Random rand) {
        return 4; // Snow blocks drop 4 snowballs
    }
}
