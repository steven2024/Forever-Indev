package net.minecraft.game.level.block;

import java.util.Random;

import net.minecraft.client.effect.EntityDiggingFX;
import net.minecraft.game.item.Item;
import net.minecraft.game.level.World;
import net.minecraft.game.level.material.Material;

public final class BlockLeaves extends BlockLeavesBase {

    protected BlockLeaves(int blockID) {
        super("Leaves", blockID, 52, Material.leaves);
        this.setTickOnLoad(true);
    }

    @Override
    public final void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.getBlockMaterial(x, y - 1, z).isSolid()) {
            boolean hasWoodNearby = false;
            for (int ix = x - 2; ix <= x + 2; ++ix) {
                for (int iy = y - 1; iy <= y; ++iy) {
                    for (int iz = z - 2; iz <= z + 2; ++iz) {
                        if (world.getBlockId(ix, iy, iz) == Block.wood.blockID) {
                            hasWoodNearby = true;
                            break;
                        }
                    }
                    if (hasWoodNearby) break;
                }
                if (hasWoodNearby) break;
            }

            if (!hasWoodNearby) {
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
                world.setBlockWithNotify(x, y, z, 0);
            }
        }
    }

    @Override
    public final int quantityDropped(Random random) {
        return random.nextInt(8) == 0 ? 1 : 0;
    }

    @Override
    public final int idDropped(int metadata, Random random) {
        return random.nextInt(2) == 0 ? Block.sapling.blockID : Item.apple.shiftedIndex;
    }

    @Override
    public final void randomDisplayTick(World world, int x, int y, int z, Random random) {
        // Check if there's no block directly underneath the leaves
        if (world.getBlockId(x, y - 1, z) == 0) {
            // Only spawn particles for the bottom layer of leaves
            if (world.getBlockId(x, y + 1, z) == this.blockID) {
                // Randomize the frequency of particle spawning
                if (random.nextInt(50) == 0) { // Spawn less frequently
                    // Generate a random number of particles
                    int numParticles = random.nextInt(3) + 1; // 1 to 3 particles

                    for (int i = 0; i < numParticles; i++) {
                        // Randomize the particle position within the block
                        float posX = (float)x + 0.5F + (random.nextFloat() - 0.5F) * 0.5F;
                        float posY = (float)y - 0.1F + (random.nextFloat() - 0.5F) * 0.1F; // Small variation in Y
                        float posZ = (float)z + 0.5F + (random.nextFloat() - 0.5F) * 0.5F;

                        // Randomize the direction of the particles
                        float offsetX = (random.nextFloat() - 0.5F) * 0.2F;
                        float offsetY = (random.nextFloat() - 0.5F) * 0.2F;
                        float offsetZ = (random.nextFloat() - 0.5F) * 0.2F;

                        // Spawn the particle
                        world.spawnParticle("leaves", posX, posY, posZ, offsetX, offsetY, offsetZ);
                    }
                }
            }
        }
    }
}