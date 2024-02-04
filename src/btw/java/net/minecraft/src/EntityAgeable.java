package net.minecraft.src;

import btw.util.MiscUtils;

public abstract class EntityAgeable extends EntityCreature
{
    private float adultWidth = -1.0F; // -1 flags super entity size has not been initialized
    private float adultHeight;

    public EntityAgeable(World par1World)
    {
        super(par1World);
    }

    public abstract EntityAgeable createChild(EntityAgeable var1);

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    // FCMOD: Added to alow calling this method directly regardless of class hierarchy
    {
    	return entityAgeableInteract(par1EntityPlayer);
    }
    
    public boolean entityAgeableInteract(EntityPlayer par1EntityPlayer)
    // END FCMOD
    {
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

        if (var2 != null && var2.itemID == Item.monsterPlacer.itemID && !this.worldObj.isRemote)
        {
            Class var3 = EntityList.getClassFromID(var2.getItemDamage());

            if (var3 != null && var3.isAssignableFrom(this.getClass()))
            {
                EntityAgeable var4 = this.createChild(this);

                if (var4 != null)
                {
                    var4.setGrowingAge( -getTicksForChildToGrow());
                    var4.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                    this.worldObj.spawnEntityInWorld(var4);

                    if (var2.hasDisplayName())
                    {
                        var4.func_94058_c(var2.getDisplayName());
                    }

                    if (!par1EntityPlayer.capabilities.isCreativeMode)
                    {
                        --var2.stackSize;

                        if (var2.stackSize <= 0)
                        {
                            par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                        }
                    }
                }
            }
        }

        return super.interact(par1EntityPlayer);
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(12, new Integer(0));
    }

    /**
     * The age value may be negative or positive or zero. If it's negative, it get's incremented on each tick, if it's
     * positive, it get's decremented each tick. Don't confuse this with EntityLiving.getAge. With a negative value the
     * Entity is considered a child.
     */
    public int getGrowingAge()
    {
        return this.dataWatcher.getWatchableObjectInt(12);
    }

    /**
     * The age value may be negative or positive or zero. If it's negative, it get's incremented on each tick, if it's
     * positive, it get's decremented each tick. With a negative value the Entity is considered a child.
     */
    public void setGrowingAge(int par1)
    {
        this.dataWatcher.updateObject(12, Integer.valueOf(par1));
        // FCNOTE: This seems grossly ineffecient as I believe this data will get sent to the 
        // clients every tick for child animals, and those waiting to recharge their love juice
    	adjustSizeForAge(isChild());
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Age", this.getGrowingAge());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setGrowingAge(par1NBTTagCompound.getInteger("Age"));
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (this.worldObj.isRemote)
        {
        	adjustSizeForAge(isChild());
        }
        else
        {
            int var1 = this.getGrowingAge();

            if (var1 < 0)
            {
	        	// FCMOD: Added
	        	if ( !canChildGrow() )
	        	{
	        		return;
	        	}
	        	// END FCMOD
	        	
                ++var1;
                
                // FCMOD: Added
        		if ( var1 == 0 )
        		{
        			// child is about to grow to adulthood.  Make sure it has enough space
        			AxisAlignedBB adultBounds = AxisAlignedBB.getAABBPool().getAABB(
        				boundingBox.minX, boundingBox.minY, boundingBox.minZ,
                        boundingBox.minX + adultWidth,
                        boundingBox.minY + adultHeight,
                        boundingBox.minZ + adultWidth);
        			
        			if ( !worldObj.getCollidingBoundingBoxes( this, adultBounds ).isEmpty() )
        			{
        				var1 = -20;
        			}        			
        		}
        		// END FCMOD
        		
                this.setGrowingAge(var1);                
            }
            else if (var1 > 0)
            {
	        	// FCMOD: Added
	        	if ( !canLoveJuiceRegenerate() )
	        	{
	        		return;
	        	}
	        	// END FCMOD
	        	
                --var1;
                this.setGrowingAge(var1);
            }
        }
    }
    
    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return this.getGrowingAge() < 0;
    }

    // FCMOD: Added New
	protected final void setSize( float fWidth, float fHeight )
	{
        boolean bSizeAlreadyInitialized = adultWidth > 0F;

        adultWidth = fWidth;
        adultHeight = fHeight;

        if ( !bSizeAlreadyInitialized )
        {
        	adjustedSizeToScale(1F);
        }
	}

    private void adjustedSizeToScale(float fScale)
    {
        super.setSize(adultWidth * fScale, adultHeight * fScale);
    }
    
    public void adjustSizeForAge(boolean bIsChild)
    {
    	adjustedSizeToScale(bIsChild ? 0.5F : 1F);
    }
    
    public boolean canChildGrow()
    {
    	// prevent animals growing up in the end
    	
    	return worldObj.provider.dimensionId != 1;
    }
    
    public boolean canLoveJuiceRegenerate()
    {
    	return true;
    }

    public int getTicksForChildToGrow()
    {
    	return MiscUtils.TICKS_PER_GAME_DAY;
    }
    // END FCMOD
}
