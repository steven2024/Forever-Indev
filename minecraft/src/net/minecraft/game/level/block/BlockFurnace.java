package net.minecraft.game.level.block;

import java.util.Random;
import net.minecraft.game.entity.player.EntityPlayer;
import net.minecraft.game.level.World;
import net.minecraft.game.level.block.tileentity.TileEntity;
import net.minecraft.game.level.block.tileentity.TileEntityFurnace;
import net.minecraft.game.level.material.Material;

public final class BlockFurnace extends BlockContainer {
	
	private final boolean isActive;

	protected BlockFurnace(String name, int blockID, boolean isActive) {
		super(name, blockID, Material.rock);
		
		this.blockIndexInTexture = 45;
		
		this.isActive = isActive;
	}

	public final void onBlockAdded(World var1, int var2, int var3, int var4) {
		super.onBlockAdded(var1, var2, var3, var4);
		setDefaultDirection(var1, var2, var3, var4);
	}

	private static void setDefaultDirection(World var0, int var1, int var2, int var3) {
		int var4 = var0.getBlockId(var1, var2, var3 - 1);
		int var5 = var0.getBlockId(var1, var2, var3 + 1);
		int var6 = var0.getBlockId(var1 - 1, var2, var3);
		int var7 = var0.getBlockId(var1 + 1, var2, var3);
		byte var8 = 3;
		if(Block.opaqueCubeLookup[var4] && !Block.opaqueCubeLookup[var5]) {
			var8 = 3;
		}

		if(Block.opaqueCubeLookup[var5] && !Block.opaqueCubeLookup[var4]) {
			var8 = 2;
		}

		if(Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var7]) {
			var8 = 5;
		}

		if(Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var6]) {
			var8 = 4;
		}

		var0.setBlockMetadata(var1, var2, var3, var8);
	}

	public final int getBlockTexture(World var1, int var2, int var3, int var4, int var5) {
		if(var5 == 1) {
			return Block.stone.blockIndexInTexture;
		} else if(var5 == 0) {
			return Block.stone.blockIndexInTexture;
		} else {
			byte var6 = var1.getBlockMetadata(var2, var3, var4);
			if(var6 == 0) {
				setDefaultDirection(var1, var2, var3, var4);
				var6 = var1.getBlockMetadata(var2, var3, var4);
			}

			return var5 != var6 ? this.blockIndexInTexture : (this.isActive ? this.blockIndexInTexture + 16 : this.blockIndexInTexture - 1);
		}
	}

	public final void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
		if(this.isActive) {
			byte var6 = var1.getBlockMetadata(var2, var3, var4);
			float var7 = (float)var2 + 0.5F;
			float var8 = (float)var3 + var5.nextFloat() * 6.0F / 16.0F;
			float var9 = (float)var4 + 0.5F;
			float var10 = var5.nextFloat() * 0.6F - 0.3F;
			if(var6 == 4) {
				var1.spawnParticle("smoke", var7 - 0.52F, var8, var9 + var10, 0.0F, 0.0F, 0.0F);
				var1.spawnParticle("flame", var7 - 0.52F, var8, var9 + var10, 0.0F, 0.0F, 0.0F);
			} else if(var6 == 5) {
				var1.spawnParticle("smoke", var7 + 0.52F, var8, var9 + var10, 0.0F, 0.0F, 0.0F);
				var1.spawnParticle("flame", var7 + 0.52F, var8, var9 + var10, 0.0F, 0.0F, 0.0F);
			} else if(var6 == 2) {
				var1.spawnParticle("smoke", var7 + var10, var8, var9 - 0.52F, 0.0F, 0.0F, 0.0F);
				var1.spawnParticle("flame", var7 + var10, var8, var9 - 0.52F, 0.0F, 0.0F, 0.0F);
			} else {
				if(var6 == 3) {
					var1.spawnParticle("smoke", var7 + var10, var8, var9 + 0.52F, 0.0F, 0.0F, 0.0F);
					var1.spawnParticle("flame", var7 + var10, var8, var9 + 0.52F, 0.0F, 0.0F, 0.0F);
				}

			}
		}
	}

	public final int getBlockTextureFromSide(int side) {
	    // Check if the side is the top (side == 1) or bottom (side == 0)
	    if (side == 1) {
	        // Top should use the stone texture
	        return Block.stone.blockIndexInTexture;
	    } else if (side == 0) {
	        // Bottom should also use the stone texture
	        return Block.stone.blockIndexInTexture;
	    } else {
	        // The front face of the furnace should have a special texture
	        // Side 3 is usually the front, check if it's active or not to use the correct texture
	        return (side == 3 ? (this.isActive ? this.blockIndexInTexture + 16 : this.blockIndexInTexture - 1) : this.blockIndexInTexture);
	    }
	}
	
	public final boolean blockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5) {
		TileEntityFurnace var6 = (TileEntityFurnace)var1.getBlockTileEntity(var2, var3, var4);
		var5.displayGUIFurnace(var6);
		return true;
	}

	protected final TileEntity getBlockEntity() {
		return new TileEntityFurnace();
	}
}
