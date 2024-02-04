package net.minecraft.src;

import btw.item.BTWItems;

public class EntitySilverfish extends EntityMob
{
    /**
     * A cooldown before this entity will search for another Silverfish to join them in battle.
     */
    private int allySummonCooldown;

    public EntitySilverfish(World par1World)
    {
        super(par1World);
        this.texture = "/mob/silverfish.png";
        this.setSize(0.3F, 0.7F);
        this.moveSpeed = 0.6F;
    }

    public int getMaxHealth()
    {
        return 8;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Finds the closest player within 16 blocks to attack, or null if this Entity isn't interested in attacking
     * (Animals, Spiders at day, peaceful PigZombies).
     */
    protected Entity findPlayerToAttack()
    {
        double var1 = 8.0D;
        return this.worldObj.getClosestVulnerablePlayerToEntity(this, var1);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.silverfish.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.silverfish.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.silverfish.kill";
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
            if (this.allySummonCooldown <= 0 && (par1DamageSource instanceof EntityDamageSource || par1DamageSource == DamageSource.magic))
            {
                this.allySummonCooldown = 20;
            }

            return super.attackEntityFrom(par1DamageSource, par2);
        }
    }

    /**
     * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
     */
    protected void attackEntity(Entity par1Entity, float par2)
    {
        if (this.attackTime <= 0 && par2 < 1.2F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY)
        {
            this.attackTime = 20;
            this.attackEntityAsMob(par1Entity);
        }
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    protected void playStepSound(int par1, int par2, int par3, int par4)
    {
        this.playSound("mob.silverfish.step", 0.15F, 1.0F);
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return 0;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.renderYawOffset = this.rotationYaw;
        super.onUpdate();
    }

    protected void updateEntityActionState()
    {
        super.updateEntityActionState();

        if (!this.worldObj.isRemote)
        {
            int var1;
            int var2;
            int var3;
            int var5;

            if (this.allySummonCooldown > 0)
            {
                --this.allySummonCooldown;

                if (this.allySummonCooldown == 0)
                {
                    var1 = MathHelper.floor_double(this.posX);
                    var2 = MathHelper.floor_double(this.posY);
                    var3 = MathHelper.floor_double(this.posZ);
                    boolean var4 = false;

                    for (var5 = 0; !var4 && var5 <= 5 && var5 >= -5; var5 = var5 <= 0 ? 1 - var5 : 0 - var5)
                    {
                        for (int var6 = 0; !var4 && var6 <= 10 && var6 >= -10; var6 = var6 <= 0 ? 1 - var6 : 0 - var6)
                        {
                            for (int var7 = 0; !var4 && var7 <= 10 && var7 >= -10; var7 = var7 <= 0 ? 1 - var7 : 0 - var7)
                            {
                                int var8 = this.worldObj.getBlockId(var1 + var6, var2 + var5, var3 + var7);

                                if (var8 !=0 && Block.blocksList[var8].isBlockInfestedBy(this))
                                {
                                    this.worldObj.destroyBlock(var1 + var6, var2 + var5, var3 + var7, true);
                                    Block.blocksList[var8].onBlockDestroyedByPlayer(this.worldObj, var1 + var6, var2 + var5, var3 + var7, 0);

                                    if (this.rand.nextBoolean())
                                    {
                                        var4 = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (this.entityToAttack == null && !this.hasPath())
            {
                var1 = MathHelper.floor_double(this.posX);
                var2 = MathHelper.floor_double(this.posY + 0.5D);
                var3 = MathHelper.floor_double(this.posZ);
                int var9 = this.rand.nextInt(6);
                var5 = this.worldObj.getBlockId(var1 + Facing.offsetsXForSide[var9], var2 + Facing.offsetsYForSide[var9], var3 + Facing.offsetsZForSide[var9]);

                if (var5 !=0)
                {
                	int infestmetadata = worldObj.getBlockMetadata( var1 + Facing.offsetsXForSide[var9], var2 + Facing.offsetsYForSide[var9], var3 + Facing.offsetsZForSide[var9]);

                	if (Block.blocksList[var5].isBlockInfestable(this, infestmetadata))
                	{
                		Block.blocksList[var5].onInfested(worldObj, this, 
                				var1 + Facing.offsetsXForSide[var9], var2 + Facing.offsetsYForSide[var9], var3 + Facing.offsetsZForSide[var9],
                				infestmetadata);
                	}
                	else
                	{
                		this.updateWanderPath();
                	}
                }
            }
            else if (this.entityToAttack != null && !this.hasPath())
            {
            	this.entityToAttack = null;
            }
        }
    }

    /**
     * Takes a coordinate in and returns a weight to determine how likely this creature will try to path to the block.
     * Args: x, y, z
     */
    public float getBlockPathWeight(int par1, int par2, int par3)
    {
        return this.worldObj.getBlockId(par1, par2 - 1, par3) == Block.stone.blockID ? 10.0F : super.getBlockPathWeight(par1, par2, par3);
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel()
    {
        return true;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        if (super.getCanSpawnHere())
        {
            EntityPlayer var1 = this.worldObj.getClosestPlayerToEntity(this, 5.0D);
            return var1 == null;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the amount of damage a mob should deal.
     */
    public int getAttackStrength(Entity par1Entity)
    {
        return 1;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }
    
    // FCMOD: Code added
    @Override
    protected void dropFewItems( boolean bKilledByPlayer, int iLootingModifier )
    {
    	if ( worldObj.provider.dimensionId == 1 )
    	{
	        int iDropChance = this.rand.nextInt( 5 ) - 3 + iLootingModifier;
	
	        if ( iDropChance > 0 )
	        {
	            dropItem( Item.clay.itemID, 1 );
	        }
    	}
    }
    
    @Override
    public void checkForScrollDrop()
    {    	
    	if ( worldObj.provider.dimensionId == 1 )
    	{
	    	if ( rand.nextInt( 1000 ) == 0 )
	    	{
	    		ItemStack itemstack = new ItemStack( BTWItems.arcaneScroll, 1, Enchantment.efficiency.effectId );
	            
	            entityDropItem(itemstack, 0.0F);
	    	}
    	}
    }    
    // END FCMOD
}
