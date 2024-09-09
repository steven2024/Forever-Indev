package net.minecraft.game.entity.misc;

import com.mojang.nbt.NBTTagCompound;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.level.World;
import net.minecraft.game.physics.AxisAlignedBB;
import net.minecraft.game.level.block.Block;

public class EntityFallingSnow extends Entity {
    public int blockID;

    // Constructor to initialize the EntityFallingSnow with a block ID and its position
    public EntityFallingSnow(World world, float x, float y, float z, int blockID) {
        super(world);  // Call the parent class constructor
        this.blockID = blockID;
        this.setPosition(x, y, z);  // Set the entity's position using float values
        this.motionX = 0.0F;
        this.motionY = -0.04F;  // Smooth falling speed for the snow layer
        this.motionZ = 0.0F;
        this.preventEntitySpawning = true;  // Prevent spawning of other entities on top of this entity
        this.setSize(0.98F, 0.98F); // Set size for the entity
    }

    // Called every tick to update the entity's state
    @Override
    public void onEntityUpdate() {
        this.moveEntity(this.motionX, this.motionY, this.motionZ);  // Move the entity by its current motion

        if (this.onGround) {
            // Place the block at the entity's current position
            this.worldObj.setBlock((int) this.posX, (int) this.posY, (int) this.posZ, Block.snow.blockID);
            this.setEntityDead();  // Mark the entity as dead after placing the block
        }
    }

    // Mark the entity as "dead" to remove it from the world
    @Override
    public void setEntityDead() {
        this.isDead = true;
    }

    // Set the position of the entity
    @Override
    public void setPosition(float x, float y, float z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float halfWidth = this.width / 2.0F;
        this.boundingBox = new AxisAlignedBB(x - halfWidth, y - this.yOffset + this.height, z - halfWidth, x + halfWidth, y + this.height, z + halfWidth);
    }

    // Save the entity's data to NBT for saving the game
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setInteger("BlockID", this.blockID);  // Save the block ID
    }

    // Load the entity's data from NBT when loading the game
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        this.blockID = nbt.getInteger("BlockID");  // Load the block ID
    }

    // Return the entity's name or identifier (used for saving/loading)
    @Override
    public String getEntityString() {
        return "FallingSnow";
    }

    // Initialize the entity (empty in this case)
    protected void entityInit() {
        // No specific data initialization required for this entity
    }
}
