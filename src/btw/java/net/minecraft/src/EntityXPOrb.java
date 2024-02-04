package net.minecraft.src;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EntityXPOrb extends Entity
{
    // FCMOD: Code added
    public boolean notPlayerOwned = false;
    // END FCMOD
    
    /**
     * A constantly increasing value that RenderXPOrb uses to control the colour shifting (Green / yellow)
     */
    public int xpColor;

    /** The age of the XP orb in ticks. */
    public int xpOrbAge = 0;
    public int field_70532_c;

    /** The health of this XP orb. */
    private int xpOrbHealth = 5;

    /** This is how much XP this orb has. */
    public int xpValue;

    /** The closest EntityPlayer to this orb. */
    private EntityPlayer closestPlayer;

    /** Threshold color for tracking players */
    private int xpTargetColor;

    // FCMOD: Method added
    public EntityXPOrb(World par1World, double par2, double par4, double par6, int par8, boolean bNotPlayerOwned )
    {
    	this( par1World, par2, par4, par6, par8 );
        notPlayerOwned = bNotPlayerOwned;
    }
    // END FCMOD

    public EntityXPOrb(World par1World, double par2, double par4, double par6, int par8)
    {
        super(par1World);
        setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(par2, par4, par6);
        this.rotationYaw = (float)(Math.random() * 360.0D);
        this.motionX = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
        this.motionY = (double)((float)(Math.random() * 0.2D) * 2.0F);
        this.motionZ = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
        this.xpValue = par8;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    public EntityXPOrb(World par1World)
    {
        super(par1World);
        this.setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
    }

    protected void entityInit() {}

    @Environment(EnvType.CLIENT)
    public int getBrightnessForRender(float par1)
    {
        float var2 = 0.5F;

        if (var2 < 0.0F)
        {
            var2 = 0.0F;
        }

        if (var2 > 1.0F)
        {
            var2 = 1.0F;
        }

        int var3 = super.getBrightnessForRender(par1);
        int var4 = var3 & 255;
        int var5 = var3 >> 16 & 255;
        var4 += (int)(var2 * 15.0F * 16.0F);

        if (var4 > 240)
        {
            var4 = 240;
        }

        return var4 | var5 << 16;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (this.field_70532_c > 0)
        {
            --this.field_70532_c;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.029999999329447746D;

        if (this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == Material.lava)
        {
            this.motionY = 0.20000000298023224D;
            this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
        }

        // FCMOD: Changed to reduce discrepancies between client and server by 
        // pusing only on server
        if ( !worldObj.isRemote )
        {
            pushOutOfBlocks( posX, ( boundingBox.minY + boundingBox.maxY) / 2D, posZ );
        }
        double var1 = 8.0D;

        if (this.xpTargetColor < this.xpColor - 20 + this.entityId % 100)
        {
            if (this.closestPlayer == null || this.closestPlayer.getDistanceSqToEntity(this) > var1 * var1)
            {
				// FCMOD: Added
		        if ( !notPlayerOwned)
	        	// END FCMOD
                this.closestPlayer = this.worldObj.getClosestPlayerToEntity(this, var1);
            }

            this.xpTargetColor = this.xpColor;
        }

        if (this.closestPlayer != null)
        {
            double var3 = (this.closestPlayer.posX - this.posX) / var1;
            double var5 = (this.closestPlayer.posY + (double)this.closestPlayer.getEyeHeight() - this.posY) / var1;
            double var7 = (this.closestPlayer.posZ - this.posZ) / var1;
            // FCMOD: Code Changed for optimization
            /*
            double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
            double var11 = 1.0D - var9;

            if (var11 > 0.0D)
            {
            */
            double dDistanceSq = var3 * var3 + var5 * var5 + var7 * var7;

            if (dDistanceSq < 1.0D)
            {
                double var9 = Math.sqrt( dDistanceSq );
                double var11 = 1.0D - var9;
            // 	END FCMOD
                var11 *= var11;
                this.motionX += var3 / var9 * var11 * 0.1D;
                this.motionY += var5 / var9 * var11 * 0.1D;
                this.motionZ += var7 / var9 * var11 * 0.1D;
            }
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        float var13 = 0.98F;

        if (this.onGround)
        {
            var13 = 0.58800006F;
            int var4 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));

            if (var4 > 0)
            {
                var13 = Block.blocksList[var4].slipperiness * 0.98F;
            }
        }

        this.motionX *= (double)var13;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= (double)var13;

        if (this.onGround)
        {
            this.motionY *= -0.8999999761581421D;
        }

        ++this.xpColor;
        ++this.xpOrbAge;

        if (this.xpOrbAge >= 6000)
        {
            this.setDead();
        }
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean handleWaterMovement()
    {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    public void dealFireDamage(int par1)
    {
        this.attackEntityFrom(DamageSource.inFire, par1);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            this.setBeenAttacked();
            this.xpOrbHealth -= par2;

            if (this.xpOrbHealth <= 0)
            {
                this.setDead();
            }

            return false;
        }
    }

    @Override
    protected boolean pushOutOfBlocks(double par1, double par3, double par5)
    {
    	// Inherited function added to revert Mojang's changes 

        int var7 = MathHelper.floor_double(par1);
        int var8 = MathHelper.floor_double(par3);
        int var9 = MathHelper.floor_double(par5);
        double var10 = par1 - (double)var7;
        double var12 = par3 - (double)var8;
        double var14 = par5 - (double)var9;

        if (this.worldObj.isBlockNormalCube(var7, var8, var9))
        {
            boolean var16 = !this.worldObj.isBlockNormalCube(var7 - 1, var8, var9);
            boolean var17 = !this.worldObj.isBlockNormalCube(var7 + 1, var8, var9);
            boolean var18 = !this.worldObj.isBlockNormalCube(var7, var8 - 1, var9);
            boolean var19 = !this.worldObj.isBlockNormalCube(var7, var8 + 1, var9);
            boolean var20 = !this.worldObj.isBlockNormalCube(var7, var8, var9 - 1);
            boolean var21 = !this.worldObj.isBlockNormalCube(var7, var8, var9 + 1);
            byte var22 = -1;
            double var23 = 9999.0D;

            if (var16 && var10 < var23)
            {
                var23 = var10;
                var22 = 0;
            }

            if (var17 && 1.0D - var10 < var23)
            {
                var23 = 1.0D - var10;
                var22 = 1;
            }

            if (var18 && var12 < var23)
            {
                var23 = var12;
                var22 = 2;
            }

            if (var19 && 1.0D - var12 < var23)
            {
                var23 = 1.0D - var12;
                var22 = 3;
            }

            if (var20 && var14 < var23)
            {
                var23 = var14;
                var22 = 4;
            }

            if (var21 && 1.0D - var14 < var23)
            {
                var23 = 1.0D - var14;
                var22 = 5;
            }

            float var25 = this.rand.nextFloat() * 0.2F + 0.1F;

            if (var22 == 0)
            {
                this.motionX = (double)(-var25);
            }

            if (var22 == 1)
            {
                this.motionX = (double)var25;
            }

            if (var22 == 2)
            {
                this.motionY = (double)(-var25);
            }

            if (var22 == 3)
            {
                this.motionY = (double)var25;
            }

            if (var22 == 4)
            {
                this.motionZ = (double)(-var25);
            }

            if (var22 == 5)
            {
                this.motionZ = (double)var25;
            }

            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("Health", (short)((byte)this.xpOrbHealth));
        par1NBTTagCompound.setShort("Age", (short)this.xpOrbAge);
        par1NBTTagCompound.setShort("Value", (short)this.xpValue);
        // FCMOD: Code added        
        par1NBTTagCompound.setBoolean("m_bNotPlayerOwned", notPlayerOwned);
        // END FCMOD
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.xpOrbHealth = par1NBTTagCompound.getShort("Health") & 255;
        this.xpOrbAge = par1NBTTagCompound.getShort("Age");
        this.xpValue = par1NBTTagCompound.getShort("Value");
        // FCMOD: Code added        
        if ( par1NBTTagCompound.hasKey( "m_bNotPlayerOwned" ) )
        {
            notPlayerOwned = par1NBTTagCompound.getBoolean("m_bNotPlayerOwned");
        }
        // END FCMOD
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer)
    {
        // FCMOD: Code added
    	if (notPlayerOwned)
    	{
    		return;
    	}
    	// END FCMOD
        if (!this.worldObj.isRemote)
        {
            if (this.field_70532_c == 0 && par1EntityPlayer.xpCooldown == 0)
            {
                par1EntityPlayer.xpCooldown = 2;
                this.playSound("random.orb", 0.1F, 0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                par1EntityPlayer.onItemPickup(this, 1);
                par1EntityPlayer.addExperience(this.xpValue);
                this.setDead();
            }
        }
    }

    /**
     * Returns the XP value of this XP orb.
     */
    public int getXpValue()
    {
        return this.xpValue;
    }

    /**
     * Returns a number from 1 to 10 based on how much XP this orb is worth. This is used by RenderXPOrb to determine
     * what texture to use.
     */
    @Environment(EnvType.CLIENT)
    public int getTextureByXP()
    {
        return this.xpValue >= 2477 ? 10 : (this.xpValue >= 1237 ? 9 : (this.xpValue >= 617 ? 8 : (this.xpValue >= 307 ? 7 : (this.xpValue >= 149 ? 6 : (this.xpValue >= 73 ? 5 : (this.xpValue >= 37 ? 4 : (this.xpValue >= 17 ? 3 : (this.xpValue >= 7 ? 2 : (this.xpValue >= 3 ? 1 : 0)))))))));
    }

    /**
     * Get xp split rate (Is called until the xp drop code in EntityLiving.onEntityUpdate is complete)
     */
    public static int getXPSplit(int par0)
    {
        return par0 >= 2477 ? 2477 : (par0 >= 1237 ? 1237 : (par0 >= 617 ? 617 : (par0 >= 307 ? 307 : (par0 >= 149 ? 149 : (par0 >= 73 ? 73 : (par0 >= 37 ? 37 : (par0 >= 17 ? 17 : (par0 >= 7 ? 7 : (par0 >= 3 ? 3 : 1)))))))));
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }
    
    // FCMOD: inherited function added so that blocks like Hoppers get collision events with items on top.  
    // Copy of code from Entity.java, changes marked with FCMOD.
    @Override
    protected void doBlockCollisions()
    {
        int i = MathHelper.floor_double(boundingBox.minX + 0.001D);
        int j = MathHelper.floor_double(boundingBox.minY - 0.01D);
        int k = MathHelper.floor_double(boundingBox.minZ + 0.001D);
        int l = MathHelper.floor_double(boundingBox.maxX - 0.001D);
        int i1 = MathHelper.floor_double(boundingBox.maxY - 0.001D);
        int j1 = MathHelper.floor_double(boundingBox.maxZ - 0.001D);

        if (worldObj.checkChunksExist(i, j, k, l, i1, j1))
        {
            for (int k1 = i; k1 <= l; k1++)
            {
                for (int l1 = j; l1 <= i1; l1++)
                {
                    for (int i2 = k; i2 <= j1; i2++)
                    {
                        int j2 = worldObj.getBlockId(k1, l1, i2);

                        if (j2 > 0)
                        {
                            Block.blocksList[j2].onEntityCollidedWithBlock(worldObj, k1, l1, i2, this);
                        }
                    }
                }
            }
        }
    }
    // END FCMOD
}
