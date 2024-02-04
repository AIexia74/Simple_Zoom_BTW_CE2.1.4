package net.minecraft.src;

import btw.block.BTWBlocks;
import btw.block.blocks.BedBlockBase;
import btw.block.tileentity.beacon.CompanionBeaconEffect;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class FoodStats
{
    /** The player's food level. */
    private int foodLevel = 60;

    /** The player's food saturation. */
    private float foodSaturationLevel = 0F;

    /** The player's food exhaustion. */
    private float foodExhaustionLevel;

    /** The player's food timer value. */
    private int foodTimer = 0;
    
    private int prevFoodLevel = 60;

    /**
     * Eat some food.
     */
    public void addStats(ItemFood par1ItemFood)
    {
        this.addStats(par1ItemFood.getHungerRestored(), par1ItemFood.getSaturationModifier());
    }

    /**
     * Reads food stats from an NBT object.
     */
    public void readNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.hasKey("foodLevel"))
        {
            this.foodLevel = par1NBTTagCompound.getInteger("foodLevel");
            this.foodTimer = par1NBTTagCompound.getInteger("foodTickTimer");
            this.foodSaturationLevel = par1NBTTagCompound.getFloat("foodSaturationLevel");
            this.foodExhaustionLevel = par1NBTTagCompound.getFloat("foodExhaustionLevel");
            
            // FCMOD: Code added
            if ( !par1NBTTagCompound.hasKey("fcFoodLevelAdjusted"))
            {
            	foodLevel = foodLevel * 3;
            	foodSaturationLevel = 0F;
            }
            
            // sanity check the values as apparently they can get fucked up when importing from vanilla
            
            if ( foodLevel > 60 || foodLevel < 0 )
            {
            	foodLevel = 60;
            }
            
            if ( foodSaturationLevel > 20F || foodSaturationLevel < 0F )
            {
            	foodSaturationLevel = 20F;
            }
            // END FCMOD
        }
    }

    /**
     * Writes food stats to an NBT object.
     */
    public void writeNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("foodLevel", this.foodLevel);
        par1NBTTagCompound.setInteger("foodTickTimer", this.foodTimer);
        par1NBTTagCompound.setFloat("foodSaturationLevel", this.foodSaturationLevel);
        par1NBTTagCompound.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
        
        // FCMOD: Code added
        par1NBTTagCompound.setBoolean( "fcFoodLevelAdjusted", true );
        // END FCMOD
    }

    /**
     * Get the player's food level.
     */
    public int getFoodLevel()
    {
        return this.foodLevel;
    }

    @Environment(EnvType.CLIENT)
    public int getPrevFoodLevel()
    {
        return this.prevFoodLevel;
    }

    /**
     * If foodLevel is not max.
     */
    public boolean needFood()
    {
        return this.foodLevel < 60;
    }

    /**
     * adds input to foodExhaustionLevel to a max of 40
     */
    public void addExhaustion(float par1)
    {
        this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + par1, 40.0F);        
    }

    /**
     * Get the player's food saturation level.
     */
    public float getSaturationLevel()
    {
        return this.foodSaturationLevel;
    }

    public void setFoodLevel(int par1)
    {
        this.foodLevel = par1;
    }

    public void setFoodSaturationLevel(float par1)
    {
        this.foodSaturationLevel = par1;
    }
    
    // FCMOD: Added New
    /**
     * Note that iFoodGain is one third regular hunger gained, with 6 units being a full pip
     */
    public void addStats( int iFoodGain, float fFatMultiplier )
    {
    	int iPreviousFoodLevel = foodLevel;
    	
        foodLevel = Math.min( iFoodGain + foodLevel, 60);
        
        int iExcessFood = iFoodGain - ( foodLevel - iPreviousFoodLevel );
        
        if ( iExcessFood > 0 )
        {
        	// divide by 3 due to increased resolution
        	
            foodSaturationLevel = Math.min( foodSaturationLevel + (float)iExcessFood * fFatMultiplier / 3F, 20F );
        }
    }
	
	public void onUpdate(EntityPlayer player) {
		// only called on server
		int difficulty = player.worldObj.difficultySetting;
		
		prevFoodLevel = foodLevel;
		
		if (difficulty > 0) {
			// burn hunger
			while (foodLevel > 0 && foodExhaustionLevel >= 1.33F && !shouldBurnFatBeforeHunger()) {
				foodExhaustionLevel -= 1.33F;
				foodLevel = Math.max(foodLevel - 1, 0);
			}
			
			// burn fat
			while (foodExhaustionLevel >= 0.5F && shouldBurnFatBeforeHunger()) {
				foodExhaustionLevel -= 0.5F;
				foodSaturationLevel = Math.max(foodSaturationLevel - 0.125F, 0F);
			}
		}
		else {
			foodExhaustionLevel = 0F;
		}
		
		if (CompanionBeaconEffect.companionStrength > 3 && player.worldObj.rand.nextInt(5000) == 0) {
			attemptToShit(player);
		}
		
		int healingHungerThreshold = 24;
		
		if (foodLevel > healingHungerThreshold && player.shouldHeal()) {
			++foodTimer;
			
			int foodTimerMax = (int) (400 * player.worldObj.getDifficulty().getHealthRegenDelayMultiplier());
			
			if (foodTimer >= foodTimerMax) {
				if (player.sleeping) {
					Block block = Block.blocksList[player.worldObj.getBlockId((int) player.posX - 1, (int) player.posY, (int) player.posZ)];
					
					if (block instanceof BedBlockBase) {
						BedBlockBase bed = (BedBlockBase) block;
						
						if (!bed.blocksHealing()) {
							player.heal(1);
						}
					}
				}
				else {
					player.heal(1);
				}
				
				foodTimer = 0;
			}
		}
		else if (foodLevel <= 0 && foodSaturationLevel <= 0.01F) {
			++foodTimer;
			
			if (foodTimer >= 80) {
				if (difficulty > 0) {
					player.attackEntityFrom(DamageSource.starve, 1);
				}
				
				foodTimer = 0;
			}
			
			// reset the exhaustion level so that it doesn't stack up while the player is starving
			foodExhaustionLevel = 0F;
		}
		else {
			foodTimer = 0;
		}
	}
    
    private boolean shouldBurnFatBeforeHunger()
    {
    	// only burn fat when the corresponding hunger pip is completely depleted
    	
    	return foodSaturationLevel > (float)( ( foodLevel + 5 ) / 6 ) * 2F;    	
    }    
    // END FCMOD
	public boolean attemptToShit(EntityPlayer player) {
		float poopVectorX = MathHelper.sin((player.rotationYawHead / 180F) * (float) Math.PI);
	
		float poopVectorZ = -MathHelper.cos((player.rotationYawHead / 180F) * (float) Math.PI);
	
		double shitPosX = player.posX + poopVectorX;
		double shitPosY = player.posY + 0.25D;
		double shitPosZ = player.posZ + poopVectorZ;
	
		int shitPosI = MathHelper.floor_double(shitPosX);
		int shitPosJ = MathHelper.floor_double(shitPosY);
		int shitPosK = MathHelper.floor_double(shitPosZ);
	
		if (!isPathToBlockOpenToShitting(shitPosI, shitPosJ, shitPosK, player)) {
			return false;
		}
	
		EntityItem entityitem =
				(EntityItem) EntityList.createEntityOfType(EntityItem.class, player.worldObj, shitPosX, shitPosY, shitPosZ, new ItemStack(BTWItems.dung));
	
		float velocityFactor = 0.05F;
	
		entityitem.motionX = poopVectorX * 10.0f * velocityFactor;
	
		entityitem.motionZ = poopVectorZ * 10.0f * velocityFactor;
	
		entityitem.motionY = (float) player.worldObj.rand.nextGaussian() * velocityFactor + 0.2F;
	
		entityitem.delayBeforeCanPickup = 10;
	
		player.worldObj.spawnEntityInWorld(entityitem);
	
		player.worldObj.playSoundAtEntity(player, "random.explode", 0.2F, 1.25F);
	
		player.worldObj.playSoundAtEntity(player, "random.classic_hurt", player.getSoundVolume(), (player.rand.nextFloat() - player.rand.nextFloat()) * 0.2F + 1.0F);
	
		// emit smoke
	
		for (int counter = 0; counter < 5; counter++) {
			double smokeX = player.posX + (poopVectorX * 0.5f) + (player.worldObj.rand.nextDouble() * 0.25F);
			double smokeY = player.posY + player.worldObj.rand.nextDouble() * 0.5F + 0.25F;
			double smokeZ = player.posZ + (poopVectorZ * 0.5f) + (player.worldObj.rand.nextDouble() * 0.25F);
		
			player.worldObj.spawnParticle("smoke", smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D);
		}
	
		return true;
	}
	
	private boolean isPathToBlockOpenToShitting(int i, int j, int k, EntityPlayer player) {
		if (!isBlockOpenToShitting(i, j, k, player)) {
			return false;
		}
		
		int wolfI = MathHelper.floor_double(player.posX);
		int wolfK = MathHelper.floor_double(player.posZ);
		
		int deltaI = i - wolfI;
		int deltaK = k - wolfK;
		
		if (deltaI != 0 && deltaK != 0) {
			// we're pooping on a diagnal.  Test to make sure that we're not warping poop through blocked off corners
			
			if (!isBlockOpenToShitting(wolfI, j, k, player) && !isBlockOpenToShitting(i, j, wolfK, player)) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isBlockOpenToShitting(int i, int j, int k, EntityPlayer player) {
		Block block = Block.blocksList[player.worldObj.getBlockId(i, j, k)];
		
		if (block != null &&
				(block == Block.waterMoving || block == Block.waterStill || block == Block.lavaMoving || block == Block.lavaStill || block == Block.fire ||
						block.blockMaterial.isReplaceable() || block == BTWBlocks.detectorLogic || block == BTWBlocks.glowingDetectorLogic ||
						block == BTWBlocks.stokedFire)) {
			block = null;
		}
		
		if (block != null) {
			return false;
		}
		
		return true;
	}
}
