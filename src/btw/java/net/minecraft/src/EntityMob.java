package net.minecraft.src;

public abstract class EntityMob extends EntityCreature implements IMob
{
    public EntityMob(World par1World)
    {
        super(par1World);
        this.experienceValue = 5;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    // FCMOD: Addded so that method can be called directly regardless of hierarchy
    {
    	entityMobOnLivingUpdate();
    }
    
    public void entityMobOnLivingUpdate()
    // END FCMOD
    {
        this.updateArmSwingProgress();
        float var1 = this.getBrightness(1.0F);

        if (var1 > 0.5F)
        {
            this.entityAge += 2;
        }

        super.onLivingUpdate();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isRemote && this.worldObj.difficultySetting == 0)
        {
            this.setDead();
        }
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        EntityPlayer var1 = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
        return var1 != null && this.canEntityBeSeen(var1) ? var1 : null;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    // FCMOD: Addded so that method can be called directly regardless of hierarchy
    {
    	return entityMobAttackEntityFrom(par1DamageSource, par2);
    }
    
    public boolean entityMobAttackEntityFrom(DamageSource par1DamageSource, int par2)
    // END FCMOD
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (super.attackEntityFrom(par1DamageSource, par2))
        {
            Entity var3 = par1DamageSource.getEntity();

            if (this.riddenByEntity != var3 && this.ridingEntity != var3)
            {
                if (var3 != this)
                {
                	// FCMOD: Code added so that mobs won't lose their target if they take damage from an environmental source
                	if ( var3 != null )
            		// END FCMOD
                    this.entityToAttack = var3;
                }

                return true;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    protected void attackEntity(Entity par1Entity, float par2)
    // FCMOD: Addded so that method can be called directly regardless of hierarchy
    {
    	entityMobAttackEntity(par1Entity, par2);
    }
    
    protected void entityMobAttackEntity(Entity par1Entity, float par2)
    // END FCMOD
    {
        if (this.attackTime <= 0 && par2 < 2.0F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY)
        {
            this.attackTime = 20;
            this.attackEntityAsMob(par1Entity);
        }
    }

    /**
     * Takes a coordinate in and returns a weight to determine how likely this creature will try to path to the block.
     * Args: x, y, z
     */
    public float getBlockPathWeight(int par1, int par2, int par3)
    {
        return 0.5F - this.worldObj.getLightBrightness(par1, par2, par3);
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);

        if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) > this.rand.nextInt(32)) {
            return false;
        }
        else {
            int blockLightValue = this.worldObj.getBlockLightValueNoSky(x, y, z);
            
            if (blockLightValue > 0) {
            	return false;
            }
            else {
                int naturalLightValue = this.worldObj.getBlockNaturalLightValue(x, y, z);
            	
                if (this.worldObj.isThundering()) {
                	naturalLightValue = Math.min(naturalLightValue, 5);
                }
                
                return naturalLightValue <= this.rand.nextInt(8);
            }
        }
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
    	return isValidLightLevel() && super.getCanSpawnHere() && canSpawnOnBlockBelow();
    }
    
    /**
     * Returns the amount of damage a mob should deal.
     */
    public int getAttackStrength(Entity par1Entity)
    {
        return 2;
    }
    
    // FCMOD: Added New
    @Override
    public int getMeleeAttackStrength(Entity target)
    {
    	return getAttackStrength( target );
    }
    
    protected boolean canSpawnOnBlockBelow()
    {
        int i = MathHelper.floor_double( posX );
        int j = (int)boundingBox.minY - 1;
        int k = MathHelper.floor_double( posZ );
        
        return canSpawnOnBlock(i, j, k);
    }
    
    protected boolean canSpawnOnBlock(int x, int y, int z) {
        Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
        return block != null && !block.isLeafBlock(worldObj, x, y, z);
    }

    protected void checkForCatchFireInSun()
    {
        if ( !worldObj.isRemote &&
                worldObj.isDaytime() &&
                !worldObj.isRainingAtPos((int) this.posX, (int) this.posY, (int) this.posZ)
                && !isChild() && !inWater)
        {
            float fBrightness = getBrightness( 1F );

            if ( fBrightness > 0.5F && rand.nextFloat() * 30F < ( fBrightness - 0.4F ) * 2F && 
            	worldObj.canBlockSeeTheSky( MathHelper.floor_double( posX ), 
            		MathHelper.floor_double( posY + (double)getEyeHeight() ), 
            		MathHelper.floor_double( posZ ) ) )
            {
            	// check to make sure water isn't directly below to prevent catching fire while bobbing
            	
            	int iBlockBelowID = worldObj.getBlockId( MathHelper.floor_double( posX ), 
            		MathHelper.floor_double( posY - 0.1F ), MathHelper.floor_double( posZ ) );
            	
            	Block blockBelow = Block.blocksList[iBlockBelowID];
            	
            	if ( blockBelow == null || blockBelow.blockMaterial != Material.water ) 
            	{            	                                    
	            	// client
	                ItemStack headStack = getCurrentItemOrArmor( 4 );
	            	// server
	                //ItemStack headStack = getEquipmentInSlot( 4 );
	
	                if ( headStack == null && !hasHeadCrabbedSquid() )
	                {
	                    setFire( 8 );
	                }
            	}
            }
        }
    }    
    // END FCMOD
}
