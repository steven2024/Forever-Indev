package net.minecraft.client.effect;

import net.minecraft.client.render.Tessellator;
import net.minecraft.game.level.World;
import net.minecraft.game.level.block.Block;
import java.util.Random;

import org.lwjgl.opengl.GL11;

public final class EntityFallingLeafFX extends EntityFX {
    private boolean hasLanded = false;
    private final int initialMaxAge = 100000;
    private int textureRotation = 0;
    private int rotationInterval = 20;
    private int nextRotationTick = rotationInterval;
    private float gravityStrength;  // Randomized gravity strength
    private float flutterFrequency; // Frequency of the fluttering
    private float flutterAmplitude; // Amplitude of the fluttering
    private float windStrength; // Strength of the wind effect
    private float windDirection; // Direction of the wind effect

    private static final Random random = new Random();  // To generate random values

    public EntityFallingLeafFX(World world, float posX, float posY, float posZ, float motionY, Block block) {
        super(world, posX, posY, posZ, 0.0F, motionY, 0.0F);
        this.particleTextureIndex = block.blockIndexInTexture;
        this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
        this.particleScale /= 2.0F;
        this.particleMaxAge = initialMaxAge;
        this.motionX = 0.0F;
        this.motionZ = 0.0F;

     // Set gravityStrength to match the height-adjusted real-world leaf fall velocity
        this.gravityStrength = 0.0192f;  // Adjusted gravity strength to match real-world tree fall

        // Random frequency between 0.05 and 0.25
        this.flutterFrequency = 0.05f + random.nextFloat() * 0.20f;

        // Random amplitude between 0.05 and 0.25
        this.flutterAmplitude = 0.05f + random.nextFloat() * 0.20f;

        // Random wind strength between 0.03 and 0.15
        this.windStrength = 0.03f + random.nextFloat() * 0.12f;

        // Random wind direction in radians between 0 and 2 * PI
        this.windDirection = random.nextFloat() * (float) Math.PI * 2;

    }

    @Override
    public void onEntityUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setEntityDead();
        }

        if (!this.hasLanded) {
        	final float airDensity = 1.225f;
        	final float dragFactor = 1.2f;
        	final float area = 0.01f;
        	final float mass = 0.002f;
        	final float gravityAcceleration = 0.0192f;  // Adjusted gravity factor for falling leaves

        	this.gravityStrength = 0.0192f;  // Scaled gravity for leaf

        	// Calculate max falling speed
        	float maxFallSpeed = (float) Math.sqrt((2 * mass * gravityAcceleration) / (dragFactor * airDensity * area));

        	// Apply gravity to the leaf
        	this.motionY -= this.gravityStrength;

        	// Apply drag to slow down fall as speed increases
        	float velocitySquared = this.motionY * this.motionY;
        	float drag = 0.5f * dragFactor * airDensity * area * velocitySquared;

        	this.motionY -= drag / mass;

        	// Cap the falling speed to maxFallSpeed
        	if (this.motionY < -maxFallSpeed) {
        	    this.motionY = -maxFallSpeed;
        	}

        	// Update position based on current movement
        	this.moveEntity(this.motionX, this.motionY, this.motionZ);

        	// Reduce horizontal movement for natural drift
        	this.motionX *= 0.92F;
        	this.motionZ *= 0.92F;

            // Apply fluttering effect
            float flutterOffset = (float) Math.sin(this.particleAge * this.flutterFrequency) * this.flutterAmplitude;
            this.posX += flutterOffset * Math.cos(windDirection);
            this.posZ += flutterOffset * Math.sin(windDirection);

            if (this.onGround || this.posY <= 0.1F) {
                this.motionX = 0F;
                this.motionY = 0F;
                this.motionZ = 0F;

                if (this.particleMaxAge == initialMaxAge) {
                    this.particleMaxAge = this.particleAge + 40;
                    this.hasLanded = true;
                }
            } else if (!this.hasLanded && this.particleAge >= this.nextRotationTick) {
                this.textureRotation = (this.textureRotation + 1) % 4;
                this.nextRotationTick = this.particleAge + rotationInterval;
            }
        }
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTickTime, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        GL11.glEnable(GL11.GL_BLEND); // Enable blending
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // Set blending function
        float uMin = ((float)(this.particleTextureIndex % 16) + this.particleTextureJitterX / 4.0F) / 16.0F;
        float uMax = uMin + 0.999F / 64.0F;
        float vMin = ((float)(this.particleTextureIndex / 16) + this.particleTextureJitterY / 4.0F) / 16.0F;
        float vMax = vMin + 0.999F / 64.0F;

        switch (textureRotation) {
            case 1:
                float temp1 = uMin;
                uMin = uMax;
                uMax = temp1;
                break;
            case 2:
                float tempU = uMin;
                uMin = uMax;
                uMax = tempU;
                float tempV = vMin;
                vMin = vMax;
                vMax = tempV;
                break;
            case 3:
                float temp2 = vMin;
                vMin = vMax;
                vMax = temp2;
                break;
        }

        float particleScaleHalf = 0.1F * this.particleScale;
        float renderPosX = this.prevPosX + (this.posX - this.prevPosX) * partialTickTime;
        float renderPosY = this.prevPosY + (this.posY - this.prevPosY) * partialTickTime;
        float renderPosZ = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTickTime;

        // Set color based on brightness without alpha
        float brightness = this.getEntityBrightness(partialTickTime);
        tessellator.setColorOpaque_F(brightness * this.particleRed, brightness * this.particleGreen, brightness * this.particleBlue);

        tessellator.addVertexWithUV(renderPosX - rotationX * particleScaleHalf - rotationXY * particleScaleHalf, renderPosY - rotationZ * particleScaleHalf, renderPosZ - rotationYZ * particleScaleHalf - rotationXZ * particleScaleHalf, uMin, vMax);
        tessellator.addVertexWithUV(renderPosX - rotationX * particleScaleHalf + rotationXY * particleScaleHalf, renderPosY + rotationZ * particleScaleHalf, renderPosZ - rotationYZ * particleScaleHalf + rotationXZ * particleScaleHalf, uMin, vMin);
        tessellator.addVertexWithUV(renderPosX + rotationX * particleScaleHalf + rotationXY * particleScaleHalf, renderPosY + rotationZ * particleScaleHalf, renderPosZ + rotationYZ * particleScaleHalf + rotationXZ * particleScaleHalf, uMax, vMin);
        tessellator.addVertexWithUV(renderPosX + rotationX * particleScaleHalf - rotationXY * particleScaleHalf, renderPosY - rotationZ * particleScaleHalf, renderPosZ + rotationYZ * particleScaleHalf - rotationXZ * particleScaleHalf, uMax, vMax);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }
}
