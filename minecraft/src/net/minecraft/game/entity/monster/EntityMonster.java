package net.minecraft.game.entity.monster;

import com.mojang.nbt.NBTTagCompound;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityCreature;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.level.World;

public class EntityMonster extends EntityCreature {
    
    protected int attackStrength = 2;
    protected ItemStack heldItem; // Field to store the item the monster is holding

    public EntityMonster(World world) {
        super(world);
        this.health = 20;
        this.heldItem = null; // Initialize with no item
    }

    @Override
    public void onLivingUpdate() {
        float brightness = this.getEntityBrightness(1.0F);
        if (brightness > 0.5F) {
            this.entityAge += 2;
        }

        super.onLivingUpdate();
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (this.worldObj.difficultySetting == 0) {
            this.setEntityDead();
        }
    }

    @Override
    protected Entity findPlayerToAttack() {
        Entity player = this.worldObj.playerEntity;
        float distance = player.getDistanceSqToEntity(this);
        return distance < 256.0F ? player : null;
    }

    @Override
    public boolean attackThisEntity(Entity attacker, int damage) {
        if (super.attackThisEntity(attacker, damage)) {
            if (attacker != this) {
                this.playerToAttack = attacker;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void attackEntity(Entity toAttack, float distance) {
        if (distance < 2.5D
                && toAttack.boundingBox.maxY > this.boundingBox.minY
                && toAttack.boundingBox.minY < this.boundingBox.maxY) {
            this.attackTime = 20;
            toAttack.attackThisEntity(this, this.attackStrength);
        }
    }

    @Override
    protected float getBlockPathWeight(int x, int y, int z) {
        return 0.5F - this.worldObj.getLightBrightness(x, y, z);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        if (heldItem != null) {
            tagCompound.setCompoundTag("HeldItem", heldItem.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        if (tagCompound.hasKey("HeldItem")) {
            this.heldItem = new ItemStack(tagCompound.getCompoundTag("HeldItem"));
        }
    }

    @Override
    protected String getEntityString() {
        return "Monster";
    }

    @Override
    public boolean getCanSpawnHere(float x, float y, float z) {
        byte lightLevel = this.worldObj.getBlockLightValue((int) x, (int) y, (int) z);
        return lightLevel <= this.rand.nextInt(8) && super.getCanSpawnHere(x, y, z);
    }

    protected int getDroppedRupeeCount() {
        return (int) (Math.random() * 3) + 1;
    }
}